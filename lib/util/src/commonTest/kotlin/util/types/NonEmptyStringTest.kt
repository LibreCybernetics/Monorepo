package util.types

import kotlin.test.*

class NonEmptyStringTest {
	@Test
	fun throwsErrors() {
		assertFailsWith(IllegalArgumentException::class) {
			NonEmptyString("")
		}
		assertFailsWith(IllegalArgumentException::class) {
			NonEmptyString(" ")
		}
		assertFailsWith(IllegalArgumentException::class) {
			NonEmptyString("\n")
		}
		assertFailsWith(IllegalArgumentException::class) {
			NonEmptyString("\t")
		}
	}

	@Test
	fun acceptsString() {
		fun check(s: String) {
			assertEquals(s, NonEmptyString(s).value)
		}

		check(0.toChar().toString())
		check("Hello World!")
	}
}
