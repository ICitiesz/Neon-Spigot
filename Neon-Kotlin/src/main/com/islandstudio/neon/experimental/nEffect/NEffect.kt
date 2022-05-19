package com.islandstudio.neon.experimental.nEffect

import com.islandstudio.neon.Neon
import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.primary.nProfile.NProfile
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object NEffect: Listener {
    private val plugin: Plugin = getPlugin(Neon::class.java)

    /* Inventory name */
    private val inventoryName: String = "${ChatColor.GREEN}nEffect"

    /* Button names */
    val EFFECT_1: String = "${ChatColor.GREEN}Haste I"
    val EFFECT_2: String = "${ChatColor.GREEN}Haste II"
    val EFFECT_3: String = "${ChatColor.GREEN}Haste III"
    val REMOVE_BUTTON: String = "${ChatColor.RED}Remove Effect"

    private fun openEffectManager(player: Player) {
        val effectInventory: Inventory = plugin.server.createInventory(null, 9, inventoryName)

        val itemFlags: Array<ItemFlag> = arrayOf(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)

        /* Item Stacks */
        val haste1 = ItemStack(Material.DIAMOND_PICKAXE)
        val haste2 = ItemStack(Material.DIAMOND_PICKAXE)
        val haste3 = ItemStack(Material.DIAMOND_PICKAXE)
        val removeEffect = ItemStack(Material.BARRIER)

        /* Item Meta */
        val haste1Meta: ItemMeta = haste1.itemMeta!!
        val haste2Meta: ItemMeta = haste2.itemMeta!!
        val haste3Meta: ItemMeta = haste3.itemMeta!!
        val removeEffectMeta: ItemMeta = removeEffect.itemMeta!!

        /* Display names */
        haste1Meta.setDisplayName(EFFECT_1)
        haste2Meta.setDisplayName(EFFECT_2)
        haste3Meta.setDisplayName(EFFECT_3)
        removeEffectMeta.setDisplayName(REMOVE_BUTTON)

        /* Unbreakable */
        haste1Meta.isUnbreakable = true
        haste2Meta.isUnbreakable = true
        haste3Meta.isUnbreakable = true
        removeEffectMeta.isUnbreakable = true

        /* Enchantments */
        haste1Meta.addEnchant(org.bukkit.enchantments.Enchantment.DIG_SPEED, 3, false)
        haste2Meta.addEnchant(org.bukkit.enchantments.Enchantment.DIG_SPEED, 3, false)
        haste3Meta.addEnchant(org.bukkit.enchantments.Enchantment.DIG_SPEED, 3, false)
        removeEffectMeta.addEnchant(org.bukkit.enchantments.Enchantment.DIG_SPEED, 3, false)

        /* Item flags */
        haste1Meta.addItemFlags(*itemFlags)
        haste2Meta.addItemFlags(*itemFlags)
        haste3Meta.addItemFlags(*itemFlags)
        removeEffectMeta.addItemFlags(*itemFlags)

        /* Set item meta */
        haste1.itemMeta = haste1Meta
        haste2.itemMeta = haste2Meta
        haste3.itemMeta = haste3Meta
        removeEffect.itemMeta = removeEffectMeta

        /* Set item into effect inventory */
        effectInventory.setItem(0, haste1)
        effectInventory.setItem(1, haste2)
        effectInventory.setItem(2, haste3)
        effectInventory.setItem(8, removeEffect)

        player.openInventory(effectInventory)
    }

    fun setCommandHandler(commander: Player) {
        val nProfile = NProfile(commander)

        if (commander.isOp || nProfile.playerRank.equals("OWNER", true) || nProfile.playerRank.equals("VIP_PLUS", true)) {
            if (commander.isSleeping) {
                commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}You can't use nEffect while sleeping!"))
                return
            }

            openEffectManager(commander)
        } else {
            commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
            return
        }
    }

    fun setEventHandler(e: InventoryClickEvent) {
        val player: Player = e.whoClicked as Player
        val clickedInventory: Inventory? = e.clickedInventory
        val playerInventory: Inventory = player.inventory
        val itemStack: ItemStack? = e.currentItem

        if (e.view.title.equals(inventoryName, true)) {
            if (clickedInventory != null) if (clickedInventory == playerInventory) e.isCancelled = true

            if (itemStack == null || !itemStack.hasItemMeta()) e.isCancelled = true

            if (itemStack != null) {
                when (itemStack.type) {
                    Material.DIAMOND_PICKAXE -> {
                        if (itemStack.itemMeta!!.displayName.equals(EFFECT_1, true) && itemStack.itemMeta!!.isUnbreakable) {
                            if (!player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                                player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 150, true, true))
                                player.sendMessage("${ChatColor.GREEN}Effect Applied!")
                                e.isCancelled = true
                            } else {
                                e.isCancelled = true
                                player.sendMessage("${ChatColor.YELLOW}You already have this effect!")
                            }

                            plugin.server.scheduler.scheduleSyncDelayedTask(plugin, player::closeInventory, 0L)
                        } else if (itemStack.itemMeta!!.displayName.equals(EFFECT_2, true) && itemStack.itemMeta!!.isUnbreakable) {
                            if (!player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                                player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 300, true, true))
                                player.sendMessage("${ChatColor.GREEN}Effect Applied!")
                                e.isCancelled = true
                            } else {
                                e.isCancelled = true
                                player.sendMessage("${ChatColor.YELLOW}You already have this effect!")
                            }

                            plugin.server.scheduler.scheduleSyncDelayedTask(plugin, player::closeInventory, 0L)
                        } else if (itemStack.itemMeta!!.displayName.equals(EFFECT_3, true) && itemStack.itemMeta!!.isUnbreakable) {
                            if (!player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                                player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 600, true, true))
                                player.sendMessage("${ChatColor.GREEN}Effect Applied!")
                                e.isCancelled = true
                            } else {
                                e.isCancelled = true
                                player.sendMessage("${ChatColor.YELLOW}You already have this effect!")
                            }

                            plugin.server.scheduler.scheduleSyncDelayedTask(plugin, player::closeInventory, 0L)
                        }
                    }

                    Material.BARRIER -> {
                        if (itemStack.itemMeta!!.displayName.equals(REMOVE_BUTTON, true) && itemStack.itemMeta!!.isUnbreakable) {
                            if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                                player.removePotionEffect(PotionEffectType.FAST_DIGGING)
                                player.sendMessage("${ChatColor.RED}Effect Removed!")
                                e.isCancelled = true
                            } else {
                                e.isCancelled = true
                                player.sendMessage("${ChatColor.YELLOW}Effect may removed or you don't have effect to be removed!")
                            }

                            plugin.server.scheduler.scheduleSyncDelayedTask(plugin, player::closeInventory, 0L)
                        }
                    }
                    else -> {}
                }
            }


        }
    }

    fun closeInventory(e: InventoryCloseEvent) {
        val playerInventory: Inventory = e.player.inventory
        val player: Player = e.player as Player

        plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
            val inventoryContents: Array<ItemStack> = playerInventory.contents

            for (i in inventoryContents.indices) {
                val item: ItemStack? = inventoryContents[i]

                for (j in 0..9) {
                    if (item == null) continue

                    if (playerInventory.getItem(j) == null) continue

                    val playerItem: ItemStack? = playerInventory.getItem(j)!!
                    val displayName: String = item.itemMeta!!.displayName

                    if (playerItem == item && (item.type == Material.DIAMOND_PICKAXE && item.itemMeta!!.isUnbreakable)
                        && (displayName.equals(EFFECT_1, true) || displayName.equals(EFFECT_2, true)
                        || displayName.equals(EFFECT_3, true) || displayName.equals(REMOVE_BUTTON, true))) {
                        playerInventory.remove(item)
                        player.updateInventory()
                    }
                }

                if (item == null) continue

                val displayName: String = item.itemMeta!!.displayName

                if ((item.type == Material.DIAMOND_PICKAXE && item.itemMeta!!.isUnbreakable)
                    && (displayName.equals(EFFECT_1, true) || displayName.equals(EFFECT_2, true)
                    || displayName.equals(EFFECT_3, true) || displayName.equals(REMOVE_BUTTON, true))) {
                    playerInventory.remove(item)
                    player.updateInventory()
                }
            }
        }, 0L)
    }

}