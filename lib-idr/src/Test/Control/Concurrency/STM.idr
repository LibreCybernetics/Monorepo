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

Show Flag where
	show Red  = "Red"
	show Blue = "Blue"

flipFlag : Flag -> Flag
flipFlag Red  = Blue
flipFlag Blue = Red

flagSwitcher : TVar (List Flag) -> Int -> IO ()
flagSwitcher tvar n = do
	_ <- commit $ update tvar (\queue =>
		let next = fromMaybe Blue $ head' queue in
		(flipFlag next) :: queue)
	if n > 0 then flagSwitcher tvar (n - 1) else pure ()

test1 : IO Bool
test1 = do
	tvar <- newTVar (the (List Flag) [])
	f1 <- forkIO $ flagSwitcher tvar 10
	f2 <- forkIO $ flagSwitcher tvar 10
	-- f3 <- forkIO $ flagSwitcher tvar 10
	-- f4 <- forkIO $ flagSwitcher tvar 10
	let r1 = await f1
	let r2 = await f2
	-- let r3 = await f3
	-- let r4 = await f4
	_ <- pure (r1, r2) --, r3, r4)
	final <- readTVar tvar
	_ <- printLn "Done with STM Test"
	_ <- printLn final
	pure True

export
allTests : List (IO Bool)
allTests = [test1]
