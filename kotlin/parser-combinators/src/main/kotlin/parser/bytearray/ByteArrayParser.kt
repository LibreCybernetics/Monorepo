package parser.bytearray

import parser.*

interface ByteArrayParser<Output> : GenericParser<ByteArray, Output> {
    override fun parse(input: ByteArray): ParserResult<ByteArray, Output>
}

object Any : ByteArrayParser<Byte> {
    override fun parse(input: ByteArray): ParserResult<ByteArray, Byte> =
        if(input.isEmpty()) EndOfInputError() else ParserSuccess(input.first(), input.drop(1).toByteArray())
}

data class Exact(val expected: Byte) : ByteArrayParser<Byte> {
    override fun parse(input: ByteArray): ParserResult<ByteArray, Byte> =
        if(input.isEmpty()) EndOfInputError() else {
            if (input.first() == expected)
                ParserSuccess(input.first(), input.drop(1).toByteArray()) else
                MatchError(expected = byteArrayOf(expected), actual = byteArrayOf(input.first()))
        }
}

data class Cond(val cond: (Byte) -> Boolean) : ByteArrayParser<Byte> {
    override fun parse(input: ByteArray): ParserResult<ByteArray, Byte> =
        if(input.isEmpty()) EndOfInputError() else {
            if (cond(input.first()))
                ParserSuccess(input.first(), input.drop(1).toByteArray()) else
                CondError(actual = byteArrayOf(input.first()))
        }
}
