package coop.fugitiva.domain

import io.getquill.MappedEncoding

opaque type ProductSpecificationId = Int

object ProductSpecificationId:
  private val proofSubtype               = summon[ProductSpecificationId <:< Int]
  given <:<[ProductSpecificationId, Int] = proofSubtype

  given Conversion[Int, ProductSpecificationId] = identity
  given MappedEncoding[ProductSpecificationId, Int] = MappedEncoding[ProductSpecificationId, Int](identity)
  given MappedEncoding[Int, ProductSpecificationId] = MappedEncoding[Int, ProductSpecificationId](identity)

  def apply(id: Int): ProductSpecificationId = id
