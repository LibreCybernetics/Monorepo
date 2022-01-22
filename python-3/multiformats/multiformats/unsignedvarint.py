from typing import *

PRACTICAL_MAX_BYTES = 9
PRACTICAL_MAX = 2 ** (7 * PRACTICAL_MAX_BYTES) - 1

UnsignedVarint = NewType("UnsignedVarint", bytes)

def _encode(i: int) -> UnsignedVarint:
    assert(i >= 0)
    if i > 127:
        r = (bytes([(i % 128) + 128]) + encode (i // 128))
    else:
        r = bytes([i])
    return UnsignedVarint(r)

def encode(i: int) -> UnsignedVarint:
    assert(i <= PRACTICAL_MAX)
    return _encode(i)

def _decode(b: List[int]) -> int:
    match b:
        case []:
            raise Exception("Expected at least 1 byte")
        case [c, *t]:
            if c >= 128:
                assert(len(t) > 0)
                return (c - 128) + 128 * _decode(t)
            else:
                assert(len(t) == 0)
                return c

def decode(b: UnsignedVarint) -> int:
    assert(len(b) <= PRACTICAL_MAX_BYTES)
    return _decode(list(b))
