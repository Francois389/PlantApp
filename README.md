# PlantApp

PlantApp est une application desktop en Kotlin et JavaSpringFX pour écrire, prévisualiser et exporter des diagrammes PlantUML.

L'application propose deux onglets principaux :

- un éditeur qui met à jour le diagramme en temps réel à partir du code PlantUML ;
- un écran d'export qui permet de générer un fichier PNG en conservant la source PlantUML dans les métadonnées de l'image.

## Fonctionnalités

- édition de source PlantUML dans une interface JavaFX
- rendu immédiat du diagramme à partir du texte saisi
- détection du titre PlantUML pour nommer le fichier d'export
- export PNG avec intégration de la source PlantUML dans un chunk `tEXt`

<details>
<summary>Screenshots</summary>

TODO

</details>

## Installation

Les installeurs sont disponibles sur la page des [releases GitHub](https://github.com/Francois389/PlantApp/releases/latest). Ils embarquent leur propre environnement d'exécution Java : il n'y a rien d'autre à installer.

PlantApp n'est pas publié dans un gestionnaire de paquets (apt, winget, Homebrew...) : l'installation se fait en téléchargeant le fichier correspondant au système.

### Windows

<details>
<summary>Détails de l'installation Windows</summary>

1. Téléchargez le fichier `PlantApp-<version>.msi` depuis la [dernière release](https://github.com/Francois389/PlantApp/releases/latest).
2. Lancez-le et suivez l'assistant.

L'installeur n'est pas signé : Windows peut afficher un avertissement SmartScreen. Cliquez sur **Informations complémentaires**, puis **Exécuter quand même**.

</details>

### macOS

<details>
<summary>Détails de l'installation macOS</summary>

1. Téléchargez le fichier `PlantApp-<version>.dmg` depuis la [dernière release](https://github.com/Francois389/PlantApp/releases/latest).
2. Ouvrez-le et glissez **PlantApp** dans le dossier **Applications**.

L'application n'est ni signée ni notarisée : au premier lancement, macOS peut la bloquer. Faites un clic droit sur l'application, choisissez **Ouvrir**, puis confirme. Sinon, autorisez-la dans **Réglages Système > Confidentialité et sécurité** en cliquant sur **Ouvrir quand même**.

L'installeur est construit sur Apple Silicon (arm64) : il n'est pas garanti sur les Mac à processeur Intel.

</details>

### Linux

L'installeur et le paquet Nix sont prévus pour l'architecture x86_64 (amd64).

#### Debian, Ubuntu et dérivés

<details>
<summary>Détails de l'installation Debian et dérivés</summary>

1. Téléchargez le fichier `plantapp_<version>_amd64.deb` depuis la [dernière release](https://github.com/Francois389/PlantApp/releases/latest).
2. Installez-le :

```bash
sudo apt install ./plantapp_*_amd64.deb
```

PlantApp apparaît ensuite dans le menu des applications, catégorie **Développement**.

Pour le désinstaller :

```bash
sudo apt remove plantapp
```

</details>

#### Nix et NixOS

<details>
<summary>Détails de l'installation NixOS</summary>

Le dépôt fournit un flake. Pour lancer PlantApp sans l'installer :

```bash
nix run github:Francois389/PlantApp
```

Pour utiliser une version précise, ajoutez le tag de la release :

```bash
nix run github:Francois389/PlantApp/vX.Y.Z
```

Pour l'installer dans le profil utilisateur :

```bash
nix profile install github:Francois389/PlantApp
```

Sur NixOS, ajoute le flake dans les `inputs` de la configuration, puis le paquet dans les paquets système :

```nix
# flake.nix
inputs.plantapp.url = "github:Francois389/PlantApp";

# configuration.nix (inputs doit être passé via specialArgs)
environment.systemPackages = [
  inputs.plantapp.packages.${pkgs.stdenv.hostPlatform.system}.default
];
```

Les flakes doivent être activés (`experimental-features = nix-command flakes`).

</details>

## Stack technique

- Kotlin 2.4.0
- Java 25
- JavaSpringFX 0.2.1
- JavaFX 26
- PlantUML 1.2026.0

## Lancer le projet

Depuis la racine du dépôt :

```bash
./gradlew run
```

L'application ouvre une fenêtre JavaFX intitulée `Plant App` avec deux onglets :

- `Editor` pour saisir la source PlantUML et voir le rendu
- `Export` pour choisir un nom de fichier et un dossier de destination.

## Utilisation

### 1. Éditer un diagramme

Dans l'onglet `Editor`, saisissez un diagramme PlantUML, par exemple :

```text
@startuml
title MonDiagramme
Alice -> Bob: Hello
Bob -> Alice: Hi!
@enduml
```

Le titre est lu depuis la ligne `title` et utilisé comme nom de fichier proposé lors de l'export.

### 2. Exporter le diagramme

Dans l'onglet `Export` :

1. renseignez le nom du fichier
2. choisissez le dossier de destination
3. cliquez sur `Exporter`.

Le bouton `Detecter` recopie automatiquement le titre détecté depuis la source du diagramme courant.

L'export produit un fichier PNG nommé `<nom du fichier>.png`.

## Licence

PlantApp est distribué sous licence [GNU GPL v3](LICENSE) (`GPL-3.0-only`).