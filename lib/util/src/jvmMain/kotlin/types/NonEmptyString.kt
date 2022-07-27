package types

@JvmInline
actual value class NonEmptyString(val value: String) {
	init {
		require(value.trim().isNotEmpty())
	}
}