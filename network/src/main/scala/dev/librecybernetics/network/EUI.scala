package dev.librecybernetics.network

import scala.collection.immutable.ArraySeq
import java.rmi.server.UnicastRemoteObject

import dev.librecybernetics.types.UnsignedByte.toUnsignedByte

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

  lazy val administredBit = address(0) and Octet(0x2.toByte.toUnsignedByte)
  lazy val administred = () match {
    case _ if administredBit == Octet(0x0.toByte.toUnsignedByte) =>
      EUI.Administred.UniversallyAdministred
    case _ if administredBit == Octet(0x2.toByte.toUnsignedByte) =>
      EUI.Administred.LocallyAdministred
  }

  lazy val castBit = address(0) and Octet(0x1.toByte.toUnsignedByte)
  lazy val cast = () match {
    case _ if castBit == Octet(0x0.toByte.toUnsignedByte) => EUI.Cast.Unicast
    case _ if castBit == Octet(0x1.toByte.toUnsignedByte) => EUI.Cast.Multicast
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
