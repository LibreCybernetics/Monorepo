module Data.BinarySearchTree

import Data.Maybe
import Data.Nat

||| An unbalanced binary search tree
|||
||| TODO: Refactor to a balanced binary search tree
public export
data BinarySearchTree
  : (degree : Nat)
  -> (A: Type)
  -> (minValue: Maybe A)
  -> (maxValue: Maybe A)
  -> Type where
    Nil : BinarySearchTree 0 a Nothing Nothing
    Branch
      : Ord a
      => {ldeg, rdeg : Nat}
      -> {lmin, lmax, rmin, rmax : Maybe a}
      -> (leftBranch: BinarySearchTree ldeg a lmin lmax)
      -> (Value: a)
      -> (rightBranch: BinarySearchTree rdeg a rmin rmax)
      -> BinarySearchTree (S (max ldeg rdeg)) a (lmin <|> Just Value) (rmax <|> Just Value)

--
-- Type Property Getters
--

public export
getDegree : {degree: Nat} -> BinarySearchTree degree a minValue maxValue -> Nat
getDegree t {degree} = degree

testGetDegree : getDegree (Branch Nil "Hello" Nil) = 1
testGetDegree = Refl

public export
getMinValue : {minValue: Maybe a} -> BinarySearchTree degree a minValue maxValue -> Maybe a
getMinValue t {minValue} = minValue

testGetMinValue : getMinValue (Branch Nil "Hello" Nil) = Just "Hello"
testGetMinValue = Refl

public export
getMaxValue : {maxValue: Maybe a} -> BinarySearchTree degree a minValue maxValue -> Maybe a
getMaxValue t {maxValue} = maxValue

testGetMaxValue : getMaxValue (Branch Nil "Hello" Nil) = Just "Hello"
testGetMaxValue = Refl

--
-- Elem Insertion
--

public export
insertNewDegree : Ord a => BinarySearchTree pdeg a pmin pmax -> a -> Nat
insertNewDegree Nil _ = 1
insertNewDegree (Branch lt r rt) v = max (getDegree lt) (getDegree rt)

public export
insertNewMin : Ord a => {pmin: Maybe a} -> BinarySearchTree pdeg a pmin pmax -> a -> a
insertNewMin t v = foldr min v (getMinValue t)

public export
insertNewMax : Ord a => {pmax: Maybe a} -> BinarySearchTree pdeg a pmin pmax -> a -> a
insertNewMax t v = foldr max v (getMaxValue t)

public export
insert : Ord a
       => (t : BinarySearchTree pdeg a pmin pmax)
       -> (v : a)
       -> BinarySearchTree (insertNewDegree t v) a (Just $ insertNewMin t v) (Just $ insertNewMax t v)
insert Nil v = Branch Nil v Nil
insert (Branch lt r rt) v = ?hole
