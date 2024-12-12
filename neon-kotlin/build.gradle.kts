
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.islandstudio"
version = "final"

val kotlinxCoroutinesVersion = "1.9.0"
val pluginFinalJarName = "neon-kotlin.jar"
val pluginShadedjarName = "neon-kotlin-shaded.jar"

plugins {
    kotlin("jvm") version "2.0.20" apply true
    id("com.gradleup.shadow") version "8.3.5" apply true
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

    maven {
        name = "jitpack.io"
        url = uri("https://jitpack.io/")
    }
}

dependencies {
    val jooqVersion = "3.19.15"
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
    implementation("me.carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    implementation("org.dhatim:fastexcel-reader:0.18.4")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.18.2")

    /* Database Library */
    api("org.modelmapper:modelmapper:3.2.1")
    implementation("org.hsqldb:hsqldb:2.7.3")
    implementation("org.jooq:jooq:$jooqVersion")
    compileOnly("org.jooq:jooq-meta:$jooqVersion")
    compileOnly("org.jooq:jooq-meta-extensions:$jooqVersion")
    compileOnly("org.jooq:jooq-codegen:$jooqVersion")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.google.guava:guava:33.2.1-jre")
    runtimeOnly("org.liquibase:liquibase-core:4.30.0")
}

kotlin {
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

tasks.named<ShadowJar>("shadowJar") {
    this.archiveFileName.set(pluginShadedjarName)

//    minimize {
//        exclude(dependency("org.hsqldb:hsqldb*"))
//        exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
//        exclude("*.kotlin_module")
//    }

    //finalizedBy("jar")
}


tasks.named<Jar>("jar") {
    //dependsOn(tasks.named<ShadowJar>("shadowJar"))

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    archiveFileName.set(pluginFinalJarName)

    from(file("${project.projectDir}/../neon-database-server/build/libs/neon-database-server.jar")) {
        into("resources/extensions/")
    }

    from(zipTree(file("${layout.buildDirectory.get()}/libs/${pluginShadedjarName}")))
}






