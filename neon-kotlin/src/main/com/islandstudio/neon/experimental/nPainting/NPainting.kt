package com.islandstudio.neon.experimental.nPainting

import com.islandstudio.neon.Neon
import com.islandstudio.neon.shared.core.AppContext
import com.islandstudio.neon.shared.core.di.IComponentInjector
import com.islandstudio.neon.shared.core.io.folder.NeonDataFolder
import com.islandstudio.neon.shared.core.server.ServerProvider
import com.islandstudio.neon.stable.core.application.AppLoader
import com.islandstudio.neon.stable.core.application.identity.NeonKey
import com.islandstudio.neon.stable.core.application.identity.NeonKeyGeneral
import com.islandstudio.neon.stable.core.application.reflection.mapping.NmsMap
import com.islandstudio.neon.stable.core.application.server.NPacketProcessor
import com.islandstudio.neon.stable.core.io.DataSourceType
import com.islandstudio.neon.stable.features.nServerFeatures.NServerFeaturesRemastered
import com.islandstudio.neon.stable.features.nServerFeatures.NServerFeaturesRemastered.saveToFile
import com.islandstudio.neon.stable.features.nServerFeatures.NServerFeaturesRemastered.toYAML
import com.islandstudio.neon.stable.primary.nCommand.CommandHandler
import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.primary.nCommand.Commands
import com.islandstudio.neon.stable.utils.ObjectSerializer
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.GlowItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.inject
import java.io.File
import java.util.*
import kotlin.jvm.optionals.getOrElse

object NPainting: IComponentInjector {
    /* General properties */
    private const val RENDER_DATA_FILE_EXTENSION = ".rendat"

    private val supportedImageFormat = arrayOf("jpg", "jpeg", "png")
    private val reusableMapIdsFile = NeonDataFolder.createNewFile(
        NeonDataFolder.NPaintingFolder,
        "reusable_map_ids.dat"
    )

    private var isEnabled = false

    private val appContext by inject<AppContext>()

    enum class RemovalType {
        FULL, // Full removal including the original image itself
        CACHED, // Removal of displayed painting, cached data and metadata, regeneration required
        DISPLAYED, // Removal of specifci displayed painting
        ALL_DISPLAYED // Removal of all displayed painting,
    }

    object Handler: Commands(), CommandHandler {
        fun run() {
            isEnabled = NServerFeaturesRemastered.serverFeatureSession.getActiveServerFeatureToggle("nPainting") ?: false

            if (!isEnabled) {
                return AppLoader.unregisterEventProcessor(EventProcessor())
            }

            /* TODO: Temp. disable nPainting for version 1.17.X */
            if (appContext.serverMajorVersion == "1.17") {
                NServerFeaturesRemastered.serverFeatureSession.also {
                    it.setServerFeatureToggle("nPainting", false)

                    saveToFile(toYAML(it.getServerFeatureList(DataSourceType.EXTERNAL_SOURCE)))
                }

                isEnabled = false
                return

//                val externalServerFeature = NServerFeatures.Handler.getLoadedExternalServerFeatures()
//
//                NServerFeatures.setToggle(
//                    externalServerFeature[ServerFeature.FeatureNames.N_PAINTING.featureName]!!,
//                    false
//                )
//
//                NServerFeatures.Handler.updateServerFeatures(externalServerFeature, NServerFeatures.Handler.SavingState.UPDATE)
//                return
            }

            AppLoader.registerEventProcessor(EventProcessor())
        }

        override fun getCommandHandler(commander: Player, args: Array<out String>) {
            if (!commander.isOp) {
                return commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
            }

            if (!isEnabled) {
                return commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.RED}This feature has been disabled!"))
            }

            when (args.size) {
                2 -> {
                    if (!validateArgument(args[1], CommandArgument.PAINTING_REMOVAL_STICK)) {
                        return commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    }

                    val removalStick = ItemStack(Material.STICK)

                    removalStick.itemMeta = removalStick.itemMeta?.let {
                        NeonKey.addNeonKey(
                            NeonKeyGeneral.N_PAINTING_REMOVAL_STICK.toString(),
                            NeonKeyGeneral.N_PAINTING_REMOVAL_STICK.key,
                            PersistentDataType.STRING, it, false)


                        it.setDisplayName("${ChatColor.GOLD}nPainting Removal Stick")
                        return@let it
                    }

                    commander.inventory.addItem(removalStick)
                }

                3 -> {
                    if (!validateArgument(args[1], CommandArgument.CREATE)) {
                        return commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    }

                    val imageFileName = args[2].also imageFileName@ {
                        /* Check if the image exist */
                        hasImageFile(it).getOrElse {
                            return commander.sendMessage(CommandSyntax.createSyntaxMessage(
                                "${ChatColor.YELLOW}Sorry, there is no such image as ${ChatColor.GRAY}'${ChatColor.GOLD}${it}" +
                                        "${ChatColor.GRAY}'${ChatColor.YELLOW}!"))
                        }.also { imageFile ->
                            if (validIamgeFileName(imageFile.nameWithoutExtension)) {
                                return@imageFileName
                            }

                            return commander.sendMessage(CommandSyntax.createSyntaxMessage(
                                "${ChatColor.RED}Image file name contain spaces or exceed 32 characters currently not supported!"
                            ))
                        }
                    }

                    createPaintingItem(imageFileName, commander)
                }

                4 -> {
                    if (!validateArgument(args[1], CommandArgument.REMOVE)) {
                        return commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    }

                    val imageFileName = args[2].also {
                        /* Check if the image exist */
                        if (hasImageFile(it).isPresent) return@also

                        return commander.sendMessage(CommandSyntax.createSyntaxMessage(
                            "${ChatColor.YELLOW}Sorry, there is no such image as ${ChatColor.GRAY}'${ChatColor.GOLD}${it}" +
                                    "${ChatColor.GRAY}'${ChatColor.YELLOW}!"))
                    }

                    /* Painting removal based on removal type */
                    RemovalType.entries
                        .filter { it != RemovalType.DISPLAYED }
                        .find { it.name.equals(args[3], true) }?.let {
                            if (!removePainting(imageFileName, removalType = it)) {
                                return commander.sendMessage(CommandSyntax.createSyntaxMessage(
                                    "${ChatColor.RED}Failed to remove painting: Metadata not found!"
                                ))
                            }

                            /* Notify messages */
                            when (it) {
                                RemovalType.FULL -> {
                                    commander.sendMessage(CommandSyntax.createSyntaxMessage(
                                        "${ChatColor.GRAY}'${ChatColor.GOLD}${imageFileName}" +
                                                "${ChatColor.GRAY}' ${ChatColor.GREEN}has been removed completely!"
                                    ))
                                }

                                RemovalType.CACHED -> {
                                    commander.sendMessage(CommandSyntax.createSyntaxMessage(
                                        "${ChatColor.YELLOW}Painting cache and metadata for " +
                                                "${ChatColor.GRAY}'${ChatColor.GOLD}${imageFileName}" +
                                                "${ChatColor.GRAY}' ${ChatColor.YELLOW}has been removed! Regeneration required for the use!"
                                    ))
                                }

                                RemovalType.ALL_DISPLAYED -> {
                                    commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Painting for " +
                                            "${ChatColor.GRAY}'${ChatColor.GOLD}${imageFileName}" +
                                            "${ChatColor.GRAY}' ${ChatColor.YELLOW}has been removed from the world!"
                                    ))
                                }

                                else -> {}
                            }
                    } ?: return commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                }

                5 -> {
                    if (!validateArgument(args[1], CommandArgument.REMOVE)) {
                        return commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    }

                    val imageFileName = args[2].also {
                        /* Check if the image exist */
                        if (hasImageFile(it).isPresent) return@also

                        return commander.sendMessage(CommandSyntax.createSyntaxMessage(
                            "${ChatColor.YELLOW}Sorry, there is no such image as ${ChatColor.GRAY}'${ChatColor.GOLD}${it}" +
                                    "${ChatColor.GRAY}'${ChatColor.YELLOW}!"))
                    }

                    if (!args[3].equals(RemovalType.DISPLAYED.name, true)) {
                        return commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    }

                    val renderId = args[4].also {
                        if (!getPaintingRenderData(imageFileName).get().renderedPainting.contains(UUID.fromString(it))) {
                            return commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}No such renderId!"))
                        }
                    }

                    if (!removePainting(imageFileName, Optional.of(UUID.fromString(renderId)), RemovalType.DISPLAYED)) {
                        return commander.sendMessage(CommandSyntax.createSyntaxMessage(
                            "${ChatColor.RED}Failed to remove painting: Metadata not found!"
                        ))
                    }

                    commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Painting for " +
                            "${ChatColor.GRAY}'${ChatColor.GOLD}${imageFileName}${ChatColor.GRAY}' " +
                            "(${ChatColor.GOLD}${renderId}${ChatColor.GRAY})${ChatColor.YELLOW} has been removed from the world!"
                    ))
                }

                else -> {
                    return commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                }
            }
        }

        override fun getTabCompletion(commander: Player, args: Array<out String>): MutableList<String> {
            if (!commander.isOp) return super.getTabCompletion(commander, args)

            val imageFileNames = NeonDataFolder.NPaintingImageFolder.listFiles()
                ?.filter { it.isFile }
                ?.filter { it.extension.lowercase() in supportedImageFormat }
                ?.filter { validIamgeFileName(it.nameWithoutExtension) }
                ?.map { it.name }

            when (args.size) {
                2 -> {
                    /* List options */
                    return listOf(CommandArgument.CREATE, CommandArgument.REMOVE, CommandArgument.PAINTING_REMOVAL_STICK)
                        .map { it.argument }
                        .filter { it.startsWith(args[1], true) }.toMutableList()
                }

                3 -> {
                    /* List image file names */
                    when (args[1]) {
                        CommandArgument.CREATE.argument, CommandArgument.REMOVE.argument -> {
                            return imageFileNames
                                ?.filter { it.startsWith(args[2], true) }
                                ?.toMutableList() ?: return super.getTabCompletion(commander, args)
                        }

                        else -> { return super.getTabCompletion(commander, args) }
                    }
                }

                4 -> {
                    if (!validateArgument(args[1], CommandArgument.REMOVE)) {
                        return super.getTabCompletion(commander, args)
                    }

                    /* List removal type */
                    return RemovalType.entries
                        .map { it.name.lowercase() }
                        .filter { it.startsWith(args[3], true) }.toMutableList()
                }

                5 -> {
                    if (!validateArgument(args[1], CommandArgument.REMOVE)) {
                        return super.getTabCompletion(commander, args)
                    }

                    if (!args[3].equals(RemovalType.DISPLAYED.name, true)) {
                        return super.getTabCompletion(commander, args)
                    }

                    val renderedPaintingIds = getPaintingRenderData(args[2])
                        .getOrElse { return super.getTabCompletion(commander, args) }.renderedPainting

                    return renderedPaintingIds.filter { it.toString().startsWith(args[4]) }.map { it.toString() }.toMutableList()
                }

                else -> { return super.getTabCompletion(commander, args) }
            }
        }

        /**
         * Get the painting render data which is required to create custom painting in the game world.
         *
         * @param imageFileName The target image file.
         * @return The target metadata.
         */
        fun getPaintingRenderData(imageFileName: String): Optional<Painting> {
            getPaintingRenderDataFile(imageFileName).also {
                if (!it.exists()) return Optional.empty()

                return Optional.of(ObjectSerializer.deserializeObjectRaw(it.readBytes()) as Painting)
            }
        }

        /**
         * Get painting render data file
         *
         * @param imageFileName The image file name
         * @return The render data file
         */
        fun getPaintingRenderDataFile(imageFileName: String): File {
            return File(NeonDataFolder.NPaintingRenderDataFolder, getPaintingRenderDataFileName(imageFileName))
        }

        fun getPaintingRenderDataFileName(imageFileName: String): String {
            return "${imageFileName}${RENDER_DATA_FILE_EXTENSION}"
        }

        /**
         * Check if the image file exist.
         *
         * @param imageFileName The image file name
         * @return True if exists, else false
         */
        fun hasImageFile(imageFileName: String): Optional<File> {
            File(NeonDataFolder.NPaintingImageFolder, imageFileName).also {
                if (!it.exists()) return Optional.empty()

                if (!it.isFile) return Optional.empty()

                return Optional.of(it)
            }
        }

        /**
         * Valid iamge file name to ensure the image name no exceed 32 characters, and no spaces.
         *  @param imageFileName Image file name without extension.
         *
         *  @return True if the name passes the validation, else false.
         */
        private fun validIamgeFileName(imageFileName: String): Boolean {
            if (imageFileName.length > 50) return false

            if (imageFileName.contains(' ')) return false

            return true
        }

        fun saveReusableMapIds(reusableMapIds: HashSet<Int>) {
            reusableMapIdsFile.writeBytes(ObjectSerializer.serializeObjectRaw(reusableMapIds))
        }

        /**
         * Get reusable map ids from the `resuable_map_ids.dat` where these ids be first priority to use before create map with new id.
         *
         * @return A list of reusable id.
         */
        fun getReusableMapIds(): HashSet<Int> {
            val reusableMapIds: HashSet<Int> = HashSet()

            reusableMapIdsFile.readBytes().apply {
                if (this.isEmpty()) return reusableMapIds

                reusableMapIds.addAll(ObjectSerializer.deserializeObjectRaw(this) as HashSet<Int>)
            }

            return reusableMapIds
        }
    }

    /**
     * Create painting item used to display the custom painting.
     *
     * @param imageFileName Image file name.
     * @param player The target player.
     */
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun createPaintingItem(imageFileName: String, player: Player) {
        val paintingItem = ItemStack(Material.PAPER).apply {
            this.itemMeta!!.apply {
                NeonKey.addNeonKey(imageFileName, NeonKeyGeneral.NPAINTING_TOOL.key, PersistentDataType.STRING, this)

                this.setDisplayName("${ChatColor.GOLD}${ChatColor.BOLD}${imageFileName}")
            }.also {
                this.itemMeta = it
            }
        }

        if (!appContext.validateServerProvider(ServerProvider.Paper)) {
            processPainting(imageFileName)
            player.inventory.addItem(paintingItem)
            return
        }

        CoroutineScope(newSingleThreadContext("nPainting Item Creation")).async {
            processPainting(imageFileName)
        }.run {
            this.invokeOnCompletion {
                player.inventory.addItem(paintingItem)
            }
        }
    }

    /**
     * Remove painting based on removal type.
     *
     * @param imageFileName The image to be removed
     * @param removalType The removal type (Full, Cached, Display)
     * @return True if the removal sucess, else false
     */
    fun removePainting(imageFileName: String, renderId: Optional<UUID> = Optional.empty(), removalType: RemovalType): Boolean {
        val neon by inject<Neon>()
        val painting = Handler.getPaintingRenderData(imageFileName).getOrElse { return false }

        /* Stage 1: Remove all the painting from the worlds */
        neon.server.worlds.forEach { world ->
            world.entities
                .filter { entity -> entity.type == EntityType.GLOW_ITEM_FRAME }
                .filter { entity -> NeonKey.hasNeonKey(NeonKeyGeneral.NPAINTING_PROPERTY_HEADER.key, PersistentDataType.STRING, entity) }
                .filter { entity ->
                    val nPaintingProperty= ObjectSerializer.deserializeObjectEncoded(
                        NeonKey.getNeonKeyValue(
                        NeonKeyGeneral.NPAINTING_PROPERTY_HEADER.key,
                        PersistentDataType.STRING,
                        entity).toString()) as HashMap<String, Any>

                    val isSameImageName = NeonKey.getNeonKeyNameWithNamespace(NeonKeyGeneral.NPAINTING_PROPERTY_IMAGE_NAME.key).run {
                        nPaintingProperty[this] as String == imageFileName
                    }

                    val isSameRenderId = NeonKey.getNeonKeyNameWithNamespace(NeonKeyGeneral.NPAINTING_PROPERTY_RENDER_ID.key).run {
                        if (renderId.isEmpty) return@run false

                        nPaintingProperty[this] as UUID == renderId.get()
                    }

                    if (removalType == RemovalType.DISPLAYED) {
                        return@filter (isSameImageName && isSameRenderId)
                    }

                    return@filter isSameImageName
                }.also {
                    if (it.isEmpty()) return@forEach

                    if (removalType == RemovalType.DISPLAYED) {
                        painting.renderedPainting.remove(renderId.get())
                    } else {
                        painting.renderedPainting.clear()
                    }

                    Handler.getPaintingRenderDataFile(imageFileName).writeBytes(ObjectSerializer.serializeObjectRaw(painting))

                    it.forEach { glowItemFrame ->
                        glowItemFrame.remove()
                    }
                }
        }

        if (!(removalType == RemovalType.FULL || removalType == RemovalType.CACHED)) return true

        val usedMapIds = painting.paintingTiles.map { it.tileId }.sorted().toHashSet()

        /* Stage 2: Add all the used ids to reusable ids metadata before removal */
        Handler.getReusableMapIds().sorted().toMutableSet().apply {
            this.addAll(usedMapIds)
            Handler.saveReusableMapIds(this.toCollection(HashSet()))
        }

        NPacketProcessor.getNWorld(Bukkit.getWorlds().find { it.environment == World.Environment.NORMAL }!!).let {
            it.javaClass.getMethod(NmsMap.GetWorldPersistentContainer.remapped).invoke(it).apply worldPersistentContainer@ {
                /* Stage 3: Remove from the cache */
                @Suppress("UNCHECKED_CAST")
                (this@worldPersistentContainer.javaClass.getField(NmsMap.WorldCache.remapped).get(this@worldPersistentContainer) as MutableMap<String, *>)
                    .also { worldCache ->
                        painting.paintingTiles.forEach { paintingTileId ->
                            if (!worldCache.containsKey("map_${paintingTileId.tileId}")) return@forEach

                            worldCache.remove("map_${paintingTileId.tileId}")
                        }
                }

                /* Stage 4: Remove the map data file */
                this@worldPersistentContainer.javaClass.getDeclaredMethod(NmsMap.GetMapDataFile.remapped, String::class.java).also { mapDataFile ->
                    mapDataFile.isAccessible = true

                    usedMapIds.forEach { mapId ->
                        (mapDataFile.invoke(this@worldPersistentContainer, "map_${mapId}") as File).also { file ->
                            if (!file.exists()) return@forEach

                            file.delete()
                        }
                    }
                }
            }
        }

        /* Stage 5: Remove the metadata file */
        Handler.getPaintingRenderDataFile(imageFileName).also {
            if (!it.exists()) return@also

            it.delete()
        }

        if (removalType != RemovalType.FULL) return true

        /* Stage 6: Remove the original image file */
        Handler.hasImageFile(imageFileName).ifPresent { it.delete() }

        return true
    }

    /**
     * Revoke painting destruction cause by Entity and Explosion.
     *
     * @param e The HangingBreakEvent
     */
    fun revokePaintingDestruction(e: HangingBreakEvent) {
        if (!NeonKey.hasNeonKey(NeonKeyGeneral.NPAINTING_PROPERTY_HEADER.key, PersistentDataType.STRING, e.entity)) {
            return
        }

        /* For destruction cause by entity */
        if (e is HangingBreakByEntityEvent) {
            if (e.cause != HangingBreakEvent.RemoveCause.ENTITY) return

            val player = e.remover?.let {
                /* Check if the remover is player,
                * If it is player continue, else just cancel the event */
                if (it !is Player) {
                    e.isCancelled = true
                    return
                }

                it
            } ?: return

            if (!player.isOp) {
                e.isCancelled = true
                return
            }

            // TODO: Temporary item to remove painting
            player.inventory.itemInMainHand.apply {
                if (this.type != Material.STICK) {
                    e.isCancelled = true
                    return
                }

                if (!NeonKey.hasNeonKey(NeonKeyGeneral.N_PAINTING_REMOVAL_STICK.key, PersistentDataType.STRING, this.itemMeta!!)) {
                    e.isCancelled = true
                    return
                }

                /* TODO: Kotlin lib bug */
//                NeonKey.hasNeonKey(NeonKeyGeneral.N_PAINTING_REMOVAL_STICK.key, PersistentDataType.STRING, this.itemMeta!!).ifFalse {
//                    e.isCancelled = true
//                    return
//                }

                val paintingFrame = e.entity as GlowItemFrame

                val paintingData = ObjectSerializer.deserializeObjectEncoded(
                    NeonKey.getNeonKeyValue(
                        NeonKeyGeneral.NPAINTING_PROPERTY_HEADER.key, PersistentDataType.STRING, paintingFrame) as String) as HashMap<String, Any>

                removePainting(
                    paintingData[NeonKey.getNeonKeyNameWithNamespace(NeonKeyGeneral.NPAINTING_PROPERTY_IMAGE_NAME.key)]!! as String,
                    Optional.of(paintingData[NeonKey.getNeonKeyNameWithNamespace(NeonKeyGeneral.NPAINTING_PROPERTY_RENDER_ID.key)]!! as UUID),
                    RemovalType.DISPLAYED
                )
            }

            return
        }

        /* For destruction cause by explosion */
        if (e.cause != HangingBreakEvent.RemoveCause.EXPLOSION) return

        e.isCancelled = true
    }

    /**
     * Pre-processing the target image into map data, where some of the infomation will be saved to the metadata file
     * such as image file name, map ids for future reference.
     *
     * @param imageFileName The name of the image.
     */
    private fun processPainting(imageFileName: String) {
        /* Folder & file existent check and creation */
        val renderDataFile = NeonDataFolder.createNewFile(
            NeonDataFolder.NPaintingRenderDataFolder,
            Handler.getPaintingRenderDataFileName(imageFileName)
        ).also {
            if (it.readBytes().isNotEmpty()) return
        }

        val paintingBuilder = PaintingBuilder()

        paintingBuilder.generatePainting(Painting(imageFileName), renderDataFile)
    }

    fun renderPainting(player: Player, interectedBlock: Block) {
        val paintingItemMeta = player.inventory.itemInMainHand.let {
            if (it.type == Material.AIR || it.type != Material.PAPER) return

            return@let it.itemMeta!!
        }

        if (!NeonKey.hasNeonKey(NeonKeyGeneral.NPAINTING_TOOL.key, PersistentDataType.STRING, paintingItemMeta)) return

        val imageFileName = NeonKey.getNeonKeyValue(NeonKeyGeneral.NPAINTING_TOOL.key, PersistentDataType.STRING, paintingItemMeta) as String

        Handler.getPaintingRenderData(imageFileName).getOrElse { return }.also { painting ->
            if (painting.isRenderFull()) {
                player.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}This painting has reached maximum number of display! " +
                        "Please remove the unwanted to display new one!"))
                return
            }

            val currentRenderId = painting.generateRenderId().also {
                painting.renderedPainting.add(it)
                Handler.getPaintingRenderDataFile(imageFileName).writeBytes(ObjectSerializer.serializeObjectRaw(painting))
            }

            PaintingRenderer().generateRenderer(player, painting, currentRenderId, interectedBlock)
        }
    }

    /*
    * If modX == 1 || modX == -1 posX = exactZ
    * If modZ == 1 || modZ == -1 posX = exactX
    *
    *
    * */

    private class EventProcessor: Listener {
        @EventHandler
        private fun onPlayerInteract(e: PlayerInteractEvent) {
            if (e.action != Action.RIGHT_CLICK_BLOCK) return

            if (e.hand != EquipmentSlot.HAND) return

            renderPainting(e.player, e.clickedBlock!!)
        }

        @EventHandler
        private fun onHangingDestroy(e: HangingBreakEvent) {
            revokePaintingDestruction(e)
        }

        @EventHandler
        private fun onHangingDestroyByEntity(e: HangingBreakByEntityEvent) {
            revokePaintingDestruction(e)
        }
    }
}