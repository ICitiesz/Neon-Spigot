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
        //NMS_PLAYER_CONTAINER(NReflector.getMapping(MappingType.FIELD, "PlayerContainer")), // Validated
        NMS_SERVER_RECIPES(NReflector.getMapping(MappingType.FIELD, "ServerRecipes")), // Supported 1.20.4
        NMS_MC_SERVER(NReflector.getMapping(MappingType.FIELD, "McServer")), // Supported 1.20.4
        NMS_MERCHANT_RECIPE_RESULT(NReflector.getMapping(MappingType.FIELD, "MerchantRecipeResult")), // Supported 1.20.4
        NMS_PLAYER_CONNECTION(NReflector.getMapping(MappingType.FIELD, "PlayerConnection")), // Supported 1.20.4
        NMS_NETWORK_MANAGER(NReflector.getMapping(MappingType.FIELD, "NetworkManager")), // Supported 1.20.4
        NMS_CHANNEL(NReflector.getMapping(MappingType.FIELD, "Channel")), // Supported 1.20.4
        NMS_CONTAINER_BASE(NReflector.getMapping(MappingType.FIELD, "ContainerBase")), // Supported 1.20.4
        NMS_BUTTON_ID(NReflector.getMapping(MappingType.FIELD, "ButtonID")), // Supported 1.20.4
        //NMS_SLOT(NReflector.getMapping(MappingType.FIELD, "Slot")), // New
        NMS_WORLD_CACHE(NReflector.getMapping(MappingType.FIELD, "WorldCache")), // Supported 1.20.4
        NMS_OVERWORLD_RESOURCE_KEY(NReflector.getMapping(MappingType.FIELD, "OverworldResourceKey")), // Supported 1.20.4
        NMS_WORLD_MAP_DATA(NReflector.getMapping(MappingType.FIELD, "WorldMapData")), // Supported 1.20.4
        NMS_WORLD_MAP_DATA_ID(NReflector.getMapping(MappingType.FIELD, "WorldMapDataId")), // Supported 1.20.4
        NMS_WORLD_MAP_DATA_MAP_VIEW(NReflector.getMapping(MappingType.FIELD, "WorldMapDataMapView")), // Supported 1.20.4
        NMS_WORLD_MAP_DATA_COLORS(NReflector.getMapping(MappingType.FIELD, "WorldMapDataColors")), // Supported 1.20.4
        NMS_ENCHANTMENT_REGISTRY(NReflector.getMapping(MappingType.FIELD, "EnchantmentRegistry")), // Supported 1.20.4
        NMS_REGISTRY_ENTRY_TOGGLE_STATE(NReflector.getMapping(MappingType.FIELD, "RegistryEntryToggleState")), // Supported 1.20.4 | Unsupported < 1.18.2

        /* NMS Methods */
        NMS_CRAFTING_MANAGER(NReflector.getMapping(MappingType.METHOD, "CraftingManager")), // Supported 1.20.4
        NMS_PLAYER_RECIPE_BOOK(NReflector.getMapping(MappingType.METHOD,"PlayerRecipeBook")), // Supported 1.20.4
        NMS_INIT_RECIPE_BOOK(NReflector.getMapping(MappingType.METHOD, "InitRecipeBook")), // Supported 1.20.4
        NMS_SEND_PACKET(NReflector.getMapping(MappingType.METHOD, "SendPacket")), // Supported 1.20.4
        NMS_GET_BUKKIT_ENTITY(NReflector.getMapping(MappingType.METHOD, "GetBukkitEntity")), // Supported 1.20.4
        NMS_SET_CONTAINER_DATA(NReflector.getMapping(MappingType.METHOD, "SetContainerData")), // Supported 1.20.4
        NMS_GET_WORLD_PERSISTENT_CONTAINER(NReflector.getMapping(MappingType.METHOD, "GetWorldPersistentContainer")), // Supported 1.20.4
        NMS_GET_MAP_DATA_FILE(NReflector.getMapping(MappingType.METHOD, "GetMapDataFile")), // Supported 1.20.4
        NMS_SET_WORLD_MAP_DATA(NReflector.getMapping(MappingType.METHOD, "SetWorldMapData")), // Supported 1.20.4
        NMS_SAVE_WORLD_CACHE(NReflector.getMapping(MappingType.METHOD, "SaveWorldCache")), // Supported 1.20.4
        NMS_GET_SET_SLOT_ITEM_STACK(NReflector.getMapping(MappingType.METHOD, "GetSetSlotItemStack")), // Supported 1.20.4
        NMS_APPLY_ENCHANTMENT(NReflector.getMapping(MappingType.METHOD, "ApplyEnchantment")), // Supported 1.20.4
        //NMS_GET_ENCHANTMENT_BY_NAMESPACEDKEY(NReflector.getMapping(MappingType.METHOD, "GetEnchantmentByNamespacedKey")),
        NMS_REGISTER_ENCHANTMENT(NReflector.getMapping(MappingType.METHOD, "RegisterEnchantment")), // Supported 1.20.4
        NMS_REVOKE_REGISTRY_ENTRY(NReflector.getMapping(MappingType.METHOD, "RevokeRegistryEntry")), // Supported 1.20.4
        NMS_LECTERN_BOOK_PAGE(NReflector.getMapping(MappingType.METHOD, "LecternBookPage")), // Supported 1.20.4

        /* NMS Constructors */
        NMS_CLIENT_PACKET_UPDATE_RECIPES(NReflector.getMapping(MappingType.CONSTRUCTOR, "ClientPacketUpdateRecipes")), // Support 1.20.4
        NMS_CLIENT_PACKET_SET_ACTION_BAR_TEXT(NReflector.getMapping(MappingType.CONSTRUCTOR, "ClientPacketSetActionBarText")), // Supported 1.20.4

        /* NMS Classes*/
        NMS_PACKET(NReflector.getMapping(MappingType.CLASS, "Packet")), // Supported 1.20.4
    }
}