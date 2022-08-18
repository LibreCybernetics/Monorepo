module Control.MonadLogger

import System.File.ReadWrite
import System.File.Virtual

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
  map f = MkNullLogger . f . .content

Applicative NullLogger where
  pure = MkNullLogger
  (<*>) f = MkNullLogger . f.content . .content

Monad NullLogger where
  fa >>= f = f fa.content

MonadLogger NullLogger where
  log _ _ = MkNullLogger ()

MonadLogger IO where
  log l =
    map (const ()) . fPutStrLn stderr
