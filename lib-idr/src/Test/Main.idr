module Test.Main

import Test.Data.BinarySearchTree as BST

main : IO ()
main = do
  bstRes <- sequence BST.allTests
  printLn bstRes
