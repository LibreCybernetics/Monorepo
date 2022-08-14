package parsers

import types.NonEmptyString

typealias StringParser<Output> = GenericParser<String, Output>

fun charPred(p: (Char) -> Boolean): StringParser<Char> = object : StringParser<Char> {
	override fun parse(input: String, column: Column, row: Row): ParserResult<String, Char> =
		if (input.isEmpty()) EndOfInputError(column, row)
		else if (p(input.first())) {
			val first = input.first()
			val newColumn = if(first == '\n') Column(1u) else column + Column(1u)
			val newRow = if(first == '\n') row + Row(1u) else row
			ParserSuccess(input.first(), input.drop(1), newColumn, newRow)
		}
		else CondError(input.take(1), column, row)
}

val anyChar: StringParser<Char> =
	charPred { true }

fun charMatch(expected: Char): StringParser<Char> =
	charPred { it == expected }

val end: StringParser<Unit> = object : StringParser<Unit> {
	override fun parse(input: String, column: Column, row: Row): ParserResult<String, Unit> =
		if (input.isEmpty()) ParserSuccess(Unit, input, column, row)
		else CondError(input, column, row)
}

fun <Output> StringParser<Output>.rep(
	min: UInt? = null, max: UInt? = null
): GenericParser<String, List<Output>> =
	this.rep(unit, min, max)

fun stringMatch(expected: String): StringParser<String> = object : StringParser<String> {
	override fun parse(input: String, column: Column, row: Row): ParserResult<String, String> =
		if (input.startsWith(expected)) ParserSuccess(expected, input.drop(expected.length), TODO(), TODO())
		else if (input.isEmpty()) EndOfInputError(TODO(), TODO())
		else CondError(input.take(expected.length), TODO(), TODO())
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

val lettersOrDigits: StringParser<NonEmptyString> =
	letterOrDigit.rep(min = 1u).map { NonEmptyString(it.toCharArray().concatToString()) }