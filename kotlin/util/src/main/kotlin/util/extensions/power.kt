package util.extensions

fun ULong.pow(exp: Int): ULong =
    if (exp == 0) 1u else {
        if (exp > 0) this * this.pow(exp - 1)
        else this / this.pow(exp + 1)
    }