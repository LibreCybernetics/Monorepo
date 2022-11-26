package dev.librecybernetics

opaque type Octet = Short

// TODO: Change from Short(Signed 16-bit) to Byte(Signed 8-bit)
object Octet {
  def apply(b: Short): Octet = {
    require(b >= 0 && b < 256)
    b
  }

  private def baseConversion(b: Int): Char = b match {
    case _ if b < 10 => (b + 48).toChar
    case _ if b < 16 => (b + 55).toChar
  }

  private def baseConversion(c: Char): Int = c match {
    case _ if c >= 48 && c < 59 => c.toShort - 48
    case _ if c >= 65 && c < 71 => c.toShort - 55
  }

  def fromHexString(s: String): Octet = {
    require(s.length == 2)
    require(s.forall(c => (c >= 48 && c < 59) || (c >= 65 && c < 71)))

    s.map(baseConversion) match {
      case Seq(a, b) => (a * 16 + b).toShort
    }
  }

  extension (o: Octet) {
    def &(os: Octet): Octet = (o & os).toShort

    def toHexString: String =
      Seq(o / 16, o % 16).map(baseConversion) match {
        case Seq(a, b) => s"$a$b"
      }
  }
}
