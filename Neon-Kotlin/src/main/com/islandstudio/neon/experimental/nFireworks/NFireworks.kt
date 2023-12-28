package com.islandstudio.neon.experimental.nFireworks

//import com.mojang.math.Vector3f
import com.islandstudio.neon.stable.core.init.NConstructor
import com.islandstudio.neon.stable.core.io.nFolder.FolderList
import com.islandstudio.neon.stable.core.io.nFolder.NFolder
import com.islandstudio.neon.stable.primary.nCommand.CommandHandler
import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.primary.nServerFeatures.NServerFeatures
import com.islandstudio.neon.stable.primary.nServerFeatures.ServerFeature
import com.islandstudio.neon.stable.utils.NeonKey
import com.islandstudio.neon.stable.utils.ObjectSerializer
import com.islandstudio.neon.stable.utils.identifier.NeonKeyGeneral
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.nGUI.NGUIConstructor
import kotlinx.coroutines.*
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FireworkExplodeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.persistence.PersistentDataType
import java.io.File
import java.util.*
import javax.imageio.ImageIO

object NFireworks {
    private val supportedImageFormat = listOf("jpg", "png")

    /* Used to draw selected image, resolution, n must be 16 >= or <= x128 [128 by 128] / [n by n]
    * 0.1 per pixel where it uses 0.1 block in-game to draw each pixel.
    * */
    const val PATTERN_FRAME_INGAME_SIZE = 12.8
    const val PATTERN_FRAME_POINTER_INCREMENT = 0.2
    private const val PATTERN_FRAME_INDEX = (((PATTERN_FRAME_INGAME_SIZE / 2) * 10) - 1).toInt()

    private val imagesFolder = FolderList.NFIREWORKS_IMAGES.folder
    private val patternFramesFolder = FolderList.NFIREWORKS_PATTERN_FRAMES.folder

    private var isEnabled = false

    object Handler: CommandHandler {
        fun run() {
            isEnabled = NServerFeatures.getToggle(ServerFeature.FeatureNames.N_FIREWORKS)

            if (!isEnabled) {
                return NConstructor.unRegisterEventProcessor(EventProcessor())
            }

            NConstructor.registerEventProcessor(EventProcessor())
        }

        override fun setCommandHandler(commander: Player, args: Array<out String>) {
            if (!commander.isOp) {
                return commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
            }

            if (!isEnabled) {
                return commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.RED}This feature has been disabled!"))
            }

            when (args.size) {
                1 -> {
                    GUIHandler(NGUI.Handler.getNGUI(commander)).openGUI()
                }

                else -> {
                    return commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
                }
            }
        }

        override fun tabCompletion(commander: Player, args: Array<out String>): MutableList<String> {
            if (!commander.isOp) return super.tabCompletion(commander, args)

            if (args.size != 2) return super.tabCompletion(commander, args)

            val imageFiles = imagesFolder.listFiles()

            if (imageFiles.isNullOrEmpty()) return super.tabCompletion(commander, args)

            val listOfImages = imageFiles
                .filter { it.isFile }
                .filter { it.extension == "jpg" || it.extension == "png" }
                .map { it.name }
                .toMutableList()


            return listOfImages
        }

        /**
         * Create pattern frame data based on selected image and store into
         * a file, {imageNameWithExtension}.pixdat after the firework item has been created.
         *
         * @param imageFileName The target image.
         */
        fun createPatternFrameData(imageFileName: String) {
            val patternFrameFile = File(patternFramesFolder, "${imageFileName}.pixdat")

            patternFramesFolder.listFiles()?.let {
                if (it.filter { file -> file.isFile }.contains(patternFrameFile)) return
            }

            NFolder.createNewFile(patternFramesFolder, patternFrameFile)

            val patternFrame: ArrayList<FireworkPattern.PixelContainer> = ArrayList()

            val imageFile = File(FolderList.NFIREWORKS_IMAGES.folder, imageFileName)
            val bufferedImageReader = ImageIO.read(imageFile)
            val alphaRaster = bufferedImageReader.alphaRaster

            for (imgVerticalPointer in PATTERN_FRAME_INDEX downTo 0 step 1) {
                val pixelContainer = FireworkPattern.PixelContainer(imgVerticalPointer)

                for (imgHorizontalPointer in 0 .. PATTERN_FRAME_INDEX step 1) {
                    var horizontalPixel: FireworkPattern.Pixel

                    /* Check if the image is transparent image */
                    if (alphaRaster == null) {
                        horizontalPixel = FireworkPattern.Pixel(imgHorizontalPointer, java.awt.Color(bufferedImageReader.getRGB(imgHorizontalPointer, imgVerticalPointer)))
                        pixelContainer.horizontalPixels.add(horizontalPixel)
                        continue
                    }

                    /* Sorted out transparency pixel if the image is transparent */
                    if (alphaRaster.getPixel(imgHorizontalPointer, imgVerticalPointer, IntArray(1))[0] != 0) {
                        horizontalPixel = FireworkPattern.Pixel(imgHorizontalPointer, java.awt.Color(bufferedImageReader.getRGB(imgHorizontalPointer, imgVerticalPointer)).brighter())
                        pixelContainer.horizontalPixels.add(horizontalPixel)
                        continue
                    }

                    /* Store transparent pixel as null */
                    horizontalPixel = FireworkPattern.Pixel(imgHorizontalPointer)
                    pixelContainer.horizontalPixels.add(horizontalPixel)
                }

                patternFrame.add(pixelContainer)
            }

            patternFrameFile.writeBytes(ObjectSerializer.serializeObjectRaw(patternFrame))
        }

        private fun getImageFiles(): TreeMap<String, File> {
            val imageFiles = imagesFolder.listFiles()
            val filteredImageFiles: TreeMap<String, File> = TreeMap()

            if (imageFiles.isNullOrEmpty()) return filteredImageFiles

            filteredImageFiles.putAll(imageFiles
                .filter { it.isFile }
                .filter { it.extension in supportedImageFormat }
                .associateBy { it.name })

            return filteredImageFiles
        }

        fun getImageFileNames(): ArrayList<String> {
            return getImageFiles().keys.toMutableList() as ArrayList<String>
        }
    }

    /**
     * Create firework based on player configured firework effects.
     *
     * @param fireworkEffects The firework effect
     * @param player The target player.
     * @return The firework with configured effect on it.
     */
    fun createFirework(fireworkEffects: FireworkProperty.FireworkEffects, player: Player): ItemStack {
        val firework = ItemStack(Material.FIREWORK_ROCKET, fireworkEffects.fireworkAmount.toInt())
        val fireworkMeta = firework.itemMeta as FireworkMeta

        val fireworkEffect = FireworkEffect.builder()

        fireworkMeta.setDisplayName("${ChatColor.GOLD}${ChatColor.BOLD}${fireworkEffects.imageName}")
        fireworkMeta.power = fireworkEffects.fireworkPower.toInt()

        fireworkEffect.with(fireworkEffects.fireworkExplosionType)
        fireworkEffect.withColor(fireworkEffects.fireworkColor.getBukkitColor())

        if (fireworkEffects.fireworkWithTrail) {
            fireworkEffect.withTrail()
        }

        if (fireworkEffects.fireworkWithFlicker) {
            fireworkEffect.withFlicker()
        }

        if (fireworkEffects.fireworkWithFade) {
            fireworkEffect.withFade(fireworkEffects.fireworkWithFadeColor.getBukkitColor())
        }

        fireworkMeta.addEffect(fireworkEffect.build())
        fireworkMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)

        val fireworkDetail: List<String> = listOf(
            "${ChatColor.GRAY}Power: ${ChatColor.GREEN}${fireworkEffects.fireworkPower}",
            "${ChatColor.GRAY}Explosion Type: ${fireworkEffects.getExplosionTypeColoredName()}",
            "${ChatColor.GRAY}Pattern Facing: ${fireworkEffects.getFireworkPatternFacingName(player)}",
            "${ChatColor.GRAY}Color: ${fireworkEffects.fireworkColor.coloredName}",
            "${ChatColor.GRAY}With Trail: ${fireworkEffects.getToggleColoredName(fireworkEffects.fireworkWithTrail)}",
            "${ChatColor.GRAY}With Flicker: ${fireworkEffects.getToggleColoredName(fireworkEffects.fireworkWithFlicker)}",
            "${ChatColor.GRAY}With Fade: ${fireworkEffects.getWithFadeColoredName()}",
        )

        fireworkMeta.lore = fireworkDetail
        firework.itemMeta = fireworkMeta
        applyPatternProperty(firework, fireworkEffects)

        return firework
    }

    /**
     * Render firework pattern into world.
     *
     * @param imageFileName The selected image.
     * @param fireworkLocation The exploded firework's location.
     * @return True if the rendering successful, else false.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun renderFireworkPattern(imageFileName: String, fireworkLocation: Location) {
        val patternFile = File(FolderList.NFIREWORKS_PATTERN_FRAMES.folder, "${imageFileName}.pixdat")

        if (!patternFile.exists()) {
            Bukkit.getServer().broadcastMessage(CommandSyntax.createSyntaxMessage(
                "${ChatColor.RED}Render firework pattern failed! '${imageFileName}.pixdat' is missing!"))
            return
        }

        val fireworkPattern: ArrayList<FireworkPattern.PixelContainer> = ObjectSerializer.deserializeObjectRaw(patternFile.readBytes()) as ArrayList<FireworkPattern.PixelContainer>

        val verticalStart = String.format("%.1f", fireworkLocation.y).toDouble()
        val verticalEnd =  String.format("%.1f", fireworkLocation.y + PATTERN_FRAME_INGAME_SIZE).toDouble()

        /* Pointer for vertical pixel where used to locate the current position within the vertical pixels */
        var verticalPointer = String.format("%.1f", verticalStart).toDouble()

        var patternFrameWorker: Job? = CoroutineScope(newSingleThreadContext("nFireworks Pattern Renderer Worker"))
            .launch {
                fireworkPattern.stream()
                    .sorted { pixelC1, pixelC2 -> pixelC1.verticalIndex.compareTo(pixelC2.verticalIndex) }
                    .toList()
                    .asReversed()
                    .forEach {
                        if (verticalPointer >= verticalEnd) verticalPointer = verticalStart

                        /* Check if the horizontalPixels is null, if it is, vertical pointer will move forward */
                        if (it.horizontalPixels.isEmpty()) {
                            verticalPointer += PATTERN_FRAME_POINTER_INCREMENT
                            verticalPointer = String.format("%.1f", verticalPointer).toDouble()
                            return@forEach
                        }

                        it.renderHorizontalPixel(fireworkLocation, verticalPointer, 20)
                        verticalPointer += PATTERN_FRAME_POINTER_INCREMENT
                        verticalPointer = String.format("%.1f", verticalPointer).toDouble()
                }
            }

        patternFrameWorker?.invokeOnCompletion { patternFrameWorker = null }
        return
    }

    /**
     * Apply pattern property to the firework item.
     *
     * @param firework The target firework item.
     * @param fireworkEffects The pattern property.
     */
    private fun applyPatternProperty(firework: ItemStack, fireworkEffects: FireworkProperty.FireworkEffects) {
        val fireworkItemMeta = firework.itemMeta!!

        NeonKey.addNeonKey(
            ObjectSerializer.serializeObjectEncoded(fireworkEffects),
            NeonKeyGeneral.NFIREWORKS_PROPERTY_HEADER.key,
            PersistentDataType.STRING, fireworkItemMeta)

        firework.itemMeta = fireworkItemMeta
    }

    fun getParticleSize(): Float {
        val particleSize = (NServerFeatures.getOptionValue(ServerFeature.FeatureNames.N_FIREWORKS.featureName, "particleSize") as Double).toFloat()

        return particleSize
    }

    fun getSpeed(): Double {
        val speed = NServerFeatures.getOptionValue(ServerFeature.FeatureNames.N_FIREWORKS.featureName, "particleSpeed") as Double

        return speed
    }

    /**
     * Update firework pattern facing wihin the firework property if the firework pattern facing option, Auto
     * has been selected.
     *
     * @param player The target player.
     */
    fun updateFireworkPatternFacing(player: Player) {
        val playerInventory = player.inventory
        val nFireworksPropertyHeader = NeonKeyGeneral.NFIREWORKS_PROPERTY_HEADER.key

        if (!playerInventory.contains(Material.FIREWORK_ROCKET)) return

        playerInventory.filterNotNull()
            .filter { it.type == Material.FIREWORK_ROCKET }
            .filter { it.itemMeta!!.persistentDataContainer.has(nFireworksPropertyHeader, PersistentDataType.STRING) }
            .forEach {
                val fireworkMeta = it.itemMeta!!
                val fireworkEffects = ObjectSerializer.deserializeObjectEncoded(fireworkMeta.persistentDataContainer
                    .get(nFireworksPropertyHeader, PersistentDataType.STRING)!!) as FireworkProperty.FireworkEffects

                val fireworkPatternFacing = fireworkEffects.fireworkPatternFacingOptions

                if (fireworkPatternFacing != FireworkProperty.FireworkPatternFacingOptions.AUTO) return@forEach

                val fireworkDetails = fireworkMeta.lore!!

                fireworkDetails[2] = "${ChatColor.GRAY}Pattern Facing: ${fireworkEffects.getFireworkPatternFacingName(player)}"

                fireworkMeta.lore = fireworkDetails
                fireworkEffects.fireworkPatternFacing = fireworkPatternFacing.getPlayerFacing(player)

                NeonKey.updateNeonKey(
                    ObjectSerializer.serializeObjectEncoded(fireworkEffects),
                    nFireworksPropertyHeader, PersistentDataType.STRING, fireworkMeta
                )

                it.itemMeta = fireworkMeta
            }
    }

    private class EventProcessor: Listener {
        @EventHandler
        private fun onFireworkExplode(e: FireworkExplodeEvent) {
            val firework = e.entity
            val fireworkMeta = firework.fireworkMeta

            if (!fireworkMeta.persistentDataContainer.has(NeonKeyGeneral.NFIREWORKS_PROPERTY_HEADER.key, PersistentDataType.STRING)) return

            val fireworkEffects = ObjectSerializer.deserializeObjectEncoded(fireworkMeta.persistentDataContainer.get(
                NeonKeyGeneral.NFIREWORKS_PROPERTY_HEADER.key, PersistentDataType.STRING)!!) as FireworkProperty.FireworkEffects

            renderFireworkPattern(fireworkEffects.imageName, firework.location)
        }

        @EventHandler
        private fun onPlayerMove(e: PlayerMoveEvent) {
            updateFireworkPatternFacing(e.player)
        }

        @EventHandler
        private fun onInventoryClick(e: InventoryClickEvent) {
            val player: Player = e.whoClicked as Player

            if (e.view.title !in GUIBuilder.GUIState.entries.map { it.stateName }.toList() ) return

            val clickedInventory: Inventory = e.clickedInventory ?: return

            val inventoryHolder: InventoryHolder = clickedInventory.holder ?: return

            if (clickedInventory == player.inventory) e.isCancelled = true

            if (inventoryHolder !is NGUIConstructor) return

            e.isCancelled = true

            if (e.currentItem == null) return

            inventoryHolder.setGUIClickHandler(e)
        }
    }
}