import csv, random, sys
import pytest
from base64 import b64decode

sys.path.append("multiformats")

from unsignedvarint import *

def test_examples():
    with open("../../spec/multiformats/unsigned-varint-examples.csv") as examples:
        for [input, output] in csv.reader(examples):
            assert(encode(int(input)) == b64decode(output))
            assert(decode(encode(int(input))) == int(input))

def test_random():
    for example in [random.randint(0, PRACTICAL_MAX) for i in range(1000)]:
        assert(decode(encode(example)) == example)

def test_errors():
    # Empty bytes can't be decoded
    with pytest.raises(Exception):
        decode(bytes([]))

    # Negative numbers don't get encoded
    with pytest.raises(AssertionError):
        encode(-1)

    # Leading nulls error
    with pytest.raises(AssertionError):
        decode(bytes([0, 0, 0]))

    # PRACTICAL_MAX_BYTES is tipping point:
    decode(bytes([255, 255, 255, 255, 255, 255, 255, 255, 127]))
    with pytest.raises(AssertionError):
        decode(bytes([255, 255, 255, 255, 255, 255, 255, 255, 255, 1]))

    # PRACTICAL_MAX is tipping point:
    encode(PRACTICAL_MAX)
    with pytest.raises(AssertionError):
        encode(PRACTICAL_MAX + 1)
