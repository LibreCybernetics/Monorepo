package multiformats.multiaddr

import parser.ParserSuccess
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.*

@ExperimentalUnsignedTypes
class UtilParsersTest {
    @Test
    fun randomIPv4Addresses() {
        for(i in 1..1000) {
            val random = Random.nextUBytes(4)
            val randomString = random.joinToString(".")
            val noiseLength = Random.nextLong(0, 10)
            val noise = (1..noiseLength).map {
                " \nabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".random()
            }.toCharArray().concatToString()
            when(val parsed = IPv4Parser.parse(randomString + noise)) {
                is ParserSuccess -> {
                    assertContentEquals(random, parsed.output)
                    assertEquals(noise, parsed.remaining)
                }
                else -> assertTrue(false)
            }
        }
    }
}