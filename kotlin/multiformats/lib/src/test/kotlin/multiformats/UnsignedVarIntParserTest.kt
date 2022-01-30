package multiformats

import parser.ParserSuccess
import kotlin.test.Test
import kotlin.test.assertEquals

class UnsignedVarIntParserTest {
    @Test
    fun simpleExamples() {
        val result1: ParserSuccess<ByteArray, UnsignedVarInt> =
                UnsignedVarIntParser.parse(byteArrayOf(0)) as ParserSuccess
        assertEquals(UnsignedVarInt(0.toUShort()), result1.output)

        val result2: ParserSuccess<ByteArray, UnsignedVarInt> =
                UnsignedVarIntParser.parse(byteArrayOf(0, 0, 0, 0)) as ParserSuccess
        assertEquals(UnsignedVarInt(0.toUShort()), result2.output)
    }
}