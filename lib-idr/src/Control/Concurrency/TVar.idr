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
getVersionTVar : HasIO io => TVar a -> io Bits64
getVersionTVar tvar = getVersion tvar.seqLock

public export
readTVar : HasIO io => TVar a -> io (Bits64, a)
readTVar tvar = do
	version <- getVersion tvar.seqLock
	case version `mod` 2 of
		0 => do
			value <- readIORef tvar.content
			versionCheck <- getVersion tvar.seqLock
			-- Unsure about memory guarantees, ¿Is this enough?
			if version == versionCheck
				then pure $ (version, value)
				else readTVar tvar
		-- Spin, only odd when being written into so wait
		_ => do
			_ <- printLn "Spinning"
			readTVar tvar

public export
writeTVar : HasIO io => TVar a -> Bits64 -> (a -> a) -> io (Bits64, a)
writeTVar tvar knownVersion f = do
	_ <- printLn "Attempting to update a TVar"
	True <- isLocked tvar.seqLock
		| False => do
			_ <- printLn "WrongPath1"
			?wrongPath1
	version <- getVersion tvar.seqLock
	case (version - knownVersion) of
		1 => do
   			value <- readIORef tvar.content
   			_ <- writeIORef tvar.content (f value)
   			_ <- printLn $ "Updated a TRef! " ++ show knownVersion ++ "->" ++ show version
   			pure (version, (f value))
		_ => do
			_ <- printLn "WrongPath2"
			?wrongPath2
