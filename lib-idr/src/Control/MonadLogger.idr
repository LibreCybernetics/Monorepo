module Control.MonadLogger

import Prelude.Interfaces

import Data.List

import System.File.ReadWrite
import System.File.Support
import System.File.Virtual

import Control.ANSI

public export
data LogLevel
  = Trace
  | Debug
  | Info
  | Warn
  | Error

public export
Show LogLevel where
  show Trace = show $ colored White        "[TRACE]"
  show Debug = show $ colored BrightWhite  "[DEBUG]"
  show Info  = show $ colored BrightBlue   "[INFO ]"
  show Warn  = show $ colored BrightYellow "[WARN ]"
  show Error = show $ colored BrightRed    "[ERROR]"

public export
interface Monad a => MonadLogger a where
  log : LogLevel -> String -> a ()

export
record NullLogger a where
  constructor MkNullLogger
  content : a

public export
Functor NullLogger where
  map f = MkNullLogger . f . .content

public export
Applicative NullLogger where
  pure = MkNullLogger
  (<*>) f = MkNullLogger . f.content . .content

public export
Monad NullLogger where
  fa >>= f = f fa.content

public export
MonadLogger NullLogger where
  log _ _ = MkNullLogger ()

public export
MonadLogger IO where
  log l =
    map (const ()) . fPutStrLn stdout . (show l ++ " " ++)

public export
testLog : IO ()
testLog = do
  log Trace "Test"
  log Debug "Test"
  log Info  "Test"
  log Warn  "Test"
  log Error "Test"
