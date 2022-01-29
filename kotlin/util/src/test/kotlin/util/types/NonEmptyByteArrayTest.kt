package util.types

import kotlin.test.*

class NonEmptyByteArrayTest {
    @Test
    fun throwsErrors() {
        assertFailsWith(IllegalArgumentException::class) {
            NonEmptyByteArray(byteArrayOf())
        }
    }

    @Test
    fun acceptsByteArrays() {
        fun check(byteArray: ByteArray) {
            assertContentEquals(byteArray, NonEmptyByteArray(byteArray).bytes)
        }

        check(byteArrayOf(0))
        check(byteArrayOf(0, 0))
        check(byteArrayOf(-10, 30))
    }
}