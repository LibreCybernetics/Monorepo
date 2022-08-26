module Test.Data.BinarySearchTree

import Data.BinarySearchTree

export
testInsert1 : IO Bool
testInsert1 = do
  pure $ insert (Branch Nil "Hello" Nil) "Hello" `treeEq` Branch Nil "Hello" Nil

export
testInsert2 : IO Bool
testInsert2 = do
  pure $ insert (Branch Nil "Hello" Nil) "World" `treeEq` Branch Nil "Hello" (Branch Nil "World" Nil)

export
testInsert3 : IO Bool
testInsert3 = do
  pure $ insert (Branch Nil "Hello" Nil) "Ahoy" `treeEq` Branch (Branch Nil "Ahoy" Nil) "Hello" Nil

export
allTests : List (IO Bool)
allTests = [testInsert1, testInsert2, testInsert3]
