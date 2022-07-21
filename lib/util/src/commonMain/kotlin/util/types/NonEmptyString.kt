package util.types

@JvmInline
value class NonEmptyString(val value: String) {
	init {
		require(value.trim().isNotEmpty())
	}
}