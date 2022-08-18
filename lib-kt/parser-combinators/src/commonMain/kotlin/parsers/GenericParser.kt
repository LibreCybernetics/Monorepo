package parsers

import parsers.string.StringParser

interface GenericParser<Input, Output> {
	fun parse(input: Input, column: Column = Column(1u), row: Row = Row(1u)): ParserResult<Input, Output>
}

fun <Input, Output, Output1> GenericParser<Input, Output>.map(
	f: (Output) -> Output1
): GenericParser<Input, Output1> {
	val self = this

	return object : GenericParser<Input, Output1> {
		override fun parse(input: Input, column: Column, row: Row): ParserResult<Input, Output1> {
			return self.parse(input, column, row).map(f)
		}
	}
}

fun <Input, Output, Output1> GenericParser<Input, Output>.bind(
	f: (Output) -> GenericParser<Input, Output1>
): GenericParser<Input, Output1> {
	val self = this

	return object : GenericParser<Input, Output1> {
		override fun parse(input: Input, column: Column, row: Row): ParserResult<Input, Output1> {
			return when (val r = self.parse(input, column, row).map(f)) {
				is ParserSuccess -> r.output.parse(r.remaining, r.column, r.row)
				is ParserError -> r.map()
			}
		}
	}
}

fun <Input> position(): GenericParser<Input, Pair<Row, Column>> = object : GenericParser<Input, Pair<Row, Column>> {
	override fun parse(input: Input, column: Column, row: Row): ParserResult<Input, Pair<Row, Column>> =
		ParserSuccess(Pair(row, column), input, column, row)
}

fun <Input, R> pass(r: R): GenericParser<Input, R> = object : GenericParser<Input, R> {
	override fun parse(input: Input, column: Column, row: Row): ParserResult<Input, R> =
		ParserSuccess(r, input, column, row)
}

val unit: StringParser<Unit> = pass(Unit)

fun <Input, Output> GenericParser<Input, Output>.unit(): GenericParser<Input, Unit> =
	this.map { }

fun <Input, Output> GenericParser<Input, Output>.not(): GenericParser<Input, Unit> {
	val self = this

	return object : GenericParser<Input, Unit> {
		override fun parse(input: Input, column: Column, row: Row): ParserResult<Input, Unit> {
			return when (val r = self.parse(input, column, row)) {
				is ParserSuccess -> CondError(input, r.column, r.row)
				is ParserError -> ParserSuccess(Unit, input, r.column, r.row)
			}
		}
	}
}