package com.islandstudio.neon.experimental.nFireworks

//import com.mojang.math.Vector3f
import com.islandstudio.neon.stable.core.io.nFolder.FolderList
import com.islandstudio.neon.stable.core.network.NPacketProcessor
import com.islandstudio.neon.stable.primary.nCommand.CommandHandler
import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nServerFeatures.NServerFeatures
import com.islandstudio.neon.stable.primary.nServerFeatures.ServerFeature
import com.islandstudio.neon.stable.utils.NeonKey
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.nGUI.NGUIConstructor
import kotlinx.coroutines.*
import net.minecraft.core.particles.DustParticleOptions
import org.bukkit.*
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FireworkExplodeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.joml.Vector3f
import java.io.File
import java.util.*
import javax.imageio.ImageIO

object NFireworks {
    private val supportedImageFormat = listOf("jpg", "png")

    /* Used to draw selected image, resolution, n must be 16 >= or <= x128 [128 by 128] / [n by n]
    * 0.1 per pixel where it uses 0.1 block in-game to draw each pixel.
    * */
    private const val IMAGE_CANVAS_SIZE = 12.6
    private const val IMAGE_CANVAS_SIZE_INDEX = ((IMAGE_CANVAS_SIZE * 10) - 1).toInt()

    private val imageFolder = FolderList.NFIREWORKS_IMAGES.folder
    private val particleDataFolder = FolderList.NFIREWORKS_PARTICLE_DATA.folder

    object Handler: CommandHandler {
        fun run() {
            NConstructor.registerEventProcessor(EventProcessor)
        }

        override fun setCommandHandler(commander: Player, args: Array<out String>) {
            if (!commander.isOp) {
                return commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
            }

            when (args.size) {
                1 -> {
                    GUIHandler(NGUI.Handler.getNGUI(commander)).openGUI()
                }

                2 -> {
                    if (args[1].equals("test", true)) {
                        val firework = ItemStack(Material.FIREWORK_ROCKET)
                        val fireworkMeta: FireworkMeta = firework.itemMeta as FireworkMeta

                        fireworkMeta.setDisplayName("Test Firework")
                        fireworkMeta.addEffect(FireworkEffect.builder()
                            .with(FireworkEffect.Type.BURST)
                            .withTrail()
                            .flicker(true)
                            .withColor(Color.GREEN).build())

                        firework.itemMeta = fireworkMeta

                        commander.inventory.addItem(firework)
                        return
                    }

//                    val imageFileName = args[1]
//                    val imageFiles = imageFolder.listFiles()
//
//                    val errMsg = "${ChatColor.RED}Invalid image file name! There is no such image file as " +
//                            "${ChatColor.GRAY}'${ChatColor.GOLD}${imageFileName}${ChatColor.GRAY}'${ChatColor.RED}!"
//
//                    if (imageFiles.isNullOrEmpty()) return commander.sendMessage(CommandSyntax.createSyntaxMessage(errMsg))
//
//                    val imageFile = File(imageFolder, imageFileName)
//
//                    if (!imageFiles.filter { it.isFile }.filter { it.extension == "jpg" || it.extension == "png" }
//                            .contains(imageFile)) {
//                        return commander.sendMessage(CommandSyntax.createSyntaxMessage(errMsg))
//                    }
//
//                    commander.inventory.addItem(createFirework(imageFileName))
                }
            }
        }

        override fun tabCompletion(commander: Player, args: Array<out String>): MutableList<String> {
            if (!commander.isOp) return super.tabCompletion(commander, args)

            if (args.size != 2) return super.tabCompletion(commander, args)

            val imageFiles = imageFolder.listFiles()

            if (imageFiles.isNullOrEmpty()) return super.tabCompletion(commander, args)

            val listOfImages = imageFiles
                .filter { it.isFile }
                .filter { it.extension == "jpg" || it.extension == "png" }
                .map { it.name }
                .toMutableList()


            return listOfImages
        }

        fun getImageFiles(): TreeMap<String, File> {
            val imageFiles = imageFolder.listFiles()
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

        fun getImageColors(imageFileName: String): ArrayList<ArrayList<java.awt.Color>> {
            val imageFile = File(FolderList.NFIREWORKS_IMAGES.folder, imageFileName)

            val bufferedImage = ImageIO.read(imageFile)
            val imageColors: ArrayList<ArrayList<java.awt.Color>> = ArrayList()

            for (imgIndexY in IMAGE_CANVAS_SIZE_INDEX downTo 0 step 1) {
                val imageColorX: ArrayList<java.awt.Color> = ArrayList()

                for (imgIndexX in 0..IMAGE_CANVAS_SIZE_INDEX step 1) {
                    imageColorX.add(java.awt.Color(bufferedImage.getRGB(imgIndexX, imgIndexY)))
                }

                imageColors.add(imageColorX)
            }

            return imageColors
        }
    }

    fun prepareFirework(fireworkEffects: FireworkProperty.FireworkEffects): ItemStack {
        val firework = ItemStack(Material.FIREWORK_ROCKET, fireworkEffects.fireworkAmount.toInt())
        val fireworkMeta = firework.itemMeta as FireworkMeta

        val fireworkEffect = FireworkEffect.builder()

        fireworkMeta.setDisplayName("${ChatColor.GOLD}${fireworkEffects.imageName}")
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
            "${ChatColor.GRAY}Color: ${fireworkEffects.fireworkColor.coloredName}",
            "${ChatColor.GRAY}With Trail: ${fireworkEffects.getToggleColoredName(fireworkEffects.fireworkWithTrail)}",
            "${ChatColor.GRAY}With Flicker: ${fireworkEffects.getToggleColoredName(fireworkEffects.fireworkWithFlicker)}",
            "${ChatColor.GRAY}With Fade: ${fireworkEffects.getWithFadeColoredName()}",
        )

        fireworkMeta.lore = fireworkDetail
        firework.itemMeta = fireworkMeta

        return firework
    }

    private fun createFirework(imageName: String): ItemStack {
        val fireworkPower = 3 //TODO
        val fireworkCount = 1 //TODO

        val firework = ItemStack(Material.FIREWORK_ROCKET)
        val fireworkMeta = firework.itemMeta as FireworkMeta

        fireworkMeta.setDisplayName("${ChatColor.GOLD}${imageName.substring(0, imageName.toCharArray().size - 4)}")
        fireworkMeta.persistentDataContainer.set(NeonKey.NamespaceKeys.NEON_FIREWORK.key,
            PersistentDataType.STRING, imageName)

        fireworkMeta.addEffect(FireworkEffect.builder()
            .with(FireworkEffect.Type.BURST)
            .withTrail()
            .flicker(true)
            .withColor(Color.GREEN).build())

        firework.itemMeta = fireworkMeta

        return firework
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun drawFireworkPattern(firework: Firework, imageFileName: String) {
        val particleSize = getParticleSize()

        val explodeLocation = firework.location

        val explodeRawX = explodeLocation.x
        val explodeRawY = explodeLocation.y

        val canvasXStart = explodeRawX - (IMAGE_CANVAS_SIZE / 2)
        val canvasXEnd = explodeRawX + (IMAGE_CANVAS_SIZE / 2)

        val canvasYStart = explodeRawY
        val canvasYEnd = explodeRawY + IMAGE_CANVAS_SIZE

        val canvasYStartRev = canvasYEnd
        val canvasYEndRev = canvasYStart

        val imageColors = Handler.getImageColors(imageFileName)

        var task: Job? = CoroutineScope(newSingleThreadContext("Test")).launch {
//            repeat(20) {
//                var canvasIndexY =  canvasYStart
//
//                for (imgIndexY in 0 .. IMAGE_CANVAS_SIZE_INDEX step  1) {
//                    var canvasIndexX = canvasXStart
//
//                    explodeLocation.y = canvasIndexY
//
//                    val imgColorY = imageColors[imgIndexY]
//
//                    for (imgIndexX in 0..IMAGE_CANVAS_SIZE_INDEX step 1 ) {
//                        explodeLocation.x = canvasIndexX
//
//                        val imgColorX = imgColorY[imgIndexX]
//
//                        val particleData = DustParticleOptions(Vector3f((imgColorX.red.toFloat()) / 255,
//                            (imgColorX.green.toFloat()) / 255, (imgColorX.blue.toFloat()) / 255), particleSize)
//
//                        NPacketProcessor.getNWorld(firework.world).sendParticles(null, particleData,
//                            explodeLocation.x, explodeLocation.y, explodeLocation.z, 1, 0.0, 0.0, 0.0, 1.0, true)
//
//                        if (canvasIndexX < canvasXEnd) canvasIndexX += 0.1
//                    }
//
//                    if (canvasIndexY < canvasYEnd) canvasIndexY += 0.1
//                }
//            }
            for (x in 1..20 step 1) {
                var canvasIndexY =  canvasYStart

                for (imgIndexY in 0 .. IMAGE_CANVAS_SIZE_INDEX step  1) {
                    var canvasIndexX = canvasXStart

                    explodeLocation.y = canvasIndexY

                    val imgColorY = imageColors[imgIndexY]

                    for (imgIndexX in 0..IMAGE_CANVAS_SIZE_INDEX step 1 ) {
                        explodeLocation.x = canvasIndexX

                        val imgColorX = imgColorY[imgIndexX]

                        val particleData = DustParticleOptions(
                            Vector3f((imgColorX.red.toFloat()) / 255,
                            (imgColorX.green.toFloat()) / 255, (imgColorX.blue.toFloat()) / 255), particleSize)

                        NPacketProcessor.getNWorld(firework.world).sendParticles(null, particleData,
                            explodeLocation.x, explodeLocation.y, explodeLocation.z, 1, 0.0, 0.0, 0.0, 1.0, true)

                        if (canvasIndexX < canvasXEnd) canvasIndexX += 0.1
                    }

                    if (canvasIndexY < canvasYEnd) canvasIndexY += 0.1
                }
            }
        }

        task?.invokeOnCompletion {
            task = null
        }

//        for (x in 1..20 step 1) {
////            var yIndex = yStartRev
////            var imgIndexY = 0
////
////            while (yIndex > yEndRev) {
////                var xIndex = xStart
////                var imgIndexX = 0
////
////                explodeLocation.y = yIndex
////
////                while (xIndex < xEnd) {
////                    explodeLocation.x = xIndex
////
////                    val xColor = java.awt.Color(bufferedImage.getRGB(imgIndexX, imgIndexY))
////                    val particleDataX: Particle.DustOptions = Particle.DustOptions(Color.fromRGB(xColor.red, xColor.green, xColor.blue), 1.0f)
////
////                    player!!.spawnParticle(Particle.REDSTONE, explodeLocation, 1, particleDataX)
////
////                    xIndex += 0.1
////
////                    if (imgIndexX != 127) {
////                        imgIndexX += 1
////                    }
////                }
////
////                yIndex -= 0.1
////
////                if (imgIndexY != 127) {
////                    imgIndexY += 1
////                }
////            }
//
//            var canvasIndexY =  canvasYStart
//
//            for (imgIndexY in 0 .. IMAGE_CANVAS_SIZE_INDEX step  1) {
//                var canvasIndexX = canvasXStart
//
//                explodeLocation.y = canvasIndexY
//
//                val imgColorY = imageColors[imgIndexY]
//
//                for (imgIndexX in 0..IMAGE_CANVAS_SIZE_INDEX step 1 ) {
//                    explodeLocation.x = canvasIndexX
//
//                    val imgColorX = imgColorY[imgIndexX]
//
//                    val particleData = DustParticleOptions(Vector3f((imgColorX.red.toFloat()) / 255,
//                        (imgColorX.green.toFloat()) / 255, (imgColorX.blue.toFloat()) / 255), particleSize)
//
//                    NPacketProcessor.getNWorld(firework.world).sendParticles(null, particleData,
//                        explodeLocation.x, explodeLocation.y, explodeLocation.z, 1, 0.0, 0.0, 0.0, 1.0, true)
//
//
//                    if (canvasIndexX < canvasXEnd) canvasIndexX += 0.1
//                }
//
//                if (canvasIndexY < canvasYEnd) canvasIndexY += 0.1
//            }
//        }
    }

    fun initFrameData(imageFileName: String, fireworkLocation: Location): ArrayList<ArrayList<Runnable>> {
        //val particleDataFile = File(FolderList.NFIREWORKS_PARTICLE_DATA.folder, "${imageFileName}.particles")

        val explodeLocation = fireworkLocation

        val explodeRawX = explodeLocation.x
        val explodeRawY = explodeLocation.y

        val canvasXStart = explodeRawX - (IMAGE_CANVAS_SIZE / 2)
        val canvasXEnd = explodeRawX + (IMAGE_CANVAS_SIZE / 2)

        val canvasYStart = explodeRawY
        val canvasYEnd = explodeRawY + IMAGE_CANVAS_SIZE

        val particleRunnable: ArrayList<ArrayList<Runnable>> = ArrayList()

        val imageFile = File(FolderList.NFIREWORKS_IMAGES.folder, imageFileName)
        val bufferedImageReader = ImageIO.read(imageFile)
        val alphaRaster = bufferedImageReader.alphaRaster

        var canvasIndexY =  canvasYStart

        for (imgIndexY in IMAGE_CANVAS_SIZE_INDEX downTo 0 step 1) {
            val particleRunnableX: ArrayList<Runnable> = ArrayList()

            var canvasIndexX = canvasXStart
            explodeLocation.y = canvasIndexY

            for (imgIndexX in 0 .. IMAGE_CANVAS_SIZE_INDEX step 1) {
                explodeLocation.x = canvasIndexX

                if (alphaRaster == null) {
                    val runnableTask = DisplayPixel(
                        Pixel(
                            java.awt.Color(bufferedImageReader.getRGB(imgIndexX, imgIndexY)).brighter(),
                            explodeLocation),
                        fireworkLocation.world!!)

                    particleRunnableX.add(runnableTask)

                    if (canvasIndexX < canvasXEnd) canvasIndexX += 0.1

                    continue
                }

                if (bufferedImageReader.alphaRaster.getPixel(imgIndexX, imgIndexY, IntArray(1))[0] != 0) {
                    val runnableTask = DisplayPixel(
                        Pixel(
                            java.awt.Color(bufferedImageReader.getRGB(imgIndexX, imgIndexY)).brighter(),
                            explodeLocation),
                        fireworkLocation.world!!)

                    particleRunnableX.add(runnableTask)
                }

                if (canvasIndexX < canvasXEnd) canvasIndexX += 0.1
            }

            particleRunnable.add(particleRunnableX)

            if (canvasIndexY < canvasYEnd) canvasIndexY += 0.1
        }

        return particleRunnable
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun drawFireworkPattern2(firework: Firework, imageFileName: String) {
        val frameData = initFrameData(imageFileName, firework.location)

        var drawFrame: Job? = CoroutineScope(newSingleThreadContext("nFireworks")).launch {
            frameData.forEach {
                it.forEach {runnable ->
                    (runnable as BukkitRunnable).runTaskTimer(NConstructor.plugin, 0L, 0)
                }
            }
        }

        drawFrame?.invokeOnCompletion { drawFrame = null }
    }

    fun getParticleSize(): Float {
        val particleSize = (NServerFeatures.getOptionValue(ServerFeature.FeatureNames.N_FIREWORKS.featureName, "particleSize") as Double).toFloat()

        return particleSize
    }

    fun getSpeed(): Double {
        val speed = NServerFeatures.getOptionValue(ServerFeature.FeatureNames.N_FIREWORKS.featureName, "particleSpeed") as Double

        return speed
    }

    private object EventProcessor: Listener {
        @EventHandler
        private fun onFireworkExplode(e: FireworkExplodeEvent) {
            val firework = e.entity
            val fireworkMeta = firework.fireworkMeta

            if (!fireworkMeta.persistentDataContainer.has(NeonKey.NamespaceKeys.NEON_FIREWORK.key, PersistentDataType.STRING)) return

            val imageFile = fireworkMeta.persistentDataContainer.get(NeonKey.NamespaceKeys.NEON_FIREWORK.key, PersistentDataType.STRING)

            drawFireworkPattern2(firework, imageFile!!)
        }

        @EventHandler
        private fun onInventoryClick(e: InventoryClickEvent) {
            val player: Player = e.whoClicked as Player

            if (e.view.title !in GUIBuilder.GUIState.values().map { it.stateName }.toList() ) return

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