package parsers.common

import parsers.ParserSuccess
import util.types.NonEmptyString
import kotlin.test.*

class CommandLineArgumentsTest {
	fun generalSuccess(input: String, expected: CommandLineArguments) {
		val r = CommandLineArguments.parse(input)
		assert(r is ParserSuccess)
		r as ParserSuccess
		println(r)
		assert(r.output == expected)
		assert(r.remaining.isEmpty())
	}

	@Test
	fun empty() {
		generalSuccess("", CommandLineArguments(mapOf(), ""))
	}

	@Test
	fun simple() {
		generalSuccess("-h", CommandLineArguments(mapOf(Pair(NonEmptyString("h"), "")), ""))
		generalSuccess("-v 99", CommandLineArguments(mapOf(Pair(NonEmptyString("v"), "99")), ""))
	}

	@Test
	fun several() {
		generalSuccess(
			"-h -v",
			CommandLineArguments(
				mapOf(
					Pair(NonEmptyString("h"), ""),
					Pair(NonEmptyString("v"), "")
				), ""
			)
		)
		generalSuccess(
			"-h hello world -v 99",
			CommandLineArguments(
				mapOf(
					Pair(NonEmptyString("v"), "99"),
					Pair(NonEmptyString("h"), "hello world")
				), ""
			)
		)
	}
}