package parser.string

val DigitParser = Cond { it.isDigit() }
val LetterParser = Cond { it.isLetter() }
val WhitespaceParser = Cond { it.isWhitespace() }

val NaturalParser =
    (Cond { it.isDigit() && it != '0' } seq DigitParser.rep())
        .map { (listOf(it.first) + it.second).toCharArray().concatToString() } or
        Exact('0').map { it.toString() }

val IntegerParser = (Exact('-').optional() seq NaturalParser).map {
    if (it.first == null)
        it.second
    else
        "${it.first}${it.second}"
}

val WordParser = LetterParser.rep()