plugins {
    java
    application
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.fsp"
version = "1.1.1"

repositories {
    mavenCentral()
    mavenLocal()
}

val junitVersion = "5.12.1"


tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("com.fsp.plantapp.PlantApp")
}
kotlin {
    jvmToolchain(21)
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web")
}

val controlsFXVersion = "11.2.3"
val junitLauncherVersion = "6.0.3"
val plantumlVersion = "1.2026.0"
dependencies {
    implementation("org.controlsfx:controlsfx:$controlsFXVersion")
    implementation("net.sourceforge.plantuml:plantuml:$plantumlVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testImplementation("org.junit.platform:junit-platform-launcher:$junitLauncherVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Copy>("copyDependencies") {
    group = "CD"
    description = "Copies dependencies to build files"
    dependsOn("jar")
    from(configurations.runtimeClasspath)
    from(tasks.named("jar"))
    into(layout.buildDirectory.dir("jpackage-input"))
}

tasks.register<Exec>("jpackage") {
    group = "CD"
    description = """
        Créer un installeur pour l'application en utilisant jpackage.
         - Assure que les dépendances sont copiées dans le répertoire d'entrée.
         - Récupère les JARs JavaFX depuis les dépendances résolues pour les inclure dans le module-path.
         - Nettoie le répertoire de sortie avant de générer l'installeur.
    """.trimIndent()
    dependsOn("copyDependencies")

    val buildDir = layout.buildDirectory.get().asFile
    val inputDir = "$buildDir/jpackage-input"
    val outputDir = "$buildDir/jpackage-output"

    // Récupère les JARs JavaFX depuis les dépendances résolues
    val javafxJars = configurations.runtimeClasspath.get()
    .filter { it.name.contains("javafx") }
    .joinToString(":") { it.absolutePath }

    doFirst {
        file(outputDir).deleteRecursively()
        file(outputDir).mkdirs()
    }

    commandLine(
        "jpackage",
        "--type", "app-image",
        "--name", "PlantApp",
        "--input", inputDir,
        "--main-jar", "${project.name}-${project.version}.jar",
        "--main-class", "com.fsp.plantapp.PlantApp",
        "--module-path", javafxJars,
        "--add-modules", "javafx.controls,javafx.fxml",
        "--dest", outputDir
    )
}
