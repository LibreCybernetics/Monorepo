package multiformats

import java.math.BigInteger
import java.io.File
import kotlin.test.*

class MultibaseTest {
	@Test
	fun exampleValuesFromSpec() {
		val firstContent: ByteArray = arrayOf(121, 101, 115, 32, 109, 97, 110, 105, 32, 33).map { it.toUByte() }.toUByteArray().toByteArray()
		val testVectors: List<Pair<String, String>> =
			File("../../../spec/multiformats/multibase-basic.csv")
				.readLines()
				.map { it.split(',') }
				.map { Pair(it.component1().trim(), it.component2().trim()) }
				.filter { listOf("identity", "base16").contains(it.component1()) }

		testVectors.forEach { (encoding, encoded) ->
			val codec = Multibase.getCodec(encoded.first())
			assertEquals(encoded, codec.encode(firstContent))
			assertContentEquals(firstContent, Multibase.decode(encoded))
		}
	}
}
