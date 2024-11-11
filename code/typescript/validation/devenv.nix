{ pkgs, lib, config, inputs, ... }:

{
  languages = {
    deno.enable = true;
    javascript = {
      enable = true;
      bun.enable = true;
      npm.enable = true;
    };
    typescript.enable = true;
  };

  packages = with pkgs; [
    eslint
  ];
}
