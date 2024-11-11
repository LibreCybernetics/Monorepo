{ pkgs, lib, config, inputs, ... }:

{
  languages = {
    deno.enable = true;
    javascript = {
      enable = true;
      bun.enable = true;
    };
    typescript.enable = true;
  };
}
