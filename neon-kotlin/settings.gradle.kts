plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
include("neon-shared", "neon-api")
includeBuild("neon-extension/")
