package dev.librecybernetics.zlib

import scodec.*
import scodec.codecs.*

enum CompressionMethodAndInfo(val byte: Byte):
  case Deflate(
      val windowSize: WindowSize
  ) extends CompressionMethodAndInfo(
    (8 << 4 + windowSize.toByte).toByte
      )

private def methodFromUInt8(i: Int) = i match
  case i: Int if (i >> 4) == 8 => CompressionMethodAndInfo.Deflate

private def windowSizeFromUInt8(i: Int): WindowSize =
  val wsb: Byte = (i % 16).toByte
  wsb match
    case 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 => wsb.toWindowSize

private def cmiFromUInt8(i: Int) =
  for
    m  <- PartialFunction.fromFunction(methodFromUInt8).unapply(i)
    ws <- PartialFunction.fromFunction(windowSizeFromUInt8).unapply(i)
  yield (m, ws)

given compressionMethodAndInfo: Decoder[CompressionMethodAndInfo] =
  uint8.emap(cmiFromUInt8(_).collect {
    case (CompressionMethodAndInfo.Deflate, ws) => Attempt.successful(CompressionMethodAndInfo.Deflate(ws))
  }.getOrElse(Attempt.failure(Err("Compression Method or Info not Supported"))))