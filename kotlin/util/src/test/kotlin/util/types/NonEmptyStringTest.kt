package util.types

import kotlin.test.*

class NonEmptyStringTest {
    @Test
    fun throwsErrors() {
        assertFailsWith(IllegalArgumentException::class) {
            NonEmptyString("")
        }
    }

    @Test
    fun acceptsString() {
        fun check(s: String) {
            assertEquals(s, NonEmptyString(s).str)
        }

        check(0.toChar().toString())
        check(" ")
        check("Hello World!")
    }
}
