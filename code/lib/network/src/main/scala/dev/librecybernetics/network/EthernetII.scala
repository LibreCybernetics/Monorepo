package dev.librecybernetics.network

object EthernetII {
  trait Payload {
    val etherType: EtherType
    val payload: Array[Byte]
  }
}

case class EthernetII[Payload <: EthernetII.Payload](
    destination: EUI48,
    source: EUI48,
    payload: Payload,
    frameCheckSequence: Long
)
