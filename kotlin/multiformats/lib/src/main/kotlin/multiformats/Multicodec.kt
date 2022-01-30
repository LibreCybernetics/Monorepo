package multiformats

import multiformats.multihash.Multihash

sealed class Multicodec {
    abstract val code: UShort


    companion object {
        private val registered: MutableMap<UShort, Multicodec> = mutableMapOf()

        protected data class Multiaddr(override val code: UShort) : Multicodec() {
            init {
                synchronized(Multicodec) {
                    require(!registered.contains(code))
                    registered.plusAssign(code to this)
                }
            }

            companion object {
                val DNS: Multiaddr by lazy { Multiaddr(53u) }
                val DNS4: Multiaddr by lazy { Multiaddr(54u) }
                val DNS6: Multiaddr by lazy { Multiaddr(55u) }
                val DNSAddr: Multiaddr by lazy { Multiaddr(56u) }
                val IPv4: Multiaddr by lazy { Multiaddr(4u) }
                val IPv6: Multiaddr by lazy { Multiaddr(41u) }
                val P2P: Multiaddr by lazy { Multiaddr(421u) }
                val P2PCircuit: Multiaddr by lazy { Multiaddr(290u) }
                val QUIC: Multiaddr by lazy { Multiaddr(460u) }
                val TCP: Multiaddr by lazy { Multiaddr(6u) }
                val UNIX: Multiaddr by lazy { Multiaddr(400u) }
                val WebSocket: Multiaddr by lazy { Multiaddr(477u) }
                val WebSocketSecure: Multiaddr by lazy { Multiaddr(478u) }
            }
        }

        data class Multihash(override val code: UShort) : Multicodec() {
            init {
                synchronized(Multicodec) {
                    require(!registered.contains(code))
                    registered.plusAssign(code to this)
                }
            }


            companion object {
                val MD5: Multihash = Multihash(213u)
                val SHA1: Multihash = Multihash(17u)
                val SHA2_256: Multihash =  Multihash(18u)
                val SHA2_384: Multihash = Multihash(32u)
                val SHA2_512: Multihash = Multihash(19u)
                val SHA3_224: Multihash = Multihash(23u)
                val SHA3_256: Multihash = Multihash(22u)
                val SHA3_384: Multihash = Multihash(21u)
                val SHA3_512: Multihash = Multihash(20u)

                fun get(ushort: UShort): Multihash {
                    require(registered.contains(ushort))
                    require(registered.getValue(ushort) is Multihash)
                    return registered.getValue(ushort) as Multihash
                }

                fun getAlgorithms(): Collection<Multihash> =
                    registered.values.filterIsInstance<Multihash>()
            }
        }
    }
}
