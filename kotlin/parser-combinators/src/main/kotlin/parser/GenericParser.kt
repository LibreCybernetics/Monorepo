package parser

interface GenericParser<Input, Output> {
    // Main function to implement low level parsers
    fun parse(input: Input): ParserResult<Input, Output>

    // Util functions

    fun <R> map(f: (Output) -> R): GenericParser<Input, R> {
        val self = this
        return object : GenericParser<Input, R> {
            override fun parse(input: Input): ParserResult<Input, R> = self.parse(input).map(f)
        }
    }

    fun <R> flatMap(f: (Output) -> GenericParser<Input, R>): GenericParser<Input, R> {
        val self = this
        return object : GenericParser<Input, R> {
            override fun parse(input: Input): ParserResult<Input, R> =
                when(val result = self.parse(input)) {
                    is ParserSuccess ->
                        f(result.output).parse(result.remaining)
                    is ParserError ->
                        SequenceError(result)
                }
        }
    }

    // Util combinators

    infix fun or(other: GenericParser<Input, Output>): GenericParser<Input, Output> {
        val self = this
        return object : GenericParser<Input, Output> {
            override fun parse(input: Input): ParserResult<Input, Output> =
                when(val result = self.parse(input)) {
                    is ParserSuccess ->
                        result
                    is ParserError ->
                        other.parse(input)
                }
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

    fun repExactly(n: UInt): GenericParser<Input, List<Output>> {
        val self = this
        return object : GenericParser<Input, List<Output>> {
            override fun parse(input: Input): ParserResult<Input, List<Output>> =
                if (n == 0u) {
                    ParserSuccess(listOf(), input)
                } else {
                    when(val result = self.parse(input)) {
                        is ParserSuccess ->
                            self.repExactly(n - 1u).parse(result.remaining).map { listOf(result.output) + it }
                        is ParserError ->
                            CondError(input)
                    }
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