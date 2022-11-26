package dev.librecybernetics

import scala.collection.immutable.ArraySeq
import java.rmi.server.UnicastRemoteObject

type MACAddress = EUI48

object EUI48 {
  enum Administred:
    case LocallyAdministred
    case UniversallyAdministred

  enum Cast:
    case Unicast
    case Multicast
}

case class EUI48(
    val address: ArraySeq[Octet]
) {
  require(address.length == 6)

  lazy val administredBit = address(0) & Octet(0x2)
  lazy val administred = () match {
    case _ if administredBit == Octet(0x0) =>
      EUI48.Administred.UniversallyAdministred
    case _ if administredBit == Octet(0x2) =>
      EUI48.Administred.LocallyAdministred
  }

  lazy val castBit = address(0) & Octet(0x1)
  lazy val cast = () match {
    case _ if castBit == Octet(0x0) => EUI48.Cast.Unicast
    case _ if castBit == Octet(0x1) => EUI48.Cast.Multicast
  }
}
