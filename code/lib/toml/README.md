# TOML library

> Tom's Obvious Minimal Language

References:

- https://github.com/toml-lang/toml/
- https://toml.io

Status: In Development

- toml-test has about 80% compliance por TOMLv1.0.0
- Missing object notation `{ key1 = value1, key2 = value2 }`
- Missing some validations (mainly around keys and strings)

## Getting Started

```scala
libraryDependencies +=
  "dev.librecybernetics.Monorepo" %%% "toml" % "lib-toml-v0.1.0-M1"
```

Reading a TOML file string:

```scala
import dev.librecybernetics.types.TOML
import dev.librecybernetics.parser.readTOML

val toml = readTOML[Either[Parser.Error, *]](string)
```

## License

[LICENSES](../../../LICENSES/)

## Future Develpment

- Deserializer Typeclass
- auto-derivation

