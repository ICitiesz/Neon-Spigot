package com.islandstudio.neon.stable.core.io.resource

enum class NeonResources(val resourcePath: String, val resourceType: ResourceType) {
    NEON_DATABASE_SERVER("resources/extensions/neon-database-server.jar", ResourceType.JAR),
    NEON_CODE_MESSAGES("resources/application/code-messages.properties", ResourceType.PROPERTIES),
    NEON_KEY_GENERAL_PROPERTIES("resources/application/NeonKeyProperties/NeonKeys-General.properties", ResourceType.PROPERTIES),
    NEON_KEY_RECIPE_PROPERTIES("resources/application/NeonKeyProperties/NeonKeys-Recipes.properties", ResourceType.PROPERTIES),
    NEON_SERVER_FEATURES("resources/nServerFeatures/nServerFeatures-reduced.yml", ResourceType.YAML),
    NEON_SERVER_OPTION_PROPERTIES("resources/nServerFeatures/nServerFeaturesOptionProperties-v2.yml", ResourceType.YAML),
    NEON_NMS_MAPPING("resources/application/reflection/nms-mappings.xlsx", ResourceType.EXCEL),

    /* Neon Database Resources */
    NEON_DATABASE_CODE_MESSAGES("resources/application/code-messages.properties", ResourceType.PROPERTIES),
    ;

    fun getResourceName(): String {
        return this.resourcePath.substringAfterLast("/")
    }
}