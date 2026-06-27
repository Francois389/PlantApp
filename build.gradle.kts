plugins {
    java
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.javafx)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.badass.runtime)
}

fun getLastGitTag(): String {
    return Runtime.getRuntime()
        .exec(arrayOf("git", "tag", "--sort=-version:refname"))
        .inputStream
        .bufferedReader()
        .readLine()
        ?.removePrefix("v")
        ?: System.getenv("PLANTAPP_VERSION")
        ?: throw RuntimeException("No git tags found")
}
version = getLastGitTag()
group = "com.fsp"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    mavenLocal()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("com.fsp.plantapp.Launcher")
}

kotlin {
    jvmToolchain(25)
}

javafx {
    version = "26"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(libs.javaspringfx)
    implementation(libs.plantuml)
    implementation(libs.fxsvgimage)
    implementation("com.github.hervegirod:fxsvgimage:1.7.3:cssparser")

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named("jpackageImage") {
    dependsOn("runtime")
}

runtime {
    imageZip.set(project.file("${project.layout.buildDirectory.get()}/image.zip"))

    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))

    // Modules nécessaires pour Spring Boot + JavaFX
    modules.set(
        listOf(
            "java.base", "java.desktop", "java.logging", "java.management",
            "java.naming", "java.net.http", "java.sql", "java.xml",
            "jdk.unsupported", "jdk.crypto.ec"
        )
    )

    jpackage {
        imageName = "PlantApp"
        skipInstaller = false
        installerName = "PlantApp"
        appVersion = project.version.toString()

        // vendor = "fsp"
        // Icône (chemins différents selon l'OS)
        imageOptions = listOf("--icon", "src/main/resources/PlantApp_Logo.png")

        targetPlatformName = "current" // build pour l'OS courant

        installerOptions = listOf(
            "--description", "PlantUML diagram editor",
            "--vendor", "fsp",
            "--linux-shortcut",
            "--linux-package-name", "plantapp",
            "--linux-app-category", "Development",
            "--resource-dir", "packaging/linux",
        )
    }
}