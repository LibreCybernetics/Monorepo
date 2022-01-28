package multiformats.multibase

abstract class Codec(val code: Char) {
    init {
        synchronized(Codec) {
            require(!registered.contains(code))
            registered.plusAssign(code to this)
        }
    }

    abstract fun directEncode(bytes: ByteArray): String
    abstract fun directDecode(encoded: String): ByteArray

    fun encode(bytes: ByteArray): String = "$code${directEncode(bytes)}"
    fun decode(str: String): ByteArray {
        require(str.isNotEmpty())
        require(str.first() == code)

        return directDecode(str.drop(1))
    }

    companion object {
        private val registered: MutableMap<Char, Codec> = mutableMapOf()

        fun getCodec(code: Char): Codec = registered.getValue(code)
        fun getCodecs(): Collection<Codec> = registered.values

        fun decode(str: String): ByteArray {
            require(str.isNotEmpty())
            require(registered.contains(str.first()))

            return getCodec(str.first()).decode(str)
        }
    }
}