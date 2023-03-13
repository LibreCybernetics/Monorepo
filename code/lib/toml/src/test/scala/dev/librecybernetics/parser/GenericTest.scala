package dev.librecybernetics.parser

import cats.implicits.*
import cats.parse.Parser
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Assertion

def genericTest[A](p: Parser[A])(input: String, expected: A): Assertion =
  val result = p.parse(input): @unchecked
  result match
    case Left(error)        => println(show"$error"); assert(false)
    case Right("", success) => success shouldBe expected
    case Right(missing, _)  => println(show"Didn't parse: $missing"); assert(false)
