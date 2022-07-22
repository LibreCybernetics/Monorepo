package parsers

sealed interface ParserResult<Input, out Output> {
	fun <R> map(f: (Output) -> R): ParserResult<Input, R>
	fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserResult<Input, R>
}

data class ParserSuccess<Input, Output>(val output: Output, val remaining: Input) : ParserResult<Input, Output> {
	override fun <R> map(f: (Output) -> R): ParserResult<Input, R> =
		ParserSuccess(f(output), remaining)

	override fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserResult<Input, R> =
		f(output)
}

sealed interface ParserError<Input, Output> : ParserResult<Input, Output> {
	fun <R> map(): ParserError<Input, R> = map { TODO("UNREACHABLE?") }
	override fun <R> map(f: (Output) -> R): ParserError<Input, R>
	override fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserError<Input, R>
}

class EndOfInputError<Input, Output> : ParserError<Input, Output> {
	override fun <R> map(f: (Output) -> R): ParserError<Input, R> =
		EndOfInputError()

	override fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserError<Input, R> =
		EndOfInputError()

	override fun equals(other: Any?): Boolean =
		when(other) {
			is EndOfInputError<*, *> -> true
			else -> super.equals(other)
		}
}

data class CondError<Input, Output>(val actual: Input) : ParserError<Input, Output> {
	override fun <R> map(f: (Output) -> R): ParserError<Input, R> =
		CondError(actual)

	override fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserError<Input, R> =
		CondError(actual)
}

data class MatchError<Input, Output>(val expected: Input, val actual: Input) : ParserError<Input, Output> {
	override fun <R> map(f: (Output) -> R): ParserError<Input, R> =
		MatchError(expected, actual)

	override fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserError<Input, R> =
		MatchError(expected, actual)
}

data class AlternativeError<Input, Output>(
	val errorLeft: ParserError<Input, Output>,
	val errorRight: ParserError<Input, Output>
) : ParserError<Input, Output> {
	override fun <R> map(f: (Output) -> R): ParserError<Input, R> =
		AlternativeError(errorLeft.map(f), errorRight.map(f))

	override fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserError<Input, R> =
		AlternativeError(errorLeft.bind(f), errorRight.bind(f))
}

data class SeqError<Input, Output, Output1>(
	val innerError: ParserError<Input, Output1>
) : ParserError<Input, Output> {
	override fun <R> map(f: (Output) -> R): ParserError<Input, R> =
		SeqError(innerError)
	override fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserError<Input, R> =
		SeqError(innerError)
}