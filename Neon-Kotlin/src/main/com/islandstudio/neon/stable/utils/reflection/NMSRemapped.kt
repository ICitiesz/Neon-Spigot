package com.islandstudio.neon.stable.utils.reflection

object NMSRemapped {
    enum class MappingType {
        FIELD,
        METHOD,
        CONSTRUCTOR,
        UNKOWNN
    }

    enum class Mapping(val remapped: String) {
        NMS_PLAYER_CONTAINER(NReflector.getMapping(MappingType.FIELD, "PlayerContainer")),
        NMS_CRAFTING_MANAGER(NReflector.getMapping(MappingType.METHOD, "CraftingManager")),
        NMS_SERVER_RECIPES(NReflector.getMapping(MappingType.FIELD, "ServerRecipes")),
        NMS_MC_SERVER(NReflector.getMapping(MappingType.FIELD, "McServer")),
        NMS_PLAYER_RECIPE_BOOK(NReflector.getMapping(MappingType.METHOD,"PlayerRecipeBook")),
        NMS_INIT_RECIPE_BOOK(NReflector.getMapping(MappingType.METHOD, "InitRecipeBook")),
        NMS_MERCHANT_RECIPE_RESULT(NReflector.getMapping(MappingType.FIELD, "MerchantRecipeResult")),
        NMS_PLAYER_CONNECTION(NReflector.getMapping(MappingType.FIELD, "PlayerConnection")),
        NMS_NETWORK_MANAGER(NReflector.getMapping(MappingType.FIELD, "NetworkManager")),
        NMS_CHANNEL(NReflector.getMapping(MappingType.FIELD, "Channel")),
        NMS_SEND_PACKET(NReflector.getMapping(MappingType.METHOD, "SendPacket")),
        NMS_GET_BUKKIT_ENTITY(NReflector.getMapping(MappingType.METHOD, "GetBukkitEntity")),
        NMS_SET_CONTAINER_DATA(NReflector.getMapping(MappingType.METHOD, "SetContainerData")),
        NMS_CONTAINER_BASE(NReflector.getMapping(MappingType.FIELD, "ContainerBase")),
    }
}