package com.islandstudio.neon.stable.utils.reflection

object NMSRemapped {
    enum class MappingType {
        FIELD,
        METHOD,
        CONSTRUCTOR,
        CLASS,
        UNKOWNN
    }

    enum class Mapping(val remapped: String) {
        /* NMS Fields */
        NMS_PLAYER_CONTAINER(NReflector.getMapping(MappingType.FIELD, "PlayerContainer")), // Validated
        NMS_SERVER_RECIPES(NReflector.getMapping(MappingType.FIELD, "ServerRecipes")), // Validated
        NMS_MC_SERVER(NReflector.getMapping(MappingType.FIELD, "McServer")), // Validated
        NMS_MERCHANT_RECIPE_RESULT(NReflector.getMapping(MappingType.FIELD, "MerchantRecipeResult")), // Validated
        NMS_PLAYER_CONNECTION(NReflector.getMapping(MappingType.FIELD, "PlayerConnection")), // Validated
        NMS_NETWORK_MANAGER(NReflector.getMapping(MappingType.FIELD, "NetworkManager")), // Validated
        NMS_CHANNEL(NReflector.getMapping(MappingType.FIELD, "Channel")), // Validated
        NMS_CONTAINER_BASE(NReflector.getMapping(MappingType.FIELD, "ContainerBase")), // Validated
        NMS_BUTTON_ID(NReflector.getMapping(MappingType.FIELD, "ButtonID")), // New
        NMS_SLOT(NReflector.getMapping(MappingType.FIELD, "Slot")), // New
        NMS_WORLD_CACHE(NReflector.getMapping(MappingType.FIELD, "WorldCache")), // 1.20.2 tested
        NMS_OVERWORLD_RESOURCE_KEY(NReflector.getMapping(MappingType.FIELD, "OverworldResourceKey")), // 1.20.2 tested
        NMS_WORLD_MAP_DATA(NReflector.getMapping(MappingType.FIELD, "WorldMapData")), // 1.20.2 tested
        NMS_WORLD_MAP_DATA_ID(NReflector.getMapping(MappingType.FIELD, "WorldMapDataId")), // 1.20.2 tested
        NMS_WORLD_MAP_DATA_MAP_VIEW(NReflector.getMapping(MappingType.FIELD, "WorldMapDataMapView")), // 1.20.2 tested
        NMS_WORLD_MAP_DATA_COLORS(NReflector.getMapping(MappingType.FIELD, "WorldMapDataColors")), // 1.20.2 tested

        /* NMS Methods */
        NMS_CRAFTING_MANAGER(NReflector.getMapping(MappingType.METHOD, "CraftingManager")), // Validated
        NMS_PLAYER_RECIPE_BOOK(NReflector.getMapping(MappingType.METHOD,"PlayerRecipeBook")), // Validated
        NMS_INIT_RECIPE_BOOK(NReflector.getMapping(MappingType.METHOD, "InitRecipeBook")), // Validated
        NMS_SEND_PACKET(NReflector.getMapping(MappingType.METHOD, "SendPacket")), // Validated
        NMS_GET_BUKKIT_ENTITY(NReflector.getMapping(MappingType.METHOD, "GetBukkitEntity")), // Validated
        NMS_SET_CONTAINER_DATA(NReflector.getMapping(MappingType.METHOD, "SetContainerData")), // Validated
        NMS_GET_WORLD_PERSISTENT_CONTAINER(NReflector.getMapping(MappingType.METHOD, "GetWorldPersistentContainer")), // 1.20.2 tested
        NMS_GET_MAP_DATA_FILE(NReflector.getMapping(MappingType.METHOD, "GetMapDataFile")), // 1.20.2 tested
        NMS_SET_WORLD_MAP_DATA(NReflector.getMapping(MappingType.METHOD, "SetWorldMapData")), // 1.20.2 tested
        NMS_SAVE_WORLD_CACHE(NReflector.getMapping(MappingType.METHOD, "SaveWorldCache")), // 1.20.2 tested
        NMS_GET_SET_SLOT_ITEM_STACK(NReflector.getMapping(MappingType.METHOD, "GetSetSlotItemStack")), // 1.20.2 tested

        /* NMS Constructors */
        NMS_CLIENT_PACKET_UPDATE_RECIPES(NReflector.getMapping(MappingType.CONSTRUCTOR, "ClientPacketUpdateRecipes")), // New
        NMS_CLIENT_PACKET_SET_ACTION_BAR_TEXT(NReflector.getMapping(MappingType.CONSTRUCTOR, "ClientPacketSetActionBarText")), // New

        /* NMS Classes*/
        NMS_PACKET(NReflector.getMapping(MappingType.CLASS, "Packet")), // New
    }
}