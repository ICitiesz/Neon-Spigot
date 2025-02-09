package com.islandstudio.neon.experimental.nPainting

import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.server.ServerProvider
import com.islandstudio.neon.stable.core.application.reflection.mapping.NmsMap
import com.islandstudio.neon.stable.core.application.server.ServerGamePacketManager
import com.islandstudio.neon.stable.utils.ObjectSerializer
import kotlinx.coroutines.*
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.map.MapPalette
import org.bukkit.map.MapView
import org.koin.core.component.inject
import java.io.File

class PaintingBuilder: IComponentInjector {
    private val world: World? = Bukkit.getWorlds().find { it.environment == World.Environment.NORMAL }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun generatePainting(painting: Painting, renderDataFile: File) {
        val usableIds = NPainting.Handler.getReusableMapIds().sorted().toMutableList()

        if (world == null) return

        /* Painting properties */
        val image = painting.image
        val tileSize = painting.tileSize.toInt()
        val imageWidth = painting.imageWidth
        val imageHeight = painting.imageHeight
        val verticalPointerMaxIndex = painting.verticalPointerMaxIndex
        val horizonatalPointerMaxIndex = painting.horizontalPointerMaxIndex

        for (verticalPointer in 0 .. verticalPointerMaxIndex) {
            for (horizontalPointer in 0 .. horizonatalPointerMaxIndex) {
                val horizontalTileSize = tileSize.run {
                    /* Check if the horizontal pointer reaches the end
                    * Add the remaining pixels to the end of the pointer if available
                    * */
                    if (horizontalPointer != horizonatalPointerMaxIndex) return@run this

                    (imageWidth % tileSize).run remainder@ {
                        if (this@remainder == 0) return@run this@run

                        return@run this@remainder
                    }
                }

                val verticalTileSize = tileSize.run {
                    /* Check if the vertical pointer reaches the end
                    * Add the remaining pixels to the end of the pointer if available
                    * */
                    if (verticalPointer != verticalPointerMaxIndex) return@run this

                    (imageHeight % tileSize).run remainder@ {
                        if (this@remainder == 0) return@run this@run

                        return@run this@remainder
                    }
                }

                val paintingTile = createPaintingTile(usableIds, world).also {
                    /* Add the id to the metadata */
                    painting.paintingTiles.add(
                        Painting.PaintingTile(
                            it.id,
                            horizontalPointer,
                            verticalPointer
                        )
                    )

                    it.scale = MapView.Scale.FARTHEST
                    it.isLocked = true
                }

                val processedMapColors = image.getSubimage(horizontalPointer * tileSize, verticalPointer * tileSize,
                    horizontalTileSize, verticalTileSize).run { MapPalette.imageToBytes(this) }

                /* Get the worldMapData from the CraftMapView class */
                val worldMapData = paintingTile.javaClass.getDeclaredField(NmsMap.WorldMapData.remapped)
                    .run {
                        this.isAccessible = true
                        this.get(paintingTile)
                    }

                val appContext by inject<AppContext>()

                if (!appContext.validateServerProvider(ServerProvider.Paper)) {
                    applyPaintingColor(worldMapData, painting, processedMapColors, horizontalPointer, horizontalTileSize)
                } else {
                    CoroutineScope(newSingleThreadContext("Test")).launch {
                        applyPaintingColor(worldMapData, painting, processedMapColors, horizontalPointer, horizontalTileSize)
                    }
                }

                /* Set the map data in world cache */
                ServerGamePacketManager.getMcWorld(world).apply {
                    this.javaClass.getMethod(
                        NmsMap.SetWorldMapData.remapped, String::class.java,
                        MapItemSavedData::class.java).invoke(this, "map_${paintingTile.id}", worldMapData)
                }
            }
        }

        renderDataFile.writeBytes(ObjectSerializer.serializeObjectRaw(painting))
        NPainting.Handler.saveReusableMapIds(usableIds.toHashSet())

        val worldPersistantContainer = ServerGamePacketManager.getMcWorld(world).run {
            this.javaClass.getMethod(NmsMap.GetWorldPersistentContainer.remapped)
                .invoke(this)
        }

        worldPersistantContainer.javaClass.run {
            /* Save the map data that stores in cache */
            this.getMethod(NmsMap.SaveWorldCache.remapped).invoke(worldPersistantContainer)
        }
    }

    /**
     * Create painting tile
     *
     * @param usableIds
     * @param world
     * @return
     */
    private fun createPaintingTile(usableIds: MutableList<Int>, world: World): MapView {
        if (usableIds.isEmpty()) return Bukkit.createMap(world)

        val usableId = usableIds.removeFirst() /* Get usable id */
        val overworldResourceKey = ServerGamePacketManager.getMcWorld(world).javaClass.superclass
            .getField(NmsMap.OverworldResourceKey.remapped).get(ServerGamePacketManager.getMcWorld(world))

        /* Construct NMS-based map data */
        val mapDataContainer = MapItemSavedData::class.java.getDeclaredConstructor(
            Int::class.java, Int::class.java, Byte::class.java,
            Boolean::class.java, Boolean::class.java, Boolean::class.java, ResourceKey::class.java
        ).run {
            this.isAccessible = true
            this.newInstance(448, 448, (4).toByte(), false, false, true, overworldResourceKey)
        }

        return mapDataContainer.javaClass.run {
            this.getField(NmsMap.WorldMapDataId.remapped).set(mapDataContainer, "map_${usableId}")
            this.getField(NmsMap.WorldMapDataMapView.remapped).get(mapDataContainer) as MapView
        }
    }

    /**
     * Apply painting color to the map data.
     *
     * @param worldMapData World map data.
     * @param painting The painting property
     * @param processedPaintingColors The ByteArray contains processed painting colors.
     * @param horizontalPointer Horizontal pointer
     * @param horizontalTileSize Horizontal tile size
     */
    private fun applyPaintingColor(worldMapData: Any, painting: Painting, processedPaintingColors: ByteArray, horizontalPointer: Int, horizontalTileSize: Int) {
        if (worldMapData !is MapItemSavedData) return

        val tileSize = painting.tileSize.toInt()
        val horizonatalPointerMaxIndex = painting.horizontalPointerMaxIndex

        (worldMapData.javaClass.getField(NmsMap.WorldMapDataColors.remapped)
            .get(worldMapData) as ByteArray).also worldMapColors@ { worldMapColors ->
            /* Fill the ByteArray with -1 */
            worldMapColors.fill(-1)

            /* Check if the horizontal pointer is the last index and the horizontalTileSize != 128
            * if true, map color will be copied to the worldMapColor instead of further processing
            *  */
            if (!(horizontalPointer == horizonatalPointerMaxIndex && horizontalTileSize != tileSize)) {
                processedPaintingColors.copyInto(worldMapColors)
                return@worldMapColors
            }

            /*
            * For the map at the last of the horizonal should be processed differently if the total pixels is < 16384 or < [128 * 128]
            * This is due to Minecraft render pixels in Map like "incremental matrix":
            *
            * ➡️⬇️➡️ 0 .. 16384 where 128 per row
            * [X, X, X]
            * [X, X, X]
            * [X, X, X]
            *
            *  */

            /* Pointers */
            var startPointer = 0
            var endPointer = horizontalTileSize - 1

            val unusedPixelCount = (tileSize - horizontalTileSize) + 1
            val mapColorPaletteIterator = processedPaintingColors.iterator()

            for (pointer in worldMapColors.indices) {
                /* Check if the current pointer is in between of startPointer and endPointer */
                if (pointer !in (startPointer .. endPointer)) continue

                /* Check if the current pointer is == to endPointer,
                * if true, both startPointer and endPointer will be updated to new pointer */
                if (pointer == endPointer) {
                    startPointer = endPointer + unusedPixelCount
                    endPointer = (startPointer + horizontalTileSize) - 1
                }

                if (!mapColorPaletteIterator.hasNext()) break

                worldMapColors[pointer] = mapColorPaletteIterator.nextByte()
            }
        }
    }
}