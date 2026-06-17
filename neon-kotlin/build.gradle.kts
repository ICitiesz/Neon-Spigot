
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.islandstudio"
version = "final"

val pluginFinalJarName = "neon-kotlin.jar"
val pluginShadedjarName = "neon-kotlin-shaded.jar"

plugins {
    alias(libs.plugins.kotlin.jvm) apply true
    alias(libs.plugins.kotlin.serialization) apply true
    alias(libs.plugins.kotlin.noarg) apply true
    alias(libs.plugins.gradle.shadow) apply true
    alias(libs.plugins.google.devtools.ksp) apply true
    alias(libs.plugins.papermc.paperweight.userdev) apply true
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

    /* Neon Library */
    implementation(libs.neon.shared)
    //implementation(libs.neon.api)
    //implementation(libs.neon.api.new)
    //compileOnly("com.islandstudio:neon-database-server")

    /* Core Language Library */
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core.jvm)

    /* Server API Reference Library */
    paperweightDevelopmentBundle(libs.papermc.dev.bundle.target) // PaperMC Mojang Mappings
    compileOnly(libs.papermc.api.base)

    /* Function Library */
    implementation(libs.koin.core.jvm)
    implementation(libs.koin.annotations.jvm)
    ksp(libs.koin.ksp.compiler)
    implementation(libs.ulid.creator)
    implementation(libs.simple.yaml)
    implementation(libs.fastexcel.reader)
    implementation(libs.dotenv.kotlin)
    implementation(libs.ktoml.core.jvm)
    implementation(libs.ktoml.file.jvm)
    implementation(libs.modelmapper)
    implementation(libs.oshi.core)
    compileOnly(libs.kotlin.csv.jvm)

    /* Database Library */
    compileOnly(libs.hsqldb)
    implementation(libs.jooq.core)
    compileOnly(libs.jooq.meta) // Should be compileOnly
    implementation(libs.jooq.meta.extensions)
    //runtimeOnly("org.xerial:sqlite-jdbc:3.50.3.0")
    //compileOnly("org.jooq:jooq-codegen:$jooqVersion")
    //compileOnly("com.h2database:h2:2.4.240")
    runtimeOnly(libs.h2)
    implementation(libs.hikari.cp)
    implementation(libs.guava)
    runtimeOnly(libs.liquibase.core)
}

kotlin {
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

noArg {
    annotation("com.islandstudio.neon.shared.annotation.NoArgConstructor")
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
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_4)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_4)
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

    from(file("${project.projectDir}/neon-extension/neon-database-server/build/libs/neon-database-server.jar")) {
        into("resources/extensions/")
    }

    from(zipTree(file("${layout.buildDirectory.get()}/libs/${pluginShadedjarName}")))
}






