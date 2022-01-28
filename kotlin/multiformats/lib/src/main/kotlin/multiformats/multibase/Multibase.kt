package multiformats.multibase

import util.types.NonEmptyString

data class Multibase(val bytes: ByteArray, val codec: Codec) {
    constructor(encoded: NonEmptyString) : this(decode(encoded), getCodec(encoded))

    companion object {
        fun decode(nestr: NonEmptyString): ByteArray = Codec.decode(nestr)
        fun getCodec(nestr: NonEmptyString): Codec = Codec.getCodec(nestr.str.first())
    }

    val encoded: NonEmptyString by lazy { codec.encode(bytes) }
}
