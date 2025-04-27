{
    inputs = {
        nixpkgs.url = "github:nixos/nixpkgs/nixpkgs-unstable";

        devshell.url = "github:numtide/devshell";
        flake-parts.url = "github:hercules-ci/flake-parts";
        sbt = {
            url = "github:zaninime/sbt-derivation";
            inputs.nixpkgs.follows = "nixpkgs";
        };
    };

    outputs = inputs@{ flake-parts, sbt, ... }:
        flake-parts.lib.mkFlake { inherit inputs; } (top@{ ... }: {
            imports = [
                inputs.devshell.flakeModule
            ];

            systems = [ "x86_64-linux" ];

            perSystem = { pkgs, ... }: {
                devshells.default = {
                    packages = [
                        pkgs.sbt
                        pkgs.scalafmt
                    ];
                };

                packages.money-package = sbt.lib.mkSbtDerivation {
                    inherit pkgs;
                    pname = "money";
                };
            };
        });
}