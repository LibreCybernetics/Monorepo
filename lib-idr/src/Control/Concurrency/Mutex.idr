module Control.Concurrency.Mutex

import System.Concurrency

%foreign "scheme:(lambda (x y) (or (and (eq? x y) 1) 0))"
prim__eq : Mutex -> Mutex -> Int

export
equal : Mutex -> Mutex -> Bool
equal m = (== 1) . prim__eq m

public export
withMutex : HasIO io => Mutex -> io a -> io a
withMutex m io = do
  mutexAcquire m
  r <- io
  mutexRelease m
  pure r
