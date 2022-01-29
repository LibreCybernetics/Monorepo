package multiformats.multibase

import java.math.BigInteger

@ExperimentalUnsignedTypes
private val base2pow: UByteArray = arrayOf(1, 2, 4, 8, 16, 32, 64, 128).map { it.toUByte() }.toUByteArray()

@ExperimentalUnsignedTypes
fun base2(bytes: ByteArray): String =
    bytes.toUByteArray().map { ubyte ->
        (0..7).map {
            if (ubyte.and(base2pow[7 - it]) > 0u) '1' else '0'
        }.toCharArray().concatToString()
    }.reduceOrNull { a, b -> a + b } ?: ""

@ExperimentalUnsignedTypes
fun base2(encoded: String): ByteArray =
    encoded.chunked(8).map {
        it.mapIndexed { idx, char ->
            (Alphabet.Companion.Base2.char(char) * base2pow[7 - idx]).toUByte()
        }.reduce { a, b -> (a + b).toUByte() }
    }.toUByteArray().toByteArray()

@ExperimentalUnsignedTypes
fun base8(bytes: ByteArray): String =
    bytes.toUByteArray().chunked(3).map { chunk ->
        val c0 = chunk[0] / 32u
        val c1 = chunk[0].rem(32u) / 4u
        val c2 = chunk[0].rem(4u) * 2u + (chunk.elementAtOrNull(1)?.div(128u) ?: 0u)
        val c3 = if (chunk.size > 1) chunk[1].rem(128u) / 16u else null
        val c4 = if (chunk.size > 1) chunk[1].rem(16u) / 2u else null
        val c5 = if (chunk.size > 1) chunk[1].rem(2u) * 4u + (chunk.elementAtOrNull(2)?.div(64u) ?: 0u) else null
        val c6 = if (chunk.size > 2) chunk[2].rem(64u) / 8u else null
        val c7 = if (chunk.size > 2) chunk[2].rem(8u) else null

        listOfNotNull(c0, c1, c2, c3, c4, c5, c6, c7).map {
            Alphabet.Companion.Base8.char(it)
        }.toCharArray().concatToString()
    }.reduceOrNull { a, b -> a + b } ?: ""

@ExperimentalUnsignedTypes
fun base8(encoded: String): ByteArray =
    encoded.chunked(8).flatMap { chunk ->
        val alphabet = Alphabet.Companion.Base8
        val b1 = alphabet.char(chunk[0]) * 32u + alphabet.char(chunk[1]) * 4u + alphabet.char(chunk[2]) / 2u
        val b2 = if (chunk.length > 3) {
            alphabet.char(chunk[2]).rem(2u) * 128u + alphabet.char(chunk[3]) * 16u + alphabet.char(chunk[4]) * 2u + alphabet.char(chunk[5]) / 4u
        } else null
        val b3 = if (chunk.length > 6) {
            alphabet.char(chunk[5]).rem(128u) * 64u + alphabet.char(chunk[6]) * 8u + alphabet.char(chunk[7])
        } else null

        listOfNotNull(b1, b2, b3).map { it.toUByte() }
    }.toUByteArray().toByteArray()

fun base10Helper(bytes: ByteArray): BigInteger =
    if (bytes.isEmpty()) BigInteger.ZERO else
        bytes.last().toUByte().toInt().toBigInteger() + 256.toBigInteger() *
            base10Helper(bytes.dropLast(1).toByteArray())

@ExperimentalUnsignedTypes
fun base10Helper(int: BigInteger): UByteArray =
    if (int < 256.toBigInteger()) ubyteArrayOf(int.intValueExact().toUByte()) else {
        val d = int.div(256.toBigInteger())
        val r = int.rem(256.toBigInteger()).intValueExact().toUByte()
        base10Helper(d) + r
    }

fun base10(bytes: ByteArray): String {
    val z = bytes.takeWhile { it == 0.toByte() }
    val nz = bytes.dropWhile { it == 0.toByte() }
    return z.map { '0' }.toCharArray().concatToString() +
        if (nz.isEmpty()) "" else base10Helper(nz.toByteArray()).toString()
}

@ExperimentalUnsignedTypes
fun base10(encoded: String): ByteArray {
    val z = encoded.takeWhile { it == '0' }
    val nz = encoded.dropWhile { it == '0' }
    return (z.map { 0.toUByte() }.toUByteArray() +
        if (nz.isEmpty()) ubyteArrayOf() else base10Helper(nz.toBigInteger())).toByteArray()
}

fun base16(bytes: ByteArray, alphabet: Alphabet): String =
    bytes.map { it.toUByte() }.flatMap {
        listOf(alphabet.char((it / 16u)), alphabet.char(it.mod(16u)))
    }.toCharArray().concatToString()

@ExperimentalUnsignedTypes
fun base16(encoded: String, alphabet: Alphabet): ByteArray =
    encoded.map { alphabet.char(it) }.chunked(2).map {
        (it.component1() * 16u + it.component2()).toUByte()
    }.toUByteArray().toByteArray()

@ExperimentalUnsignedTypes
fun base32(bytes: ByteArray, alphabet: Alphabet, pad: Boolean): String =
    bytes.toUByteArray().chunked(5).map { chunk ->
        val c0 = chunk[0] / 8u
        val c1 = chunk[0].rem(8u) * 4u + (chunk.elementAtOrNull(1)?.div(64u) ?: 0u)
        val c2 = if (chunk.size > 1) chunk[1].rem(64u) / 2u else null
        val c3 = if (chunk.size > 1) chunk[1].rem(2u) * 16u + (chunk.elementAtOrNull(2)?.div(16u) ?: 0u) else null
        val c4 = if (chunk.size > 2) chunk[2].rem(16u) * 2u + (chunk.elementAtOrNull(3)?.div(128u) ?: 0u) else null
        val c5 = if (chunk.size > 3) chunk[3].rem(128u) / 4u else null
        val c6 = if (chunk.size > 3) chunk[3].rem(4u) * 8u + (chunk.elementAtOrNull(4)?.div(32u) ?: 0u) else null
        val c7 = if (chunk.size > 4) chunk[4].rem(32u) else null

        arrayOf(c0, c1, c2, c3, c4, c5, c6, c7).mapNotNull {
            if (it != null) alphabet.char(it) else (if (pad) '=' else null)
        }.toCharArray().concatToString()
    }.reduceOrNull { a, b -> a + b } ?: ""

@ExperimentalUnsignedTypes
fun base32(encoded: String, alphabet: Alphabet): ByteArray =
    encoded.dropLastWhile { it == '=' }.chunked(8).flatMap { chunk ->
        val b0 = alphabet.char(chunk[0]) * 8u + alphabet.char(chunk[1]) / 4u
        val b1 = if (chunk.length > 2) {
            alphabet.char(chunk[1]).rem(4u) * 64u + alphabet.char(chunk[2]) * 2u + alphabet.char(chunk[3]) / 16u
        } else null
        val b2 = if (chunk.length > 4) {
            alphabet.char(chunk[3]).rem(16u) * 16u + alphabet.char(chunk[4]) / 2u
        } else null
        val b3 = if (chunk.length > 5) {
            alphabet.char(chunk[4]).rem(2u) * 128u + alphabet.char(chunk[5]) * 4u + alphabet.char(chunk[6]) / 8u
        } else null
        val b4 = if (chunk.length > 7) {
            alphabet.char(chunk[6]).rem(8u) * 32u + alphabet.char(chunk[7])
        } else null

        listOfNotNull(b0, b1, b2, b3, b4).map { it.toUByte() }
    }.toUByteArray().toByteArray()

fun baseHelper(int: BigInteger, alphabet: Alphabet): String =
    if (int < alphabet.radix.toBigInteger()) alphabet.char(int.intValueExact()).toString() else
        baseHelper(int / alphabet.radix.toBigInteger(), alphabet) + alphabet.char(int.rem(alphabet.radix.toBigInteger()).intValueExact())

fun baseHelper(encoded: String, alphabet: Alphabet): BigInteger =
    if (encoded.isEmpty()) BigInteger.ZERO else
        alphabet.char(encoded.last()).toUInt().toInt().toBigInteger() + alphabet.radix.toBigInteger() * baseHelper(encoded.dropLast(1), alphabet)

fun genericNonPower2Base(bytes: ByteArray, alphabet: Alphabet): String {
    val z = bytes.takeWhile { it == 0.toByte() }
    val nz = bytes.dropWhile { it == 0.toByte() }
    return z.map { alphabet.char(0) }.toCharArray().concatToString() +
        if (nz.isEmpty()) "" else baseHelper(base10Helper(nz.toByteArray()), alphabet)
}

@ExperimentalUnsignedTypes
fun genericNonPower2Base(encoded: String, alphabet: Alphabet): ByteArray {
    val z = encoded.takeWhile { it == alphabet.char(0) }
    val nz = encoded.dropWhile { it == alphabet.char(0) }
    return (z.map { 0.toUByte() }.toUByteArray() +
        if (nz.isEmpty()) ubyteArrayOf() else base10Helper(baseHelper(nz, alphabet))).toByteArray()
}
