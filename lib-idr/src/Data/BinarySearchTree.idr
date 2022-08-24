module Data.BinarySearchTree

import Builtin
import Prelude.EqOrd

import Data.Maybe
import Data.Nat

import public Data.BinarySearchTree.Definition

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
-- Extra Type Properties
--

public export
data HasElem : (BinarySearchTree degree a treeOrd minValue maxValue) -> a -> Type where
  Here : (treeOrd : Ord a)
       => (lt : BinarySearchTree degl a treeOrd lmin lmax)
       -> (v : a)
       -> (rt : BinarySearchTree degr a treeOrd rmin rmax)
       -> {auto valueGTlmax : fromMaybe GT (map (compare v @{treeOrd}) (getMaxValue lt)) = GT}
       -> {auto valueLTrmin : fromMaybe LT (map (compare v @{treeOrd}) (getMinValue rt)) = LT}
       -> (Branch lt v rt) `HasElem` v
  ThereLeft : (treeOrd : Ord a)
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

testHasElem1 : (Branch Nil "Hello" Nil) `HasElem` "Hello"
testHasElem1 = Here Nil "Hello" Nil

testHasElem2 : (Branch (Branch Nil "Hello" Nil) "World" Nil) `HasElem` "Hello"
testHasElem2 = ThereLeft (Branch Nil "Hello" Nil) testHasElem1 "World" Nil

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
