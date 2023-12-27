package com.islandstudio.neon.experimental.nPainting

import com.islandstudio.neon.stable.core.init.NConstructor
import com.islandstudio.neon.stable.utils.NeonKey
import com.islandstudio.neon.stable.utils.ObjectSerializer
import com.islandstudio.neon.stable.utils.identifier.NeonKeyGeneral
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.GlowItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

class PaintingRenderer {
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun generateRenderer(player: Player, painting: Painting, paintingId: UUID, interectedBlock: Block) {
        val renderArea = calculateAndGetRenderArea(player, painting, interectedBlock)

        val playerFacing = player.facing
        val playerFacingModX = playerFacing.modX
        val playerFacingModZ = playerFacing.modZ

        renderArea.asReversed().forEachIndexed renderArea@ { verticalPointer, horizontalRenderArea ->
            horizontalRenderArea.forEachIndexed { horizontalPointer, location ->
                when {
                    (playerFacingModZ == -1) -> {
                        location.add(0.0, 0.0, 1.0)
                    }

                    (playerFacingModZ == 1) -> {
                        location.subtract(0.0, 0.0, 1.0)
                    }

                    (playerFacingModX == 1) -> {
                        location.subtract(1.0, 0.0, 0.0)
                    }

                    (playerFacingModX == -1) -> {
                        location.add(1.0, 0.0, 0.0)
                    }
                }

                val glowItemFrame = (location.world!!.spawnEntity(location, EntityType.GLOW_ITEM_FRAME) as GlowItemFrame).also {
                    it.isFixed = true
                    it.isVisible = false
                    it.isInvulnerable = true
                    it.setFacingDirection(playerFacing.oppositeFace)

                    val nPaintingDataContainer: HashMap<String, Any> = hashMapOf(
                        NeonKey.getNeonKeyNameWithNamespace(NeonKeyGeneral.NPAINTING_PROPERTY_IMAGE_NAME.key) to painting.imageFileName,
                        NeonKey.getNeonKeyNameWithNamespace(NeonKeyGeneral.NPAINTING_PROPERTY_RENDER_ID.key) to paintingId
                    )

                    NeonKey.addNeonKey(
                        ObjectSerializer.serializeObjectEncoded(nPaintingDataContainer),
                        NeonKeyGeneral.NPAINTING_PROPERTY_HEADER.key,
                        PersistentDataType.STRING,
                        it
                    )
                }

                if (!NConstructor.isUsingPaperMC) {
                    glowItemFrame.setItem(applyPaintingData(painting, horizontalPointer, verticalPointer))
                    return@forEachIndexed
                }

                CoroutineScope(newSingleThreadContext("Painting Renderer")).async {
                    applyPaintingData(painting, horizontalPointer, verticalPointer)
                }.run {
                    this.invokeOnCompletion {
                        glowItemFrame.setItem(this.getCompleted())
                    }
                }
            }
        }
    }

    private fun calculateAndGetRenderArea(player: Player, painting: Painting, interactedBlock: Block): ArrayList<ArrayList<Location>> {
        val renderArea: ArrayList<ArrayList<Location>> = ArrayList()

        val initialBlock = interactedBlock.getRelative(BlockFace.UP) // Initial block for the painting to render
        val initialBlockLocation = initialBlock.location
        val initialBlockX = initialBlockLocation.blockX
        val initialBlockY = initialBlockLocation.blockY
        val initialBlockZ = initialBlockLocation.blockZ

        val playerFacingModZ = player.facing.modZ
        val playerFacingModX = player.facing.modX

        val horizontalRenderCount = painting.paintingTileCountByWidth.toInt()
        val verticalRenderCount = painting.paintingTileCountByHeight.toInt()

        val verticalRenderMaxCount = initialBlockY + verticalRenderCount

        /* Calculate render area based on the player facing */
        when {
            (playerFacingModZ == -1) -> {
                val horizontalRenderMaxCount = initialBlockX + horizontalRenderCount

                for (verticalPointer in initialBlockY .. verticalRenderMaxCount) {
                    val horizontalRenderArea: ArrayList<Location> = ArrayList()

                    for (horizontalPointer in initialBlockX .. horizontalRenderMaxCount) {
                        initialBlockLocation.world!!.getBlockAt(horizontalPointer, verticalPointer, initialBlockZ).run {
                            horizontalRenderArea.add(this.location)
                        }
                    }

                    renderArea.add(horizontalRenderArea)
                }
            }

            (playerFacingModZ == 1) -> {
                val horizontalRenderMaxCount = initialBlockX - horizontalRenderCount

                for (verticalPointer in initialBlockY .. verticalRenderMaxCount) {
                    val horizontalRenderArea: ArrayList<Location> = ArrayList()

                    for (horizontalPointer in initialBlockX downTo  horizontalRenderMaxCount) {
                        initialBlockLocation.world!!.getBlockAt(horizontalPointer, verticalPointer, initialBlockZ).run {
                            horizontalRenderArea.add(this.location)
                        }
                    }

                    renderArea.add(horizontalRenderArea)
                }
            }

            (playerFacingModX == 1) -> {
                val horizontalRenderMaxCount = initialBlockZ + horizontalRenderCount

                for (verticalPointer in initialBlockY .. verticalRenderMaxCount) {
                    val horizontalRenderArea: ArrayList<Location> = ArrayList()

                    for (horizontalPointer in initialBlockZ .. horizontalRenderMaxCount) {
                        initialBlockLocation.world!!.getBlockAt(initialBlockX, verticalPointer, horizontalPointer).run {
                            horizontalRenderArea.add(this.location)
                        }
                    }

                    renderArea.add(horizontalRenderArea)
                }
            }

            (playerFacingModX == -1) -> {
                val horizontalRenderMaxCount = initialBlockZ - horizontalRenderCount

                for (verticalPointer in initialBlockY .. verticalRenderMaxCount) {
                    val horizontalRenderArea: ArrayList<Location> = ArrayList()

                    for (horizontalPointer in initialBlockZ downTo horizontalRenderMaxCount) {
                        initialBlockLocation.world!!.getBlockAt(initialBlockX, verticalPointer, horizontalPointer).run {
                            horizontalRenderArea.add(this.location)
                        }
                    }

                    renderArea.add(horizontalRenderArea)
                }
            }
        }

        return renderArea
    }

    private fun applyPaintingData(painting: Painting, horizontalPointer: Int, verticalPointer: Int): ItemStack {
        val paintingTile = ItemStack(Material.FILLED_MAP).apply paintingTile@ {
            (this.itemMeta as MapMeta).apply {
                /* Apply map view */
                (Bukkit.getMap(painting.paintingTiles.find { it.horizontalPointer == horizontalPointer && it.verticalPointer == verticalPointer }!!.tileId)
                    ?.let {
                        this.mapView = it
                    } ?: throw Exception("Map data missing!"))

                /* Set display name */
                this.setDisplayName("${ChatColor.WHITE}${ChatColor.BOLD}(${horizontalPointer}, ${verticalPointer})")

                /* Set nPainting data to Item Meta */
                val nPaintingDataContainer: HashMap<String, Any> = hashMapOf(
                    NeonKey.getNeonKeyNameWithNamespace(NeonKeyGeneral.NPAINTING_PROPERTY_IMAGE_NAME.key) to painting.imageFileName,
                    NeonKey.getNeonKeyNameWithNamespace(NeonKeyGeneral.NPAINTING_PROPERTY_POSITION.key) to "${horizontalPointer},${verticalPointer}"
                )

                NeonKey.addNeonKey(
                    ObjectSerializer.serializeObjectEncoded(nPaintingDataContainer),
                    NeonKeyGeneral.NPAINTING_PROPERTY_HEADER.key,
                    PersistentDataType.STRING,
                    this
                )
            }.also {
                this.itemMeta = it
            }
        }

        return paintingTile
    }
}