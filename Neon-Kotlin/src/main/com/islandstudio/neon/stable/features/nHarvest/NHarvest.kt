package com.islandstudio.neon.stable.features.nHarvest

import com.islandstudio.neon.stable.core.application.init.NConstructor
import com.islandstudio.neon.stable.features.nDurable.NDurable
import com.islandstudio.neon.stable.features.nServerFeatures.NServerFeaturesRemastered
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.data.Ageable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object NHarvest {
    private var isEnabled = false

    object Handler {
        fun run() {
            isEnabled = (NServerFeaturesRemastered.serverFeatureSession.getActiveServerFeatureToggle("nHarvest") ?: false).also {
                if (!it) {
                    return NConstructor.unRegisterEventProcessor(EventProcessor())
                }
            }

            NConstructor.registerEventProcessor(EventProcessor())
        }
    }

    /**
     * Harvest the crops.
     *
     * @param block Block to harvest. (Block)
     * @param heldItem Item in hand. (ItemStack)
     * @param hasItem True if the player has an item in hand. (Boolean)
     */
    private fun harvest(block: Block, heldItem: ItemStack?, hasItem: Boolean, player: Player) {
        var unplantable: ItemStack? = null
        var plantable: ItemStack? = null
        val world: World = block.world
        val location: Location = block.location

        var dropsAmount = 0

        if (hasItem) {
            if (heldItem == null) return

            if (NDurable.revokeFortuneHarvest(heldItem, player)) {
                harvest(block, heldItem, hasItem = false, player)
                return
            }

            block.getDrops(heldItem).forEach {
                when (it.type) {
                    Material.WHEAT,
                    Material.POISONOUS_POTATO,
                    Material.BEETROOT -> {
                        unplantable = it
                    }

                    Material.WHEAT_SEEDS,
                    Material.CARROT,
                    Material.POTATO,
                    Material.BEETROOT_SEEDS,
                    Material.NETHER_WART -> {
                        plantable = it
                        dropsAmount += it.amount
                    }

                    else -> { return }
                }
            }
        } else {
            block.drops.forEach {
                when (it.type) {
                    Material.WHEAT,
                    Material.POISONOUS_POTATO,
                    Material.BEETROOT -> {
                        unplantable = it
                    }

                    Material.WHEAT_SEEDS,
                    Material.CARROT,
                    Material.POTATO,
                    Material.BEETROOT_SEEDS,
                    Material.NETHER_WART -> {
                        plantable = it
                        dropsAmount += it.amount
                    }

                    else -> { return }
                }
            }
        }

        if (unplantable != null) {
            when (unplantable?.type) {
                Material.WHEAT,
                Material.POISONOUS_POTATO,
                Material.BEETROOT -> {
                    world.dropItemNaturally(location, unplantable!!)
                }

                else -> { return }
            }
        }

        if (plantable != null) {
            when (plantable?.type) {
                Material.WHEAT_SEEDS,
                Material.CARROT,
                Material.POTATO,
                Material.BEETROOT_SEEDS,
                Material.NETHER_WART -> {
                    if ((dropsAmount - 1) <= 0) return

                    plantable!!.amount = dropsAmount - 1
                    world.dropItemNaturally(location, plantable!!)
                }

                else -> { return }
            }
        }
    }

    private class EventProcessor: Listener {
        @EventHandler
        private fun onPlayerInteract(e: PlayerInteractEvent) {
            val player: Player = e.player

            val block = e.clickedBlock
            val heldItem = e.item

            if (e.hand != EquipmentSlot.HAND) return

            if (e.action != Action.RIGHT_CLICK_BLOCK) return

            val ageable: Ageable

            if (block == null) return

            when (block.type) {
                Material.WHEAT,
                Material.CARROTS,
                Material.POTATOES,
                Material.BEETROOTS,
                Material.NETHER_WART -> {
                    ageable = block.blockData as Ageable

                    if (ageable.age != ageable.maximumAge) return

                    harvest(block, heldItem, e.hasItem(), player)
                    block.world.playSound(block.location.add(0.5, 0.0, 0.5), Sound.ITEM_CROP_PLANT, 1f, 1f)
                    ageable.age = 0
                    block.blockData = ageable
                }

                else -> { return }
            }
        }
    }
}

