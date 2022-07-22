package parsers.common

import parsers.*
import parsers.StringParser.charMatch
import parsers.StringParser.charPred
import parsers.StringParser.end
import parsers.StringParser.rep

/**
 *
 * Related RFC: 1035
 */
object DomainName : GenericParser<String, List<String>> {
	val hyphen: GenericParser<String, Char> =
		charMatch('-')

	val letter: GenericParser<String, Char> =
		charPred { it.isLetter() }

	val letterDigit: GenericParser<String, Char> =
		charPred { it.isLetterOrDigit() }

	val letterDigitHyphen: GenericParser<String, Char> =
		charPred { it.isLetterOrDigit() || it == '-' }

	val letterDigitHyphenString: GenericParser<String, String> =
		(letterDigitHyphen lookahead (hyphen.rep() seq letterDigit))
			.rep(max = 61u)
			.map { it.toCharArray().concatToString() }

	val label: GenericParser<String, String> =
		((letter seq letterDigitHyphenString)
			.map { (l, ls) -> l + ls } seq letterDigit)
			.map { (ls, l) -> ls + l }

	val domainName: GenericParser<String, List<String>> =
		(label seq
				(charMatch('.') seq label).map { it.second }.rep(max = 127u)
				).map { (d, ds) -> listOf(d) + ds }

	override fun parse(input: String): ParserResult<String, List<String>> =
		(domainName seq end)
			.map { it.first }
			.parse(input)
}