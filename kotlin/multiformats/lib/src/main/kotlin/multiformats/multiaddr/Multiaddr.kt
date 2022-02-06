package multiformats.multiaddr

import parser.GenericParser
import parser.ParserResult
import parser.string.AlphaNumericWordParser
import multiformats.Multicodec.Companion.Multiaddr as MCMultiaddr

sealed interface MultiaddrPayload
data class MAByteArray(val data: ByteArray) : MultiaddrPayload
data class MAShort(val data: Short) : MultiaddrPayload

@ExperimentalUnsignedTypes
val MultiaddrParser: GenericParser<String, Multiaddr> =
    (SlashParser seq (AlphaNumericWordParser.flatMap { type ->
        when(type) {
            "ip4" -> IPv4Parser.map { type to it.toByteArray() }
            "tcp" -> PortParser.map { type to it }
            "udp" -> PortParser.map { type to it }
            else -> TODO()
        }
    })).map { it.second }.map {
        Multiaddr(
            MCMultiaddr.get(it.first),
            it.second
        )
    }

data class Multiaddr(
    val type: MCMultiaddr,
    val data: ByteArray
) {
    companion object {
        @ExperimentalUnsignedTypes
        fun parse(str: String): ParserResult<String, List<Multiaddr>> =
            MultiaddrParser.rep(min = 1, null).parse(str)
    }
}
