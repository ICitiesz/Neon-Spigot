import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.islandstudio"
version = "final"

plugins {
    alias(libs.plugins.kotlin.jvm) apply true
    alias(libs.plugins.kotlin.serialization) apply true
    alias(libs.plugins.google.devtools.ksp) apply true
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    /* Neon Library */
    compileOnly(libs.neon.shared)
    compileOnly(libs.neon.api.rework)

    /* Core Language Library */
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.kotlinx.coroutines.core.jvm)
    compileOnly(libs.kotlinx.serialization.json.jvm)

    /* Function Library */
    compileOnly(libs.koin.core.jvm)
    compileOnly(libs.koin.annotations.jvm)
    ksp(libs.koin.ksp.compiler)
    compileOnly(libs.modelmapper)

    /* Database Library */
    implementation(libs.hikari.cp)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
    implementation(libs.mariadb.java.client)
    implementation(libs.mariadb4j.core)
    implementation(libs.mariadb4j.exce)
    compileOnly(libs.mariadb4j.winx64)
    compileOnly(libs.mariadb4j.linux64)
    compileOnly(libs.mariadb4j.macosarm64)
}

kotlin {
    jvmToolchain(17)

    sourceSets {
        main {
            kotlin.srcDir("src/main/kotlin")

            resources.srcDir("src/main/resources")
            resources.exclude("**")

            dependencies {
                api(libs.koin.annotations.jvm)
            }
        }
    }
}

tasks.processResources {
    from("src/main/resources") {
        exclude("application/*.template")
        exclude("META-INF")
        into("resources/")
    }

    from("src/main/resources/META-INF") {
        into("META-INF")
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_4)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_4)
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}