package parsers

import kotlin.jvm.JvmInline

@JvmInline
value class Column(val value: UInt) {
	operator fun compareTo(other: Column): Int = value.compareTo(other.value)
	operator fun plus(other: Column): Column = Column(value + other.value)
}

@JvmInline
value class Row(val value: UInt) {
	operator fun compareTo(other: Row): Int = value.compareTo(other.value)
	operator fun plus(other: Row): Row = Row(value + other.value)
}