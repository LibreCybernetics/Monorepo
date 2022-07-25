package parsers

import util.types.NonEmptyString

typealias StringParser<Output> = GenericParser<String, Output>

val anyChar: StringParser<Char> = GenericParser {
	if (it.isEmpty()) EndOfInputError() else ParserSuccess(it.first(), it.drop(1))
}

val end: StringParser<Unit> = GenericParser {
	if (it.isEmpty()) ParserSuccess(Unit, it) else CondError(it)
}

fun charMatch(expected: Char): StringParser<Char> = GenericParser {
	when (val actual = anyChar.parse(it)) {
		is ParserError -> actual
		is ParserSuccess ->
			if (actual.output == expected) ParserSuccess(expected, actual.remaining)
			else MatchError(expected.toString(), actual.output.toString())
	}
}

fun charPred(p: (Char) -> Boolean): StringParser<Char> = GenericParser {
	if (it.isEmpty()) EndOfInputError()
	else if (p(it.first())) ParserSuccess(it.first(), it.drop(1))
	else CondError(it.take(1))
}

fun <Output> StringParser<Output>.rep(
	min: UInt? = null, max: UInt? = null
): GenericParser<String, List<Output>> =
	this.rep(unit, min, max)

fun stringMatch(expected: String): StringParser<String> = GenericParser {
	if (it.startsWith(expected)) ParserSuccess(expected, it.drop(expected.length))
	else if (it.isEmpty()) EndOfInputError()
	else MatchError(expected, it.take(expected.length))
}

fun takeWhile(p: (Char) -> Boolean): StringParser<String> =
	charPred(p).rep().map { it.toCharArray().concatToString() }

val space: StringParser<Char> =
	charMatch(' ')

val newline: StringParser<Char> =
	charMatch('\n')

val letter: StringParser<Char> =
	charPred { it.isLetter() }

val letters: StringParser<NonEmptyString> =
	letter.rep(min = 1u).map { NonEmptyString(it.toCharArray().concatToString()) }

val digit: StringParser<Char> =
	charPred { it.isDigit() }

val letterOrDigit: StringParser<Char> =
	charPred { it.isLetterOrDigit() }
