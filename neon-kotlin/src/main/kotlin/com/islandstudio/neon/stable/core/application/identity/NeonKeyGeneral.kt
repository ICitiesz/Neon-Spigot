package com.islandstudio.neon.stable.core.application.identity

import org.bukkit.NamespacedKey

enum class NeonKeyGeneral(val key: NamespacedKey) {
    NGUI_BUTTON(
        NeonKey.fromProperty("nGUI.button.key", NeonKey.NeonKeyType.GENERAL)
    ),
    NGUI_HIGHTLIGHT_BUTTON(
        NeonKey.fromProperty("nGUI.button.highlight.key", NeonKey.NeonKeyType.GENERAL)
    ),
    NGUI_BUTTON_TYPE(
        NeonKey.fromProperty("nGUI.button.type.key", NeonKey.NeonKeyType.GENERAL)
    ),
    NGUI_BUTTON_DATA_CONTAINER(
        NeonKey.fromProperty("nGUI.button.dataContainer.key", NeonKey.NeonKeyType.GENERAL)
    ),
    NGUI_SESSION_ID(
        NeonKey.fromProperty("nGUI.sessionId.key", NeonKey.NeonKeyType.GENERAL)
    ),
    NFIREWORKS_PROPERTY_HEADER(
        NeonKey.fromProperty("nFireworks.property.header.key", NeonKey.NeonKeyType.GENERAL)
    ),
    NPAINTING_TOOL(
        NeonKey.fromProperty("nPainting.tool.id.key", NeonKey.NeonKeyType.GENERAL)
    ),
    NPAINTING_PROPERTY_HEADER(
        NeonKey.fromProperty("nPainting.property.header.key", NeonKey.NeonKeyType.GENERAL)
    ),
    NPAINTING_PROPERTY_IMAGE_NAME(
        NeonKey.fromProperty("nPainting.property.imageName.key", NeonKey.NeonKeyType.GENERAL)
    ),
    NPAINTING_PROPERTY_POSITION(
        NeonKey.fromProperty("nPainting.property.position.key", NeonKey.NeonKeyType.GENERAL)
    ),
    NPAINTING_PROPERTY_RENDER_ID(
        NeonKey.fromProperty("nPainting.property.renderId.key", NeonKey.NeonKeyType.GENERAL)
    ),
    NDURABLE_PROPERTY_HEADER(
        NeonKey.fromProperty("nDurable.property.header.key", NeonKey.NeonKeyType.GENERAL)
    ),
    NDURABLE_PROPERTY_DAMAGE(
        NeonKey.fromProperty("nDurable.property.damage.key", NeonKey.NeonKeyType.GENERAL)
    ),
    NCOMMAND_LIST_PROPERTY_ID(
        NeonKey.fromProperty("nCommandList.property.id.key", NeonKey.NeonKeyType.GENERAL)
    ),
    N_PAINTING_REMOVAL_STICK(
        NeonKey.fromProperty("nPainting.tool.removal.key", NeonKey.NeonKeyType.GENERAL)
    ),
}