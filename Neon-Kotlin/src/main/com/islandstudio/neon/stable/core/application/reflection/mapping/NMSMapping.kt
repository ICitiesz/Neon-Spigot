package com.islandstudio.neon.stable.core.application.reflection.mapping

import com.islandstudio.neon.stable.core.application.init.NConstructor
import com.islandstudio.neon.stable.core.application.reflection.NReflector
import org.simpleyaml.configuration.MemorySection


enum class NMSMapping(val remapped: String) {
    /* Fields Mapping */
    //NMS_PLAYER_CONTAINER(NReflector.getMapping(MappingType.FIELD, "PlayerContainer")), // Validated
    NMS_SERVER_RECIPES(NReflector.getMapping(MappingType.FIELDS, "ServerRecipes")), // Supported 1.20.4
    NMS_MC_SERVER(NReflector.getMapping(MappingType.FIELDS, "McServer")), // Supported 1.20.4
    NMS_MERCHANT_RECIPE_RESULT(NReflector.getMapping(MappingType.FIELDS, "MerchantRecipeResult")), // Supported 1.20.4
    NMS_PLAYER_CONNECTION(NReflector.getMapping(MappingType.FIELDS, "PlayerConnection")), // Supported 1.20.4
    NMS_NETWORK_MANAGER(NReflector.getMapping(MappingType.FIELDS, "NetworkManager")), // Supported 1.20.4
    NMS_CHANNEL(NReflector.getMapping(MappingType.FIELDS, "Channel")), // Supported 1.20.4
    NMS_CONTAINER_BASE(NReflector.getMapping(MappingType.FIELDS, "ContainerBase")), // Supported 1.20.4
    NMS_BUTTON_ID(NReflector.getMapping(MappingType.FIELDS, "ButtonID")), // Supported 1.20.4
    //NMS_SLOT(NReflector.getMapping(MappingType.FIELD, "Slot")), // New
    NMS_WORLD_CACHE(NReflector.getMapping(MappingType.FIELDS, "WorldCache")), // Supported 1.20.4
    NMS_OVERWORLD_RESOURCE_KEY(NReflector.getMapping(MappingType.FIELDS, "OverworldResourceKey")), // Supported 1.20.4
    NMS_WORLD_MAP_DATA(NReflector.getMapping(MappingType.FIELDS, "WorldMapData")), // Supported 1.20.4
    NMS_WORLD_MAP_DATA_ID(NReflector.getMapping(MappingType.FIELDS, "WorldMapDataId")), // Supported 1.20.4
    NMS_WORLD_MAP_DATA_MAP_VIEW(NReflector.getMapping(MappingType.FIELDS, "WorldMapDataMapView")), // Supported 1.20.4
    NMS_WORLD_MAP_DATA_COLORS(NReflector.getMapping(MappingType.FIELDS, "WorldMapDataColors")), // Supported 1.20.4
    NMS_ENCHANTMENT_REGISTRY(NReflector.getMapping(MappingType.FIELDS, "EnchantmentRegistry")), // Supported 1.20.4
    NMS_REGISTRY_ENTRY_TOGGLE_STATE(NReflector.getMapping(MappingType.FIELDS, "RegistryEntryToggleState")), // Supported 1.20.4 | Unsupported < 1.18.2

    /* NMS Methods */
    NMS_CRAFTING_MANAGER(NReflector.getMapping(MappingType.METHODS, "CraftingManager")), // Supported 1.20.4
    NMS_PLAYER_RECIPE_BOOK(NReflector.getMapping(MappingType.METHODS, "PlayerRecipeBook")), // Supported 1.20.4
    NMS_INIT_RECIPE_BOOK(NReflector.getMapping(MappingType.METHODS, "InitRecipeBook")), // Supported 1.20.4
    NMS_SEND_PACKET(NReflector.getMapping(MappingType.METHODS, "SendPacket")), // Supported 1.20.4
    NMS_GET_BUKKIT_ENTITY(NReflector.getMapping(MappingType.METHODS, "GetBukkitEntity")), // Supported 1.20.4
    NMS_SET_CONTAINER_DATA(NReflector.getMapping(MappingType.METHODS, "SetContainerData")), // Supported 1.20.4
    NMS_GET_WORLD_PERSISTENT_CONTAINER(NReflector.getMapping(MappingType.METHODS, "GetWorldPersistentContainer")), // Supported 1.20.4
    NMS_GET_MAP_DATA_FILE(NReflector.getMapping(MappingType.METHODS, "GetMapDataFile")), // Supported 1.20.4
    NMS_SET_WORLD_MAP_DATA(NReflector.getMapping(MappingType.METHODS, "SetWorldMapData")), // Supported 1.20.4
    NMS_SAVE_WORLD_CACHE(NReflector.getMapping(MappingType.METHODS, "SaveWorldCache")), // Supported 1.20.4
    NMS_GET_SET_SLOT_ITEM_STACK(NReflector.getMapping(MappingType.METHODS, "GetSetSlotItemStack")), // Supported 1.20.4
    NMS_APPLY_ENCHANTMENT(NReflector.getMapping(MappingType.METHODS, "ApplyEnchantment")), // Supported 1.20.4
    //NMS_GET_ENCHANTMENT_BY_NAMESPACEDKEY(NReflector.getMapping(MappingType.METHOD, "GetEnchantmentByNamespacedKey")),
    NMS_REGISTER_ENCHANTMENT(NReflector.getMapping(MappingType.METHODS, "RegisterEnchantment")), // Supported 1.20.4
    NMS_REVOKE_REGISTRY_ENTRY(NReflector.getMapping(MappingType.METHODS, "RevokeRegistryEntry")), // Supported 1.20.4
    NMS_LECTERN_BOOK_PAGE(NReflector.getMapping(MappingType.METHODS, "LecternBookPage")), // Supported 1.20.4

    /* NMS Constructors */
    NMS_CLIENT_PACKET_UPDATE_RECIPES(NReflector.getMapping(MappingType.CONSTRUCTORS, "ClientPacketUpdateRecipes")), // Support 1.20.4
    NMS_CLIENT_PACKET_SET_ACTION_BAR_TEXT(
        NReflector.getMapping(
            MappingType.CONSTRUCTORS,
            "ClientPacketSetActionBarText"
        )
    ), // Supported 1.20.4
    NMS_CLIENT_PACKET_UPDATE_GAME_PROFILE(
        NReflector.getMapping(
            MappingType.CONSTRUCTORS,
            "ClientPacketUpdateGameProfile"
        )
    ),

    /* NMS Classes*/
    NMS_PACKET(NReflector.getMapping(MappingType.CLASSES, "Packet")); // Supported 1.20.4

    data class Mapping(private val nmsMappingsData: Map.Entry<String, Any>) {
        private val mappingDetails: HashMap<String, String> = HashMap()

        val mappingType: MappingType? = MappingType.valueOfMappingType(nmsMappingsData.key)

        init {
            /* NMSMapping.yml structure
            * field:
            *    {mappingName}:
            *           {remappedValue}:
            *                       version:
            *
            * */

            val version = NConstructor.getMinorVersion()
            val mappingNames = (nmsMappingsData.value as MemorySection).getValues(false)

            mappingNames.entries.forEach { mappingName ->
                val remappedValues = (mappingName.value as MemorySection).getValues(false)

                @Suppress("UNCHECKED_CAST")
                remappedValues.forEach remappedValues@{ remappedValue ->
                    if (version !in (remappedValue.value as MemorySection)["version"] as ArrayList<String>) return@remappedValues

                    mappingDetails[mappingName.key] = remappedValue.key
                }
            }
        }

        fun getRemappedValue(mappingName: String): String = mappingDetails[mappingName] ?: ""
    }
}