package util.types

@JvmInline
value class NonEmptyString(val str: String) {
	init {
		require(str.isNotEmpty())
	}

	fun first(): Char = str.first()
}
