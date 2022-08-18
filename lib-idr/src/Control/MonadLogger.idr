module Control.MonadLogger

data LogLevel
  = Trace
  | Debug
  | Info
  | Warn
  | Error

interface Monad a => MonadLogger a where
  log : LogLevel -> String -> a ()

record NullLogger a where
  constructor MkNullLogger
  content : a

Functor NullLogger where
  map f a = MkNullLogger (f a.content)

Applicative NullLogger where
  pure a = MkNullLogger a
  f <*> fa = MkNullLogger (f.content fa.content)

Monad NullLogger where
  fa >>= f = f fa.content

MonadLogger NullLogger where
  log _ _ = MkNullLogger ()
