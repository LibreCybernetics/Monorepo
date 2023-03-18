package dev.librecybernetics.parser

import scala.language.postfixOps

import cats.data.NonEmptyList
import cats.implicits.*
import cats.parse.Parser
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Assertion

def genericSuccess[A](p: Parser[A])(input: String, expected: A): Assertion =
  val result = p.parse(input): @unchecked
  result match
    case Left(error)        => println(show"$error"); assert(false)
    case Right("", success) => success shouldBe expected
    case Right(missing, _)  => println(show"Didn't parse: $missing"); assert(false)

def genericFailure[A](p: Parser[A])(input: String, messages: String*): Assertion =
  val result = p.parse(input)
  result match
    case Left(error) =>
      val errorMessages = error.expected.toList.map(_.show)
      println(show"Unexpected error messages returned: ${errorMessages.filterNot(messages contains)}")
      println(show"Missing expected error messages: ${messages.filterNot(errorMessages contains)}")
      errorMessages shouldBe messages

    case Right(_, result) =>
      println(show"Expected failure but succeeded: ${result.toString()}")
      assert(false)
