package multiformats

sealed class Multicodec {
    abstract val code: Int

    init {
        synchronized(Multicodec) {
            require(!registered.contains(code))
            registered.add(code)
            println(registered)
        }
    }

    companion object {
        private val registered: MutableSet<Int> = mutableSetOf()

        protected data class Multiaddr(override val code: Int) : Multicodec() {
            companion object {
                val DNS: Multiaddr by lazy { Multiaddr(53) }
                val DNS4: Multiaddr by lazy { Multiaddr(54) }
                val DNS6: Multiaddr by lazy { Multiaddr(55) }
                val DNSAddr: Multiaddr by lazy { Multiaddr(56) }
                val IPv4: Multiaddr by lazy { Multiaddr(4) }
                val IPv6: Multiaddr by lazy { Multiaddr(41) }
                val P2P: Multiaddr by lazy { Multiaddr(421) }
                val P2PCircuit: Multiaddr by lazy { Multiaddr(290) }
                val QUIC: Multiaddr by lazy { Multiaddr(460) }
                val TCP: Multiaddr by lazy { Multiaddr(6) }
                val UNIX: Multiaddr by lazy { Multiaddr(400) }
                val WebSocket: Multiaddr by lazy { Multiaddr(477) }
                val WebSocketSecure: Multiaddr by lazy { Multiaddr(478) }
            }
        }

        protected data class Multihash(override val code: Int) : Multicodec() {
            companion object {
                val MD4: Multihash by lazy { Multihash(212) }
                val MD5: Multihash by lazy { Multihash(213) }
                val Murmur3_x86_64: Multihash by lazy { Multihash(34) }
                val Murmur3_i686: Multihash by lazy { Multihash(35) }
                val SHA1: Multihash by lazy { Multihash(17) }
                val SHA2_256: Multihash by lazy { Multihash(18) }
                val SHA2_384: Multihash by lazy { Multihash(32) }
                val SHA2_512: Multihash by lazy { Multihash(19) }
                val SHA3_224: Multihash by lazy { Multihash(23) }
                val SHA3_256: Multihash by lazy { Multihash(22) }
                val SHA3_384: Multihash by lazy { Multihash(21) }
                val SHA3_512: Multihash by lazy { Multihash(20) }
            }
        }
    }
}
