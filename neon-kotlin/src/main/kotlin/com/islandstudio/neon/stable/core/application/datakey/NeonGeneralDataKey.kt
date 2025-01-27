package com.islandstudio.neon.stable.core.application.datakey

sealed class NeonGeneralDataKey(keyName: String) : AbstractDataKey(keyName) {
    data class NeonGuiButton(
        private val keyName: String = "neon_gui.button.key"
    ): NeonGeneralDataKey(keyName)

    data class NeonGuiHighlightButton(
        private val keyName: String = "neon_gui.button.highlight.key"
    ): NeonGeneralDataKey(keyName)

    data class NeonGuiButtonType(
        private val keyName: String = "neon_gui.button.type.key"
    ): NeonGeneralDataKey(keyName)

    data class NeonGuiButtonDataContainer(
        private val keyName: String = "neon_gui.button.dataContainer.key"
    ): NeonGeneralDataKey(keyName)

    data class NeonGuiSessionId(
        private val keyName: String = "neon_gui.sessionId.key"
    ): NeonGeneralDataKey(keyName)

    data class NFireworksPropertyHeader(
        private val keyName: String = "nFireworks.property.header.key"
    ): NeonGeneralDataKey(keyName)

    data class NPaintingTool(
        private val keyName: String = "nPainting.tool.id.key"
    ): NeonGeneralDataKey(keyName)

    data class NPaintingPropertyHeader(
        private val keyName: String = "nPainting.property.header.key"
    ): NeonGeneralDataKey(keyName)

    data class NPaintingPropertyImageName(
        private val keyName: String = "nPainting.property.imageName.key"
    ): NeonGeneralDataKey(keyName)

    data class NPaintingPropertyPosition(
        private val keyName: String = "nPainting.property.position.key"
    ): NeonGeneralDataKey(keyName)

    data class NPaintingPropertyRenderId(
        private val keyName: String = "nPainting.property.renderId.key"
    ): NeonGeneralDataKey(keyName)

    data class NDurablePropertyHeader(
        private val keyName: String = "nDurable.property.header.key"
    ): NeonGeneralDataKey(keyName)

    data class NDurablePropertyDamage(
        private val keyName: String = "nDurable.property.damage.key"
    ): NeonGeneralDataKey(keyName)

    data class NCommandListPropertyId(
        private val keyName: String = "nCommandList.property.id.key"
    ): NeonGeneralDataKey(keyName)

    data class NPaintingRemovalStick(
        private val keyName: String = "nPainting.tool.removal.key"
    ): NeonGeneralDataKey(keyName)
}