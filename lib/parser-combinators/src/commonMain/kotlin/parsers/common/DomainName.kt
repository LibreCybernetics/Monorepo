package parsers.common

import parsers.*
import util.types.NonEmptyString

/**
 *
 * Related RFC: 1035
 */
object DomainName : GenericParser<String, List<NonEmptyString>> {
	val hyphen: StringParser<Char> =
		charMatch('-')

	val letter: StringParser<Char> =
		charPred { it.isLetter() }

	val letterDigit: StringParser<Char> =
		charPred { it.isLetterOrDigit() }

	val letterDigitHyphen: StringParser<Char> =
		charPred { it.isLetterOrDigit() || it == '-' }

	val letterDigitHyphenString: StringParser<String> =
		(letterDigitHyphen lookahead (hyphen.rep() seq letterDigit))
			.rep(max = 61u)
			.map { it.toCharArray().concatToString() }

	val label: StringParser<NonEmptyString> =
		((letter seq letterDigitHyphenString)
			.map { (l, ls) -> l + ls } seq letterDigit)
			.map { (ls, l) -> ls + l }
			.map { NonEmptyString(it) }

	val domainName: StringParser<List<NonEmptyString>> =
		(label seq
				(charMatch('.') seq label).map { it.second }.rep(max = 127u)
				).map { (d, ds) -> listOf(d) + ds }

	override fun parse(input: String): ParserResult<String, List<NonEmptyString>> =
		(domainName seq end)
			.map { it.first }
			.parse(input)
}