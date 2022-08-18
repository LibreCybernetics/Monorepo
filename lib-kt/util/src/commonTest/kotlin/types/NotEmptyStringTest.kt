package types

import kotlin.test.*

class NotEmptyStringTest {
	@Test
	fun throwsErrors() {
		assertFailsWith(IllegalArgumentException::class) {
			NotEmptyString("")
		}
		assertFailsWith(IllegalArgumentException::class) {
			NotEmptyString(" ")
		}
		assertFailsWith(IllegalArgumentException::class) {
			NotEmptyString("\n")
		}
		assertFailsWith(IllegalArgumentException::class) {
			NotEmptyString("\t")
		}
	}

	@Test
	fun acceptsString() {
		fun check(s: String) {
			assertEquals(s, NotEmptyString(s).value)
		}

		check(0.toChar().toString())
		check("Hello World!")
	}
}
