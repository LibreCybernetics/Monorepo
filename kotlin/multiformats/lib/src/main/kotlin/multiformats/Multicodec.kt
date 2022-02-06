package multiformats

import multiformats.multihash.Multihash

sealed class Multicodec {
    abstract val code: UShort


    companion object {
        private val registered: MutableMap<UShort, Multicodec> = mutableMapOf()

        data class Multiaddr(override val code: UShort, val string: String) : Multicodec() {
            init {
                synchronized(Multicodec) {
                    require(!registered.contains(code))
                    registered.plusAssign(code to this)
                }
            }

            companion object {
                // Network Layer
                val IPv4: Multiaddr = Multiaddr(4u, "ip4")
                val IPv6: Multiaddr = Multiaddr(41u, "ip6")
                // Transport Layer
                val QUIC: Multiaddr by lazy { Multiaddr(460u, "quic") }
                val TCP: Multiaddr = Multiaddr(6u, "tcp")
                val UDP: Multiaddr = Multiaddr(273u, "udp")
                // Application Layer
                val DNS: Multiaddr by lazy { Multiaddr(53u, "dns") }
                val DNS4: Multiaddr by lazy { Multiaddr(54u, "dns4") }
                val DNS6: Multiaddr by lazy { Multiaddr(55u, "dns6") }
                val DNSAddr: Multiaddr by lazy { Multiaddr(56u, "dnsaddr") }
                val WebSocket: Multiaddr by lazy { Multiaddr(477u, "ws") }
                val WebSocketSecure: Multiaddr by lazy { Multiaddr(478u, "wss") }
                // Other
                val P2P: Multiaddr by lazy { Multiaddr(421u, "p2p") }
                val P2PCircuit: Multiaddr by lazy { Multiaddr(290u, "p2p-circuit") }
                val UNIX: Multiaddr by lazy { Multiaddr(400u, "unix") }

                fun get(ushort: UShort): Multiaddr {
                    require(registered.contains(ushort))
                    require(registered.getValue(ushort) is Multiaddr)
                    return registered.getValue(ushort) as Multiaddr
                }

                fun getProtocols(): Collection<Multiaddr> =
                    registered.values.filterIsInstance<Multiaddr>()

                fun get(string: String): Multiaddr {
                    val candidates = getProtocols().filter { it.string == string}
                    assert(candidates.size == 1)
                    return candidates.first()
                }
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
