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

    fun optional(): GenericParser<Input, Output?> {
        val self = this
        return object : GenericParser<Input, Output?> {
            override fun parse(input: Input): ParserResult<Input, Output?> =
                when(val result = self.parse(input)) {
                    is ParserSuccess ->
                        result
                    is ParserError ->
                        ParserSuccess(null, input)
                }
        }
    }

    infix fun or(other: GenericParser<Input, Output>): GenericParser<Input, Output> {
        val self = this
        return object : GenericParser<Input, Output> {
            override fun parse(input: Input): ParserResult<Input, Output> =
                when(val first = self.parse(input)) {
                    is ParserSuccess -> first
                    is ParserError ->
                        when(val second = other.parse(input)) {
                            is ParserSuccess -> second
                            is ParserError -> AlternativeError(first, second)
                        }
                }
        }
    }

    fun rep(sep: GenericParser<Input, Unit>, max: Int?): GenericParser<Input, List<Output>> {
        val self = this
        return object : GenericParser<Input, List<Output>> {
            override fun parse(input: Input): ParserResult<Input, List<Output>> {
                var done = false
                val acc = mutableListOf<Output>()
                var cont: Input = input

                while(!done) {
                    when(val result = self.parse(cont)) {
                        is ParserSuccess -> {
                            acc.plusAssign(result.output)
                            cont = result.remaining
                            if (max != null && acc.size >= max) done = true
                            when(val resultSep = sep.parse(cont)) {
                                is ParserSuccess -> cont = resultSep.remaining
                                is ParserError -> done = true
                            }
                        }
                        is ParserError -> done = true
                    }
                }

                return ParserSuccess(acc, cont)
            }
        }
    }

    fun rep(sep: GenericParser<Input, Unit>, min: Int, max: Int?): GenericParser<Input, List<Output>> {
        val self = this
        return object : GenericParser<Input, List<Output>> {
            override fun parse(input: Input): ParserResult<Input, List<Output>> =
                when(val result = self.rep(sep, max).parse(input)) {
                    is ParserSuccess -> {
                        if (result.output.size >= min)
                            ParserSuccess(result.output, result.remaining)
                        else self.parse(result.remaining).map { listOf(it) } as ParserError
                    }
                    else -> result
                }
        }
    }

    fun repExactly(sep: GenericParser<Input, Unit>, n: Int): GenericParser<Input, List<Output>> =
        rep(sep, n, n)

    fun rep(max: Int? = null): GenericParser<Input, List<Output>> {
        val self = this
        return object : GenericParser<Input, List<Output>> {
            override fun parse(input: Input): ParserResult<Input, List<Output>> {
                var done = false
                val acc = mutableListOf<Output>()
                var cont: Input = input

                while(!done) {
                    when(val result = self.parse(cont)) {
                        is ParserSuccess -> {
                            acc.plusAssign(result.output)
                            cont = result.remaining
                            if (max != null && acc.size >= max) done = true
                        }
                        is ParserError ->
                            done = true
                    }
                }

                return ParserSuccess(acc, cont)
            }
        }
    }

    fun rep(min: Int, max: Int?): GenericParser<Input, List<Output>> {
        val self = this
        return object : GenericParser<Input, List<Output>> {
            override fun parse(input: Input): ParserResult<Input, List<Output>> =
                when(val result = self.rep(max).parse(input)) {
                    is ParserSuccess -> {
                        if (result.output.size >= min)
                            ParserSuccess(result.output, result.remaining)
                        else self.parse(result.remaining).map { listOf(it) } as ParserError
                    }
                    else -> result
                }
        }
    }

    fun repExactly(n: Int): GenericParser<Input, List<Output>> = rep(n, n)

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