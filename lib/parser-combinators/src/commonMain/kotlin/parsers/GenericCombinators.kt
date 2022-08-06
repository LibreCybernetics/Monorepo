package parsers

infix fun <Input, Output> GenericParser<Input, Output>.or(
	other: GenericParser<Input, Output>
): GenericParser<Input, Output> {
	val self = this

	return object : GenericParser<Input, Output> {
		override fun parse(input: Input, column: Column, row: Row): ParserResult<Input, Output> {
			return when (val first = self.parse(input, column, row)) {
				is ParserSuccess -> first
				is ParserError -> when (val second = other.parse(input, column, row)) {
					is ParserSuccess -> second
					is ParserError -> {
						check(column == first.column)
						check(first.column == second.column)
						check(row == first.row)
						check(first.row == second.row)

						AlternativeError(first, second, column, row)
					}
				}
			}
		}
	}
}

infix fun <Input, Output, Output1> GenericParser<Input, Output>.seq(
	other: GenericParser<Input, Output1>
): GenericParser<Input, Pair<Output, Output1>> {
	val self = this

	return object : GenericParser<Input, Pair<Output, Output1>> {
		override fun parse(input: Input, column: Column, row: Row): ParserResult<Input, Pair<Output, Output1>> {
			return when (val first = self.parse(input, column, row)) {
				is ParserError -> first.map()
				is ParserSuccess -> when (val second = other.parse(first.remaining, first.column, first.row)) {
					is ParserError -> SeqError(second, column, row)
					is ParserSuccess -> ParserSuccess(
						Pair(first.output, second.output),
						second.remaining,
						second.column,
						second.row
					)
				}
			}
		}
	}
}

infix fun <Input, Output, Output1> GenericParser<Input, Output>.seqLeft(
	other: GenericParser<Input, Output1>
): GenericParser<Input, Output> = (this seq other).map { it.first }

infix fun <Input, Output, Output1> GenericParser<Input, Output>.seqRight(
	other: GenericParser<Input, Output1>
): GenericParser<Input, Output1> = (this seq other).map { it.second }