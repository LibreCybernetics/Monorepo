package parsers.common

import kotlin.test.*

import parsers.*
import parsers.Column
import parsers.Row
import types.NotEmptyString

class DomainNameTest {
	private fun successTest(input: String, expected: List<NotEmptyString>) {
		val result = DomainName.parse(input, Column(1u), Row(1u))
		result as ParserSuccess
		assertEquals(expected, result.output)
		assertEquals(result.remaining, "")
	}

	private fun failureTest(input: String, expected: ParserError<String, List<NotEmptyString>>) {
		val result = DomainName.parse(input, Column(1u), Row(1u))
		result as ParserError
		assertEquals(expected, result)
	}

	@Test
	fun withoutTld() {
		listOf("cooperative", "localhost", "my-domain", "my-123domain").map {
			successTest(it, listOf(NotEmptyString(it)))
		}
	}

	@Test
	fun withTld() {
		successTest("orbea.com", listOf("orbea", "com").map { NotEmptyString(it) })
	}

	@Test
	fun reject() {
		failureTest("", EndOfInputError(Column(1u), Row(1u)))
		failureTest(".", CondError(".", Column(1u), Row(1u)))
		failureTest("1", CondError("1", Column(1u), Row(1u)))
		failureTest("-domain", CondError("-", Column(1u), Row(1u)))
		failureTest(
			"domain-",
			SeqError<String, List<NotEmptyString>, String>(
				CondError("-", Column(1u), Row(7u)),
				Column(1u),
				Row(1u)
			)
		)
	}
}
