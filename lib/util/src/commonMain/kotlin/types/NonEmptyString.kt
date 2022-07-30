package types

import kotlin.jvm.JvmInline

@JvmInline
value class NonEmptyString(val value: String) {
	init {
		require(value.trim().isNotEmpty())
	}
}