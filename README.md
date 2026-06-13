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