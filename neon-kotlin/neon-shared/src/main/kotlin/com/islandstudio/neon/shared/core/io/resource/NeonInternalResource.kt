package com.islandstudio.neon.shared.core.io.resource

enum class NeonInternalResource(val resourceURL: String, val resourceType: ResourceType) {
    NeonDatabaseServer(
        "resources/extensions/neon-database-server.jar",
        ResourceType.Jar
    ),

    NeonKeyGeneralProperties(
        "resources/application/NeonKeyProperties/NeonKeys-General.properties",
        ResourceType.Properties
    ),

    NeonKeyRecipeProperties(
        "resources/application/NeonKeyProperties/NeonKeys-Recipes.properties",
        ResourceType.Properties
    ),

    NeonServerFeatures(
        "resources/nServerFeatures/nServerFeatures-reduced.yml",
        ResourceType.Yaml
    ),

    NeonServerOptionProperties(
        "resources/nServerFeatures/nServerFeaturesOptionProperties-v2.yml",
        ResourceType.Yaml
    ),

    NeonNmsMapping(
        "resources/application/reflection/nms-mappings.xlsx",
        ResourceType.Excel
    ),

    NWaypointsGlobal(
        "resources/nWaypoints.json",
        ResourceType.Json
    ),

    /* Neon Shared Resource */
    NeonSharedEnvironmentValue(
        "/resources/application/.env",
        ResourceType.DotEnv
    ),

    NeonSharedCodeMessages(
        "resources/application/code-messages.properties",
        ResourceType.Properties
    ),

    /* Neon Database Server Resources */
    NeonDBServerUpdateScript(
        "resources/database/neon-database-changelog.xml",
        ResourceType.XML
    ),

    NeonDBServerConfig(
        "resources/database/NeonDB-config.toml",
        ResourceType.Toml
    )
    ;

    fun getResourceName(): String {
        return this.resourceURL.substringAfterLast("/")
    }

    fun getDirectoryPath(): String {
        return this.resourceURL.substringBeforeLast("/")
    }
}