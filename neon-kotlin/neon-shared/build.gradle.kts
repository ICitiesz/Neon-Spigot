import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.islandstudio"
version = "final"

val kotlinxCoroutinesVersion = "1.9.0"
val pluginFinalJarName = "neon-kotlin.jar"
val pluginShadedjarName = "neon-kotlin-shaded.jar"

plugins {
    kotlin("jvm") version "2.0.20" apply true
    kotlin("plugin.serialization") version "2.0.20" apply true
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply true
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    val koinAnnotationsVersion = "2.0.0-Beta1"

    /* Core Language Library */
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${kotlinxCoroutinesVersion}")

    /* Server API Reference Library */
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT:shaded")
    compileOnly("org.spigotmc:spigot:1.20.4-R0.1-SNAPSHOT:remapped-mojang")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    /* Function Library */
    implementation("io.insert-koin:koin-core-jvm:4.0.0")
    implementation("io.insert-koin:koin-annotations-jvm:$koinAnnotationsVersion")
    ksp("io.insert-koin:koin-ksp-compiler:$koinAnnotationsVersion")
    implementation("com.akuleshov7:ktoml-core:0.5.2")
    implementation("com.akuleshov7:ktoml-file-jvm:0.5.2")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
}

kotlin {
    jvmToolchain(17)

    sourceSets {
        main {
            kotlin.srcDir("src/main/")

            resources.srcDir("src/main/resources")
            resources.exclude("**")

            dependencies {
                api("io.insert-koin:koin-annotations-jvm:2.0.0-Beta1")
            }
        }
    }
}

tasks.processResources {
    from("src/main/resources") {
        exclude("application/*.template")
        into("resources/")
    }

    from(file("src/main/plugin.yml"))
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}