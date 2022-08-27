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

partial
flagSwitcher : TVar (List Flag) -> Int -> IO ()
flagSwitcher tvar n = do
	_ <- commit $ get tvar `stmBind` \queue =>
		let next = fromMaybe Blue $ head' queue in
		update tvar ((flipFlag next) ::) `stmBind` (\(h::_) =>
			pure ()
		)
	if n > 0 then flagSwitcher tvar (n - 1) else pure ()

partial
test1 : IO Bool
test1 = do
	tvar <- newTVar (the (List Flag) [])
	_ <- forkIO $ flagSwitcher tvar 1000
	_ <- forkIO $ flagSwitcher tvar 1000
	_ <- forkIO $ flagSwitcher tvar 1000
	_ <- forkIO $ flagSwitcher tvar 1000
	pure True

export
partial
allTests : List (IO Bool)
allTests = [test1]
