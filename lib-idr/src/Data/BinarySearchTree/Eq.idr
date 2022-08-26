module Data.BinarySearchTree.Eq

import public Data.Maybe

import public Data.BinarySearchTree.Definition
import public Data.BinarySearchTree.ListInterop

public export
treeEq
	: (ord : Ord a)
	=> (BinarySearchTree degL a ord minValueL maxValueL)
	-> (BinarySearchTree degR a ord minValueR maxValueR)
	-> Bool
treeEq lt rt = (flatten lt == flatten rt)
