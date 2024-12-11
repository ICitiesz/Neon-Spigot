package com.islandstudio.neon.stable.core.io.resource

enum class NeonInternalResources(val resourceURL: String, val resourceType: ResourceType) {
    NeonDatabaseServer(
        "resources/extensions/neon-database-server.jar",
        ResourceType.JAR
    ),

    NeonCodeMessages(
        "resources/application/code-messages.properties",
        ResourceType.PROPERTIES
    ),

    NeonKeyGeneralProperties(
        "resources/application/NeonKeyProperties/NeonKeys-General.properties",
        ResourceType.PROPERTIES
    ),

    NeonKeyRecipeProperties(
        "resources/application/NeonKeyProperties/NeonKeys-Recipes.properties",
        ResourceType.PROPERTIES
    ),

    NeonServerFeatures(
        "resources/nServerFeatures/nServerFeatures-reduced.yml",
        ResourceType.YAML
    ),

    NeonServerOptionProperties(
        "resources/nServerFeatures/nServerFeaturesOptionProperties-v2.yml",
        ResourceType.YAML
    ),

    NeonNmsMapping(
        "resources/application/reflection/nms-mappings.xlsx",
        ResourceType.EXCEL
    ),

    /* Neon Database Server Resources */
    NeonDatabaseServerCodeMessages(
        "resources/application/code-messages.properties",
        ResourceType.PROPERTIES
    )
    ;

    fun getResourceName(): String {
        return this.resourceURL.substringAfterLast("/")
    }
}