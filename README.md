# PlantApp

PlantApp est une application desktop en Kotlin et JavaFX pour écrire, prévisualiser et exporter des diagrammes PlantUML.

L'application propose deux onglets principaux :

- un éditeur qui met à jour le diagramme en temps réel à partir du code PlantUML ;
- un écran d'export qui permet de générer un fichier PNG en conservant la source PlantUML dans les métadonnées de l'
  image.

## Fonctionnalités

- édition de source PlantUML dans une interface JavaFX
- rendu immédiat du diagramme à partir du texte saisi
- détection du titre PlantUML pour nommer le fichier d'export
- export PNG avec intégration de la source PlantUML dans un chunk `tEXt`

<details>
<summary>Screenshots</summary>

![Main](./screenshot/Main.png "Main")
![Main avec un titre](./screenshot/Main-Title.png "Main avec un titre")
![Main avec un plus grand diagramme](./screenshot/Main-BiggerDiagram.png "Main avec un plus grand diagramme")
![Export](./screenshot/Export.png "Export")
![Export reussie](./screenshot/Export-Reussi.png "Export réussi")
![Export echec](./screenshot/Export-Echec.png "Export échec")

</details>

## Stack technique

- Kotlin 2.1.20
- Java 21
- JavaFX 21.0.6
- PlantUML 1.2026.0

## Prérequis

- JDK 21 installé ;

Le projet est configuré avec `jvmToolchain(21)`. En pratique, il vaut mieux exécuter Gradle avec une JDK 21 active. Sur
un environnement où la JDK par défaut est plus récente, certaines tâches Gradle peuvent échouer au démarrage.

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

L'export produit un fichier PNG nommé `nomDuFichier.png`.

## Structure du projet

```text
src/
  main/
    kotlin/com/fsp/plantapp/
      PlantApp.kt                Point d'entrée JavaFX
      Navigator.kt               Navigation et changement d'écran
      main/MainView.kt           Onglets principaux
      editor/                    Éditeur PlantUML
      export/                    Écran d'export PNG
      diagram/                   Modèle, rendu et service métier
  test/
    kotlin/com/fsp/plantapp/     Tests unitaires
```

## Architecture

- `PlantApp` initialise l'application, le service de diagramme et les écrans.
- `DiagramService` centralise l'état du diagramme courant et notifie les vues.
- `PlantUMLDiagram` encapsule la source, le titre et l'image rendue.
- `EditorViewModel` met à jour le rendu à chaque modification du texte.
- `ExportViewModel` valide les champs et gère l'écriture du PNG exporté.

## Points d'attention

- l'export refuse d'écraser un fichier existant