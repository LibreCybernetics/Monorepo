package ini

import parsers.*
import parsers.string.*
import types.NotEmptyString

data class Section(
	val position: Pair<Row, Column>,
	val name: List<NotEmptyString>,
	val values: Map<NotEmptyString, Pair<Pair<Row, Column>, String>>
)

object Ini : StringParser<List<Section>> {
	val commentStart: StringParser<Char> =
		charMatch(';')

	val comment: StringParser<Pair<Pair<Row, Column>, String>> =
		(position<String>() seq (commentStart seqRight
				(negativeLookahead(newline) seqRight anyChar)
					.rep().map { it.toCharArray().concatToString() }))

	val sectionHeader: StringParser<Pair<Pair<Row, Column>, List<NotEmptyString>>> =
		(position<String>() seq (
				charMatch('[') seqRight
						lettersOrDigits.rep(min = 1u) seqLeft
						charMatch(']') seqLeft
						(space.rep() seq comment.optional()) seqLeft newline))

	val keyValue: StringParser<Pair<Pair<Row, Column>, Pair<NotEmptyString, String>>> =
		position<String>() seq (
				(lettersOrDigits) seqLeft
						(space.rep() seq charMatch('=') seq space.rep()) seq
						(negativeLookahead(newline.or(commentStart)) seqRight anyChar)
							.rep().map { it.toCharArray().concatToString() }
						seqLeft comment.optional() seqLeft newline
				)

	val section: StringParser<Section> =
		(sectionHeader seq
				((comment seqLeft newline).rep() seqRight keyValue).rep()
				).map { (header, keyValues) ->
				Section(
					header.first,
					header.second,
					keyValues.map { Pair(it.second.first, Pair(it.first, it.second.second)) }.toMap()
				)
			}

	override fun parse(input: String, column: Column, row: Row): ParserResult<String, List<Section>> =
		(
				(comment seqLeft newline).unit().or(
					(space.rep() seq newline).unit()
				).rep() seqRight
						section).rep().parse(input, column, row)
}