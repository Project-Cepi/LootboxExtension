import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.jetbrains.dokka") version "1.5.30"
    kotlin("plugin.serialization") version "1.4.21"
    `maven-publish`

    // Apply the application plugin to add support for building a jar
    java
}

repositories {
    // Use mavenCentral
    mavenCentral()

    maven(url = "https://jitpack.io")
    maven(url = "https://repo.spongepowered.org/maven")
    maven(url = "https://repo.velocitypowered.com/snapshots/")
}

dependencies {
    // Align versions of all Kotlin components
    compileOnly(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    compileOnly(kotlin("stdlib"))

    // Use the Kotlin reflect library.
    compileOnly(kotlin("reflect"))

    // Compile Minestom into project
    compileOnly("com.github.Minestom:Minestom:748b24b8ef")

    // Get KStom
    compileOnly("com.github.Project-Cepi:KStom:dc289bb91e")

    // import kotlinx serialization
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

    // Add Kepi
    compileOnly("com.github.Project-Cepi:Kepi:28fe5e5e2b")

    // Add support for level rewards
    compileOnly("com.github.Project-Cepi:LevelExtension:cfcbcd8bf7")

    // Add items
    compileOnly("com.github.Project-Cepi:ItemExtension:3962a5d204")

    // Add mobs
    compileOnly("com.github.Project-Cepi:MobExtension:629803c1cd")

    // Add economy
    compileOnly("com.github.Project-Cepi:EconomyExtension:c4bc1b2484")

    // Use the JUpiter test library.
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set("crates-extension")
        mergeServiceFiles()
        minimize()

    }

    test { useJUnitPlatform() }

    build { dependsOn(shadowJar) }

}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "11" }
val compileKotlin: KotlinCompile by tasks

configurations {
    testImplementation {
        extendsFrom(configurations.compileOnly.get())
    }
}