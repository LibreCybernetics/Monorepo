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

fun <Input, R> pass(r: R): GenericParser<Input, R> = GenericParser {
	ParserSuccess(r, it)
}

val unit: StringParser<Unit> = pass(Unit)

fun <Input, Output> GenericParser<Input, Output>.unit(): GenericParser<Input, Unit> =
	this.map { }

fun <Input, Output> GenericParser<Input, Output>.not(): GenericParser<Input, Unit> {
	val self = this

	return GenericParser<Input, Unit> { input ->
		when(self.parse(input)) {
			is ParserSuccess -> CondError(input)
			is ParserError   -> ParserSuccess(Unit, input)
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

infix fun <Input, Output, Output1> GenericParser<Input, Output>.seqLeft(
	other: GenericParser<Input, Output1>
): GenericParser<Input, Output> = (this seq other).map { it.first }

infix fun <Input, Output, Output1> GenericParser<Input, Output>.seqRight(
	other: GenericParser<Input, Output1>
): GenericParser<Input, Output1> = (this seq other).map { it.second }

fun <Input, Output> lookahead(parser: GenericParser<Input, Output>): GenericParser<Input, Unit> =
	GenericParser {
	  when(val r = parser.parse(it)) {
		is ParserSuccess -> ParserSuccess(Unit, it)
		is ParserError -> r.map { }
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