package multiformats.multibase

object Codecs {
    val Identity = object : Codec(0.toChar()) {
        override fun directEncode(bytes: ByteArray): String = String(bytes, Charsets.ISO_8859_1)
        override fun directDecode(encoded: String): ByteArray = encoded.toByteArray(Charsets.ISO_8859_1)
    }

    @ExperimentalUnsignedTypes
    val Base2 = object : Codec('0') {
        override fun directEncode(bytes: ByteArray): String = base2(bytes)
        override fun directDecode(encoded: String): ByteArray = base2(encoded)
    }

    @ExperimentalUnsignedTypes
    val Base8 = object : Codec('7') {
        override fun directEncode(bytes: ByteArray): String = base8(bytes)
        override fun directDecode(encoded: String): ByteArray = base8(encoded)
    }

    @ExperimentalUnsignedTypes
    val Base10 = object : Codec('9') {
        override fun directEncode(bytes: ByteArray): String = base10(bytes)
        override fun directDecode(encoded: String): ByteArray = base10(encoded)
    }

    @ExperimentalUnsignedTypes
    val Base16Lower = object : Codec('f') {
        override fun directEncode(bytes: ByteArray): String = base16(bytes, Alphabet.Companion.Base16Lower)
        override fun directDecode(encoded: String): ByteArray = base16(encoded, Alphabet.Companion.Base16Lower)
    }

    @ExperimentalUnsignedTypes
    val Base16Upper = object : Codec('F') {
        override fun directEncode(bytes: ByteArray): String = base16(bytes, Alphabet.Companion.Base16Upper)
        override fun directDecode(encoded: String): ByteArray = base16(encoded, Alphabet.Companion.Base16Upper)
    }

    @ExperimentalUnsignedTypes
    val Base32Lower = object : Codec('b') {
        override fun directEncode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32Lower, false)
        override fun directDecode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32Lower)
    }

    @ExperimentalUnsignedTypes
    val Base32Upper = object : Codec('B') {
        override fun directEncode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32Upper, false)
        override fun directDecode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32Upper)
    }

    @ExperimentalUnsignedTypes
    val Base32LowerPad = object : Codec('c') {
        override fun directEncode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32Lower, true)
        override fun directDecode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32Lower)
    }

    @ExperimentalUnsignedTypes
    val Base32UpperPad = object : Codec('C') {
        override fun directEncode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32Upper, true)
        override fun directDecode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32Upper)
    }

    @ExperimentalUnsignedTypes
    val Base32HexLower = object : Codec('v') {
        override fun directEncode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32HexLower, false)
        override fun directDecode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32HexLower)
    }

    @ExperimentalUnsignedTypes
    val Base32HexUpper = object : Codec('V') {
        override fun directEncode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32HexUpper, false)
        override fun directDecode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32HexUpper)
    }

    @ExperimentalUnsignedTypes
    val Base32HexLowerPad = object : Codec('t') {
        override fun directEncode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32HexLower, true)
        override fun directDecode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32HexLower)
    }

    @ExperimentalUnsignedTypes
    val Base32HexUpperPad = object : Codec('T') {
        override fun directEncode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.Base32HexUpper, true)
        override fun directDecode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.Base32HexUpper)
    }

    @ExperimentalUnsignedTypes
    val ZBase32 = object : Codec('h') {
        override fun directEncode(bytes: ByteArray): String = base32(bytes, Alphabet.Companion.ZBase32, false)
        override fun directDecode(encoded: String): ByteArray = base32(encoded, Alphabet.Companion.ZBase32)
    }

    @ExperimentalUnsignedTypes
    val Base36Lower = object : Codec('k') {
        override fun directEncode(bytes: ByteArray): String = genericNonPower2Base(bytes, Alphabet.Companion.Base36Lower)
        override fun directDecode(encoded: String): ByteArray = genericNonPower2Base(encoded, Alphabet.Companion.Base36Lower)
    }

    @ExperimentalUnsignedTypes
    val Base36Upper = object : Codec('K') {
        override fun directEncode(bytes: ByteArray): String = genericNonPower2Base(bytes, Alphabet.Companion.Base36Upper)
        override fun directDecode(encoded: String): ByteArray = genericNonPower2Base(encoded, Alphabet.Companion.Base36Upper)
    }

    @ExperimentalUnsignedTypes
    val Base58 = object : Codec('z') {
        override fun directEncode(bytes: ByteArray): String = genericNonPower2Base(bytes, Alphabet.Companion.Base58)
        override fun directDecode(encoded: String): ByteArray = genericNonPower2Base(encoded, Alphabet.Companion.Base58)
    }

    @ExperimentalUnsignedTypes
    val Base58Flickr = object : Codec('Z') {
        override fun directEncode(bytes: ByteArray): String = genericNonPower2Base(bytes, Alphabet.Companion.Base58Flickr)
        override fun directDecode(encoded: String): ByteArray = genericNonPower2Base(encoded, Alphabet.Companion.Base58Flickr)
    }

    private val base64encoder = java.util.Base64.getEncoder()
    private val base64decoder = java.util.Base64.getDecoder()
    private val base64urlencoder = java.util.Base64.getUrlEncoder()
    private val base64urldecoder = java.util.Base64.getUrlDecoder()

    val Base64 = object : Codec('m') {
        override fun directEncode(bytes: ByteArray): String = base64encoder.withoutPadding().encodeToString(bytes)
        override fun directDecode(encoded: String): ByteArray = base64decoder.decode(encoded)
    }

    val Base64Pad = object : Codec('M') {
        override fun directEncode(bytes: ByteArray): String = base64encoder.encodeToString(bytes)
        override fun directDecode(encoded: String): ByteArray = base64decoder.decode(encoded)
    }

    val Base64URL = object : Codec('u') {
        override fun directEncode(bytes: ByteArray): String = base64urlencoder.withoutPadding().encodeToString(bytes)
        override fun directDecode(encoded: String): ByteArray = base64urldecoder.decode(encoded)
    }

    val Base64URLPad = object : Codec('U') {
        override fun directEncode(bytes: ByteArray): String = base64urlencoder.encodeToString(bytes)
        override fun directDecode(encoded: String): ByteArray = base64urldecoder.decode(encoded)
    }
}
