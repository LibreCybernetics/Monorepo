package parsers.common

import parsers.*
import types.NotEmptyString

/**
 *
 * Related RFC: 1035
 */
object DomainName : StringParser<List<NotEmptyString>> {
	val hyphen: StringParser<Char> =
		charMatch('-')

	val letterDigitHyphen: StringParser<Char> =
		charPred { it.isLetterOrDigit() || it == '-' }

	val letterDigitHyphenString: StringParser<String> =
		(letterDigitHyphen lookahead (hyphen.rep() seq letterOrDigit))
			.rep(max = 61u)
			.map { it.toCharArray().concatToString() }

	val label: StringParser<NotEmptyString> =
		((letter seq letterDigitHyphenString)
			.map { (l, ls) -> l + ls } seq letterOrDigit)
			.map { (ls, l) -> ls + l }
			.map { NotEmptyString(it) }

	val domainName: StringParser<List<NotEmptyString>> =
		(label seq
				(charMatch('.') seqRight label).rep(max = 127u)
				).map { (d, ds) -> listOf(d) + ds }

	override fun parse(input: String, column: Column, row: Row): ParserResult<String, List<NotEmptyString>> =
		(domainName seqLeft end).parse(input, column, row)
}
