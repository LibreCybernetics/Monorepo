package parser.string

import parser.*

interface StringParser<Output> : GenericParser<String, Output> {
    override fun <R> map(f: (Output) -> R): StringParser<R> =
            super.map(f) as StringParser<R>
    override fun <R> flatMap(f: (Output) -> GenericParser<String, R>): StringParser<R> =
            super.flatMap(f) as StringParser<R>

    override fun rep(): StringParser<List<Output>> =
            super.rep() as StringParser<List<Output>>
    override fun repExactly(n: UInt): StringParser<List<Output>> =
            super.repExactly(n) as StringParser<List<Output>>

    override fun <Output2> seq(second: GenericParser<String, Output2>): StringParser<Pair<Output, Output2>> =
            super.seq(second) as StringParser<Pair<Output, Output2>>
}

object Any : StringParser<Char> {
    override fun parse(input: String): ParserResult<String, Char> =
        if(input.isEmpty()) EndOfInputError()
        else ParserSuccess(input.first(), input.drop(1))
}

data class Exact(val expected: Char) : StringParser<Char> {
    override fun parse(input: String): ParserResult<String, Char> =
        Any.parse(input).flatMap {
            if (it == expected) ParserSuccess(it, input.drop(1))
            else MatchError(expected.toString(), input.take(1))
        }
}

data class Cond(val cond: (Char) -> Boolean) : StringParser<Char> {
    override fun parse(input: String): ParserResult<String, Char> =
        Any.parse(input).flatMap {
            if(cond(it)) ParserSuccess(it, input.drop(1))
            else CondError(actual = it.toString())
        }
}