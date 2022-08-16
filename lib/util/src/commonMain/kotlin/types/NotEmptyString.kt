package types

import kotlin.jvm.JvmInline

@JvmInline
value class NotEmptyString(val value: String) {
	init {
		require(value.trim().isNotEmpty())
	}
}