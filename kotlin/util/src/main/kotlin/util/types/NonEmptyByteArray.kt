package util.types

@JvmInline
value class NonEmptyByteArray(val bytes: ByteArray) {
    init {
        require(bytes.isNotEmpty())
    }

    fun dropLast(n: Int): List<Byte> = bytes.dropLast(n)
    fun last(): Byte = bytes.last()

    operator fun plus(bytes: ByteArray): NonEmptyByteArray =
        NonEmptyByteArray(this.bytes + bytes)
    operator fun plus(nebytes: NonEmptyByteArray): NonEmptyByteArray =
        NonEmptyByteArray(this.bytes + nebytes.bytes)

    inline fun <R> mapIndexed(transform: (index: Int, Byte) -> R): List<R> =
        this.bytes.mapIndexedTo(ArrayList(bytes.size), transform)
}
