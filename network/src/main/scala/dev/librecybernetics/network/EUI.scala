package dev.librecybernetics.network

import scala.collection.immutable.ArraySeq
import java.rmi.server.UnicastRemoteObject

object EUI {
  enum Administred:
    case LocallyAdministred
    case UniversallyAdministred

  enum Cast:
    case Unicast
    case Multicast
}

abstract class EUI {
  val address: ArraySeq[Octet]
  val addressLength: Int
  val organizationIdLength: Int

  lazy val administredBit = address(0) & Octet(0x2)
  lazy val administred = () match {
    case _ if administredBit == Octet(0x0) =>
      EUI.Administred.UniversallyAdministred
    case _ if administredBit == Octet(0x2) =>
      EUI.Administred.LocallyAdministred
  }

  lazy val castBit = address(0) & Octet(0x1)
  lazy val cast = () match {
    case _ if castBit == Octet(0x0) => EUI.Cast.Unicast
    case _ if castBit == Octet(0x1) => EUI.Cast.Multicast
  }
}

object EUI48 {
  def apply(address: List[Octet]): EUI48 = EUI48(ArraySeq.from(address))
}

case class EUI48(override val address: ArraySeq[Octet]) extends EUI {
  override val addressLength: Int = 6
  override val organizationIdLength: Int = 3

  require(address.length == addressLength)
}

object EUI64 {
  def apply(address: List[Octet]): EUI64 = EUI64(ArraySeq.from(address))
}

case class EUI64(override val address: ArraySeq[Octet]) extends EUI {
  override val addressLength: Int = 8
  override val organizationIdLength: Int = 4

  require(address.length == addressLength)
}
