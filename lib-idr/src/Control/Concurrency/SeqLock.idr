module Control.Concurrency.SeqLock

export
data SeqLock : Type where [external]

%foreign "scheme:blodwen-seqlock-make"
prim__makeSeqLock : PrimIO SeqLock

export
make : HasIO io => io SeqLock
make = primIO prim__makeSeqLock

--
-- Non-locking
--

%foreign "scheme:blodwen-seqlock-locked?"
prim__lockedSeqLock : SeqLock -> PrimIO Int

%foreign "scheme:blodwen-seqlock-version"
prim__versionSeqLock : SeqLock -> PrimIO Bits64

export
isLocked : HasIO io => SeqLock -> io Bool
isLocked = map intToBool . primIO . prim__lockedSeqLock

export
getVersion : HasIO io => SeqLock -> io Bits64
getVersion = primIO . prim__versionSeqLock

--
-- Locking
--

%foreign "scheme:(lambda (x) (or (and (blodwen-seqlock-lock x) 1) 0))"
prim__lockSeqLock : SeqLock -> PrimIO Int

%foreign "scheme:blodwen-seqlock-unlock"
prim__unlockSeqLock : SeqLock -> PrimIO ()

%foreign "scheme:blodwen-seqlock-increase"
prim__increaseSeqLock : SeqLock -> PrimIO ()

export
lock : HasIO io => SeqLock -> io Bool
lock = map intToBool . primIO . prim__lockSeqLock

export
unlock : HasIO io => SeqLock -> io ()
unlock = primIO . prim__unlockSeqLock

export
increaseVersion : HasIO io => SeqLock -> io ()
increaseVersion = primIO . prim__increaseSeqLock

-- Doesn't check much since a SeqLock doesn't have many guards
basicTest : IO ()
basicTest = do
  l <- make
  r <- lock l
  v <- getVersion l
  printLn $ "Got Lock: " ++ show r ++ "; version: " ++ show v
  increaseVersion l
  r <- lock l
  v <- getVersion l
  printLn $ "Got Lock: " ++ show r ++ "; version: " ++ show v
  increaseVersion l
  unlock l
  v <- getVersion l
  printLn $ "Final version: " ++ show v
