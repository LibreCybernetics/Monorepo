module Test.Main

import Test.Control.Concurrency.STM as STM
import Test.Data.BinarySearchTree as BST

partial
main : IO ()
main = do
  bstRes <- sequence BST.allTests
  stmRes <- sequence STM.allTests
  printLn bstRes
