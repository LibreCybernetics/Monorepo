module Data.BinarySearchTree.ListInterop

import Data.Maybe

import public Data.BinarySearchTree.Definition
import public Data.BinarySearchTree.Insert

public export
flatten : BinarySearchTree degree a treeOrd minValue maxValue -> List a
flatten Nil = Nil
flatten (Branch lt r rt) = flatten lt ++ [r] ++ flatten rt

public export
raiseNewDegree : (treeOrd : Ord a) => List a -> Nat
raiseNewDegree Nil = 0
raiseNewDegree (_::xs) = ?holeDeg

public export
raiseNewMinValue : (treeOrd : Ord a) => List a -> Maybe a
raiseNewMinValue Nil = Nothing
raiseNewMinValue (_::xs) = ?holeMin

public export
raiseNewMaxValue : (treeOrd : Ord a) => List a -> Maybe a
raiseNewMaxValue Nil = Nothing
raiseNewMaxValue (_::xs) = ?holeMax

public export
raise : (treeOrd : Ord a) => (l : List a) -> BinarySearchTree (raiseNewDegree l) a treeOrd (raiseNewMinValue l) (raiseNewMaxValue l)
raise Nil   = Nil
raise (x::xs) = ?holeRaise
