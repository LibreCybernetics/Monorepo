package multiformats.multibase

internal open class Alphabet(private val alphabet: CharArray) {
    fun char(char: Char): UByte = alphabet.indexOf(char).toUByte()
    fun char(uint: UInt): Char = alphabet[uint.toInt()]

    companion object {
        internal object Base2 : Alphabet("01".toCharArray())
        internal object Base8 : Alphabet("01234567".toCharArray())
        internal object Base16Lower : Alphabet("0123456789abcdef".toCharArray())
        internal object Base16Upper : Alphabet("0123456789ABCDEF".toCharArray())
    }
}
