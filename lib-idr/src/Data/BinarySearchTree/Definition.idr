module Data.BinarySearchTree.Definition

import Data.Maybe

||| An unbalanced binary search tree
|||
||| TODO: Refactor to a balanced binary search tree
public export
data BinarySearchTree
	: (degree : Nat)
	-> (a : Type)
	-> (treeOrd : Ord a)
	-> (minValue: Maybe a)
	-> (maxValue: Maybe a)
	-> Type where
		Nil
			: (treeOrd: Ord a)
			=> BinarySearchTree 0 a treeOrd Nothing Nothing
		Branch
			: (treeOrd: Ord a)
			=> {ldeg, rdeg : Nat}
			-> {lmin, lmax, rmin, rmax : Maybe a}
			-> (leftBranch: BinarySearchTree ldeg a treeOrd lmin lmax)
			-> (Value: a)
			-> {auto valueGTlmax: fromMaybe GT (map (compare @{treeOrd} Value) lmax) = GT }
			-> {auto valueLTrmin: fromMaybe LT (map (compare @{treeOrd} Value) rmin) = LT }
			-> (rightBranch: BinarySearchTree rdeg a treeOrd rmin rmax)
			-> BinarySearchTree (S (max ldeg rdeg)) a treeOrd (Just $ Maybe.fromMaybe Value lmin) (Just $ Maybe.fromMaybe Value rmax)

--
-- Type Property Getters
--

public export
getDegree : {degree: Nat} -> BinarySearchTree degree a treeOrd minValue maxValue -> Nat
getDegree _ = degree

testGetDegree : getDegree (Branch Nil "Hello" Nil) = 1
testGetDegree = Refl

public export
getMinValue : {minValue: Maybe a} -> BinarySearchTree degree a treeOrd minValue maxValue -> Maybe a
getMinValue _ = minValue

testGetMinValue : getMinValue (Branch Nil "Hello" Nil) = Just "Hello"
testGetMinValue = Refl

public export
getMaxValue : {maxValue: Maybe a} -> BinarySearchTree degree a treeOrd minValue maxValue -> Maybe a
getMaxValue _ = maxValue

testGetMaxValue : getMaxValue (Branch Nil "Hello" Nil) = Just "Hello"
testGetMaxValue = Refl

--
-- Simple Functions
--

public export
size : BinarySearchTree degree a treeOrd minValue maxValue -> Nat
size Nil = 0
size (Branch lt r rt) = S $ size lt + size rt
