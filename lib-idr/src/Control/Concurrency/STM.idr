module Control.Concurrency.STM

import Data.IORef
import Data.List
import System.Concurrency

import Control.Concurrency.Mutex
import Control.Concurrency.SeqLock

||| Only needed when locking/unlocking a collection of TVars
globalLock : Mutex
globalLock = unsafePerformIO makeMutex

export
record TVar a where
	constructor MkTVar
	seqLock : SeqLock
	condition : Condition
	content : (IORef a)

export
Eq (TVar a) where
	x == y = x.seqLock == y.seqLock

public export
newTVar : HasIO io => a -> io (TVar a)
newTVar initialValue = do
	seqLock <- newSeqLock
	condition <- makeCondition
	ioRef <- newIORef initialValue
	pure $ MkTVar seqLock condition ioRef

record TVarTypeWrapper where
  constructor MkTVarTypeWrapper
  type  : Type
  value : TVar type

Eq TVarTypeWrapper where
  x == y = (x.value.seqLock) == (y.value.seqLock)

export
data STMOperation : Type where
	Get : (a : Type) -> TVar a -> a -> STMOperation
	Update : (a : Type) -> TVar a -> a -> (f: a -> a) -> STMOperation

export
record STM a where
  constructor MkSTM
  operations : (List STMOperation)
  return : a

typeWrapTVar : STMOperation -> TVarTypeWrapper
typeWrapTVar op = case op of
	(Get    type tvar _)   => MkTVarTypeWrapper type tvar
	(Update type tvar _ _) => MkTVarTypeWrapper type tvar

nubTVars : STM a -> List TVarTypeWrapper
nubTVars = nub . (typeWrapTVar <$>) . operations

attemptLock : HasIO io => STM a -> io ()
attemptLock s = do
	printLn "Nothing Yet"
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
					(c::cs) => conditionWaitTimeout (head conditionsToWatch {ok=believe_me ()}) globalLock 1000
			attemptLock s

public export
commit : HasIO io => STM a -> io a
