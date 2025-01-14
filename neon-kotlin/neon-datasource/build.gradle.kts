import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.islandstudio"
version = "final"

val kotlinxCoroutinesVersion = "1.9.0"

plugins {
    kotlin("jvm") version "2.0.20" apply true

    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply true
}

repositories {
    mavenLocal()
    mavenCentral()

    maven {
        name = "spigot"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly(project(":neon-shared"))

    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${kotlinxCoroutinesVersion}")
}

kotlin {
    jvmToolchain(17)

    sourceSets {
        main {
            kotlin.srcDir("src/main/kotlin")

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
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}