package parsers

fun <Input, Output> lookahead(parser: GenericParser<Input, Output>): GenericParser<Input, Unit> =
	object : GenericParser<Input, Unit> {
		override fun parse(input: Input, column: Column, row: Row): ParserResult<Input, Unit> {
			return when (val r = parser.parse(input, column, row)) {
				is ParserSuccess -> ParserSuccess(Unit, input, column, row)
				is ParserError -> r.map { }
			}
		}
	}

fun <Input, Output> negativeLookahead(parser: GenericParser<Input, Output>): GenericParser<Input, Unit> =
	lookahead(parser.not())

infix fun <Input, Output, Output1> GenericParser<Input, Output>.lookahead(
	other: GenericParser<Input, Output1>
): GenericParser<Input, Output> {
	return this seqLeft (parsers.lookahead(other))
}

infix fun <Input, Output, Output1> GenericParser<Input, Output>.negativeLookahead(
	other: GenericParser<Input, Output1>
): GenericParser<Input, Output> =
	this lookahead (other.not())

fun <Input, Output> GenericParser<Input, Output>.rep(
	sep: GenericParser<Input, Unit>, min: UInt?, max: UInt?
): GenericParser<Input, List<Output>> {
	require(min == null || max == null || min <= max)
	val self = this

	return object : GenericParser<Input, List<Output>> {
		override fun parse(input: Input, column: Column, row: Row): ParserResult<Input, List<Output>> {
			return if (max == 0u) ParserSuccess(listOf(), input, column, row)
			else when (val head = self.parse(input, column, row)) {
				is ParserSuccess -> {
					val newMin = if (min == 0u) null else min?.minus(1u)
					self
						.rep(sep, newMin, max?.minus(1u))
						.parse(head.remaining, head.column, head.row)
						.map { listOf(head.output) + it }
				}

				is ParserError ->
					if (min == null || min == 0u) ParserSuccess(listOf(), input, head.column, head.row)
					else head.map { listOf(it) }
			}
		}
	}
}
