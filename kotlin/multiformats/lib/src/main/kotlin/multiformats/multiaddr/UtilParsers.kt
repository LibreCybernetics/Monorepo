package multiformats.multiaddr

import parser.string.Exact
import parser.string.NaturalParser
import parser.string.StringParser

val DotParser : StringParser<Unit> =
    Exact('.').map { }

val SlashParser : StringParser<Unit> =
    Exact('/').map { }

val OctetParser : StringParser<UByte> =
    NaturalParser.map { it.toUByte() }

val ShortParser : StringParser<UShort> =
    NaturalParser.map { it.toUShort() }

@ExperimentalUnsignedTypes
val PortParser : StringParser<ByteArray> =
    ShortParser.map { ubyteArrayOf(
        it.div(256u).toUByte(),
        it.rem(256u).toUByte()
    ).toByteArray() }

@ExperimentalUnsignedTypes
val IPv4Parser: StringParser<UByteArray> =
    OctetParser.repExactly(DotParser, 4).map { it.toUByteArray() }