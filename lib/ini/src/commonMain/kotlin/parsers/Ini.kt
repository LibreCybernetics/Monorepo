package parsers

import types.NonEmptyString

data class Section(
	val position: Pair<Row, Column>,
	val name: List<NonEmptyString>,
	val values: Map<NonEmptyString, Pair<Pair<Row, Column>, String>>
	)

object Ini {
	val sectionHeader: StringParser<Pair<Pair<Row, Column>, List<NonEmptyString>>> =
		position<String>() seq (
				charMatch('[') seqRight
				letters.rep(min=1u) seqLeft
				charMatch(']') seqLeft
				newline)

	val keyValue: StringParser<Pair<Pair<Row, Column>, Pair<NonEmptyString,String>>> =
		position<String>() seq (
				letters seqLeft charMatch('=') seq
				(negativeLookahead(newline) seqRight anyChar)
					.rep().map { it.toCharArray().concatToString() }
				)

	val section: StringParser<Section> =
		(sectionHeader seq keyValue.rep()).map { (header, keyValues) ->
			Section(
				header.first,
				header.second,
				keyValues.map{ Pair(it.second.first, Pair(it.first, it.second.second)) }.toMap())
		}
}