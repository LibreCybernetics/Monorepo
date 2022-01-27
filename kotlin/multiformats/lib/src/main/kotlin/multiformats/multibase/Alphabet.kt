package multiformats.multibase

internal open class Alphabet(private val alphabet: CharArray) {
    constructor(str: String) : this(str.toCharArray())

    fun char(char: Char): UByte = alphabet.indexOf(char).toUByte()
    fun char(uint: UInt): Char = alphabet[uint.toInt()]

    companion object {
        internal object Base2 : Alphabet("01")
        internal object Base8 : Alphabet("01234567")
        internal object Base16Lower : Alphabet("0123456789abcdef")
        internal object Base16Upper : Alphabet("0123456789ABCDEF")
        internal object Base32Lower : Alphabet("abcdefghijklmnopqrstuvwxyz234567")
        internal object Base32Upper : Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567")
        internal object Base32HexLower : Alphabet("0123456789abcdefghijklmnopqrstuv")
        internal object Base32HexUpper : Alphabet("0123456789ABCDEFGHIJKLMNOPQRSTUV")
        internal object ZBase32 : Alphabet("ybndrfg8ejkmcpqxot1uwisza345h769")
    }
}
