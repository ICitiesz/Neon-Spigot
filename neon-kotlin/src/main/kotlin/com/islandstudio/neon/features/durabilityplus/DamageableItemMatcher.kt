package com.islandstudio.neon.features.durabilityplus

import com.google.common.collect.Multimap
import com.islandstudio.neon.core.nmsmapping.NmsManager
import com.islandstudio.neon.item.NeonItemMaterial
import com.islandstudio.neon.shared.utils.data.DataUtil
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.DiggerItem
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack


object DamageableItemMatcher {
    private val generalToolItems = NeonItemMaterial.buildItemMaterialList {
        add(Material.FLINT_AND_STEEL)
        add(Material.SHEARS)
        add(Material.FISHING_ROD)
        add(Material.CARROT_ON_A_STICK)
        add(Material.WARPED_FUNGUS_ON_A_STICK)
        add(NeonItemMaterial.BRUSH)
    }

    private val diggerToolItems = NeonItemMaterial.buildItemMaterialList {
        addAll(Material.entries
            .filter { x ->
                Tag.ITEMS_TOOLS.isTagged(x) && NmsManager.toNmsItem(ItemStack(x)) is DiggerItem
            }
        )
    }

    private val generalWeaponItems = NeonItemMaterial.buildItemMaterialList {
        addAll(Material.entries.filter { x -> Tag.ITEMS_SWORDS.isTagged(x) })
        add(Material.TRIDENT)
    }

    private val bowWeaponItems = NeonItemMaterial.buildItemMaterialList {
        add(Material.BOW)
        add(Material.CROSSBOW)
    }

    private val axeItems = NeonItemMaterial.buildItemMaterialList {
        addAll(Material.entries.filter { x -> Tag.ITEMS_AXES.isTagged(x) })
    }

    private val shovelItems = NeonItemMaterial.buildItemMaterialList {
        addAll(Material.entries.filter { x -> Tag.ITEMS_SHOVELS.isTagged(x) })
    }

    private val hoeItems = NeonItemMaterial.buildItemMaterialList {
        addAll(Material.entries.filter { x -> Tag.ITEMS_HOES.isTagged(x) })
    }

    /**
     * Matches the damageable item
     *
     * @param itemStack The item
     * @return
     */
    fun matchesItem(itemStack: ItemStack): Boolean {
        val isDamageableItemFuncName = "i" // TODO: Need add to nms mapping

        val nmsItemStack = NmsManager.toNmsItemStack(DataUtil.asType(itemStack))
        val isDamagaebleItem = DataUtil.asType<Boolean>(nmsItemStack.javaClass.getMethod(isDamageableItemFuncName).invoke(nmsItemStack))

        if (!isDamagaebleItem) return false

        return matchesGeneralToolItems(itemStack) || matchesDiggerToolItems(itemStack) || matchesGeneralWeaponItems(itemStack) || matchesBowWeaponItems(itemStack)
    }

    fun matchesGeneralToolItems(itemStack: ItemStack, vararg refGeneralToolItems: Material): Boolean {
        return itemStack.type in generalToolItems.filter { x -> if (refGeneralToolItems.isNotEmpty()) x in refGeneralToolItems else true }
    }

    fun matchesDiggerToolItems(itemStack: ItemStack): Boolean {
        return itemStack.type in diggerToolItems
    }

    fun matchesGeneralWeaponItems(itemStack: ItemStack): Boolean {
        return itemStack.type in generalWeaponItems
    }

    fun matchesBowWeaponItems(itemStack: ItemStack, vararg refBowWeaponItems: Material): Boolean {
        return itemStack.type in bowWeaponItems.filter { x -> if (refBowWeaponItems.isNotEmpty()) x in refBowWeaponItems else true }
    }

    fun matchesAxeItems(itemStack: ItemStack): Boolean {
        return itemStack.type in axeItems
    }

    fun matchesShovelItems(itemStack: ItemStack): Boolean {
        return itemStack.type in shovelItems
    }

    fun matchesHoeItems(itemStack: ItemStack): Boolean {
        return itemStack.type in hoeItems
    }

    private fun hasAttackAttribute(itemStack: ItemStack): Boolean {
        val nmsItem = NmsManager.toNmsItem(itemStack)

        return DataUtil.asType<Multimap<net.minecraft.world.entity.ai.attributes.Attribute, AttributeModifier>>(nmsItem.javaClass.getMethod("a", EquipmentSlot::class.java)
            .invoke(nmsItem, EquipmentSlot.MAINHAND)).keys().contains(Attributes.ATTACK_DAMAGE)
    }
}