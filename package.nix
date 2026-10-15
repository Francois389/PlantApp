{ lib, stdenv, gradle_9, jdk25, makeWrapper
, libGL, gtk3, glib, libX11, libXtst, libXxf86vm }:

stdenv.mkDerivation (finalAttrs: {
  pname = "plantapp";
  version = "0.1.0"; # à synchroniser avec ton dernier tag

  src = lib.cleanSource ./.;

  nativeBuildInputs = [ gradle_9 makeWrapper ];

  mitmCache = gradle_9.fetchDeps {
    inherit (finalAttrs) pname;
    data = ./deps.json;
  };

  env.PLANTAPP_VERSION = finalAttrs.version;
  gradleFlags = [ "-Dorg.gradle.java.home=${jdk25}" ];
  gradleBuildTask = "installDist";

  installPhase = ''
    runHook preInstall
    mkdir -p $out/share/plantapp
    cp -r build/install/*/* $out/share/plantapp/
    makeWrapper $out/share/plantapp/bin/plantapp $out/bin/plantapp \
      --set JAVA_HOME ${jdk25} \
      --prefix LD_LIBRARY_PATH : ${lib.makeLibraryPath [ libGL gtk3 glib libX11 libXtst libXxf86vm ]}
    runHook postInstall
  '';

  meta = {
    description = "Éditeur et exporteur de diagrammes PlantUML (JavaFX)";
    mainProgram = "plantapp";
    license = lib.licenses.mit; # à remplacer par la vraie licence
    platforms = lib.platforms.linux;
  };
})