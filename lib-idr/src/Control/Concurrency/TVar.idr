module Control.Concurrency.TVar

import Data.IORef
import System.Concurrency

import Control.Concurrency.SeqLock

public export
record TVar a where
	constructor MkTVar
	seqLock : SeqLock
	condition : Condition
	content : (IORef a)

public export
Eq (TVar a) where
	x == y = x.seqLock == y.seqLock

public export
newTVar : HasIO io => a -> io (TVar a)
newTVar initialValue = do
	seqLock <- newSeqLock
	condition <- makeCondition
	ioRef <- newIORef initialValue
	pure $ MkTVar seqLock condition ioRef

public export
readTVar : HasIO io => TVar a -> io (Bits64, a)
readTVar tvar = do
	version <- getVersion tvar.seqLock
	value   <- readIORef  tvar.content
	pure (version, value)
