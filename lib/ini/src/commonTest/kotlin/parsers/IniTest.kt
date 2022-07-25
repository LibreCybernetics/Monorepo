package parsers

import util.types.NonEmptyString
import kotlin.test.*

class IniTest {
	@Test
	fun simpleTest() {
		val text =
			"""[section]
				|key=value
			""".trimMargin()

		println(text)
		val parsed = Ini.section.parse(text)
		assert(parsed is ParserSuccess)
		parsed as ParserSuccess
		val output = parsed.output
		println(output.name.first().value)
		assert(output.name == listOf(NonEmptyString("section")))
		assert(output.values == mapOf(Pair(NonEmptyString("key"), "value")))
	}
}