package multiformats.multibase

data class Multibase(val bytes: ByteArray, val codec: Codec) {
    constructor(encoded: String) : this(decode(encoded), getCodec(encoded))

    companion object {
        fun decode(str: String): ByteArray = Codec.decode(str)
        fun getCodec(str: String): Codec = Codec.getCodec(str.first())
    }

    val encoded by lazy { codec.encode(bytes) }
}
