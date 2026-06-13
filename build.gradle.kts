plugins {
    java
    application
    kotlin("jvm") version "2.4.0"
    kotlin("plugin.spring") version "2.1.20"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.springframework.boot") version "4.0.6"
}

group = "com.fsp"
version = "1.1.1"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io")  }
    mavenLocal()
}

val junitVersion = "6.0.3"


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
    implementation("io.github.francois389:javaspringfx:0.2.1")
    implementation("net.sourceforge.plantuml:plantuml:1.2026.0")
    implementation("com.github.hervegirod:fxsvgimage:1.7.3")
    implementation("com.github.hervegirod:fxsvgimage:1.7.3:cssparser")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-launcher:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
