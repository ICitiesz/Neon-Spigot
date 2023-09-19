package com.islandstudio.neon.stable.secondary.nDurable

import com.islandstudio.neon.experimental.nEffect.NEffect
import com.islandstudio.neon.stable.core.network.NPacketProcessor
import com.islandstudio.neon.stable.primary.nCommand.CommandHandler
import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nServerFeatures.NServerFeatures
import com.islandstudio.neon.stable.primary.nServerFeatures.ServerFeature
import com.islandstudio.neon.stable.utils.NeonKey
import com.islandstudio.neon.stable.utils.ObjectSerializer
import com.islandstudio.neon.stable.utils.reflection.NMSRemapped
import com.islandstudio.neon.stable.utils.reflection.NReflector
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.AbstractContainerMenu
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.*
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.kotlin.utils.addToStdlib.ifFalse
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue
import java.util.*

object NDurable {
    private val plugin = NConstructor.plugin

    private var isEnabled = false

    private var showItemDurability = true
    private var isFortuneHarvestRestricted = false

    object Handler: CommandHandler {
        /**
         * Initialization for nDurable.
         *
         */
        fun run() {
            isEnabled = NServerFeatures.getToggle(ServerFeature.FeatureNames.N_DURABLE)

            if (!isEnabled) {
                toggleDamageProperty(isEnabled)

                return NConstructor.unRegisterEvent(EventProcessor())
            }

            toggleDamageProperty(isEnabled)
            isFortuneHarvestRestricted = true
            showItemDurability = NServerFeatures.getOptionValue(ServerFeature.FeatureNames.N_DURABLE.featureName, "showItemDurability") as Boolean

            NConstructor.registerEventProcessor(EventProcessor())
        }

        /**
         * Apply damage property to the tool/weapon.
         *
         * @param itemStack The tool/weapon.
         * @param damagePerformed The damage that have done to the tool/weapon.
         * @return The tool/weapon with damage property applied.
         */
        fun applyDamageProperty(itemStack: ItemStack, damagePerformed: Int): ItemStack {
            if (!isItemMatch(itemStack)) return itemStack

            val itemMaxDamage = itemStack.type.maxDurability
            val damageableItemMeta = itemStack.itemMeta as Damageable
            val itemDamage = damageableItemMeta.damage
            var finalItemDamage: Int = if (damagePerformed == itemMaxDamage.toInt() || itemDamage > itemMaxDamage.toInt()) itemMaxDamage.toInt()
            else itemDamage + damagePerformed

            isItemDamaged(finalItemDamage, itemMaxDamage.toInt()).ifTrue {
                finalItemDamage = itemMaxDamage.toInt()
                damageableItemMeta.damage = itemMaxDamage.toInt()
            }

            var damagePropertyContainer: HashMap<String, Any> = hashMapOf(NeonKey.getNeonKeyNameWithNamespace(
                NeonKey.NamespaceKeys.NEON_N_DURABLE_DAMAGE) to finalItemDamage)

            if (NeonKey.hasNeonKey(NeonKey.NamespaceKeys.NEON_N_DURABLE, PersistentDataType.STRING, damageableItemMeta)) {
                damagePropertyContainer = getDamageProperty(damageableItemMeta)

                damagePropertyContainer[NeonKey.getNeonKeyNameWithNamespace(NeonKey.NamespaceKeys.NEON_N_DURABLE_DAMAGE)] = finalItemDamage
            }

            NeonKey.updateNeonKey(
                ObjectSerializer.serializeObjectEncoded(damagePropertyContainer),
                NeonKey.NamespaceKeys.NEON_N_DURABLE, PersistentDataType.STRING, damageableItemMeta)

            itemStack.itemMeta = damageableItemMeta

            showDamageProperty(itemStack)
            return itemStack
        }

        /**
         * Apply damage property to tool/weapon when player getting it from /give command
         * or from creative inventory.
         *
         * @param nPlayer The nPlayer (Player object used by packet).
         * @param rawSlotIndex The slot index in raw for the inventory.
         */
        fun applyDamagePropertyOnGive(nPlayer: ServerPlayer, rawSlotIndex: Int) {
            if (!isEnabled) return

            /* Getting the inventory view from player that received the tool/weapon
            * from /give command or getting from creative inventory */
            val inventoryView: InventoryView = (nPlayer.javaClass.superclass.getField(NMSRemapped.Mapping.NMS_PLAYER_CONTAINER.remapped).get(nPlayer)
                    as AbstractContainerMenu).bukkitView

            /* Get and check the inventory type. */
            inventoryView.getInventory(rawSlotIndex)?.let {
                if (it.type != InventoryType.PLAYER) return
            } ?: return

            val gaveItem = inventoryView.getItem(rawSlotIndex) ?: return

            applyDamageProperty(gaveItem, 0)
        }

        /**
         * Apply damage property to the tool/weapon when player trading with Tool Smith/Weapon Smith Villager.
         *
         * @param villager The villager.
         */
        fun applyDamagePropertyOnTrading(villager: Entity) {
            if (villager !is Villager) return

            /* Villager profession check */
            if (!(villager.profession == Villager.Profession.TOOLSMITH || villager.profession == Villager.Profession.WEAPONSMITH)) return

            /* NMS Remapped */
            val merchantRecipeResultNMS = NMSRemapped.Mapping.NMS_MERCHANT_RECIPE_RESULT.remapped

            villager.recipes.forEach {
                val merchantRecipeField = it.javaClass.getDeclaredField("handle")
                merchantRecipeField.isAccessible = true

                val merchantRecipe = merchantRecipeField.get(it)

                /* Damage property for trade offer ingredient */
                it.ingredients.forEach InnerFE@ { ingredient ->
                    if (!isItemMatch(ingredient)) return@InnerFE

                    val originalIngredient = merchantRecipe.javaClass.getDeclaredField(merchantRecipeResultNMS)
                    originalIngredient.isAccessible = true

                    val newIngredient = if (isEnabled) applyDamageProperty(ingredient, 0)
                    else hideDamageProperty(ingredient)

                    val baseItemStack = NReflector.getCraftBukkitClass("inventory.CraftItemStack")
                        .getMethod("asNMSCopy", ItemStack::class.java).invoke(null, newIngredient)

                    originalIngredient.set(merchantRecipe, baseItemStack)
                }

                /* Damage property for trade offer result */
                val originalResult = merchantRecipe.javaClass.getDeclaredField(merchantRecipeResultNMS)
                originalResult.isAccessible = true

                val newResult = if (isEnabled) applyDamageProperty(it.result, 0)
                else hideDamageProperty(it.result)

                val baseItemStack = NReflector.getCraftBukkitClass("inventory.CraftItemStack")
                    .getMethod("asNMSCopy", ItemStack::class.java).invoke(null, newResult)

                originalResult.set(merchantRecipe, baseItemStack)
            }
        }

        /**
         * Remove damage property from the tool/weapon that have applied the damage property.
         *
         * @param damageableItem The tool/weapon.
         */
        private fun removeDamageProperty(damageableItem: ItemStack) {
            if (!isItemMatch(damageableItem)) return

            hideDamageProperty(damageableItem)

            NeonKey.removeNeonKey(NeonKey.NamespaceKeys.NEON_N_DURABLE, PersistentDataType.STRING, damageableItem)
        }

        /**
         * Get damage property that already applied to the tool/weapon.
         *
         * @param damageableItemMeta The item meta for the tool/weapon.
         * @return The damage property.
         */
        fun getDamageProperty(damageableItemMeta: Damageable): HashMap<String, Any> {
            if (!NeonKey.hasNeonKey(NeonKey.NamespaceKeys.NEON_N_DURABLE, PersistentDataType.STRING, damageableItemMeta)) {
                return hashMapOf()
            }

            @Suppress("UNCHECKED_CAST")
            return ObjectSerializer.deserializeObjectEncoded(
                NeonKey.getNeonKeyValue(NeonKey.NamespaceKeys.NEON_N_DURABLE, PersistentDataType.STRING, damageableItemMeta) as String)
                    as HashMap<String, Any>
        }

        override fun setCommandHandler(commander: Player, args: Array<out String>) {
            commander.isOp.ifFalse {
                commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
                return
            }

            when (args.size) {
                2 -> {
                    args[1].equals("removeDamageProperty", true).ifFalse {
                        commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                        return
                    }

                    val damageableItem = commander.inventory.itemInMainHand

                    (damageableItem.type == Material.AIR).ifTrue {
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.RED}No tool/weapon has been selected by your main hand!"))
                        return
                    }

                    isItemMatch(damageableItem).ifFalse {
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.RED}Invalid tool/weapon!"))
                        return
                    }

                    removeDamageProperty(damageableItem)
                    commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.GREEN}Damage property for " +
                            "${ChatColor.GOLD}${getDamageableItemName(damageableItem)}${ChatColor.GREEN} has been removed!"))
                }

                3 -> {
                    args[1].equals("removeDamageProperty", true).ifFalse {
                        commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                        return
                    }

                    val targetPlayerName = args[2]

                    plugin.server.onlinePlayers.find { it.name == targetPlayerName }?.let {
                        it.inventory.contents.filterNotNull().filter { contentItem -> isItemMatch(contentItem) }
                            .forEach { damageableItem ->
                                removeDamageProperty(damageableItem)
                            }
                    } ?: commander.sendMessage(CommandSyntax.createSyntaxMessage(
                        "${ChatColor.YELLOW}No such player as ${ChatColor.GRAY}'${ChatColor.WHITE}" +
                                "${targetPlayerName}${ChatColor.GRAY}'${ChatColor.YELLOW}!"))

                    commander.sendMessage(CommandSyntax.createSyntaxMessage(
                        "${ChatColor.GREEN}Damage property for tool(s)/weapon(s) in ${ChatColor.GOLD}" +
                                "${targetPlayerName}${ChatColor.GREEN}'s inventory has been validated and removed!"))
                }

                else -> {
                    commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    return
                }
            }
        }

        override fun tabCompletion(commander: Player, args: Array<out String>): MutableList<String> {
            if (!commander.isOp) return super.tabCompletion(commander, args)

            when (args.size) {
                2 -> {
                    return listOf("removeDamageProperty").filter { it.startsWith(args[1], true) }.toMutableList()
                }

                3 -> {
                    if (!args[1].equals("removeDamageProperty", true)) return super.tabCompletion(commander, args)

                    return plugin.server.onlinePlayers.parallelStream().map { targetPlayer -> targetPlayer.name }
                        .toList().filter { it.startsWith(args[2]) }.toMutableList()
                }
            }

            return super.tabCompletion(commander, args)
        }
    }

    fun isEnabled() = isEnabled

    /**
     * Toggle damage property for player and villager.
     *
     * @param isEnabled The toggle status of nDurable.
     * @param player The specific player.
     */
    fun toggleDamageProperty(isEnabled: Boolean, player: Player? = null) {
        if (player != null) {
            player.inventory.contents.filterNotNull()
                .filter { contentItem -> contentItem.itemMeta is Damageable }
                .filter { damageableItem -> isItemMatch(damageableItem) }.forEach innerFE@ { damageableItem ->
                    isEnabled.ifTrue {
                        Handler.applyDamageProperty(damageableItem, 0)

                        return@innerFE
                    }

                    hideDamageProperty(damageableItem)
                }

            return
        }
        /* Toggle damage property display for all online players if no player specify  */
        plugin.server.onlinePlayers.forEach { onlinePlayer ->
            onlinePlayer.inventory.contents.filterNotNull()
                .filter { contentItem -> contentItem.itemMeta is Damageable }
                .filter { damageableItem -> isItemMatch(damageableItem) }.forEach innerFE@ { damageableItem ->
                    isEnabled.ifTrue {
                        Handler.applyDamageProperty(damageableItem, 0)

                        return@innerFE
                    }

                    hideDamageProperty(damageableItem)
                }
        }

        isEnabled.ifFalse {
            /* Hide damage property display from all tool smith villager & weapon smith villager */
            plugin.server.worlds.forEach {
                it.entities.parallelStream()
                    .filter { entity -> entity is Villager }
                    .filter { entity -> (entity as Villager).profession == Villager.Profession.TOOLSMITH
                            || entity.profession == Villager.Profession.WEAPONSMITH }.forEach { entity ->
                        Handler.applyDamagePropertyOnTrading(entity as Villager)
                    }
            }
        }
    }

    /**
     * Once damage property applied or updated, it will show as Lore within the tool tip of the tool/weapon.
     *
     * @param itemStack The tool/weapon.
     */
    private fun showDamageProperty(itemStack: ItemStack) {
        if (!isItemMatch(itemStack)) return

        val damageableItemMeta = itemStack.itemMeta as Damageable
        val damageProperty: HashMap<String, Any> = Handler.getDamageProperty(damageableItemMeta)

        if (damageProperty.isEmpty()) return

        /* Check and get damage detail of the tool/weapon. */
        val itemDamage = damageProperty.entries.find { it.key == NeonKey.getNeonKeyNameWithNamespace(
            NeonKey.NamespaceKeys.NEON_N_DURABLE_DAMAGE) }?.value ?: return

        val itemMaxDurability = itemStack.type.maxDurability
        val damagePropertyDisplay: MutableList<String> = if (damageableItemMeta.hasLore()) damageableItemMeta.lore!! else mutableListOf()

        val damagedTag = "${net.md_5.bungee.api.ChatColor.of("#ab0000")}DAMAGED"

        /* Add new line as separator */
        if (damagePropertyDisplay.isEmpty() || damagePropertyDisplay.first() != "") {
            damagePropertyDisplay.add(0, "")
        }

        /* Showing item durability as percentage if the 'showItemDurability' option is true */
        showItemDurability.ifTrue {
            damagePropertyDisplay.find { it == damagedTag }?.let {
                damagePropertyDisplay.removeAt(damagePropertyDisplay.indexOf(it))
            }

            val currentItemDurability = itemMaxDurability - (itemDamage as Int)
            /* Percentage format: #.##% */
            val percentageFormat: String = String.format("%.2f",
                (currentItemDurability.toDouble() / itemMaxDurability.toDouble()) * 100)

            when  {
                percentageFormat.toDouble() == 100.0 -> {
                    val DARK_GREEN_COLOR = net.md_5.bungee.api.ChatColor.of("#00ba19")

                    damagePropertyDisplay.find { it.contains("${ChatColor.GRAY}Durability:") }?.let {
                        damagePropertyDisplay[damagePropertyDisplay.indexOf(it)] = "${ChatColor.GRAY}Durability: ${DARK_GREEN_COLOR}${percentageFormat}%"
                        return@ifTrue
                    }

                    damagePropertyDisplay.add("${ChatColor.GRAY}Durability: ${DARK_GREEN_COLOR}${percentageFormat}%")
                }

                percentageFormat.toDouble() in 75.0..99.99 -> {
                    val LIGHT_GREEN_COLOR = net.md_5.bungee.api.ChatColor.of("#5bff14")

                    damagePropertyDisplay.find { it.contains("${ChatColor.GRAY}Durability:") }?.let {
                        damagePropertyDisplay[damagePropertyDisplay.indexOf(it)] = "${ChatColor.GRAY}Durability: ${LIGHT_GREEN_COLOR}${percentageFormat}%"
                        return@ifTrue
                    }

                    damagePropertyDisplay.add("${ChatColor.GRAY}Durability: ${LIGHT_GREEN_COLOR}${percentageFormat}%")
                }

                percentageFormat.toDouble() in 50.0 .. 74.99 -> {
                    val YELLOW_COLOR = net.md_5.bungee.api.ChatColor.of("#ddff00")

                    damagePropertyDisplay.find { it.contains("${ChatColor.GRAY}Durability:") }?.let {
                        damagePropertyDisplay[damagePropertyDisplay.indexOf(it)] = "${ChatColor.GRAY}Durability: ${YELLOW_COLOR}${percentageFormat}%"
                        return@ifTrue
                    }

                    damagePropertyDisplay.add("${ChatColor.GRAY}Durability: ${YELLOW_COLOR}${percentageFormat}%")
                }

                percentageFormat.toDouble() in 25.0 .. 49.99 -> {
                    val ORANGE_COLOR = net.md_5.bungee.api.ChatColor.of("#ffbb00")

                    damagePropertyDisplay.find { it.contains("${ChatColor.GRAY}Durability:") }?.let {
                        damagePropertyDisplay[damagePropertyDisplay.indexOf(it)] = "${ChatColor.GRAY}Durability: ${ORANGE_COLOR}${percentageFormat}%"
                        return@ifTrue
                    }

                    damagePropertyDisplay.add("${ChatColor.GRAY}Durability: ${ORANGE_COLOR}${percentageFormat}%")
                }

                (percentageFormat.toDouble() > 0.0 && percentageFormat.toDouble() <= 24.99) -> {
                    val LIGHT_RED_COLOR = net.md_5.bungee.api.ChatColor.of("#ff4538")

                    damagePropertyDisplay.find { it.contains("${ChatColor.GRAY}Durability:") }?.let {
                        damagePropertyDisplay[damagePropertyDisplay.indexOf(it)] = "${ChatColor.GRAY}Durability: ${LIGHT_RED_COLOR}${percentageFormat}%"
                        return@ifTrue
                    }

                    damagePropertyDisplay.add("${ChatColor.GRAY}Durability: ${LIGHT_RED_COLOR}${percentageFormat}%")
                }

                percentageFormat.toDouble() == 0.0 -> {
                    val DARK_RED_COLOR = net.md_5.bungee.api.ChatColor.of("#ab0000")

                    damagePropertyDisplay.find { it.contains("${ChatColor.GRAY}Durability:") }?.let {
                        damagePropertyDisplay[damagePropertyDisplay.indexOf(it)] = "${ChatColor.GRAY}Durability: ${DARK_RED_COLOR}0% ${ChatColor.GRAY}| $damagedTag"
                        return@ifTrue
                    }

                    damagePropertyDisplay.add("${ChatColor.GRAY}Durability: ${DARK_RED_COLOR}0% ${ChatColor.GRAY}| $damagedTag")
                }

                else -> {}
            }
        }

        /* Hide item durability as percentage if the 'showItemDurability' option is false */
        showItemDurability.ifFalse {
            damagePropertyDisplay.find { it.contains("${ChatColor.GRAY}Durability:") }?.let {
                damagePropertyDisplay.removeAt(damagePropertyDisplay.indexOf(it))
            }
        }

        /* Show 'DAMAGED' tag if the tool/weapon is broken */
        isItemDamaged(itemDamage as Int, itemMaxDurability.toInt()).ifTrue {
            if (showItemDurability) return@ifTrue

            if (damagePropertyDisplay.any {it == damagedTag }) return@ifTrue

            damagePropertyDisplay.add(damagedTag)
        }

        /* Remove 'DAMAGED' tag after the tool/weapon being repaired */
        isItemDamaged(itemDamage, itemMaxDurability.toInt()).ifFalse {
            if (showItemDurability) return@ifFalse

            damagePropertyDisplay.removeFirst()

            damagePropertyDisplay.find {it == damagedTag }?.let {
                damagePropertyDisplay.removeAt(damagePropertyDisplay.indexOf(it))
            }
        }

        damageableItemMeta.lore = damagePropertyDisplay
        itemStack.itemMeta = damageableItemMeta
    }

    /**
     * Hide damage property display from the tool/weapon.
     *
     * @param itemStack The tool/weapon.
     * @return The hidden damage property display of tool/weapon.
     */
    private fun hideDamageProperty(itemStack: ItemStack): ItemStack {
        if (!isItemMatch(itemStack)) return itemStack

        val damageableItemMeta = itemStack.itemMeta as Damageable

        val damageProperty: MutableList<String> = if (damageableItemMeta.hasLore()) damageableItemMeta.lore!! else return itemStack

        damageProperty.removeAt(0)

        damageProperty.find { it.contains("${ChatColor.GRAY}Durability:") }?.let {
            damageProperty.remove(it)
        }

        damageableItemMeta.lore = damageProperty
        itemStack.itemMeta = damageableItemMeta

        return itemStack
    }

    /**
     * Cancel fortune harvest performed by nHarvest if the tool/weapon has been damaged.
     *
     * @param heldItem Tools/Weapon.
     * @param player The player who perform the harvest.
     * @return
     */
    fun revokeFortuneHarvest(heldItem: ItemStack, player: Player): Boolean {
        if (!isFortuneHarvestRestricted) return false

        if (!isItemMatch(heldItem)) return false

        val heldItemItemMeta = heldItem.itemMeta as Damageable

        Handler.getDamageProperty(heldItemItemMeta).run {
            this.isNotEmpty().ifTrue {
                if (!isItemDamaged(this[NeonKey.getNeonKeyNameWithNamespace(NeonKey.NamespaceKeys.NEON_N_DURABLE_DAMAGE)] as Int,
                        heldItem.type.maxDurability.toInt())
                ) return false
            }

            if (!isItemDamaged(heldItemItemMeta.damage, heldItem.type.maxDurability.toInt())) return false
            Handler.applyDamageProperty(heldItem, 0)
        }

        return player.gameMode != GameMode.CREATIVE
    }

    /**
     * Get the display name of the tool/weapon.
     *
     * @param damageableItem The tool/weapon.
     * @return The display name of the tool/weapon.
     */
    fun getDamageableItemName(damageableItem: ItemStack): String {
        val damageableItemMeta = damageableItem.itemMeta ?: return ""

        if (damageableItemMeta.hasDisplayName()) return "${ChatColor.ITALIC}${damageableItemMeta.displayName}"

        val damageableItemName = damageableItem.type.name

        damageableItemName.contains("_").ifTrue {
            var tempDamageableItemName = ""

            damageableItemName.split("_").forEach {
                tempDamageableItemName += it.lowercase().replaceFirstChar { splitName ->
                    if (splitName.isLowerCase()) {
                        splitName.titlecase(Locale.getDefault())
                    } else {
                        splitName.toString()
                    }
                }.plus(" ")
            }

             return tempDamageableItemName.trimEnd()
        }

        return damageableItemName.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())

            else it.toString()
        }
    }

    /**
     * Revoke alternative use when using certain damaged tool.
     *
     * @param player The player who using the damaged tool.
     * @param damageableItem The damaged tool.
     * @param useAction Use action like Left/Right-Clicking on Air/Block.
     * @param useOnBlock The target block where use action on.
     * @return True if it can be revoked, else false
     */
    fun revokeAlternativeUse(player: Player, damageableItem: ItemStack, useAction: Action, useOnBlock: Block?): Boolean {
        if (!isItemMatch(damageableItem)) return false

        val damageableItemMeta = damageableItem.itemMeta as Damageable

        Handler.getDamageProperty(damageableItemMeta).run {
            this.isNotEmpty().ifTrue {
                if (!isItemDamaged(this[NeonKey.getNeonKeyNameWithNamespace(NeonKey.NamespaceKeys.NEON_N_DURABLE_DAMAGE)] as Int,
                        damageableItem.type.maxDurability.toInt())
                ) return false
            }

            if (!isItemDamaged(damageableItemMeta.damage, damageableItem.type.maxDurability.toInt())) return false
            Handler.applyDamageProperty(damageableItem, 0)
        }

        when {
            /* Axe usages */
            isItemMatch(damageableItem, DamageableItems.Items.AXE) -> {
                if (useAction != Action.RIGHT_CLICK_BLOCK) return false

                useOnBlock?.let { block ->
                    val blockName = block.type.name

                    if (blockName.startsWith("stripped_", true)) return false

                    DamageableItems.Blocks.values().filter {
                            !(it == DamageableItems.Blocks.PUMPKIN
                            || it == DamageableItems.Blocks.COMMAND_BLOCK
                            || it == DamageableItems.Blocks.FENCE
                            || it == DamageableItems.Blocks.ROOTED_DIRT
                            || it == DamageableItems.Blocks.GRASS_BLOCK
                            || it == DamageableItems.Blocks.DIRT_BLOCK
                            || it == DamageableItems.Blocks.DIRT_PATH
                            || it == DamageableItems.Blocks.COARSE_DIRT) }.find {
                        blockName.contains(it.blockName, true)
                    }?.let InnerLet@ { return@let } ?: return false
                } ?: return false
            }

            /* Land tilling */
            isItemMatch(damageableItem, DamageableItems.Items.HOE) -> {
                if (useAction != Action.RIGHT_CLICK_BLOCK) return false

                useOnBlock?.let { block ->
                    val blockName = block.type.name

                    DamageableItems.Blocks.values().filter {
                        it == DamageableItems.Blocks.ROOTED_DIRT
                            || it == DamageableItems.Blocks.GRASS_BLOCK
                            || it == DamageableItems.Blocks.DIRT_BLOCK
                            || it == DamageableItems.Blocks.DIRT_PATH
                            || it == DamageableItems.Blocks.COARSE_DIRT
                    }.find {
                        blockName.equals(it.blockName, true)
                    }?.let InnerLet@ { return@let } ?: return false
                } ?: return false
            }

            /* Fishing */
            isItemMatch(damageableItem, DamageableItems.Items.FISHING_ROD) -> {
                if (!(useAction == Action.RIGHT_CLICK_AIR || useAction == Action.RIGHT_CLICK_BLOCK)) return false
            }

            /* Pumpkin carving */
            isItemMatch(damageableItem, DamageableItems.Items.SHEARS) -> {
                if (useAction != Action.RIGHT_CLICK_BLOCK) return false

                useOnBlock?.let {
                    if (!it.type.name.equals(DamageableItems.Blocks.PUMPKIN.blockName, true)) return false
                } ?: return false
            }

            /* Ignition on block */
            isItemMatch(damageableItem, DamageableItems.Items.FLINT_AND_STEEL) -> {
                if (useAction != Action.RIGHT_CLICK_BLOCK) return false

                useOnBlock?.let { block ->
                    val isInteratable = block.type.isInteractable

                    if (!isInteratable) return@let

                    when {
                        block.type.name.contains(DamageableItems.Blocks.COMMAND_BLOCK.name, true) -> {
                            return@let
                        }

                        (block.type.name.contains(DamageableItems.Blocks.FENCE.name, true)
                                || block.type.name.contains(DamageableItems.Blocks.PUMPKIN.name, true)) && !player.isSneaking -> {
                            return@let
                        }

                        !block.type.name.contains(DamageableItems.Blocks.COMMAND_BLOCK.name, true) && !player.isSneaking -> {
                            return false
                        }
                    }
                } ?: return false
            }

            /* Crossbow shooting */
            isItemMatch(damageableItem, DamageableItems.Items.CROSSBOW) -> {
                useAction.run {
                    when (this) {
                        Action.RIGHT_CLICK_AIR -> { return@run }

                        Action.RIGHT_CLICK_BLOCK -> {
                            useOnBlock?.let { block ->
                                val isInteratable = block.type.isInteractable

                                if (!isInteratable) return@let

                                when {
                                    block.type.name.contains(DamageableItems.Blocks.COMMAND_BLOCK.name, true) -> {
                                        return@let
                                    }

                                    (block.type.name.contains(DamageableItems.Blocks.FENCE.name, true)
                                            || block.type.name.contains(DamageableItems.Blocks.PUMPKIN.name, true)) && !player.isSneaking -> {
                                        return@let
                                    }

                                    !block.type.name.contains(DamageableItems.Blocks.COMMAND_BLOCK.name, true) && !player.isSneaking -> {
                                        return false
                                    }
                                }
                            } ?: return false
                        }

                        else -> { return false }
                    }
                }
            }

            /* Right-clicking on specific block with other damaged tool/weapon */
            isItemMatch(damageableItem) -> {
                return false
            }
        }

        if (player.gameMode == GameMode.CREATIVE) return false

        alertItemDamaged(player, damageableItem)
        return true
    }

    /**
     * Revoke creeper ignition when using damaged Flint & Steel to ignite creeper.
     *
     * @param player The player who perform the ignition.
     * @param creeper Creeper Aww man.
     * @return True if it can be revoked, else false.
     */
    fun revokeCreeperIgnition(player: Player, creeper: Entity): Boolean {
        if (creeper !is Creeper) return false

        val lighter = when {
                player.inventory.itemInMainHand.type == Material.FLINT_AND_STEEL -> {
                    player.inventory.itemInMainHand
                }

                player.inventory.itemInOffHand.type == Material.FLINT_AND_STEEL -> {
                    player.inventory.itemInOffHand
                }
            else -> {
                return false
            }
        }

        if (!isItemMatch(lighter)) return false

        val damageableItemMeta = lighter.itemMeta as Damageable

        Handler.getDamageProperty(damageableItemMeta).run {
            this.isNotEmpty().ifTrue {
                if (!isItemDamaged(this[NeonKey.getNeonKeyNameWithNamespace(NeonKey.NamespaceKeys.NEON_N_DURABLE_DAMAGE)] as Int,
                        lighter.type.maxDurability.toInt())
                ) return false
            }

            if (!isItemDamaged(damageableItemMeta.damage, lighter.type.maxDurability.toInt())) return false
            Handler.applyDamageProperty(lighter, 0)
        }

        if (player.gameMode == GameMode.CREATIVE) return false

        alertItemDamaged(player, lighter)
        return true
    }

    /**
     * Revoke any attack action if the current using tool/weapon has been damaged.
     *
     * @param attacker The player who attack any other entity using damaged tool/weapon.
     * @return True if it can be revoked, else false
     */
    fun revokeAttack(attacker: Entity): Boolean {
        if (attacker !is Player) return false

        val damageableItem: ItemStack = attacker.inventory.itemInMainHand

        if (!isItemMatch(damageableItem)) return false

        if (isItemMatch(damageableItem, DamageableItems.Items.FISHING_ROD)
            || isItemMatch(damageableItem, DamageableItems.Items.FLINT_AND_STEEL)
            || isItemMatch(damageableItem, DamageableItems.Items.SHEARS)
            || isItemMatch(damageableItem, DamageableItems.Items.CROSSBOW)
            || isItemMatch(damageableItem, DamageableItems.Items.BOW)
        ) {
            return false
        }

        val damageableItemMeta = damageableItem.itemMeta as Damageable

        Handler.getDamageProperty(damageableItemMeta).run {
            this.isNotEmpty().ifTrue {
                if (!isItemDamaged(this[NeonKey.getNeonKeyNameWithNamespace(NeonKey.NamespaceKeys.NEON_N_DURABLE_DAMAGE)] as Int,
                        damageableItem.type.maxDurability.toInt())
                ) return false
            }

            if (!isItemDamaged(damageableItemMeta.damage, damageableItem.type.maxDurability.toInt())) return false
            Handler.applyDamageProperty(damageableItem, 0)
        }

        if (attacker.gameMode == GameMode.CREATIVE) return false

        alertItemDamaged(attacker, damageableItem)
        return true
    }

    /**
     * Revoke wool shearing action by player if the Shears has been damaged.
     *
     * @param player The player who perform wool shearing.
     * @param shears The Shears.
     * @return True if it can be revoked, else false.
     */
    fun revokeWoolShearing(player: Player, shears: ItemStack): Boolean {
        if (!isItemMatch(shears, DamageableItems.Items.SHEARS)) return false

        val shearsItemMeta = shears.itemMeta as Damageable

        Handler.getDamageProperty(shearsItemMeta).run {
            this.isNotEmpty().ifTrue {
                if (!isItemDamaged(this[NeonKey.getNeonKeyNameWithNamespace(NeonKey.NamespaceKeys.NEON_N_DURABLE_DAMAGE)] as Int,
                        shears.type.maxDurability.toInt())
                ) return false
            }

            if (!isItemDamaged(shearsItemMeta.damage, shears.type.maxDurability.toInt())) return false
            Handler.applyDamageProperty(shears, 0)
        }

        if (player.gameMode == GameMode.CREATIVE) return false

        alertItemDamaged(player, shears)
        return true
    }


    /**
     * Revoke bow shooting by player if the Bow has been damaged.
     *
     * @param shooter The player who perform the Bow shooting.
     * @param bow The Bow.
     * @return True if it can be revoked, else false.
     */
    fun revokeBowShooting(shooter: Entity, bow: ItemStack): Boolean {
        if (!(isItemMatch(bow, DamageableItems.Items.BOW) || isItemMatch(bow, DamageableItems.Items.CROSSBOW))) return false

        if (shooter !is Player) return false

        val bowItemMeta = bow.itemMeta as Damageable

        Handler.getDamageProperty(bowItemMeta).run {
            this.isNotEmpty().ifTrue {
                if (!isItemDamaged(this[NeonKey.getNeonKeyNameWithNamespace(NeonKey.NamespaceKeys.NEON_N_DURABLE_DAMAGE)] as Int,
                        bow.type.maxDurability.toInt())
                ) return false
            }

            if (!isItemDamaged(bowItemMeta.damage, bow.type.maxDurability.toInt())) return false
            Handler.applyDamageProperty(bow, 0)
        }

        if (shooter.gameMode == GameMode.CREATIVE) return false

        alertItemDamaged(shooter, bow)
        return true
    }

    /**
     * Alert player that the current tool/weapon has been damaged.
     *
     * @param player The player who using the damaged tool/weapon.
     * @param damagedItem The damaged tool/weapon.
     */
    private fun alertItemDamaged(player: Player, damagedItem: ItemStack) {
        val alertMsg = "${ChatColor.GOLD}${getDamageableItemName(damagedItem)} " +
                "${ChatColor.RED}has been damaged!"

        val actionTitlePacket = ClientboundSetActionBarTextPacket(
            Component.Serializer.fromJson("{\"text\":\"${alertMsg}\"}")
        )

        NPacketProcessor.sendGamePacket(player, actionTitlePacket)
    }

    /**
     * Match the given item with DamageableItems
     *
     * @param itemToMatch The given item to match.
     * @param referenceItem The specific item as reference to match if available.
     * @return True if the given item is match with DamageableItems else false.
     */
    private fun isItemMatch(itemToMatch: ItemStack, referenceItem: DamageableItems.Items? = null): Boolean {
        val itemName = itemToMatch.type.name

        if (referenceItem == null) {
            return DamageableItems.Items.values().any { itemName.contains(it.itemName, true) }
        }

        if (!itemName.contains(referenceItem.itemName, true)) return false

        if (!(referenceItem == DamageableItems.Items.BOW || referenceItem == DamageableItems.Items.CROSSBOW)) return true

        return itemName.length == referenceItem.itemName.length
    }

    /**
     * Check if the given item durability is less than the item max durability.
     *
     * @param itemDamageCount The current durability of the given item.
     * @param itemMaxDurability The max durability of the given item.
     * @return True if the given item durability is less than the item max durability else false.
     */
    private fun isItemDamaged(itemDamageCount: Int, itemMaxDurability: Int): Boolean = itemDamageCount >= itemMaxDurability

    /**
     * Recover tool/weapon durability either by repair it from Anvil or from Mending enchantment.
     *
     * @param damagedItem The damaged tool/weapon.
     * @param recoverFromMending The repair amount from the Mending enchantment.
     * @return The item meta with updated damaged property after being repaired.
     */
    fun recoverItemDurability(damagedItem: ItemStack, recoverFromMending: Int = 0): ItemMeta {
        if (!isItemMatch(damagedItem)) return damagedItem.itemMeta!!

        Handler.applyDamageProperty(damagedItem, recoverFromMending)

        return damagedItem.itemMeta!!
    }

    /**
     * Revoke any block breaking action if the current using tool/weapon has been damaged.
     *
     * @param player The player who perform block breaking.
     * @param brokenBlock The target broken block.
     * @return True if it can be revoked, else false.
     */
    fun revokeBlockBreaking(player: Player, brokenBlock: Block): Boolean {
        val damageableItem = player.inventory.itemInMainHand

        if (!isItemMatch(damageableItem)) return false

        if (isItemMatch(damageableItem, DamageableItems.Items.FISHING_ROD)
            || isItemMatch(damageableItem, DamageableItems.Items.FLINT_AND_STEEL)
            || isItemMatch(damageableItem, DamageableItems.Items.CROSSBOW)
            || isItemMatch(damageableItem, DamageableItems.Items.BOW)
        ) {
            return false
        }

        val damageableItemMeta = damageableItem.itemMeta as Damageable

        Handler.getDamageProperty(damageableItemMeta).run {
            this.isNotEmpty().ifTrue {
                if (!isItemDamaged(this[NeonKey.getNeonKeyNameWithNamespace(NeonKey.NamespaceKeys.NEON_N_DURABLE_DAMAGE)] as Int,
                        damageableItem.type.maxDurability.toInt())
                ) return false
            }

            if (!isItemDamaged(damageableItemMeta.damage, damageableItem.type.maxDurability.toInt())) return false
            Handler.applyDamageProperty(damageableItem, 0)
        }

        val isInstantBreakBlock = (brokenBlock.javaClass.getMethod("getBreakSpeed", Player::class.java)
            .invoke(brokenBlock, player) as Float) >= 1.0f

        if (isInstantBreakBlock && !isItemMatch(damageableItem, DamageableItems.Items.SHEARS) || player.gameMode == GameMode.CREATIVE) {
            return false
        }

        alertItemDamaged(player, damageableItem)
        return true
    }

    private class EventProcessor: Listener {
        @EventHandler
        private fun onItemDamage(e: PlayerItemDamageEvent) {
            val item = e.item
            val itemDamage = e.damage

            if (!isItemMatch(item)) return

            isItemDamaged((item.itemMeta as Damageable).damage + itemDamage,
                item.type.maxDurability.toInt()).ifTrue {
                    val player = e.player

                    player.world.playSound(player.location, Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f)

                    e.isCancelled = true
                }

            Handler.applyDamageProperty(item, itemDamage)
        }

        @EventHandler
        private fun onBlockBreak(e: BlockBreakEvent) {
            if (revokeBlockBreaking(e.player, e.block)) e.isCancelled = true
        }

        @EventHandler
        private fun onPlayerInteract(e: PlayerInteractEvent) {
            e.item?.let {
                if (revokeAlternativeUse(e.player, it, e.action, e.clickedBlock)) e.isCancelled = true
            }
        }

        @EventHandler
        private fun onPlayerInteractOnEntity(e: PlayerInteractEntityEvent) {
            if (revokeCreeperIgnition(e.player, e.rightClicked)) e.isCancelled = true

            Handler.applyDamagePropertyOnTrading(e.rightClicked)
        }

        @EventHandler
        private fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {
            if (revokeAttack(e.damager)) e.isCancelled = true
        }

        @EventHandler
        private fun onWoolShear(e: PlayerShearEntityEvent) {
            if (revokeWoolShearing(e.player, e.item)) e.isCancelled = true
        }

        @EventHandler
        private fun onBowShooting(e: EntityShootBowEvent) {
            if (revokeBowShooting(e.entity, e.bow!!)) {
                e.isCancelled = true
                (e.entity as Player).updateInventory()
            }
        }

        @EventHandler
        private fun onRepairByAnvil(e: PrepareAnvilEvent) {
            e.result?.let {
                if (it.type == Material.AIR) return
                recoverItemDurability(it)
            }
        }

        @EventHandler
        private fun onRepairByMending(e: PlayerItemMendEvent) {
            recoverItemDurability(e.item, -e.repairAmount)
        }

        @EventHandler
        private fun onCraftingItem(e: PrepareItemCraftEvent) {
            val craftItem = e.view.getItem(0)?.let {
                if (it.type == Material.AIR) return
                it
            } ?: return

            e.isRepair.ifTrue {
                recoverItemDurability(craftItem)
                return
            }

            Handler.applyDamageProperty(craftItem, 0)
        }

        @EventHandler
        private fun onItemPickup(e: EntityPickupItemEvent) {
            if (e.entityType != EntityType.PLAYER) return

            Handler.applyDamageProperty(e.item.itemStack, 0)
        }

        @EventHandler
        private fun onItemSpawn(e: ItemSpawnEvent) {
            Handler.applyDamageProperty(e.entity.itemStack, 0)
        }

        @EventHandler
        private fun onChestLootGenerate(e: LootGenerateEvent) {
            if (!e.lootTable.key.toString().startsWith("minecraft:chests")) return

            e.setLoot(e.loot.fold(mutableListOf<ItemStack>()) { newChestLoot, itemStack ->
                Handler.applyDamageProperty(itemStack, 0)
                newChestLoot.add(itemStack)
                newChestLoot
            })
        }

        @EventHandler
        private fun onInventoryOpen(e: InventoryOpenEvent) {
            if (e.view.title == NEffect.inventoryName) return

            e.inventory.contents.filterNotNull().forEach {
                Handler.applyDamageProperty(it, 0)
            }
        }
    }
}