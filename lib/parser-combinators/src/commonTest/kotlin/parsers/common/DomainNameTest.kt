package parsers.common

import kotlin.test.*

import parsers.*
import types.NonEmptyString

class DomainNameTest {
	private fun successTest(input: String, expected: List<NonEmptyString>) {
		val result = DomainName.parse(input)
		result as ParserSuccess
		assertEquals(result.output, expected)
		assertEquals(result.remaining, "")
	}

	private fun failureTest(input: String, expected: ParserError<String, List<NonEmptyString>>) {
		val result = DomainName.parse(input)
		result as ParserError
		assertEquals(result, expected)
	}

	@Test
	fun withoutTld() {
		listOf("cooperative", "localhost", "my-domain", "my-123domain").map {
			successTest(it, listOf(NonEmptyString(it)))
		}
	}

	@Test
	fun withTld() {
		successTest("orbea.com", listOf("orbea", "com").map { NonEmptyString(it) })
	}

	@Test
	fun reject() {
		failureTest("", EndOfInputError())
		failureTest(".", CondError("."))
		failureTest("1", CondError("1"))
		failureTest("-domain", CondError("-"))
		failureTest("domain-", CondError("-"))
	}
}
