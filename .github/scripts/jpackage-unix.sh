#!/usr/bin/env bash
set -euo pipefail

TYPE="$1"
VERSION="$2"
MAIN_CLASS="com.fsp.plantapp.PlantApp"

# macOS n'accepte que des versions numériques (ex: 1.0.0 ou 1.0.0.1)
# Les suffixes texte (-SNAPSHOT, -beta...) sont remplacés par un segment numérique
if [[ "$TYPE" == "dmg" && "$VERSION" == *-* ]]; then
  SUFFIX="${VERSION#*-}"
  BASE="${VERSION%-*}"
  case "${SUFFIX,,}" in
    snapshot*)  PATCH=1 ;;
    alpha*)     PATCH=2 ;;
    beta*)      PATCH=3 ;;
    rc*)        PATCH=4 ;;
    *)          PATCH=9 ;;
  esac
  VERSION="${BASE}.${PATCH}"
  echo "Version sanitisée pour macOS : $VERSION"
fi

JAVAFX_JARS=$(find ~/.gradle/caches -name "javafx*.jar" | grep -v sources | tr '\n' ':' | sed 's/:$//')
MAIN_JAR=$(ls build/jpackage-input/*.jar | grep -Ev "javafx|controlsfx|plantuml|junit" | head -1 | xargs basename)
echo "Main JAR : $MAIN_JAR"

jpackage \
  --type        "$TYPE" \
  --name        PlantApp \
  --app-version "$VERSION" \
  --input       build/jpackage-input \
  --main-jar    "$MAIN_JAR" \
  --main-class  "$MAIN_CLASS" \
  --module-path "$JAVAFX_JARS" \
  --add-modules javafx.controls,javafx.fxml \
  --dest        build/jpackage-output

# Patch du .deb : ajoute un symlink dans /usr/bin pour que la commande soit dans le PATH
if [[ "$TYPE" == "deb" ]]; then
  DEB_FILE=$(ls build/jpackage-output/*.deb | head -1)
  echo "Patch .deb : $DEB_FILE"

  mkdir -p deb-patch/extract
  dpkg-deb -R "$DEB_FILE" deb-patch/extract
  rm "$DEB_FILE"

  # Symlink dans /usr/bin pour que la commande soit dans le PATH
  mkdir -p deb-patch/extract/usr/bin
  ln -sf /opt/plantapp/bin/PlantApp deb-patch/extract/usr/bin/plantapp

  # Fichier .desktop pour l'intégration dans les menus (KDE, GNOME, etc.)
  mkdir -p deb-patch/extract/usr/share/applications
  cp "$(dirname "$0")/plantapp.desktop" deb-patch/extract/usr/share/applications/plantapp.desktop

  dpkg-deb -b deb-patch/extract "build/jpackage-output/PlantApp_${VERSION}.deb"

  echo "OK : .deb patché"
fi