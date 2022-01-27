package multiformats.multibase

data class Multibase(val bytes: ByteArray, val codec: Codec) {
    companion object {
        fun decode(str: String): ByteArray = Codec.decode(str)
    }

    val encoded by lazy { codec.encode(bytes) }
}
