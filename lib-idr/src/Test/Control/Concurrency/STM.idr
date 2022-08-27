module Test.Control.Concurrency.STM

import Data.Maybe
import Data.List
import System.Future

import Control.Concurrency.TVar
import Control.Concurrency.STM

data Flag = Red | Blue

Eq Flag where
  Red  ==  Red = True
  Blue == Blue = True
  _ == _ = False

flipFlag : Flag -> Flag
flipFlag Red  = Blue
flipFlag Blue = Red

flagSwitcher : HasIO io => TVar (List Flag) -> Nat -> io ()
flagSwitcher tvar n = commit . the (STM io Unit) $ do
  queue <- get tvar
  let next = fromMaybe Blue $ head' queue
  (h::_) <- update tvar ((flipFlag next) ::)
  pure ()

test1 : HasIO io => io ()
test1 = do
  tvar <- newTVar $ the (List Flag) []
  printLn "Success!"
