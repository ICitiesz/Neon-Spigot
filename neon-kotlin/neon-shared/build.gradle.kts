import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.islandstudio"
version = "final"

val kotlinxCoroutinesVersion = "1.9.0"

plugins {
    alias(libs.plugins.kotlin.jvm) apply true
    alias(libs.plugins.kotlin.serialization) apply true
    alias(libs.plugins.kotlin.noarg) apply true
    alias(libs.plugins.google.devtools.ksp) apply true
}

repositories {
    mavenLocal()
    mavenCentral()

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    /* Core Language Library */
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core.jvm)
    implementation(libs.kotlinx.serilazation.json.jvm)

    /* Server API Reference Library */
    compileOnly(libs.papermc.api.base)
    compileOnly(libs.papermc.dev.bundle.target) // PaperMC Mojang Mappings

    /* Function Library */
    implementation("com.squareup.okhttp3:okhttp:5.4.0")
    compileOnly(libs.ulid.creator)
    compileOnly(libs.modelmapper)
    compileOnly(libs.koin.core.jvm)
    compileOnly(libs.koin.annotations.jvm)
    ksp(libs.koin.ksp.compiler)
    compileOnly(libs.ktoml.core.jvm)
    compileOnly(libs.ktoml.file.jvm)
    compileOnly(libs.dotenv.kotlin)
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

noArg {
    annotation("com.islandstudio.neon.shared.annotation.NoArgConstructor")
}

tasks.processResources {
    from("src/main/resources") {
        exclude("application/*.template")
        into("resources/")
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_4)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_4)
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}