package parser.bytearray

import parser.*

interface ByteArrayParser<Output> : GenericParser<ByteArray, Output> {
    override fun <R> map(f: (Output) -> R): ByteArrayParser<R> =
        super.map(f) as ByteArrayParser<R>
    override fun <R> flatMap(f: (Output) -> GenericParser<ByteArray, R>): ByteArrayParser<R> =
        super.flatMap(f) as ByteArrayParser<R>

    override fun rep(): ByteArrayParser<List<Output>> =
        super.rep() as ByteArrayParser<List<Output>>
    override fun repExactly(n: UInt): ByteArrayParser<List<Output>> =
        super.repExactly(n) as ByteArrayParser<List<Output>>

    override fun <Output2> seq(second: GenericParser<ByteArray, Output2>): ByteArrayParser<Pair<Output, Output2>> =
        super.seq(second) as ByteArrayParser<Pair<Output, Output2>>
}

object Any : ByteArrayParser<Byte> {
    override fun parse(input: ByteArray): ParserResult<ByteArray, Byte> =
        if(input.isEmpty()) EndOfInputError()
        else ParserSuccess(input.first(), input.drop(1).toByteArray())
}

data class Exact(val expected: Byte) : ByteArrayParser<Byte> {
    override fun parse(input: ByteArray): ParserResult<ByteArray, Byte> =
        Any.parse(input).flatMap {
            if (it == expected)
                ParserSuccess(it, input.drop(1).toByteArray())
            else
                MatchError(expected = byteArrayOf(expected), actual = byteArrayOf(input.first()))
        }
}

data class Cond(val cond: (Byte) -> Boolean) : ByteArrayParser<Byte> {
    override fun parse(input: ByteArray): ParserResult<ByteArray, Byte> =
        Any.parse(input).flatMap {
            if (cond(it))
                ParserSuccess(it, input.drop(1).toByteArray())
            else
                CondError(actual = byteArrayOf(input.first()))
        }
}
