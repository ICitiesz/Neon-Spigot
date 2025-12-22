
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.CatalogMappingType
import org.jooq.meta.jaxb.SchemaMappingType

group = "com.islandstudio"
version = "final"

val kotlinxCoroutinesVersion = "1.9.0"

plugins {
    kotlin("jvm") version "2.0.20" apply true
    kotlin("plugin.serialization") version "2.0.20" apply true
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply true
    id("org.jooq.jooq-codegen-gradle") version "3.19.15" apply true
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    val jooqVersion = "3.19.15"
    val koinAnnotationsVersion = "2.0.0-Beta1"

    /* Neon Library */
    compileOnly(project(":neon-shared"))
    implementation(files("build/classes/java/main"))

    /* Core Language Library */
    compileOnly(kotlin("stdlib"))
    compileOnly(kotlin("reflect"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${kotlinxCoroutinesVersion}")

    /* Server API Reference Library */
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT:shaded")
    compileOnly("org.spigotmc:spigot:1.20.4-R0.1-SNAPSHOT:remapped-mojang")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    /* Database Library */
    implementation("com.zaxxer:HikariCP:5.1.0")
    compileOnly("org.jooq:jooq:$jooqVersion")
    compileOnly("org.jooq:jooq-meta:$jooqVersion")
    implementation("org.jooq:jooq-meta-extensions:$jooqVersion")
    compileOnly("org.jooq:jooq-codegen:$jooqVersion")
    jooqCodegen(project)
    jooqCodegen("org.mariadb.jdbc:mariadb-java-client:3.5.6")

    /*  Experimental Library */
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.6")
    implementation("ch.vorburger.mariaDB4j:mariaDB4j-core:3.3.0") // Used for MariaDB4j
    implementation("ch.vorburger.exec:exec:3.3.2") // Used for MariaDB4j
    compileOnly("ch.vorburger.mariaDB4j:mariaDB4j-db-winx64:11.4.5") // Used for MariaDB4j
    compileOnly("ch.vorburger.mariaDB4j:mariaDB4j-db-linux64:11.4.5") // Used for MariaDB4j
    compileOnly("ch.vorburger.mariaDB4j:mariaDB4j-db-macos-arm64:11.4.5") // Used for MariaDB4j

    /* Function Library */
    compileOnly("org.modelmapper:modelmapper:3.2.6")
    compileOnly("io.insert-koin:koin-core-jvm:4.0.0")
    compileOnly("io.insert-koin:koin-annotations-jvm:$koinAnnotationsVersion")
    ksp("io.insert-koin:koin-ksp-compiler:$koinAnnotationsVersion")
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

java.sourceSets {
    named("main") {
        java.srcDir("src/main/java")
    }
}

jooq {
    configuration {
        /* Reference: https://www.jooq.org/doc/latest/manual/code-generation/codegen-advanced/codegen-config-generator/ */
        generator {
            database {
//                /* Specify the location of your SQL script. */
//                val scriptProperty = org.jooq.meta.jaxb.Property()
//                    .withKey("scripts")
//                    .withValue("${rootDir}/neon-api-new/src/main/resources/database/NeonDatabaseInit.sql")

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
                    this.key = "parseIgnoreComments"
                    this.value = "true"
                }

                /* Change the starting token */
                val parseIgnoreCommentStart = org.jooq.meta.jaxb.Property().apply {
                    this.key = "parseIgnoreCommentStart"
                    this.value = "[IGNORE START]"
                }

                /* Change the stopping token */
                val parseIgnoreCommentEnd = org.jooq.meta.jaxb.Property().apply {
                    this.key = "parseIgnoreCommentEnd"
                    this.value = "[IGNORE END]"
                }

                //name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                name = "org.jooq.meta.mariadb.MariaDBDatabase"

                jdbc {
                    driver = "org.mariadb.jdbc.Driver"
                    url = "jdbc:mariadb://localhost:3306/neon_dev"
                    user = "root"
                    password = ""
                }

                withProperties(
                    defaultNameCaseProperty,
                    parseIgnoreComment,
                    parseIgnoreCommentStart,
                    parseIgnoreCommentEnd
                )

                withCatalogs(
                    CatalogMappingType()
                        .withSchemata(
                        SchemaMappingType()
                                .withInputSchema("neon_dev")
                            .withOutputSchemaToDefault(true)
                        ),
                )
            }

            name = "org.jooq.codegen.KotlinGenerator"

            target {
                packageName = "com.islandstudio.neon.apinew.schema"
                directory = "src/main/kotlin/"
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
                withPojos(false)

                /* Generate POJOs as data classes, when using the KotlinGenerator. Default is true. */
                withPojosAsKotlinDataClasses(true)

                /* Generate non-nullable types on POJO attributes, where column is not null. Default is false. */
                withKotlinNotNullPojoAttributes(false)

                /* Generate non-nullable types on Record attributes, where column is not null. Default is false. */
                withKotlinNotNullRecordAttributes(true)

                /* Generate non-nullable types on interface attributes, where column is not null. Default is false. */
                withKotlinNotNullInterfaceAttributes(true)

                /* Generate defaulted nullable POJO attributes. Default is true. */
                withKotlinDefaultedNullablePojoAttributes(false)

                /* Generate defaulted nullable Record attributes. Default is true */
                withKotlinDefaultedNullableRecordAttributes(false)
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