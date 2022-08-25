module Data.BinarySearchTree.Search

import Data.Maybe

import public Data.BinarySearchTree.Definition

public export
data HasElem : (BinarySearchTree degree a treeOrd minValue maxValue) -> a -> Type where
	Here
		: (treeOrd : Ord a)
		=> (lt : BinarySearchTree degl a treeOrd lmin lmax)
		-> (v : a)
		-> (rt : BinarySearchTree degr a treeOrd rmin rmax)
		-> {auto valueGTlmax : fromMaybe GT (map (compare v @{treeOrd}) (getMaxValue lt)) = GT}
		-> {auto valueLTrmin : fromMaybe LT (map (compare v @{treeOrd}) (getMinValue rt)) = LT}
		-> (Branch lt v rt) `HasElem` v
	ThereLeft
		: (treeOrd : Ord a)
		=> (lt : BinarySearchTree degl a treeOrd lmin lmax)
		-> (hasElem : lt `HasElem` v)
		-> (r : a)
		-> (rt : BinarySearchTree degr a treeOrd rmin rmax)
		-> {auto valueGTlmax : fromMaybe GT (map (compare r @{treeOrd}) (getMaxValue lt)) = GT}
		-> {auto valueLTrmin : fromMaybe LT (map (compare r @{treeOrd}) (getMinValue rt)) = LT}
		-> (Branch lt r rt) `HasElem` v
	ThereRight : (treeOrd : Ord a)
		=> (lt : BinarySearchTree degl a treeOrd lmin lmax)
		-> (r : a)
		-> (rt : BinarySearchTree degr a treeOrd rmin rmax)
		-> (hasElem : rt `HasElem` v)
		-> {auto valueGTlmax : fromMaybe GT (map (compare r @{treeOrd}) (getMaxValue lt)) = GT}
		-> {auto valueLTrmin : fromMaybe LT (map (compare r @{treeOrd}) (getMinValue rt)) = LT}
		-> (Branch lt r rt) `HasElem` v

(treeOrd : Ord a) => Uninhabited (BinarySearchTree.Definition.Nil @{treeOrd} `HasElem` x) where
	uninhabited Here		impossible
	uninhabited ThereLeft	impossible
	uninhabited ThereRight	impossible

testHasElem1 : (Branch Nil "Hello" Nil) `HasElem` "Hello"
testHasElem1 = Here Nil "Hello" Nil

testHasElem2 : (Branch (Branch Nil "Hello" Nil) "World" Nil) `HasElem` "Hello"
testHasElem2 = ThereLeft (Branch Nil "Hello" Nil) testHasElem1 "World" Nil

hasElem
	: (t : BinarySearchTree degree a treeOrd minValue maxValue)
	-> (x : a)
	-> Dec (t `HasElem` x)

public export
maybeGetElem : (treeOrd : Ord a) => BinarySearchTree degree a treeOrd minValue maxValue -> a -> Maybe a
maybeGetElem Nil _ = Nothing
maybeGetElem (Branch lt r rt) v =
	case compare r v of
		LT => maybeGetElem rt v
		EQ => Just r
		GT => maybeGetElem lt v
