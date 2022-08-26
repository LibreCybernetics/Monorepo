module Control.Concurrency.STM

import Control.Concurrency.SeqLock
import Data.IORef

export
data TVar a = MkTVar SeqLock (IORef a)

export
Eq (TVar a) where
	(MkTVar leftSeqLock leftIORef) == (MkTVar rightSeqLock rightIORef) =
		leftSeqLock == rightSeqLock

public export
newTVar : HasIO io => a -> io (TVar a)
newTVar initialValue = do
	seqLock <- newSeqLock
	ioRef <- newIORef initialValue
	pure $ MkTVar seqLock ioRef

export
data STMOperation : Type where
	Get : (a : Type) -> TVar a -> a -> STMOperation
	Update : (a : Type) -> TVar a -> a -> (f: a -> a) -> STMOperation

export
data STM = MkSTM (List STMOperation)
