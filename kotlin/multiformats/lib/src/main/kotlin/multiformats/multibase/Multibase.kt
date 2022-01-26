package multiformats.multibase

data class Multibase(val bytes: ByteArray, val codec: MultibaseCodec) {
    companion object {
        fun decode(str: String): ByteArray = MultibaseCodec.decode(str)
    }

    val encoded by lazy { codec.encode(bytes) }
}
