package dev.librecybernetics.types

opaque type UnsignedByte = Byte

extension (ub: UnsignedByte)
  def toString: String = ub.toString