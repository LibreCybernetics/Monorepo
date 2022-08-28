module Control.Concurrency.STM

import Data.IORef
import Data.List
import Data.Maybe
import System.Concurrency
import Debug.Trace

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

Show STMOperation where
	show (Update _ _ v _) = "Update v: " ++ show v
	show (Get _ _ v) = "Get v: " ++ show v

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

allUncommitted : STM io a -> List STMOperation
allUncommitted s = (maybe [] allUncommitted s.stack) ++ s.uncommitted

public export
Functor (STM io) where
  map f = { return $= f, op $= map (map f .) }

||| Warning: Has a bug that drops operations
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

nubTVars : List STMOperation -> List TVarTypeWrapper
nubTVars = nub . (typeWrapTVar <$>)

acquireLocks : HasIO io => STM io a -> io ()
acquireLocks s = do
	let pendingOperations = allUncommitted s
	let tvarsToLock = nubTVars pendingOperations
	tvarsLocked <- sequence $ (lock . .value.seqLock) <$> tvarsToLock
	case all id tvarsLocked of
		True => pure ()
		False => do
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
						conditionWaitTimeout c globalLock 1
			acquireLocks s

releaseLocks : HasIO io => STM io a -> io ()
releaseLocks s = withMutex globalLock $ do
	let completedOperations = allUncommitted s
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

doOperation : HasIO io => (o : STMOperation) -> io Bits64
doOperation (Update a tvar version f) = fst <$> writeTVar tvar version f
doOperation (Get a tvar _) = fst <$> readTVar tvar

operationTVarVersion : HasIO io => STMOperation -> io Bits64
operationTVarVersion (Update a tvar _ _) = getVersionTVar tvar
operationTVarVersion (Get a tvar _) = getVersionTVar tvar


-- validate : HasIO io -> STM io a -> io
-- validate = do

executeCommit : HasIO io => List STMOperation -> io ()
executeCommit ops = do
    _ <- printLn ops
    let updateOps = filter isUpdate ops
    let updateTVars = nubTVars updateOps
    increased <- sequence $ (increaseVersion . .value.seqLock) <$> updateTVars
    _ <- sequence_ (doOperation <$> updateOps)
    increased <- sequence $ (increaseVersion . .value.seqLock) <$> updateTVars
    pure ()

public export
commit : HasIO io => STM io a -> io a
commit s = withLocks s $ do
  let pendingOperations = reverse $ allUncommitted s
  currentVersions <- sequence (operationTVarVersion <$> pendingOperations)
  _ <- printLn currentVersions
  validated <- ?validationHole
  _ <- executeCommit pendingOperations
  pure s.return
