module Data.BinarySearchTree

import Builtin
import Prelude.EqOrd

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
size : BinarySearchTree degree a treeOrd minValue maxValue -> Nat
size Nil = 0
size (Branch lt r rt) = S $ size lt + size rt

public export
maybeGetElem : (treeOrd : Ord a) => BinarySearchTree degree a treeOrd minValue maxValue -> a -> Maybe a
maybeGetElem Nil _ = Nothing
maybeGetElem (Branch lt r rt) v =
  case compare r v of
    LT => maybeGetElem rt v
    EQ => Just r
    GT => maybeGetElem lt v

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
    LT => S (max (getDegree lt) (insertNewDegree rt v))
    EQ => S (max (getDegree lt) (getDegree rt))
    GT => S (max (insertNewDegree lt v) (getDegree rt))

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

--
-- TODO: Investigate why REPL can reduce the following but the compiler can't
--

testInsert2: insert (Branch Nil "Hello" Nil) "Hello" = Branch Nil "Hello" Nil
testInsert2 = ?hole2

testInsert3 : insert (Branch Nil "Hello" Nil) "World" = Branch Nil "Hello" (Branch Nil "World" Nil)
testInsert3 = ?hole3

testInsert4 : insert (Branch Nil "Hello" Nil) "Ahoy" = Branch (Branch Nil "Ahoy" Nil) "Hello" Nil
testInsert4 = ?hole4
