package dev.librecybernetics.parser.toml

import org.openjdk.jmh.annotations.*

@State(Scope.Benchmark)
object Simple:
  val sample: String =
    """nest = [
      |        [
      |                ["a"],
      |                [1, 2, [3]]
      |        ]
      |       ]
    """.stripMargin

class Simple:
  @Benchmark
  def main(): Unit =
    val Right(result) = Toml.toml.parse(Simple.sample) : @unchecked
