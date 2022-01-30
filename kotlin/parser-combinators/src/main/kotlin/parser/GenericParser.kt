package parser

interface GenericParser<Input, Output> {
    fun parse(input: Input): ParserResult<Input, Output>

    fun <R> map(f: (Output) -> R): GenericParser<Input, R> {
        val self = this
        return object : GenericParser<Input, R> {
            override fun parse(input: Input): ParserResult<Input, R> = self.parse(input).map(f)
        }
    }

    fun rep(): GenericParser<Input, List<Output>> {
        val self = this
        return object : GenericParser<Input, List<Output>> {
            override fun parse(input: Input): ParserResult<Input, List<Output>> =
                when(val result = self.parse(input)) {
                    is ParserSuccess ->
                        this.parse(result.remaining).map { listOf(result.output) + it }
                    is ParserError ->
                        ParserSuccess(listOf(), input)
                }
        }
    }

    infix fun <Output2> seq(second: GenericParser<Input, Output2>): GenericParser<Input, Pair<Output, Output2>> {
        val self = this
        return object : GenericParser<Input, Pair<Output, Output2>> {
            override fun parse(input: Input): ParserResult<Input, Pair<Output, Output2>> =
                when(val result = self.parse(input)) {
                    is ParserSuccess ->
                        second.parse(result.remaining).map { result.output to it }
                    is ParserError ->
                        SequenceError(result)
                }
        }
    }
}