package com.islandstudio.neon.stable.secondary.nHarvest

import com.islandstudio.neon.experimental.nDurable.NDurable
import com.islandstudio.neon.experimental.nServerFeatures.NServerFeatures
import com.islandstudio.neon.experimental.nServerFeatures.ServerFeature
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nExperimental.NExperimental
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
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object NHarvest {
    private val players: MutableSet<Player> = HashSet()
    private val isNDurableOn: () -> Boolean = {
        var tempBool = false

        NExperimental.Handler.getClientElement().forEach {
            val nExperimental = NExperimental(it)

            if (!nExperimental.experimentalName.equals("nDurable", true)) return@forEach

            if (!nExperimental.isEnabled) return@forEach

            tempBool = true
        }

        tempBool
    }

    fun run() {
        if (!NServerFeatures.getToggle(ServerFeature.FeatureNames.N_HARVEST)) return NConstructor.unRegisterEvent(EventController())

        NConstructor.registerEventProcessor(EventController())
    }

    /**
     * Add player to the set of players that can harvest
     *
     * @param player Player to add. (Player)
     */
    fun addPlayer(player: Player) {
        if (players.contains(player)) return

        players.add(player)
    }

    /**
     * Remove player from the set of players that can harvest
     *
     * @param player Player to remove. (Player)
     */
    fun removePlayer(player: Player?) {
        if (player == null) {
            players.clear()
            return
        }

        players.remove(player)
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

                    else -> {
                        return
                    }
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

                    else -> {
                        return
                    }
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

                else -> {
                    return
                }
            }
        }

        if (plantable != null) {
            when (plantable?.type) {
                Material.WHEAT_SEEDS,
                Material.CARROT,
                Material.POTATO,
                Material.BEETROOT_SEEDS,
                Material.NETHER_WART -> {
                    if ((dropsAmount - 1) > 0) {
                        plantable!!.amount = dropsAmount - 1
                        world.dropItemNaturally(location, plantable!!)
                    }
                }

                else -> {
                    return
                }
            }
        }
    }

    private class EventController: Listener {
        @EventHandler
        private fun onPlayerJoin(e: PlayerJoinEvent) {
            addPlayer(e.player)
        }

        @EventHandler
        private fun onPlayerQuit(e: PlayerQuitEvent) {
            removePlayer(e.player)
        }

        @EventHandler
        private fun onServerLoad(e: ServerLoadEvent) {
            when (e.type) {
                ServerLoadEvent.LoadType.STARTUP, ServerLoadEvent.LoadType.RELOAD -> {
                    NConstructor.plugin.server.onlinePlayers.parallelStream().forEach {player ->
                        if (!NServerFeatures.getToggle(ServerFeature.FeatureNames.N_HARVEST)) return@forEach removePlayer(player)
                        addPlayer(player)
                    }
                }
            }
        }

        @EventHandler
        private fun onPlayerInteract(e: PlayerInteractEvent) {
            val player: Player = e.player

            if (!players.contains(player)) return

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

                    if (ageable.age == ageable.maximumAge) {
                        harvest(block, heldItem, e.hasItem(), player)
                        block.world.playSound(block.location.add(0.5, 0.0, 0.5), Sound.ITEM_CROP_PLANT, 1f, 1f)
                        ageable.age = 0
                        block.blockData = ageable
                    }

                }

                else -> {
                    return
                }
            }
        }
    }
}

