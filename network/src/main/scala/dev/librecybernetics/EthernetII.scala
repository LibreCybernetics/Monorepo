package dev.librecybernetics

case class EthernetII[Payload](
    destination: MACAddress,
    source: MACAddress,
    ethertype: EtherType,
    payload: Payload,
    frameCheckSequence: Long
)
