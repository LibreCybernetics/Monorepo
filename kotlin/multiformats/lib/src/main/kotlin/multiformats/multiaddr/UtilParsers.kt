package multiformats.multiaddr

import parser.GenericParser
import parser.string.Exact
import parser.string.NaturalParser

val DotParser : GenericParser<String, Unit> =
    Exact('.').map { }

val OctetParser : GenericParser<String, UByte> =
    NaturalParser.map { it.toUByte() }

val IPv4Parser: GenericParser<String, List<UByte>> =
    OctetParser.repExactly(DotParser, 4)