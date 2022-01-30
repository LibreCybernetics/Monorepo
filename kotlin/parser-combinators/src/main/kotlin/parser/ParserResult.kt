package parser

sealed interface ParserResult<Input, out Output> {
    fun <R> map(f: (Output) -> R): ParserResult<Input, R>
    fun <R> flatMap(f: (Output) -> ParserResult<Input, R>): ParserResult<Input, R>
}
data class ParserSuccess<Input, Output>(val output: Output, val remaining: Input) : ParserResult<Input, Output> {
    override fun <R> map(f: (Output) -> R): ParserResult<Input, R> =
        ParserSuccess(f(output), remaining)

    override fun <R> flatMap(f: (Output) -> ParserResult<Input, R>): ParserResult<Input, R> =
        f(output)
}

sealed interface ParserError<Input, Output> : ParserResult<Input, Output>
class EndOfInputError<Input, Output> : ParserError<Input, Output> {
    override fun <R> map(f: (Output) -> R): ParserResult<Input, R> =
        EndOfInputError()
    override fun <R> flatMap(f: (Output) -> ParserResult<Input, R>): ParserResult<Input, R> =
        EndOfInputError()
}
data class CondError<Input, Output>(val actual: Input) : ParserError<Input, Output> {
    override fun <R> map(f: (Output) -> R): ParserResult<Input, R> =
            CondError(actual)
    override fun <R> flatMap(f: (Output) -> ParserResult<Input, R>): ParserResult<Input, R> =
            CondError(actual)
}
data class MatchError<Input, Output>(val expected: Input, val actual: Input) : ParserError<Input, Output> {
    override fun <R> map(f: (Output) -> R): ParserResult<Input, R> =
        MatchError(expected, actual)
    override fun <R> flatMap(f: (Output) -> ParserResult<Input, R>): ParserResult<Input, R> =
        MatchError(expected, actual)
}

data class SequenceError<Input, Output1, Output2>(val error: ParserError<Input, Output1>) : ParserError<Input, Output2> {
    override fun <R> map(f: (Output2) -> R): ParserResult<Input, R> =
            SequenceError(error)
    override fun <R> flatMap(f: (Output2) -> ParserResult<Input, R>): ParserResult<Input, R> =
            SequenceError(error)
}