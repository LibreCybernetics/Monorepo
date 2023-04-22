load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library", "scala_test")

scala_library(
    name = "toml-core",
    srcs = glob(["src/main/**/*.scala"]),
    deps = [
      "@maven//:org_typelevel_cats_core_3",
    ],
)

scala_test(
    name = "toml-core/test",
    srcs = glob(["src/test/**/*.scala"])
)
