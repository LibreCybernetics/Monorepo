package util

sealed interface Platform

object JSPlatform : Platform

object JVMPlatform : Platform

sealed interface NativePlatform : Platform
object LinuxNativePlatform : NativePlatform

expect val platform: Platform