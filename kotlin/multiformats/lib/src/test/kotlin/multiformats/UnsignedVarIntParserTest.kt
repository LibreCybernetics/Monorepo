package multiformats

import parser.ParserSuccess
import kotlin.test.Test
import kotlin.test.assertEquals

class UnsignedVarIntParserTest {
    @ExperimentalUnsignedTypes
    @Test
    fun simpleExamples() {
        val result1: ParserSuccess<ByteArray, UnsignedVarInt> =
                UnsignedVarIntParser.parse(byteArrayOf(0)) as ParserSuccess
        assertEquals(UnsignedVarInt(0.toUShort()), result1.output)

        val result2: ParserSuccess<ByteArray, UnsignedVarInt> =
                UnsignedVarIntParser.parse(byteArrayOf(0, 0, 0, 0)) as ParserSuccess
        assertEquals(UnsignedVarInt(0.toUShort()), result2.output)
        val result3: ParserSuccess<ByteArray, UnsignedVarInt> =
                UnsignedVarIntParser.parse(ubyteArrayOf(200u, 10u, 0u).toByteArray()) as ParserSuccess
        assertEquals(UnsignedVarInt(1352.toUShort()), result3.output)
    }
}