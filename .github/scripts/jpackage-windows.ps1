param(
  [string]$Type,
  [string]$Version
)

$MainClass = "com.fsp.plantapp.PlantApp"

$javafxJars = (
  Get-ChildItem -Recurse "$env:USERPROFILE\.gradle\caches" -Filter "javafx*.jar" |
  Where-Object { $_.Name -notlike "*sources*" } |
  Select-Object -ExpandProperty FullName
) -join ";"

$mainJar = (
  Get-ChildItem "build/jpackage-input/*.jar" |
  Where-Object { $_.Name -notmatch "javafx|controlsfx|plantuml|junit" } |
  Select-Object -First 1
).Name

Write-Host "→ Main JAR : $mainJar"

jpackage `
  --type        $Type `
  --name        PlantApp `
  --app-version $Version `
  --input       build/jpackage-input `
  --main-jar    $mainJar `
  --main-class  $MainClass `
  --module-path $javafxJars `
  --add-modules javafx.controls,javafx.fxml `
  --dest        build/jpackage-output