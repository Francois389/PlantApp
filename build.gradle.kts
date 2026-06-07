plugins {
    java
    application
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.springframework.boot") version "3.4.4"
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
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("io.github.francois389:javaspringfx:0.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.junit.platform:junit-platform-launcher:6.0.3")
    implementation("net.sourceforge.plantuml:plantuml:1.2026.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Copy>("copyDependencies") {
    dependsOn("jar")
    from(configurations.runtimeClasspath)
    from(tasks.named("jar"))
    into(layout.buildDirectory.dir("jpackage-input"))
}

tasks.register<Exec>("jpackage") {
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
