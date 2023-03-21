# Summary

Dependencies:

 - `cats.MonadError` (from `cats`)
 - `cats.parse.Parser.Error` (from `cats-parse`)

Public APIs:

- `dev.librecybernetics.parser.readTOML`
  Signature: `[F[_]: MonadError[_, Parser.Error]](input: String): F[TOML]`

Example usage: `readTOML[Either[Parser.Error, TOML]]("key = value # TOML String")`

# Supported Platforms

Scala Version: 3.2.2
JDK Versions: 11, 17
Scala.JS: 1.13.0
Scala Native: 0.4.11

# Performance

With a simple example of about 70 lines measured with JMH:

```
[info] Benchmark            Mode  Cnt     Score     Error  Units
[info] Simple.main         thrpt   25  6925.654 ± 144.693  ops/s
[success] Total time: 510 s (08:30), completed Mar 20, 2023, 7:25:00 PM
```