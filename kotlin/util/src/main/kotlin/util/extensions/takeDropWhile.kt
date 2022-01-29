package util.extensions

fun ByteArray.takeDropWhile(p: (Byte) -> Boolean): Pair<ByteArray, ByteArray> =
        this.takeWhile(p).toByteArray() to this.dropWhile(p).toByteArray()

fun String.takeDropWhile(p: (Char) -> Boolean): Pair<String, String> =
        this.takeWhile(p) to this.dropWhile(p)