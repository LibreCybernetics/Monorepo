package parsers

import util.types.NonEmptyString

data class Section(val name: List<NonEmptyString>, val values: Map<NonEmptyString, String>)

object Ini {
	val sectionHeader: StringParser<List<NonEmptyString>> =
		charMatch('[') seqRight
				(letters.rep(min=1u)) seqLeft
				charMatch(']') seqLeft
				newline

	val keyValue: StringParser<Pair<NonEmptyString,String>> =
		letters seqLeft charMatch('=') seq
				(negativeLookahead(newline) seqRight anyChar).rep().map { it.toCharArray().concatToString() }

	val section: StringParser<Section> =
		(sectionHeader seq keyValue.rep()).map { (header, keyValues) ->
			Section(header, keyValues.toMap())
		}
}