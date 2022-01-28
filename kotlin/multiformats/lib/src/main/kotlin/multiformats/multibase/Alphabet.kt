package multiformats.multibase

open class Alphabet(private val alphabet: CharArray) {
    constructor(str: String) : this(str.toCharArray())

    fun char(char: Char): UByte = alphabet.indexOf(char).toUByte()
    fun char(int: Int): Char = alphabet[int]
    fun char(uint: UInt): Char = char(uint.toInt())

    val radix: Int by lazy { alphabet.size }

    companion object {
        internal object Base2 : Alphabet("01")
        internal object Base8 : Alphabet("01234567")
        internal object Base16Lower : Alphabet("0123456789abcdef")
        internal object Base16Upper : Alphabet("0123456789ABCDEF")
        internal object Base32Lower : Alphabet("abcdefghijklmnopqrstuvwxyz234567")
        internal object Base32Upper : Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567")
        internal object Base32HexLower : Alphabet("0123456789abcdefghijklmnopqrstuv")
        internal object Base32HexUpper : Alphabet("0123456789ABCDEFGHIJKLMNOPQRSTUV")
        internal object Base36Lower : Alphabet("0123456789abcdefghijklmnopqrstuvwxyz")
        internal object Base36Upper : Alphabet("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ")
        internal object Base58 : Alphabet("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz")
        internal object Base58Flickr : Alphabet("123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ")
        internal object ZBase32 : Alphabet("ybndrfg8ejkmcpqxot1uwisza345h769")
    }
}
