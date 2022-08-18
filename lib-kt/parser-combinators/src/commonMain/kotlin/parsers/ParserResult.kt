package parsers

sealed interface ParserResult<Input, out Output> {
	val column: Column
	val row: Row

	fun <R> map(f: (Output) -> R): ParserResult<Input, R>
	fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserResult<Input, R>
}


data class ParserSuccess<Input, Output>(
	val output: Output,
	val remaining: Input,
	override val column: Column,
	override val row: Row,
) : ParserResult<Input, Output> {
	override fun <R> map(f: (Output) -> R): ParserResult<Input, R> =
		ParserSuccess(f(output), remaining, column, row)

	override fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserResult<Input, R> =
		f(output)
}

sealed interface ParserError<Input, Output> : ParserResult<Input, Output> {
	fun <R> map(): ParserError<Input, R> = map { TODO("UNREACHABLE?") }
	override fun <R> map(f: (Output) -> R): ParserError<Input, R>
	override fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserError<Input, R>
}

data class EndOfInputError<Input, Output>(
	override val column: Column,
	override val row: Row
) : ParserError<Input, Output> {
	override fun <R> map(f: (Output) -> R): ParserError<Input, R> =
		EndOfInputError(column, row)

	override fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserError<Input, R> =
		EndOfInputError(column, row)
}

data class CondError<Input, Output>(
	val actual: Input,
	override val column: Column,
	override val row: Row,
) : ParserError<Input, Output> {
	override fun <R> map(f: (Output) -> R): ParserError<Input, R> =
		CondError(actual, column, row)

	override fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserError<Input, R> =
		CondError(actual, column, row)
}

data class AlternativeError<Input, Output>(
	val errorLeft: ParserError<Input, Output>,
	val errorRight: ParserError<Input, Output>,
	override val column: Column,
	override val row: Row
) : ParserError<Input, Output> {
	override fun <R> map(f: (Output) -> R): ParserError<Input, R> =
		AlternativeError(errorLeft.map(f), errorRight.map(f), column, row)

	override fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserError<Input, R> =
		AlternativeError(errorLeft.bind(f), errorRight.bind(f), column, row)
}

data class SeqError<Input, Output, Output1>(
	val innerError: ParserError<Input, Output1>,
	override val column: Column,
	override val row: Row
) : ParserError<Input, Output> {
	override fun <R> map(f: (Output) -> R): ParserError<Input, R> =
		SeqError(innerError, column, row)

	override fun <R> bind(f: (Output) -> ParserResult<Input, R>): ParserError<Input, R> =
		SeqError(innerError, column, row)
}