package parsers.common

import parsers.*
import util.types.NonEmptyString

data class CommandLineArguments(val arguments: Map<NonEmptyString, String>, val remaining: String) {
	companion object : StringParser<CommandLineArguments> {
		val hyphen: StringParser<Char> =
			charMatch('-')

		val notHyphen: StringParser<Char> =
			charPred { it != '-' }

		val shortArgumentStart: StringParser<Char> =
			hyphen seqRight letter

		val shortArgument: StringParser<Pair<Char, String>> =
			(space.rep() seqRight shortArgumentStart seqLeft (space.unit() or end)) seq
					(negativeLookahead(space seq shortArgumentStart) seqRight anyChar).rep().map { it.toCharArray().concatToString() }


		override fun parse(input: String): ParserResult<String, CommandLineArguments> =
			(shortArgument).rep().parse(input)
				.map {
					CommandLineArguments(
						it.map { Pair(NonEmptyString(it.first.toString()), it.second) }.toMap(),
						remaining = ""
					)
				}
	}
}