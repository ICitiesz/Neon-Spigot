plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "neon-extension"

includeBuild("../")
include("neon-database-server")
