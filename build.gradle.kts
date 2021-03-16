plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("plugin.serialization") version "1.4.21"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    maven

    // Apply the application plugin to add support for building a jar
    java
    id("org.jetbrains.dokka") version "1.4.30"
    kotlin("jvm") version "1.4.21-2"
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
}

dependencies {
    // Align versions of all Kotlin components
    compileOnly(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    compileOnly(kotlin("stdlib"))

    // Use the Kotlin reflect library.
    compileOnly(kotlin("reflect"))

    // Compile Minestom into project
    compileOnly("com.github.Minestom:Minestom:c960bb297b")

    // implement KStom
    compileOnly("com.github.Project-Cepi:KStom:eeca6b96b8")

    // import kotlinx serialization
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

    // Add support for level rewards
    compileOnly("com.github.Project-Cepi:LevelExtension:cfcbcd8bf7")

    // Add items
    compileOnly("com.github.Project-Cepi:ItemExtension:0c931a7440")

    // Add mobs
    compileOnly("com.github.Project-Cepi:MobExtension:bae2f225d4")
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
