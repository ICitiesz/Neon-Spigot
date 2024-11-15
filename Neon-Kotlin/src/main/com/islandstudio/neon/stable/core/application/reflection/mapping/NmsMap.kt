package com.islandstudio.neon.stable.core.application.reflection.mapping

import com.islandstudio.neon.stable.core.application.reflection.NmsProcessor

enum class NmsMap(nmsObject: NmsObject?) {
    /* Field Mapping */
    //PlayerContainer(NmsProcessor.getNmsObject(MappingType.FIELDS, "PlayerContainer")),
    ServerRecipes(NmsProcessor.getNmsObject(MappingType.FIELD, "ServerRecipes")), // Supported 1.20.4
    McServer(NmsProcessor.getNmsObject(MappingType.FIELD, "McServer")), // Supported 1.20.4
    MerchantRecipeResult(NmsProcessor.getNmsObject(MappingType.FIELD, "MerchantRecipeResult")), // Supported 1.20.4
    PlayerConnection(NmsProcessor.getNmsObject(MappingType.FIELD, "PlayerConnection")), // Supported 1.20.4
    NetworkManager(NmsProcessor.getNmsObject(MappingType.FIELD, "NetworkManager")), // Supported 1.20.4
    Channel(NmsProcessor.getNmsObject(MappingType.FIELD, "Channel")), // Supported 1.20.4
    ContainerBase(NmsProcessor.getNmsObject(MappingType.FIELD, "ContainerBase")), // Supported 1.20.4
    ButtonId(NmsProcessor.getNmsObject(MappingType.FIELD, "ButtonId")), // Supported 1.20.4
    //Slot(NmsProcessor.getNmsObject(MappingType.FIELDS, "Slot")), // New
    WorldCache(NmsProcessor.getNmsObject(MappingType.FIELD, "WorldCache")), // Supported 1.20.4
    OverworldResourceKey(NmsProcessor.getNmsObject(MappingType.FIELD, "OverworldResourceKey")), // Supported 1.20.4
    WorldMapData(NmsProcessor.getNmsObject(MappingType.FIELD, "WorldMapData")), // Supported 1.20.4
    WorldMapDataId(NmsProcessor.getNmsObject(MappingType.FIELD, "WorldMapDataId")), // Supported 1.20.4
    WorldMapDataMapView(NmsProcessor.getNmsObject(MappingType.FIELD, "WorldMapDataMapView")), // Supported 1.20.4
    WorldMapDataColors(NmsProcessor.getNmsObject(MappingType.FIELD, "WorldMapDataColors")), // Supported 1.20.4
    EnchantmentRegistry(NmsProcessor.getNmsObject(MappingType.FIELD, "EnchantmentRegistry")), // Supported 1.20.4
    RegistryEntryToggleState(NmsProcessor.getNmsObject(MappingType.FIELD, "RegistryEntryToggleState")), // Supported 1.20.4 | Unsupported < 1.18.2

    /* Method Mapping */
    CraftingManager(NmsProcessor.getNmsObject(MappingType.METHOD, "CraftingManager")), // Supported 1.20.4
    PlayerRecipeBook(NmsProcessor.getNmsObject(MappingType.METHOD, "PlayerRecipeBook")), // Supported 1.20.4
    InitRecipeBook(NmsProcessor.getNmsObject(MappingType.METHOD, "InitRecipeBook")), // Supported 1.20.4
    SendPacket(NmsProcessor.getNmsObject(MappingType.METHOD, "SendPacket")), // Supported 1.20.4
    GetBukkitEntity(NmsProcessor.getNmsObject(MappingType.METHOD, "GetBukkitEntity")), // Supported 1.20.4
    SetContainerData(NmsProcessor.getNmsObject(MappingType.METHOD, "SetContainerData")), // Supported 1.20.4
    GetWorldPersistentContainer(NmsProcessor.getNmsObject(MappingType.METHOD, "GetWorldPersistentContainer")), // Supported 1.20.4
    GetMapDataFile(NmsProcessor.getNmsObject(MappingType.METHOD, "GetMapDataFile")), // Supported 1.20.4
    SetWorldMapData(NmsProcessor.getNmsObject(MappingType.METHOD, "SetWorldMapData")), // Supported 1.20.4
    SaveWorldCache(NmsProcessor.getNmsObject(MappingType.METHOD, "SaveWorldCache")), // Supported 1.20.4
    GetSetSlotItemStack(NmsProcessor.getNmsObject(MappingType.METHOD, "GetSetSlotItemStack")), // Supported 1.20.4
    ApplyEnchantment(NmsProcessor.getNmsObject(MappingType.METHOD, "ApplyEnchantment")), // Supported 1.20.4
    //GetEnchantmentByNamespacedKey(NmsProcessor.getNmsObject(MappingType.METHOD, "GetEnchantmentByNamespacedKey")),
    RegisterEnchantment(NmsProcessor.getNmsObject(MappingType.METHOD, "RegisterEnchantment")), // Supported 1.20.4
    RevokeRegistryEntry(NmsProcessor.getNmsObject(MappingType.METHOD, "RevokeRegistryEntry")), // Supported 1.20.4
    LecternBookPage(NmsProcessor.getNmsObject(MappingType.METHOD, "LecternBookPage")), // Supported 1.20.4

    /* Constructor Mapping */
    ClientPacketUpdateRecipes(NmsProcessor.getNmsObject(MappingType.CONSTRUCTOR, "ClientPacketUpdateRecipes")), // Support 1.20.4
    ClientPacketSetActionBarText(NmsProcessor.getNmsObject(MappingType.CONSTRUCTOR, "ClientPacketSetActionBarText")), // Support 1.20.4
    ClientPacketUpdateGameProfile(NmsProcessor.getNmsObject(MappingType.CONSTRUCTOR, "ClientPacketUpdateProfile")),

    /* Class Mapping */
    Packet(NmsProcessor.getNmsObject(MappingType.CLASS, "Packet")); // Support 1.20.4

    val remapped = nmsObject?.remappedName ?: ""
}