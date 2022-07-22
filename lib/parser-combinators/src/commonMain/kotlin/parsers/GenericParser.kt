package parsers

fun interface GenericParser<Input, Output> {
	fun parse(input: Input): ParserResult<Input, Output>
}

fun <Input, Output, Output1> GenericParser<Input, Output>.map(
	f: (Output) -> Output1
): GenericParser<Input, Output1> {
	val self = this

	return GenericParser {
		self.parse(it).map(f)
	}
}

fun <Input, Output, Output1> GenericParser<Input, Output>.bind(
	f: (Output) -> GenericParser<Input, Output1>
): GenericParser<Input, Output1> {
	val self = this

	return GenericParser {
		when(val r = self.parse(it).map(f)) {
			is ParserSuccess -> r.output.parse(r.remaining)
			is ParserError -> r.map()
		}
	}
}

infix fun <Input, Output> GenericParser<Input, Output>.or(
	other: GenericParser<Input, Output>
): GenericParser<Input, Output> {
	val self = this

	return GenericParser {
		when(val first = self.parse(it)) {
			is ParserSuccess -> first
			is ParserError -> when(val second = other.parse(it)) {
				is ParserSuccess -> second
				is ParserError -> AlternativeError(first, second)
			}
		}
	}
}

infix fun <Input, Output, Output1> GenericParser<Input, Output>.seq(
	other: GenericParser<Input, Output1>
): GenericParser<Input, Pair<Output, Output1>> {
	val self = this

	return GenericParser {
		when(val first = self.parse(it)) {
			is ParserError -> first.map()
			is ParserSuccess -> other.parse(first.remaining).map { Pair(first.output, it) }
		}
	}
}

infix fun <Input, Output, Output1> GenericParser<Input, Output>.lookahead(
	other: GenericParser<Input, Output1>
): GenericParser<Input, Output> {
	val self = this

	return GenericParser {
		when(val first = self.parse(it)) {
			is ParserSuccess -> {
				when(val second = other.parse(first.remaining)) {
					is ParserSuccess -> first
					is ParserError -> SeqError(second)
				}
			}
			is ParserError -> first
		}
	}
}

fun <Input, Output> GenericParser<Input, Output>.rep(
	sep: GenericParser<Input, Unit>, min: UInt?, max: UInt?
): GenericParser<Input, List<Output>> {
	require(min == null || max == null || min <= max)
	val self = this

	return GenericParser {
		if (max == 0u) ParserSuccess(listOf(), it)

		else when (val head = self.parse(it)) {
			is ParserSuccess -> {
				val newMin = if (min == 0u) null else min?.minus(1u)
				self
					.rep(sep, newMin, max?.minus(1u))
					.parse(head.remaining)
					.map { listOf(head.output) + it }
			}

			is ParserError ->
				if (min == null || min == 0u) ParserSuccess(listOf(), it)
				else head.map { listOf(it) }
		}
	}
}