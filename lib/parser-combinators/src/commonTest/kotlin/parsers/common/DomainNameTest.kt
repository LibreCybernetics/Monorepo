package parsers.common

import parsers.*
import kotlin.test.*

class DomainNameTest {
	private fun successTest(input: String, expected: List<String>) {
		val result = DomainName.parse(input)
		assert(result is ParserSuccess)
		result as ParserSuccess
		assert(result.output == expected)
		assert(result.remaining == "")
	}

	private fun failureTest(input: String, expected: ParserError<String, List<String>>) {
		val result = DomainName.parse(input)
		assert(result is ParserError)
		assert(result == expected)
	}

	@Test
	fun withoutTld() {
		successTest("cooperative", listOf("cooperative"))
		successTest("localhost", listOf("localhost"))
		successTest("my-domain", listOf("my-domain"))
		successTest("my-123domain", listOf("my-123domain"))
	}

	@Test
	fun withTld() {
		successTest("orbea.com", listOf("orbea", "com"))
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