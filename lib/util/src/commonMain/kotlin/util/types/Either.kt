package util.types

sealed interface Either<Left, Right>
data class Left<Left, Right>(val left: Left) : Either<Left, Right>
data class Right<Left, Right>(val right: Right) : Either<Left, Right>