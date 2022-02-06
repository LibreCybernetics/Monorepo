package multiformats.multiaddr

import parser.GenericParser
import parser.string.Exact
import parser.string.NaturalParser

val DotParser : GenericParser<String, Unit> =
    Exact('.').map { }

val SlashParser : GenericParser<String, Unit> =
    Exact('/').map { }

val OctetParser : GenericParser<String, UByte> =
    NaturalParser.map { it.toUByte() }

val ShortParser : GenericParser<String, UShort> =
    NaturalParser.map { it.toUShort() }

@ExperimentalUnsignedTypes
val PortParser : GenericParser<String, ByteArray> =
    ShortParser.map { ubyteArrayOf(
        it.div(256u).toUByte(),
        it.rem(256u).toUByte()
    ).toByteArray() }

@ExperimentalUnsignedTypes
val IPv4Parser: GenericParser<String, UByteArray> =
    OctetParser.repExactly(DotParser, 4).map { it.toUByteArray() }