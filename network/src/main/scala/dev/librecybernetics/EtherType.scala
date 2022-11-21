package dev.librecybernetics

enum EtherType(value: Int):
  case ARP extends EtherType(0x0806)
  case IPv4 extends EtherType(0x0800)
