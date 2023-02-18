package dev.librecybernetics.zlib

enum CompressionMethodAndInfo(val byte: Byte):
  case Deflate(
      val windowSize: WindowSize
  ) extends CompressionMethodAndInfo(
    (8 << 4 + windowSize.toByte).toByte
      )
