module Data.BinarySearchTree

import Data.Maybe

public export
data BinarySearchTree
  : (degree : Nat)
  -> (minimum: Maybe a)
  -> (maximum: Maybe a)
  -> a
  -> Type where
    Nil : BinarySearchTree 0 Nothing Nothing a
    Branch
      : (leftBranch: BinarySearchTree ldeg lmin lmax a)
      -> (value: a)
      -> (rightBranch: BinarySearchTree rdeg rmin rmax a)
      -> BinarySearchTree (S (max ldeg  rdeg)) lmin rmax a

public export
Functor (BinarySearchTree tdeg tmin tmax) where
  map _ Nil = Nil
  map f (Branch l v r) = Branch (map f l) (f v) (map f r)
