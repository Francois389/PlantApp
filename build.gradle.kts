plugins {
    java
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.javafx)
    alias(libs.plugins.spring.boot)
}

group = "com.fsp"
version = "1.1.1"

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