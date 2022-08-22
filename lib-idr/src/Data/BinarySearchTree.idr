module Data.BinarySearchTree

import Data.Maybe
import Data.Nat

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
-- Getters
--

public export
flatten : BinarySearchTree degree a treeOrd minValue maxValue -> List a
flatten Nil = Nil
flatten (Branch lt r rt) = flatten lt ++ [r] ++ flatten rt

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
-- Elem Insertion
--

public export
insertNewDegree : (treeOrd : Ord a)
                => BinarySearchTree pdeg a treeOrd pmin pmax
                -> a
                -> Nat
insertNewDegree Nil _ = 1
insertNewDegree (Branch lt r rt) v =
  -- TODO: Incorrect implementation
  case compare r v @{treeOrd} of
    LT => S (max (getDegree lt) (getDegree rt))
    EQ => S (max (getDegree lt) (getDegree rt))
    GT => S (max (getDegree lt) (getDegree rt))

public export
insertNewMin : (treeOrd : Ord a)
             => {pmin: Maybe a}
             -> BinarySearchTree pdeg a treeOrd pmin pmax
             -> a
             -> a
insertNewMin t v = foldr (min @{treeOrd}) v (getMinValue t)

public export
insertNewMax : (treeOrd : Ord a)
             => {pmax: Maybe a}
             -> BinarySearchTree pdeg a treeOrd pmin pmax
             -> a
             -> a
insertNewMax t v = foldr (max @{treeOrd}) v (getMaxValue t)

hypothesis_compare : (treeOrd : Ord a) => (x, y : a) -> compare x y @{treeOrd} = EQ -> compare x = compare y
hypothesis_equal_min : (treeOrd : Ord a) => (x, y : a) -> compare x y @{treeOrd} = EQ -> min x y = y
hypothesis_equal_max : (treeOrd : Ord a) => (x, y : a) -> compare x y @{treeOrd} = EQ -> max x y = y

public export
insert : (treeOrd : Ord a)
       => (t : BinarySearchTree pdeg a treeOrd pmin pmax)
       -> (v : a)
       -> BinarySearchTree (insertNewDegree t v @{treeOrd}) a treeOrd (Just $ insertNewMin t v {treeOrd}) (Just $ insertNewMax t v {treeOrd})
insert Nil v = Branch Nil v Nil
insert (Branch lt r rt) v =
  case compare r v @{treeOrd} of
    LT =>
      rewrite the (compare r v @{treeOrd} = EQ) $ believe_me () in
      ?hole1
    EQ =>
      rewrite the (compare r v @{treeOrd} = EQ) $ believe_me () in
      ?hole2
    GT =>
      rewrite the (compare r v @{treeOrd} = EQ) $ believe_me () in
      ?hole3
