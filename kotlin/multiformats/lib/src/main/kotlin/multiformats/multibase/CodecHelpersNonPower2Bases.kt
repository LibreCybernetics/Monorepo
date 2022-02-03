package multiformats.multibase

import util.extensions.takeDropWhile
import java.math.BigInteger

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
    val (z, nz) = bytes.takeDropWhile { it == 0.toByte() }
    return z.map { '0' }.toCharArray().concatToString() +
        if (nz.isEmpty()) "" else base10Helper(nz).toString()
}

@ExperimentalUnsignedTypes
fun base10(encoded: String): ByteArray {
    val (z, nz) = encoded.takeDropWhile { it == '0' }
    return (z.map { 0.toUByte() }.toUByteArray() +
        if (nz.isEmpty()) ubyteArrayOf() else base10Helper(nz.toBigInteger())).toByteArray()
}

fun baseHelper(int: BigInteger, alphabet: Alphabet): String =
    if (int < alphabet.radix.toBigInteger()) alphabet.char(int.intValueExact()).toString() else
        baseHelper(int / alphabet.radix.toBigInteger(), alphabet) + alphabet.char(int.rem(alphabet.radix.toBigInteger()).intValueExact())

fun baseHelper(encoded: String, alphabet: Alphabet): BigInteger =
    if (encoded.isEmpty()) BigInteger.ZERO else
        alphabet.char(encoded.last()).toUInt().toInt().toBigInteger() + alphabet.radix.toBigInteger() * baseHelper(encoded.dropLast(1), alphabet)

fun genericNonPower2Base(bytes: ByteArray, alphabet: Alphabet): String {
    val (z, nz) = bytes.takeDropWhile { it == 0.toByte() }
    return z.map { alphabet.char(0) }.toCharArray().concatToString() +
        if (nz.isEmpty()) "" else baseHelper(base10Helper(nz), alphabet)
}

@ExperimentalUnsignedTypes
fun genericNonPower2Base(encoded: String, alphabet: Alphabet): ByteArray {
    val (z, nz) = encoded.takeDropWhile { it == alphabet.char(0) }
    return (z.map { 0.toUByte() }.toUByteArray() +
        if (nz.isEmpty()) ubyteArrayOf() else base10Helper(baseHelper(nz, alphabet))).toByteArray()
}
