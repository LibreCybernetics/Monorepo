module Control.Concurrency.STM

import Data.IORef
import Data.List
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
	Update : (a : Type) -> TVar a -> Bits64 -> (f: a -> a) -> a -> STMOperation

public export
record STM a where
  constructor MkSTM
  -- To regenerate forwards
  inputType : Type
  input : inputType
  op : Maybe (inputType -> STM a)
  -- To move backwards
  stack : Maybe (STM inputType)
  -- Operations that haven't been commited but should affect TVars in the transaction
  uncommitted : List STMOperation
  return : a

public export
stmBind : {i : Type} -> STM i -> (f : i -> STM o) -> STM o
stmBind s f = let r := f (s.return) in
  {inputType := i, input := s.return, op := Just f, stack := Just s} r

get : {a : Type} -> TVar a -> STM a
get tvar = unsafePerformIO $ do
  version <- getVersion tvar.seqLock
  case version `mod` 2 of
    0 => do
      value <- readIORef tvar.content
      pure $ MkSTM Unit () Nothing Nothing [(Get a tvar version)] value
    -- Spin, only odd when being written into so wait
    _ => pure $ get tvar

typeWrapTVar : STMOperation -> TVarTypeWrapper
typeWrapTVar op = case op of
	(Get    type tvar _)   => MkTVarTypeWrapper type tvar
	(Update type tvar _ _ _) => MkTVarTypeWrapper type tvar

nubTVars : STM a -> List TVarTypeWrapper
nubTVars = nub . (typeWrapTVar <$>) . .uncommitted

attemptLock : HasIO io => STM a -> io ()
attemptLock s = do
	let tvarsToLock = nubTVars s
	tvarsLocked <- sequence $ (lock . .value.seqLock) <$> tvarsToLock
	case all id tvarsLocked of
		True => do
			increased <- sequence $ (increaseVersion . .value.seqLock) <$> tvarsToLock
			?hole
		False => do
			let tvarsToUnlock = filter snd $ zip tvarsToLock tvarsLocked
			withMutex globalLock $ do
				tvarsUnlocked <- sequence $ map (unlock . .value.seqLock . fst) tvarsToUnlock
				lockedTVars  <- sequence $ (isLocked . .value.seqLock) <$> tvarsToLock
				let tvarsToWatch = filter snd $ zip tvarsToLock lockedTVars
				let conditionsToWatch = map (.value.condition . fst) tvarsToWatch
				case conditionsToWatch of
					[]      => pure ()
					(c::cs) =>
						-- NOTE: Timeout is in µs; on 1GHz processor a 1µs wait ~1,000 clock cycles
						-- NOTE: Parameter hasn't been tweaked yet; could be sub-optimal
						conditionWaitTimeout (head conditionsToWatch {ok=believe_me ()}) globalLock 1
			attemptLock s

public export
commit : HasIO io => STM a -> io a
