package multiformats.multibase

import util.types.NonEmptyString

abstract class Codec(val code: Char) {
    init {
        synchronized(Codec) {
            require(!registered.contains(code))
            registered.plusAssign(code to this)
        }
    }

    abstract fun directEncode(bytes: ByteArray): String
    abstract fun directDecode(encoded: String): ByteArray

    fun encode(bytes: ByteArray): NonEmptyString = NonEmptyString("$code${directEncode(bytes)}")
    fun decode(nestr: NonEmptyString): ByteArray {
        require(nestr.str.first() == code)

        return directDecode(nestr.str.drop(1))
    }

    companion object {
        private val registered: MutableMap<Char, Codec> = mutableMapOf()

        fun getCodec(code: Char): Codec = registered.getValue(code)
        fun getCodecs(): Collection<Codec> = registered.values

        fun decode(nestr: NonEmptyString): ByteArray {
            require(registered.contains(nestr.str.first()))

            return getCodec(nestr.str.first()).decode(nestr)
        }
    }
}
