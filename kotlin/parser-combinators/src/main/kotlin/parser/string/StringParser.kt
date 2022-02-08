package parser.string

import parser.*

typealias StringParser<Output> = GenericParser<String, Output>

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