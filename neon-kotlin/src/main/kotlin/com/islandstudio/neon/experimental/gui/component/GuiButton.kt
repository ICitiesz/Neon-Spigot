package com.islandstudio.neon.experimental.gui.component

import com.islandstudio.neon.core.datakey.container.DataContainerManager
import com.islandstudio.neon.core.datakey.container.DataContainerType
import com.islandstudio.neon.shared.utils.serialization.ObjectSerializer
import com.islandstudio.neon.stable.item.NItemGlinter
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

data class GuiButton(
    val buttonRefId: String,
    val buttonMaterial: Material,
    val buttonDisplayName: String?
) {
    private val buttonItem = ItemStack(buttonMaterial, 1)
    private val buttonMetaData = buttonItem.itemMeta!!

    companion object {
        fun matchesButtonRefId(guiButton: GuiButton, clickedButtonMetaData: ItemMeta): Boolean {
            if (!hasButtonRefId(clickedButtonMetaData)) return false

            return getButtonRefId(clickedButtonMetaData) == guiButton.buttonRefId
        }

        fun hasButtonRefId(clickedButtonMetaData: ItemMeta): Boolean {
            return DataContainerManager.hasDataContainerAttached(clickedButtonMetaData, DataContainerType.NeonGuiButtonRefIdContainer)
        }

        fun getButtonRefId(clickedButtonMetaData: ItemMeta): String? {
            if (!hasButtonRefId(clickedButtonMetaData)) return null

            return DataContainerManager.getAttachedData(clickedButtonMetaData, DataContainerType.NeonGuiButtonRefIdContainer)
        }

        fun hasCustomData(clickedButtonMetaData: ItemMeta): Boolean {
            return DataContainerManager.hasDataContainerAttached(clickedButtonMetaData, DataContainerType.NeonGuiButtonCustomDataContainer)
        }

        fun hasConfirmationStatus(clickedButtonMetaData: ItemMeta): Boolean {
            return DataContainerManager.hasDataContainerAttached(clickedButtonMetaData, DataContainerType.NeonGuiButtonConfirmationStatusContainer)
        }

        fun onConfirmation(clickedButtonMetaData: ItemMeta, block: (Boolean) -> Unit) {
            if (!hasConfirmationStatus(clickedButtonMetaData)) return block(false)

            val confirmationStatus = DataContainerManager.getAttachedData(
                clickedButtonMetaData,
                DataContainerType.NeonGuiButtonConfirmationStatusContainer
            ) ?: return block(false)

            val isConfirmed: Boolean = when(confirmationStatus) {
                false -> {
                    updateConfirmationStatus(clickedButtonMetaData, true)

                    clickedButtonMetaData.lore = LinkedList<String>().apply {
                        add("${ChatColor.YELLOW}Click again to confirm.")
                    }
                    false
                }

               true -> {
                   updateConfirmationStatus(clickedButtonMetaData, false)

                   clickedButtonMetaData.lore = null
                   true
               }
            }

            return block(isConfirmed)
        }

        fun resetConfirmationStatus(clickedButtonMetaData: ItemMeta) {
            updateConfirmationStatus(clickedButtonMetaData, false)
        }

        private fun updateConfirmationStatus(clickedButtonMetaData: ItemMeta, status: Boolean) {
            if (!hasConfirmationStatus(clickedButtonMetaData)) return

            DataContainerManager.updateAttachedData(
                clickedButtonMetaData,
                status,
                DataContainerType.NeonGuiButtonConfirmationStatusContainer
            )
        }

        fun <T> getCustomData(clickedButtonMetaData: ItemMeta): T? {
            if (!hasCustomData(clickedButtonMetaData)) return null

            val encodedCustomData = DataContainerManager.getAttachedData(
                clickedButtonMetaData, DataContainerType.NeonGuiButtonCustomDataContainer) ?: return null

            return ObjectSerializer.deserialzeFromBase64(encodedCustomData)
        }

        fun <T> updateCustomData(clickedButtonMetaData: ItemMeta, customData: T) {
            if (!hasCustomData(clickedButtonMetaData)) return

            DataContainerManager.updateAttachedData(clickedButtonMetaData, ObjectSerializer.serializeToBase64(customData), DataContainerType.NeonGuiButtonCustomDataContainer)
        }

        fun updateButtonGlintEffect(clickedButtonMetaData: ItemMeta, isButtonGlint: Boolean) {
            if (isButtonGlint) {
                clickedButtonMetaData.addEnchant(NItemGlinter.ItemGlinterType.NGUI_BUTTON_GLINT.glint, 0, true)
                return
            }

            clickedButtonMetaData.removeEnchant(NItemGlinter.ItemGlinterType.NGUI_BUTTON_GLINT.glint)
        }
    }

    fun configureButton(block: GuiButton.() -> Unit) {
        block(this)
    }

    fun createButton() {
        buttonMetaData.setDisplayName(buttonDisplayName)
        DataContainerManager.attachData(buttonMetaData, buttonRefId, DataContainerType.NeonGuiButtonRefIdContainer)
        buttonItem.itemMeta = buttonMetaData
    }

    fun withButtonLabels(buttonLabels: LinkedList<String> = LinkedList(), block: LinkedList<String>.() -> Unit = {}) {
        block(buttonLabels).apply {
            buttonMetaData.lore = buttonLabels
        }
    }

    fun withButtonGlintEffect() {
        updateButtonGlintEffect(buttonMetaData, true)
    }

    fun <T> withCustomData(buttonNode: T, block: T.() -> Unit = {}) {
        block(buttonNode).apply {
            DataContainerManager.attachData(buttonMetaData,  ObjectSerializer.serializeToBase64(buttonNode), DataContainerType.NeonGuiButtonCustomDataContainer)
        }
    }

    fun withConfirmationStatus() {
        DataContainerManager.attachData(buttonMetaData, false, DataContainerType.NeonGuiButtonConfirmationStatusContainer)
    }

    fun getButtonItem(): ItemStack = buttonItem

    fun clone(
        buttonRefId: String = this.buttonRefId,
        buttonMaterial: Material = this.buttonMaterial,
        buttonDisplayName: String? = this.buttonDisplayName
    ): GuiButton {
        return GuiButton(buttonRefId, buttonMaterial, buttonDisplayName)
    }
}
