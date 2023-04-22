{
  description = "LibreCybernetics Monorepo Flake";

  inputs.devshell.url    = "github:numtide/devshell";
  inputs.flake-utils.url = "github:numtide/flake-utils";

  outputs = { self, nixpkgs, devshell, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;

          overlays = [ devshell.overlays.default ];
        };
      in rec {
        config = {
          env = [{
            name = "JAVA_HOME";
            value = "${pkgs.jdk11.home}";
          }];
          
          packages = with pkgs;[
            bazel_6
            gcc12
            jdk11
            protobuf
          ];
        };

        devShell = pkgs.devshell.mkShell {
          env = config.env;
          packages = config.packages;
        };
      }
    );
}
