package coop.fugitiva.domain

import io.getquill.MappedEncoding

opaque type CooperativeId = Int

object CooperativeId:
  private val proofSubtype = summon[CooperativeId <:< Int]
  given <:<[CooperativeId, Int] = proofSubtype

  given Conversion[Int, CooperativeId] = identity
  given MappedEncoding[CooperativeId, Int] = MappedEncoding[CooperativeId, Int](identity)
  given MappedEncoding[Int, CooperativeId] = MappedEncoding[Int, CooperativeId](identity)

  def apply(id: Int): CooperativeId = id
