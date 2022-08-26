module Data.BinarySearchTree.Insert

import Data.Maybe

import public Data.BinarySearchTree.Definition

public export
insertNewDegree
	: (treeOrd : Ord a)
	=> BinarySearchTree pdeg a treeOrd pmin pmax
	-> a
	-> Nat
insertNewDegree Nil _ = 1
insertNewDegree (Branch lt r rt) v =
	case compare r v @{treeOrd} of
		LT => S (max (getDegree lt) (insertNewDegree rt v))
		EQ => S (max (getDegree lt) (getDegree rt))
		GT => S (max (insertNewDegree lt v) (getDegree rt))

public export
insertNewMin
	: (treeOrd : Ord a)
	=> {pmin: Maybe a}
	-> BinarySearchTree pdeg a treeOrd pmin pmax
	-> a
	-> a
insertNewMin t v = foldr (min @{treeOrd}) v (getMinValue t)

public export
insertNewMax
	: (treeOrd : Ord a)
	=> {pmax: Maybe a}
	-> BinarySearchTree pdeg a treeOrd pmin pmax
	-> a
	-> a
insertNewMax t v = foldr (max @{treeOrd}) v (getMaxValue t)

hypothesis_compare : (treeOrd : Ord a) => (x, y : a) -> compare x y @{treeOrd} = EQ -> compare x = compare y
hypothesis_equal_min : (treeOrd : Ord a) => (x, y : a) -> compare x y @{treeOrd} = EQ -> min x y = y
hypothesis_equal_max : (treeOrd : Ord a) => (x, y : a) -> compare x y @{treeOrd} = EQ -> max x y = y

public export
insert
	: (treeOrd : Ord a)
	=> (t : BinarySearchTree pdeg a treeOrd pmin pmax)
	-> (v : a)
	-> BinarySearchTree (insertNewDegree t v @{treeOrd}) a treeOrd (Just $ insertNewMin t v {treeOrd}) (Just $ insertNewMax t v {treeOrd})
insert Nil v = Branch Nil v Nil
insert (Branch lt r rt) v =
	case compare r v @{treeOrd} of
		LT =>
			rewrite the (compare r v @{treeOrd} = LT) $ believe_me () in
			rewrite the (min (fromMaybe r (getMinValue lt)) v = fromMaybe r (getMinValue lt)) $ believe_me () in
			rewrite the (max (fromMaybe r (getMaxValue rt)) v = foldr max v (getMaxValue rt)) $ believe_me () in
			Branch lt r (insert rt v) {valueLTrmin = believe_me ()} @{treeOrd}
		EQ =>
			rewrite the (compare r v @{treeOrd} = EQ) $ believe_me () in
			rewrite the (min (fromMaybe r (getMinValue lt)) v = fromMaybe v (getMinValue lt)) $ believe_me () in
			rewrite the (max (fromMaybe r (getMaxValue rt)) v = fromMaybe v (getMaxValue rt)) $ believe_me () in
			Branch lt v rt {valueLTrmin = believe_me ()} {valueGTlmax = believe_me ()}
		GT =>
			rewrite the (compare r v @{treeOrd} = GT) $ believe_me () in
			rewrite the (max (fromMaybe r (getMaxValue rt)) v = fromMaybe r (getMaxValue rt)) $ believe_me () in
			rewrite the (min (fromMaybe r (getMinValue lt)) v = foldr min v (getMinValue lt)) $ believe_me () in
			Branch (insert lt v) r rt @{treeOrd} {valueGTlmax = believe_me ()}

testInsert1 : insert Nil "Hello" = Branch Nil "Hello" Nil
testInsert1 = Refl
