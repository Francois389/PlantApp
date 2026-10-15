{
  description = "PlantApp - éditeur PlantUML en JavaFX";
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { self, nixpkgs }:
    let
      system = "x86_64-linux";
      pkgs = import nixpkgs {
        inherit system;
        overlays = [ self.overlays.default ];
      };
    in {
      overlays.default = final: prev: {
        plantapp = final.callPackage ./package.nix { };
      };

      packages.${system} = {
        plantapp = pkgs.plantapp;
        default = pkgs.plantapp;
      };

      apps.${system}.default = {
        type = "app";
        program = "${pkgs.plantapp}/bin/plantapp";
      };
    };
}