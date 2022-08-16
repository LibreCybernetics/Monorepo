package parsers

import kotlin.test.*

import types.NotEmptyString

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
		assertEquals(
			Pair(Row(1u), Column(1u)),
			output.position
		)
		assertEquals(
			listOf(NotEmptyString("section")),
			output.name
		)
		assertEquals(
			mapOf(
				Pair(
					NotEmptyString("key"),
					Pair(Pair(Row(2u), Column(1u)), "value")
				)
			), output.values
		)
	}
}
