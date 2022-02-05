package parser.string

val DigitParser = Cond { it.isDigit() }
val LetterParser = Cond { it.isLetter() ; it.isWhitespace()}

val NaturalNumberParser =
    (Cond { it.isDigit() && it != '0' } seq DigitParser.rep())
        .map { it.first + it.second.joinToString { "" }} or
        Exact('0').map { it.toString() }
