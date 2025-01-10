package com.islandstudio.neon.experimental.nPainting

import com.islandstudio.neon.shared.core.io.folder.NeonDataFolder
import java.awt.image.BufferedImage
import java.io.File
import java.io.Serializable
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.ceil

data class Painting(val imageFileName: String): Serializable {
    val renderedPainting: HashSet<UUID> = HashSet()
    val paintingTiles: ArrayList<PaintingTile> = ArrayList()
    val tileSize = 128.0

    val paintingTileCountByWidth: Double
    val paintingTileCountByHeight: Double

    val horizontalPointerMaxIndex: Int
    val verticalPointerMaxIndex: Int

    val imageWidth: Int
    val imageHeight: Int

    @Transient val image: BufferedImage

    init {
        val imageFile = File(NeonDataFolder.NPaintingImageFolder, imageFileName)
        image = ImageIO.read(imageFile)

        imageWidth = image.width
        imageHeight = image.height

        paintingTileCountByWidth = ceil((imageWidth / tileSize)) - 1
        paintingTileCountByHeight = ceil((imageHeight / tileSize)) - 1

        horizontalPointerMaxIndex = paintingTileCountByWidth.toInt()
        verticalPointerMaxIndex = paintingTileCountByHeight.toInt()
    }

    data class PaintingTile(
        val tileId: Int,
        val horizontalPointer: Int,
        val verticalPointer: Int
        ): Serializable

    fun isRenderFull(): Boolean = renderedPainting.size >= 17

    fun generateRenderId(): UUID {
        var renderId = UUID.randomUUID()

        while (renderedPainting.contains(renderId)) {
            renderId = UUID.randomUUID()
        }

        return renderId
    }
}