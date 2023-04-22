load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

#
# External Dependencies
#

bazel_skylib_version = "1.4.1"
bazel_skylib_hash    = "060426b186670beede4104095324a72bd7494d8b4e785bf0d84a612978285908"

http_archive(
    name = "bazel_skylib",
    strip_prefix = "bazel-skylib-%s" % bazel_skylib_version,
    url = "https://github.com/bazelbuild/bazel-skylib/archive/refs/tags/%s.tar.gz" % bazel_skylib_version,
    sha256 = bazel_skylib_hash,
)

# proto (Protocol Buffers)

rules_proto_version = "4.0.0"
rules_proto_hash = "66bfdf8782796239d3875d37e7de19b1d94301e8972b3cbd2446b332429b4df1"

http_archive(
    name = "rules_proto",
    strip_prefix = "rules_proto-%s" % rules_proto_version,
    url = "https://github.com/bazelbuild/rules_proto/archive/%s.tar.gz" % rules_proto_version,
    sha256 = rules_proto_hash,
    # Reference: https://github.com/tweag/rules_nixpkgs/issues/262
    patch_args = ["-p1"],
    patches = ["@//:patches/protoc_nixos.patch"],
)

# bazelbuilds/rules_java

rules_java_version = "5.4.0"
rules_java_hash    = "f90111a597b2aa77b7104dbdc685fd35ea0cca3b7c3f807153765e22319cbd88"

http_archive(
    name = "rules_java",
    strip_prefix = "rules_java-%s" % rules_java_version,
    url = "https://github.com/bazelbuild/rules_java/archive/refs/tags/%s.tar.gz" % rules_java_version,
    sha256 = rules_java_hash,
)

# bazelbuilds/rules_scala

rules_scala_version = "394c0aaf585652e75d475f49dcba1b33095f3446"
rules_scala_hash    = "b548d60ac29dab6b7cce4086bc61fc0d2815a3d7c9dbbf04c41115a203ef2a84"
    
http_archive(
    name = "io_bazel_rules_scala",
    strip_prefix = "rules_scala-%s" % rules_scala_version,
    url = "https://github.com/bazelbuild/rules_scala/archive/%s.tar.gz" % rules_scala_version,
    sha256 = rules_scala_hash,
)

# tweag/rules_nixpkgs

rules_nixpkgs_version = "4dddbafba508cd2dffd95b8562cab91c9336fe36"
rules_nixpkgs_hash    = "cb1030a6134f625e2d30d2a34dcfe7960157ae21ec8f20c2b1adb0665f789f50"

http_archive(
    name = "io_tweag_rules_nixpkgs",
    strip_prefix = "rules_nixpkgs-%s" % rules_nixpkgs_version,
    url = "https://github.com/tweag/rules_nixpkgs/archive/%s.tar.gz" % rules_nixpkgs_version,
    sha256 = rules_nixpkgs_hash,
)

#
# Usage
#

# Base ProtoC

load("@rules_proto//proto:repositories.bzl", "rules_proto_dependencies", "rules_proto_toolchains")

rules_proto_dependencies()
rules_proto_toolchains()

# Base Java

load("@rules_java//java:repositories.bzl", "rules_java_dependencies", "rules_java_toolchains")

rules_java_dependencies()

# Nixpkgs

load("@io_tweag_rules_nixpkgs//nixpkgs:repositories.bzl", "rules_nixpkgs_dependencies")

rules_nixpkgs_dependencies()

# Nix Flakes

load("@io_tweag_rules_nixpkgs//nixpkgs:nixpkgs.bzl", "nixpkgs_local_repository")

nixpkgs_local_repository(
    name = "nixpkgs",
    nix_flake_lock_file = "//:flake.lock",
    nix_file_deps = ["//:flake.lock"],
)

# Nix + Java Toolchain

load("@io_tweag_rules_nixpkgs//nixpkgs:nixpkgs.bzl", "nixpkgs_java_configure")

nixpkgs_java_configure(
    repository = "@nixpkgs",
    attribute_path = "jdk11.home",
    toolchain = True,
    toolchain_name = "nixpkgs_java",
    toolchain_version = "11",
)

rules_java_toolchains()

#
# Scala
#
# Reference: https://github.com/bazelbuild/rules_scala/tree/v5.0.0#getting-started

load("@io_bazel_rules_scala//:scala_config.bzl", "scala_config")

scala_config(scala_version = "3.2.1")

load("@io_bazel_rules_scala//scala:scala.bzl", "rules_scala_setup", "rules_scala_toolchain_deps_repositories")

rules_scala_setup()
rules_scala_toolchain_deps_repositories(fetch_sources = True)

load("@io_bazel_rules_scala//scala:toolchains.bzl", "scala_register_toolchains")

scala_register_toolchains()

load("@io_bazel_rules_scala//testing:scalatest.bzl", "scalatest_repositories", "scalatest_toolchain")

scalatest_repositories()
scalatest_toolchain()

