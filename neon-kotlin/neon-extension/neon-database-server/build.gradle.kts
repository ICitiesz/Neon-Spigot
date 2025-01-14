
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.islandstudio"
version = "final"

val kotlinxCoroutinesVersion = "1.9.0"
val pluginFinalJarName = "neon-database-server.jar"

plugins {
    kotlin("jvm") version "2.0.20" apply true
    kotlin("plugin.serialization") version "2.0.20" apply true
    java apply true
    id("com.gradleup.shadow") version "8.3.5" apply true
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply true
    id("org.jooq.jooq-codegen-gradle") version "3.19.15" apply true
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
    val jooqVersion = "3.19.15"
    val koinAnnotationsVersion = "2.0.0-Beta1"

    /* Misc */
    implementation("com.islandstudio:neon-shared")
    implementation(files("build/classes/java/main"))
    jooqCodegen(project)

    /* Core Language Library */
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${kotlinxCoroutinesVersion}")

    /* Server API Reference Library */
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT:shaded")
    compileOnly("org.spigotmc:spigot:1.20.4-R0.1-SNAPSHOT:remapped-mojang")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    /* Database Library */
    implementation("org.hsqldb:hsqldb:2.7.3")
    implementation("org.jooq:jooq-meta:$jooqVersion")
    implementation("org.jooq:jooq-meta-extensions:$jooqVersion")
    compileOnly("org.jooq:jooq-codegen:$jooqVersion")
    implementation("org.liquibase:liquibase-core:4.30.0")

    /* Function Library */
    implementation("io.insert-koin:koin-core-jvm:4.0.0")
    implementation("io.insert-koin:koin-annotations-jvm:$koinAnnotationsVersion")
    ksp("io.insert-koin:koin-ksp-compiler:$koinAnnotationsVersion")
    compileOnly("me.carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("org.apache.logging.log4j:log4j-api:2.19.0")
    compileOnly("org.apache.logging.log4j:log4j-core:2.19.0")
    compileOnly("com.akuleshov7:ktoml-core:0.5.2")
    compileOnly("com.akuleshov7:ktoml-file-jvm:0.5.2")
}

java.sourceSets {
    named("main") {
        java.srcDir("src/main")
    }
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("src/main")

            resources.srcDir("src/main/resources")
            resources.exclude("**")

            dependencies {
                api("io.insert-koin:koin-annotations-jvm:2.0.0-Beta1")
            }
        }
    }
}



jooq {
    configuration {
        /* Reference: https://www.jooq.org/doc/latest/manual/code-generation/codegen-advanced/codegen-config-generator/ */
        generator {
            database {
                /* Specify the location of your SQL script. */
                val scriptProperty = org.jooq.meta.jaxb.Property().apply {
                    this.key = "scripts"
                    this.value = "../../src/main/resources/database/sql-scripts/NeonDatabase.sql"
                }

                /* The default name case for unquoted objects:
                * - as_is: unquoted object names are kept unquoted
                * - upper: unquoted object names are turned into upper case (most databases)
                * - lower: unquoted object names are turned into lower case (e.g. PostgreSQL)
                */
                val defaultNameCaseProperty = org.jooq.meta.jaxb.Property().apply {
                    this.key = "defaultNameCase"
                    this.value = "as_is"
                }

                /* Turn on/off ignoring contents between such tokens. Defaults to true */
                val parseIgnoreComment = org.jooq.meta.jaxb.Property().apply {
                    this.key = "parseIgnoreComment"
                    this.value = "true"
                }

                /* Change the starting token */
                val parseIgnoreCommentStart = org.jooq.meta.jaxb.Property().apply {
                    this.key = "parseIgnoreCommentStart"
                    this.value = "[IGNORE_START]"
                }

                /* Change the stopping token */
                val parseIgnoreCommentEnd = org.jooq.meta.jaxb.Property().apply {
                    this.key = "parseIgnoreCommentEnd"
                    this.value = "[IGNORE_END]"
                }

                name = "org.jooq.meta.extensions.ddl.DDLDatabase"

                withProperties(
                    scriptProperty,
                    defaultNameCaseProperty,
                    parseIgnoreComment,
                    parseIgnoreCommentStart,
                    parseIgnoreCommentEnd
                )
            }

            name = "org.jooq.codegen.KotlinGenerator"

            target {
                packageName = "com.islandstudio.neon.stable.core.database.schema"
                directory = "build/generated/jooq"
                isClean = true
            }

            strategy {
                name = "jooq.generator.GeneratorStrategy"
            }

            generate {
                /* Tell the KotlinGenerator to generate properties in addition to methods for these paths. Default is true. */
                withImplicitJoinPathsAsKotlinProperties(true)

                /* Workaround for Kotlin generating setX() setters instead of setIsX() in byte code for mutable properties called
                  <code>isX</code>. Default is true. */
                withKotlinSetterJvmNameAnnotationsOnIsPrefix(true)

                /* Generate the DAO classes */
                withDaos(false)

                /* Generate POJOs */
                withPojos(true)

                /* Generate POJOs as data classes, when using the KotlinGenerator. Default is true. */
                withPojosAsKotlinDataClasses(true)

                /* Generate non-nullable types on POJO attributes, where column is not null. Default is false. */
                withKotlinNotNullPojoAttributes(false)

                /* Generate non-nullable types on Record attributes, where column is not null. Default is false. */
                withKotlinNotNullRecordAttributes(false)

                /* Generate non-nullable types on interface attributes, where column is not null. Default is false. */
                withKotlinNotNullInterfaceAttributes(false)

                /* Generate defaulted nullable POJO attributes. Default is true. */
                withKotlinDefaultedNullablePojoAttributes(true)

                /* Generate defaulted nullable Record attributes. Default is true */
                withKotlinDefaultedNullableRecordAttributes(true)
            }
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
        targetCompatibility = JavaVersion.VERSION_17
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
    this.archiveFileName.set(pluginFinalJarName)

    //exclude("com/islandstudio/neon")

    //mergeServiceFiles()

//    minimize {
//        exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
//        exclude(dependency("com.redgate.flyway:flyway-database-hsqldb"))
//        exclude(dependency("org.hsqldb:hsqldb"))
//        exclude("*.kotlin_module")
//    }
}

