import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.5.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.jetbrains.dokka") version "1.4.32"
    kotlin("plugin.serialization") version "1.4.21"
    `maven-publish`

    // Apply the application plugin to add support for building a jar
    java
}

repositories {
    // Use jcenter for resolving dependencies.
    jcenter()

    // Use mavenCentral
    maven(url = "https://jitpack.io")
    maven(url = "https://repo1.maven.org/maven2/")
    maven(url = "https://repo.spongepowered.org/maven")
    maven(url = "https://libraries.minecraft.net")
    maven(url = "https://jcenter.bintray.com/")
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
    compileOnly("com.github.Project-Cepi:Minestom:5ddda986a7")

    // Get KStom
    compileOnly("com.github.Project-Cepi:KStom:ea3624d804")

    // import kotlinx serialization
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")

    // Add Kepi
    compileOnly("com.github.Project-Cepi:Kepi:12349e0470")

    // Add support for level rewards
    compileOnly("com.github.Project-Cepi:LevelExtension:cfcbcd8bf7")

    // Add items
    compileOnly("com.github.Project-Cepi:ItemExtension:1792f5aab7")

    // Add mobs
    compileOnly("com.github.Project-Cepi:MobExtension:2f477c2d75")

    // Add economy
    compileOnly("com.github.Project-Cepi:EconomyExtension:c4bc1b2484")
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions.jvmTarget = "11" }
val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
