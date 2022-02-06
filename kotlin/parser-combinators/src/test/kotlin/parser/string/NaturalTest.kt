package parser.string

import parser.ParserSuccess
import kotlin.random.Random
import kotlin.test.*

class NaturalTest {
    @Test
    fun randomValues() {
        for(i in 1..1000) {
            val random = Random.nextLong(Long.MAX_VALUE)
            val noiseLength = Random.nextLong(0, 10)
            val noise = (1..noiseLength).map {
                " \nabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".random()
            }
            val input = random.toString() + noise.toCharArray().concatToString()
            when(val parsed = NaturalParser.parse(input)) {
                is ParserSuccess -> {
                    assertEquals(random.toString(), parsed.output)
                    assertContentEquals(noise, parsed.remaining.toList())
                }
                else ->
                    assertTrue(false)
            }
        }
    }
}
