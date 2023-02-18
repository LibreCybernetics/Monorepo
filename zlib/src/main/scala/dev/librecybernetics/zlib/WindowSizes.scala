package dev.librecybernetics.zlib

opaque type WindowSize = Int & (256 | 512 | 1024 | 2048 | 4096 | 8192 | 16384 | 32768)

extension (windowSize: WindowSize)
  def toByte: Byte = windowSize match
    case 256   => 0
    case 512   => 1
    case 1024  => 2
    case 2048  => 3
    case 4096  => 4
    case 8192  => 5
    case 16384 => 6
    case 32768 => 7

extension (byte: Byte & (0 | 1 | 2 | 3 | 4 | 5 | 6 | 7))
  def toWindowsSize: WindowSize = byte match
    case 0 => 256
    case 1 => 512
    case 2 => 1024
    case 3 => 2048
    case 4 => 4096
    case 5 => 8192
    case 6 => 16384
    case 7 => 32768
