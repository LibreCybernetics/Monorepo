package dev.librecybernetics

object EthernetII {
  trait Payload {
    val etherType: EtherType
    val payload: Array[Byte]
  }
}

case class EthernetII[Payload <: EthernetII.Payload](
    destination: MACAddress,
    source: MACAddress,
    payload: Payload,
    frameCheckSequence: Long
)
