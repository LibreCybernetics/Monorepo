module Control.Concurrency.STM

import Data.IORef
import Data.List
import Data.Maybe
import System.Concurrency

import Control.Concurrency.Mutex
import Control.Concurrency.SeqLock
import Control.Concurrency.TVar

||| Only needed when locking/unlocking a collection of TVars
globalLock : Mutex
globalLock = unsafePerformIO makeMutex

public export
record TVarTypeWrapper where
	constructor MkTVarTypeWrapper
	type  : Type
	value : TVar type

export
Eq TVarTypeWrapper where
	x == y = (x.value.seqLock) == (y.value.seqLock)

export
data STMOperation : Type where
	Get : (a : Type) -> TVar a -> Bits64 -> STMOperation
	Update : (a : Type) -> TVar a -> Bits64 -> (f: a -> a) -> STMOperation

isUpdate : STMOperation -> Bool
isUpdate (Update _ _ _ _) = True
isUpdate _ = False

public export
record STM io a where
  constructor MkSTM
  -- To regenerate forwards
  inputType : Type
  input : inputType
  op : Maybe (inputType -> STM io a)
  -- To move backwards
  stack : Maybe (STM io inputType)
  -- Operations that haven't been commited but should affect TVars in the transaction
  -- WARNING: You have to recurse the stack to get every operation
  uncommitted : List STMOperation
  return : a

public export
Functor (STM io) where
  map f = { return $= f, op $= map (map f .) }

public export
stmBind : {i : Type} -> STM io i -> (f : i -> STM io o) -> STM io o
stmBind s f = let r := f (s.return) in
  {inputType := i, input := s.return, op := Just f, stack := Just s} r

public export
stmApp : {a, b : Type} -> STM io (a -> b) -> STM io a -> STM io b
stmApp sf sa = sf `stmBind` (\f => map f sa)

public export
pure : HasIO io => a -> STM io a
pure v = MkSTM Unit () Nothing Nothing [] v

stmRetryLast : STM io a -> STM io a
stmRetryLast s = fromMaybe s (($ s.input) <$> s.op)

-- stmRetryAll : STM io a -> STM io a
-- stmRetryAll s = (fromMaybe ?holeMaybe (stmRetryAll <$> s.stack)) `stmBind` stmRetryLast s

public export
get : {a : Type} -> HasIO io => TVar a -> STM io a
get tvar = unsafePerformIO $ do
	(version, value) <- readTVar tvar
	pure $ MkSTM Unit () Nothing Nothing [(Get a tvar version)] value

public export
update : {a : Type} -> HasIO io => TVar a -> (a -> a) -> STM io a
update tvar f = unsafePerformIO $ do
	(version, value) <- readTVar tvar
	pure $ MkSTM Unit () Nothing Nothing [(Update a tvar version f)] (f value)

typeWrapTVar : STMOperation -> TVarTypeWrapper
typeWrapTVar op = case op of
	(Get    type tvar _)   => MkTVarTypeWrapper type tvar
	(Update type tvar _ _) => MkTVarTypeWrapper type tvar

allUncommitted : STM io a -> List STMOperation
allUncommitted s = (maybe [] allUncommitted s.stack) ++ s.uncommitted

nubTVars : List STMOperation -> List TVarTypeWrapper
nubTVars = nub . (typeWrapTVar <$>)

acquireLocks : HasIO io => STM io a -> io ()
acquireLocks s = do
	let pendingOperations = allUncommitted s
	let tvarsToLock = nubTVars pendingOperations
	tvarsLocked <- sequence $ (lock . .value.seqLock) <$> tvarsToLock
	case all id tvarsLocked of
		True => do
			_ <- printLn "Got all locks"
			let tvarsToIncrease = nubTVars . filter isUpdate $ pendingOperations
			increased <- sequence $ (increaseVersion . .value.seqLock) <$> tvarsToIncrease
			pure ()
		False => do
			_ <- printLn "Didn't get all locks"
			let tvarsToUnlock = filter snd $ zip tvarsToLock tvarsLocked
			withMutex globalLock $ do
				tvarsUnlocked <- sequence $ map (unlock . .value.seqLock . fst) tvarsToUnlock
				tvarsUnlockedNotified <- sequence $ map (conditionSignal . .value.condition . fst) tvarsToUnlock
				lockedTVars  <- sequence $ (isLocked . .value.seqLock) <$> tvarsToLock
				let tvarsToWatch = filter snd $ zip tvarsToLock lockedTVars
				let conditionsToWatch = map (.value.condition . fst) tvarsToWatch
				case conditionsToWatch of
					[]      => pure ()
					(c::cs) =>
						-- NOTE: Timeout is in µs; on 1GHz processor a 1µs wait ~1,000 clock cycles
						-- NOTE: Parameter hasn't been tweaked yet; could be sub-optimal
						conditionWaitTimeout (head conditionsToWatch {ok=believe_me ()}) globalLock 1
			acquireLocks s

releaseLocks : HasIO io => STM io a -> io ()
releaseLocks s = withMutex globalLock $ do
	let completedOperations = allUncommitted s
	let tvarsToIncrease = nubTVars . filter isUpdate $ completedOperations
	increased <- sequence $ (increaseVersion . .value.seqLock) <$> tvarsToIncrease
	let tvarsToUnlock = nubTVars completedOperations
	tvarsUnlocked <- sequence $ (unlock . .value.seqLock) <$> tvarsToUnlock
	tvarsUnlockedNotified <- sequence $ map (conditionSignal . .value.condition) tvarsToUnlock
	pure ()

withLocks : HasIO io => STM io a -> io a -> io a
withLocks s io = do
  _ <- acquireLocks s
  r <- io
  _ <- releaseLocks s
  pure r

public export
operationType : STMOperation -> Type
operationType (Update a _ _ _) = a
operationType (Get a _ _) = a

public export
doOperation : HasIO io => (o : STMOperation) -> io Bits64
doOperation (Update a tvar version f) = fst <$> writeTVar tvar version f
doOperation (Get a tvar _) = fst <$> readTVar tvar

public export
commit : HasIO io => STM io a -> io a
commit s = do
  _ <- acquireLocks s
  _ <- printLn "Going to Commit!"
  let pendingOperations = allUncommitted s
  _ <- sequence_ (doOperation <$> pendingOperations)
  _ <- releaseLocks s
  pure $ s.return
