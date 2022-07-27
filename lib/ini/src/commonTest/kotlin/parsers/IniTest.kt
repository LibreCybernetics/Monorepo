package parsers

import kotlin.test.*

import types.NonEmptyString

class IniTest {
	@Test
	fun simpleTest() {
		val text =
			"""[section]
				|key=value
			""".trimMargin()

		val parsed = Ini.section.parse(text)
		parsed as ParserSuccess
		val output = parsed.output
		assertEquals(output.name, listOf(NonEmptyString("section")))
		assertEquals(output.values, mapOf(Pair(NonEmptyString("key"), "value")))
	}
}
