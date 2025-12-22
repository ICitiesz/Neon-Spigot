package com.islandstudio.neon.item

import com.islandstudio.neon.core.datakey.AbstractDataKey
import com.islandstudio.neon.shared.core.di.IComponentProvider
import com.islandstudio.neon.shared.core.di.getComponent
import com.islandstudio.neon.shared.core.exception.NeonException
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.enchantment.EnchantmentCategory
import org.bukkit.enchantments.Enchantment

sealed class NeonEnchantment(keyName: String): AbstractDataKey(keyName), IComponentProvider {
    private val enchantmentManager = getComponent<EnchantmentManager>()

    protected abstract val enchantmentRarity: net.minecraft.world.item.enchantment.Enchantment.Rarity
    protected abstract val enchantmentCategory: EnchantmentCategory
    protected abstract val enchantmentEquipmentSlots: Array<EquipmentSlot>

    fun register() {
        enchantmentManager.registerEnchantment(this, enchantmentManager.buildMcEnchantment(this.enchantmentRarity, this.enchantmentCategory, this.enchantmentEquipmentSlots))
    }

    /**
     * Get the custom enchantment.
     *
     * @return The enchantment
     */
    fun getEnchantment(): Enchantment {
        return org.bukkit.Registry.ENCHANTMENT.get(this.dataKey) ?:
            throw NeonException("Neon enchantment '${this.dataKey}' not register yet!")
    }

    fun isRegistered(): Boolean {
        return org.bukkit.Registry.ENCHANTMENT.get(this.dataKey) != null
    }

    companion object {
        fun getAllNeonEnchantment(): ArrayList<NeonEnchantment> {
            return NeonEnchantment::class.sealedSubclasses
                .map { x -> x.objectInstance as NeonEnchantment }
                .toCollection(ArrayList())
        }
    }

    data object NeonGuiButtonHighlight: NeonEnchantment("neon.gui.button.highlight") {
        override val enchantmentRarity: net.minecraft.world.item.enchantment.Enchantment.Rarity = net.minecraft.world.item.enchantment.Enchantment.Rarity.RARE
        override val enchantmentCategory: EnchantmentCategory = EnchantmentCategory.VANISHABLE
        override val enchantmentEquipmentSlots: Array<EquipmentSlot> = emptyArray()
    }
}