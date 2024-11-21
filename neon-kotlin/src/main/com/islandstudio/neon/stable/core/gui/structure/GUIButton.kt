package com.islandstudio.neon.stable.core.gui.structure

import com.islandstudio.neon.stable.core.application.identifier.NeonKey
import com.islandstudio.neon.stable.core.application.identifier.NeonKeyGeneral
import com.islandstudio.neon.stable.item.NItemGlinter
import com.islandstudio.neon.stable.utils.ObjectSerializer
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

data class GUIButton(
    val buttonType: String,
    val buttonItem: ItemStack,
    val buttonName: String?
) {
    private val buttonMeta = buttonItem.itemMeta!!

    object Handler {
        fun hasButtonType(clickedButtonMeta: ItemMeta): Boolean {
            return NeonKey.hasNeonKey(
                NeonKeyGeneral.NGUI_BUTTON_TYPE.key,
                PersistentDataType.STRING,
                clickedButtonMeta
            )
        }

        fun getClickedButtonType(clickedButtonMeta: ItemMeta): String? {
            if (!hasButtonType(clickedButtonMeta)) return null

            return NeonKey.getNeonKeyValue(
                NeonKeyGeneral.NGUI_BUTTON_TYPE.key,
                PersistentDataType.STRING,
                clickedButtonMeta
            ) as String
        }

        fun getDataContainer(clickedButtonMeta: ItemMeta): HashMap<String, Any>? {
            if (!hasDataContainer(clickedButtonMeta)) return null

            @Suppress("UNCHECKED_CAST")
            return ObjectSerializer.deserializeObjectEncoded(
                NeonKey.getNeonKeyValue(
                    NeonKeyGeneral.NGUI_BUTTON_DATA_CONTAINER.key,
                    PersistentDataType.STRING,
                    clickedButtonMeta
                ) as String) as HashMap<String, Any>
        }

        fun updateDataContainer(clickedButtonMeta: ItemMeta, dataContainer: MutableMap<String, Any>) {
            if (!hasDataContainer(clickedButtonMeta)) return

            NeonKey.updateNeonKey(
                ObjectSerializer.serializeObjectEncoded(dataContainer as HashMap),
                NeonKeyGeneral.NGUI_BUTTON_DATA_CONTAINER.key,
                PersistentDataType.STRING,
                clickedButtonMeta
            )
        }

        fun updateIsGlint(clickedButtonMeta: ItemMeta, isGlint: Boolean) {
            if (isGlint) {
                clickedButtonMeta.addEnchant(NItemGlinter.ItemGlinterType.NGUI_BUTTON_GLINT.glint, 0, true)
                return
            }

            clickedButtonMeta.removeEnchant(NItemGlinter.ItemGlinterType.NGUI_BUTTON_GLINT.glint)
        }

        fun onConfirm(applyBtnMeta: ItemMeta) {
            val applyBtnDetail = LinkedList<String>().apply {
                this.add("${ChatColor.YELLOW}Click again to confirm.")
            }

            applyBtnMeta.lore = applyBtnDetail
        }

        private fun hasDataContainer(clickedButtonMeta: ItemMeta): Boolean {
            return NeonKey.hasNeonKey(
                NeonKeyGeneral.NGUI_BUTTON_DATA_CONTAINER.key,
                PersistentDataType.STRING,
                clickedButtonMeta
            )
        }
    }

    fun clone(buttonType: String = this.buttonType, buttonItem: ItemStack = this.buttonItem.clone(), buttonName: String? = this.buttonName): GUIButton {
        return GUIButton(buttonType, buttonItem, buttonName)
    }

    /**
     * Initialize button meta with default configuration.
     *
     * @param buttonDetail (Optional) Button details
     * @param isGlint (Optional) Make the button glint
     * @return
     */
    fun initButtonMeta(buttonDetail: LinkedList<String>? = null, isGlint: Boolean = false): GUIButton {
        buttonMeta.setDisplayName(buttonName)
        buttonMeta.lore = buttonDetail

        /* Add button type */
        NeonKey.addNeonKey(
            buttonType,
            NeonKeyGeneral.NGUI_BUTTON_TYPE.key,
            PersistentDataType.STRING,
            buttonMeta
        )

        /* Add item glint */
        if (isGlint) {
            buttonMeta.addEnchant(NItemGlinter.ItemGlinterType.NGUI_BUTTON_GLINT.glint, 0, true)
        }

        return this
    }

    /**
     * Initialize data container for storing additional data within the button.
     *
     * @param dataContainer The data container which contains additional data.
     * @return
     */
    fun initDataContainer(dataContainer: MutableMap<String, Any>): GUIButton {
        NeonKey.addNeonKey(
            ObjectSerializer.serializeObjectEncoded(dataContainer as HashMap),
            NeonKeyGeneral.NGUI_BUTTON_DATA_CONTAINER.key,
            PersistentDataType.STRING,
            buttonMeta
        )

        return this
    }

    fun initConfirmationStatus(confirmStatus: Int): GUIButton {
        when (confirmStatus) {
            0, 2 -> {
                buttonMeta.lore = null
            }

            1 -> {
                buttonMeta.lore = LinkedList<String>().apply {
                    this.add("${ChatColor.YELLOW}Click again to confirm.")
                }
            }
        }

        return this
    }

    fun getButtonMeta(): ItemMeta = buttonMeta

    fun createButton(): ItemStack {
        buttonItem.itemMeta = buttonMeta

        return buttonItem
    }

    fun matchesButtonType(clickedButtonMeta: ItemMeta): Boolean {
        if (!Handler.hasButtonType(clickedButtonMeta)) return false

        return Handler.getClickedButtonType(clickedButtonMeta) == buttonType
    }
}
