package parsers.common

import parsers.*
import types.NonEmptyString

/**
 *
 * Related RFC: 1035
 */
object DomainName : StringParser<List<NonEmptyString>> {
	val hyphen: StringParser<Char> =
		charMatch('-')

	val letterDigitHyphen: StringParser<Char> =
		charPred { it.isLetterOrDigit() || it == '-' }

	val letterDigitHyphenString: StringParser<String> =
		(letterDigitHyphen lookahead (hyphen.rep() seq letterOrDigit))
			.rep(max = 61u)
			.map { it.toCharArray().concatToString() }

	val label: StringParser<NonEmptyString> =
		((letter seq letterDigitHyphenString)
			.map { (l, ls) -> l + ls } seq letterOrDigit)
			.map { (ls, l) -> ls + l }
			.map { NonEmptyString(it) }

	val domainName: StringParser<List<NonEmptyString>> =
		(label seq
				(charMatch('.') seqRight label).rep(max = 127u)
				).map { (d, ds) -> listOf(d) + ds }

	override fun parse(input: String, column: Column, row: Row): ParserResult<String, List<NonEmptyString>> =
		(domainName seqLeft end).parse(input, column, row)
}
