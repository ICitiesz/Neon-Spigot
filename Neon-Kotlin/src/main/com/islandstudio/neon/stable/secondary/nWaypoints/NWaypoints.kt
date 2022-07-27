package com.islandstudio.neon.stable.secondary.nWaypoints

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.islandstudio.neon.Neon
import com.islandstudio.neon.experimental.nServerConfigurationNew.NServerConfigurationNew
import com.islandstudio.neon.stable.primary.nServerConfiguration.NServerConfiguration
import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.primary.nCommand.NCommand
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nFolder.FolderList
import com.islandstudio.neon.stable.utils.NItemHighlight
import com.islandstudio.neon.stable.utils.NNamespaceKeys
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.nGUI.NGUIConstructor
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.max

data class NWaypoints(private val waypointData: Map.Entry<String, JSONObject>) {
    val location: Location = Handler.locationDeserializer(waypointData.value["Location"] as String)
    val waypointName: String = waypointData.key

    val dimension: () -> String = fun(): String {
        return when (location.world!!.environment.toString()) {
            "NORMAL" -> "${ChatColor.GREEN}Over World"
            "NETHER" -> "${ChatColor.RED}Nether"
            "THE_END" -> "${ChatColor.DARK_PURPLE}The End"
            else -> "${ChatColor.GRAY}Unknown"
        }
    }

    val availability: (player: Player) -> String = fun(player: Player): String {
        if (player.location.world!!.environment.toString().equals(location.world!!.environment.toString(), true)) return "${ChatColor.GREEN}Available!"

        return "${ChatColor.RED}Unavailable!"
    }

    val blockX: Int = location.blockX
    val blockY: Int = location.blockY
    val blockZ: Int = location.blockZ

    object Handler {
        val waypointDataContainer: TreeMap<String, TreeMap<String, JSONObject>> = TreeMap()

        private val classLoader: ClassLoader = NWaypoints::class.java.classLoader

        private val jsonParser: JSONParser = JSONParser()
        private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

        /**
         * Initialize the nWaypoints.
         */
        fun run() {
            createNewFiles()

            val fileReader = FileReader(getNWaypointsFile())
            val clientBufferedReader = BufferedReader(fileReader)
            val clientNWaypointsFileSize: Long = clientBufferedReader.lines().count()

            fileReader.close()
            clientBufferedReader.close()

            if (clientNWaypointsFileSize == 0L) {
                val clientFileOutputStream = FileOutputStream(getNWaypointsFile())
                val clientBufferedWriter = BufferedWriter(OutputStreamWriter(clientFileOutputStream))

                val stringBuilder = StringBuilder()
                val inputStream: InputStream = classLoader.getResourceAsStream("resources/nWaypoints.json")!!
                val sourceBufferedReader = BufferedReader(InputStreamReader(inputStream))

                val sourceElement: Array<Any> = sourceBufferedReader.lines()!!.toArray()

                sourceElement.forEach { content ->
                    stringBuilder.append(content)
                }

                clientBufferedWriter.write(gson.toJson(jsonParser.parse(stringBuilder.toString()) as JSONObject))
                clientBufferedWriter.close()
                clientFileOutputStream.close()
            }
        }

        /**
         * Teleport the player to the selected waypoint.
         *
         * @param player The player to teleport. (Player)
         * @param location Player's location. (Location)
         * @param goldWaypoint The waypoint name. (String)
         * @param posX The X coordinate of the waypoint. (Int)
         * @param posY The Y coordinate of the waypoint. (Int)
         * @param posZ The Z coordinate of the waypoint. (Int)
         */
        fun teleportToWaypoint(player: Player, location: Location, goldWaypoint: String, posX: Int, posY: Int, posZ: Int) {
            player.spawnParticle(Particle.PORTAL, player.location, 700)
            player.teleport(location)

            player.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f)
            player.spawnParticle(Particle.PORTAL, player.location, 700)
            player.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.GREEN}You have been teleported to $goldWaypoint${ChatColor.GRAY}, " +
                    "${ChatColor.AQUA}$posX${ChatColor.GRAY}, ${ChatColor.AQUA}$posY${ChatColor.GRAY}, ${ChatColor.AQUA}$posZ${ChatColor.GREEN}!"))
        }

        /**
         * Set command handler for the nWaypoints.
         *
         * @param commander The player who executed the command.
         * @param args The command arguments.
         */
        fun setCommandHandler(commander: Player, args: Array<out String>) {
            if (commander.isSleeping) {
                commander.sendMessage(CommandSyntax.createSyntaxMessage(
                    "${ChatColor.YELLOW}You may not using nWaypoints while sleeping!"
                ))
                return
            }

            when (args.size) {
                1 -> {
                    if (getNWaypointsData().size == 0) {
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}There are currently no waypoints!"))
                        return
                    }

                    waypointDataContainer[commander.uniqueId.toString()] = getNWaypointsData()
                    GUIHandlerCreation(NGUI.Handler.getNGUI(commander)).openGUI()
                }

                2 -> {
                    if (!args[1].equals("remove", true)) {
                        commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                        return
                    }

                    if (getNWaypointsData().size == 0) {
                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}There are currently no waypoints!"))
                        return
                    }

                    waypointDataContainer[commander.uniqueId.toString()] = getNWaypointsData()
                    GUIHandlerRemoval(NGUI.Handler.getNGUI(commander)).openGUI()
                }

                3 -> {
                    if (args[1].equals("add", true)) {
                        if (args[2].contains("\\") || args[2].contains("\"")) {
                            commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.RED}The waypoint name must not contain${ChatColor.GOLD} " +
                                "\\ ${ChatColor.RED}or${ChatColor.GOLD} \"${ChatColor.RED}!"))
                            return
                        }

                        if (getNWaypointsData().containsKey(args[2])) {
                            commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}The waypoint name ${ChatColor.GRAY}'${ChatColor.GOLD}${args[2]}" +
                                    "${ChatColor.GRAY}' ${ChatColor.YELLOW}already exist! Please try another one!"))
                            return
                        }

                        addWaypoint(args[2], commander)

                        commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.GREEN}The waypoint has been saved as " +
                                "${ChatColor.GRAY}'${ChatColor.GOLD}${args[2]}${ChatColor.GRAY}'${ChatColor.GREEN}!"))
                        return
                    }

                    if (args[1].equals("remove", true)) {
                        val waypointName = args[2]

                        GUIHandlerRemoval.removalContainer.forEach { container ->
                            if (!container.key.equals(commander.uniqueId.toString(), true)) {
                                GUIHandlerRemoval.removalContainer[container.key]!!.remove(waypointName)
                            }
                        }

                        waypointDataContainer.forEach { waypointData ->
                            if (!waypointData.key.equals(commander.uniqueId.toString(), true)) {
                                waypointDataContainer[waypointData.key]!!.remove(waypointName)
                            }
                        }
                        removeWaypoint(args[2])

                        Bukkit.getServer().broadcastMessage(CommandSyntax.createSyntaxMessage(
                    "${ChatColor.RED}The waypoint ${ChatColor.GRAY}'${ChatColor.GOLD}${waypointName}${ChatColor.GRAY}'${ChatColor.RED} has been " +
                                    "removed by ${ChatColor.WHITE}${commander.name}${ChatColor.RED}!"))
                        return
                    }
                }

                else -> {
                    commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                    return
                }
            }
        }

        /**
         * Set tab completion for the nWaypoints.
         *
         * @param commander The player who executed the command. (Player)
         * @param args The arguments of the command. (String[])
         * @return A list of tab completion. (List<String>)
         */
        fun tabCompletion(commander: Player, args: Array<out String>): MutableList<String> {
            when (args.size) {
                2 -> {
                    return listOf("add", "remove").filter { it.startsWith(args[1]) }.toMutableList()
                }

                3 -> {
                    if (!args[1].equals("remove", true)) return mutableListOf()

                    return getNWaypointsData().keys.toList().filter { it.startsWith(args[2]) }.toMutableList()
                }
            }

            return mutableListOf()
        }

        /* Get waypoints data */
        fun getNWaypointsData(): TreeMap<String, JSONObject> {
            val waypointsData: MutableMap<String, JSONObject> = TreeMap()

            val fileReader = FileReader(getNWaypointsFile())
            val jsonParser = JSONParser()
            val waypoints: JSONObject = jsonParser.parse(fileReader) as JSONObject

            waypoints.forEach { waypoint ->
                val innerJsonArray: JSONArray = waypoint.value as JSONArray
                val innerJsonObject: JSONObject = innerJsonArray[0] as JSONObject

                waypointsData[waypoint.key as String] = innerJsonObject
            }

            fileReader.close()

            return waypointsData as TreeMap<String, JSONObject>
        }

        /* Remove waypoints operation */
        fun removeWaypoint(waypointName: String) {
            val fileReader = FileReader(getNWaypointsFile())

            val jsonParser = JSONParser()
            val waypoints: JSONObject = jsonParser.parse(fileReader) as JSONObject

            if (waypoints.containsKey(waypointName)) {
                waypoints.remove(waypointName)
            }

            fileReader.close()

            val fileOutputStream = FileOutputStream(getNWaypointsFile())
            val bufferedWriter = BufferedWriter(OutputStreamWriter(fileOutputStream))

            bufferedWriter.write(gson.toJson(waypoints))
            bufferedWriter.close()
        }

        /* Deserialize the base64 encoded string into location object */
        fun locationDeserializer(waypointLocation: String): Location {
            val byteArrayInputStream = ByteArrayInputStream(Base64.getDecoder().decode(waypointLocation))
            val bukkitObjetInputStream = BukkitObjectInputStream(byteArrayInputStream)

            return bukkitObjetInputStream.readObject() as Location
        }

        /* Serialize the location object into base64 encoded string */
        private fun locationSerializer(player: Player): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val bukkitObjectOutputStream = BukkitObjectOutputStream(byteArrayOutputStream)

            bukkitObjectOutputStream.writeObject(player.location)
            bukkitObjectOutputStream.flush()

            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
        }

        /* Add waypoints operation */
        private fun addWaypoint(waypointName: String, player: Player) {
            val waypointsFile: File = getNWaypointsFile()

            if (!waypointsFile.exists()) run()

            val fileReader = FileReader(waypointsFile)
            val clientBufferedReader = BufferedReader(waypointsFile.reader())
            val clientNWaypointsFileSize: Long = clientBufferedReader.lines().count()
            clientBufferedReader.close()

            if (clientNWaypointsFileSize == 0L) run()

            val mainParent: JSONObject = jsonParser.parse(fileReader) as JSONObject
            val waypointHeader = JSONArray()
            val waypointBody = JSONObject()

            waypointBody["Location"] = locationSerializer(player)
            waypointHeader.add(waypointBody)
            mainParent[waypointName] = waypointHeader

            val fileOutputStream = FileOutputStream(waypointsFile)
            val bufferedWriter = BufferedWriter(OutputStreamWriter(fileOutputStream))

            fileReader.close()
            bufferedWriter.write(gson.toJson(mainParent))
            bufferedWriter.close()
        }

        /* Get waypoints file */
        private fun getNWaypointsFile(): File {
            return File(FolderList.NWAYPOINTS_FOLDER.folder, "nWaypoints-Global.json")
        }

        /* File generation */
        private fun createNewFiles() {
            val nWaypointsFile = getNWaypointsFile()

            if (!FolderList.NWAYPOINTS_FOLDER.folder.exists()) {
                FolderList.NWAYPOINTS_FOLDER.folder.mkdirs()
            }

            if (!nWaypointsFile.exists()) {
                nWaypointsFile.createNewFile()
            }
        }
    }

    /* This class handle main GUI for nWaypoints */
    class GUIHandlerCreation(nGUI: NGUI): GUIBuilderCreation(nGUI) {
        companion object {
            var isClicked: Boolean = false // isClicked is used to remove the player from nGUIContainer
        }

        private val plugin: Plugin = getPlugin(Neon::class.java)
        private val player: Player = nGUI.getGUIOwner()

        override fun getGUIName(): String {
            return "${ChatColor.LIGHT_PURPLE}${ChatColor.MAGIC}--------${ChatColor.DARK_PURPLE}${ChatColor.BOLD}nWaypoints${ChatColor.LIGHT_PURPLE}${ChatColor.MAGIC}--------"
        }

        override fun getGUISlots(): Int {
            return 54
        }

        override fun setItems() {
            val waypoints: ArrayList<Map.Entry<String, JSONObject>> = Handler.getNWaypointsData().entries.toCollection(ArrayList())
            val waypointDetails: ArrayList<String> = ArrayList()

            maxPage = ceil(waypoints.size.toDouble() / maxItemPerPage.toDouble()).toInt()

            addGUIButtons()

            for (i in 0 until super.maxItemPerPage) {
                itemIndex = super.maxItemPerPage * pageIndex + i

                if (itemIndex >= waypoints.size) break

                val waypoint = ItemStack(Material.BEACON)
                val waypointMeta: ItemMeta = waypoint.itemMeta!!

                val nWaypoints = NWaypoints(waypoints[itemIndex])

                val waypointName = nWaypoints.waypointName
                val waypointBlockX: Int = nWaypoints.blockX
                val waypointBlockY: Int = nWaypoints.blockY
                val waypointBlockZ: Int = nWaypoints.blockZ
                val waypointAvailability: (player: Player) -> String = nWaypoints.availability
                val waypointDimension: () -> String = nWaypoints.dimension

                waypointDetails.add(
                    "${ChatColor.GRAY}Coordinate: ${ChatColor.AQUA}${waypointBlockX}${ChatColor.GRAY}, " +
                    "${ChatColor.AQUA}${waypointBlockY}${ChatColor.GRAY}, ${ChatColor.AQUA}${waypointBlockZ}"
                )
                waypointDetails.add("${ChatColor.GRAY}Dimension: ${waypointDimension()}")

                if (!(NServerConfigurationNew.getOptionValue("nWaypoints", "cross_dimension") as Boolean)) {
                    waypointDetails.add("${ChatColor.GRAY}Status: ${waypointAvailability(player)}")
                }

                waypointMeta.setDisplayName("${ChatColor.GOLD}${waypointName}")
                waypointMeta.lore = waypointDetails

                waypointMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

                waypoint.itemMeta = waypointMeta
                inventory.addItem(waypoint)

                waypointDetails.clear()
            }

        }

        override fun guiClickHandler(e: InventoryClickEvent) {
            val currentItem = e.currentItem!!
            val currentItemMeta = currentItem.itemMeta!!
            val persistentDataContainer: PersistentDataContainer = currentItemMeta.persistentDataContainer

            when (currentItem.type) {
                /* Waypoint button */
                Material.BEACON -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    Handler.getNWaypointsData().forEach { waypointData ->
                        val nWaypoints = NWaypoints(waypointData)
                        val goldWaypointName = "${ChatColor.GOLD}${nWaypoints.waypointName}"

                        if (!currentItemMeta.displayName.equals(goldWaypointName, true)) return@forEach

                        val waypointBlockX: Double = nWaypoints.blockX.toDouble()
                        val waypointBlockY: Double = nWaypoints.blockY.toDouble()
                        val waypointBlockZ: Double = nWaypoints.blockZ.toDouble()

                        val modifiedLocation: Location  = nWaypoints.location
                        modifiedLocation.x = waypointBlockX + 0.5
                        modifiedLocation.z = waypointBlockZ + 0.5

                        if (!(NServerConfigurationNew.getOptionValue("nWaypoints", "cross_dimension") as Boolean)) {
                            if (player.location.world!!.environment.toString().equals(modifiedLocation.world!!.environment.toString(), true)) {
                                Handler.teleportToWaypoint(player, modifiedLocation, goldWaypointName,
                                    waypointBlockX.toInt(), waypointBlockY.toInt(), waypointBlockZ.toInt()
                                )
                                return
                            }

                            player.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Cross dimension for nWaypoints has been restricted!"))
                            return
                        }

                        Handler.teleportToWaypoint(player, modifiedLocation, goldWaypointName,
                            waypointBlockX.toInt(), waypointBlockY.toInt(), waypointBlockZ.toInt()
                        )

                        e.isCancelled = true
                        plugin.server.scheduler.scheduleSyncDelayedTask(plugin, player::closeInventory, 0L)
                        return
                    }
                }

                /* Navigation button  */
                Material.SPECTRAL_ARROW -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    when (currentItemMeta.displayName) {
                        previousButtonDisplayName -> {
                            isClicked = true

                            if (pageIndex == 0) return

                            pageIndex--
                            super.openGUI()
                        }

                        nextButtonDisplayName -> {
                            isClicked = true

                            if ((itemIndex + 1) >= Handler.getNWaypointsData().size) return

                            pageIndex++
                            super.openGUI()
                        }
                    }
                }

                /* Close button */
                Material.BARRIER -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    if (currentItemMeta.displayName != closeButtonDisplayName) return

                    player.closeInventory()
                }

                else -> {
                    return
                }
            }
        }

        /* Set event handler */
        fun setEventHandler(e: InventoryClickEvent) {
            val player: Player = e.whoClicked as Player

            if (e.view.title != GUIHandlerCreation(NGUI.Handler.getNGUI(player)).getGUIName()) return

            val clickedInventory: Inventory = e.clickedInventory ?: return

            val inventoryHolder: InventoryHolder = clickedInventory.holder!!

            if (clickedInventory == player.inventory) e.isCancelled = true

            if (inventoryHolder !is NGUIConstructor) return

            e.isCancelled = true

            if (e.currentItem == null) return

            val nGuiConstructor: NGUIConstructor = inventoryHolder
            nGuiConstructor.guiClickHandler(e)
        }

    }

    /* This class handle GUI for nWaypoints Removal */
    class GUIHandlerRemoval(nGUI: NGUI) : GUIBuilderRemoval(nGUI) {
        companion object {
            var isClicked: Boolean = false // isClicked is used to remove the player from nGUIContainer
            var removalContainer: MutableMap<String, MutableSet<String>> = HashMap()
        }

        private val player: Player = nGUI.getGUIOwner()

        override fun getGUIName(): String {
            return "${ChatColor.LIGHT_PURPLE}${ChatColor.MAGIC}----${ChatColor.DARK_PURPLE}${ChatColor.BOLD}nWaypoints " +
                    "${ChatColor.RED}${ChatColor.BOLD}Removal${ChatColor.LIGHT_PURPLE}${ChatColor.MAGIC}----"
        }

        override fun getGUISlots(): Int {
            return 54
        }

        override fun setItems() {
            val waypoints: ArrayList<Map.Entry<String, JSONObject>> = Handler.getNWaypointsData().entries.toCollection(ArrayList())
            val waypointDetails: ArrayList<String> = ArrayList()

            maxPage = ceil(waypoints.size.toDouble() / maxItemPerPage.toDouble()).toInt()

            addGUIButtons()

            for (i in 0 until super.maxItemPerPage) {
                itemIndex = super.maxItemPerPage * pageIndex + i

                if (itemIndex >= waypoints.size) break

                val waypoint = ItemStack(Material.BEACON)
                val waypointMeta: ItemMeta = waypoint.itemMeta!!

                val nWaypoints = NWaypoints(waypoints[itemIndex])

                val waypointName: String = nWaypoints.waypointName
                val waypointBlockX: Int = nWaypoints.blockX
                val waypointBlockY: Int = nWaypoints.blockY
                val waypointBlockZ: Int = nWaypoints.blockZ
                val waypointDimension: () -> String = nWaypoints.dimension

                waypointDetails.add(
                    "${ChatColor.GRAY}Coordinate: ${ChatColor.AQUA}${waypointBlockX}${ChatColor.GRAY}, " +
                    "${ChatColor.AQUA}${waypointBlockY}${ChatColor.GRAY}, ${ChatColor.AQUA}${waypointBlockZ}"
                )
                waypointDetails.add("${ChatColor.GRAY}Dimension: ${waypointDimension()}")

                waypointMeta.setDisplayName("${ChatColor.GOLD}${waypointName}")
                waypointMeta.lore = waypointDetails

                waypointMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

                waypoint.itemMeta = waypointMeta
                inventory.addItem(waypoint)

                waypointDetails.clear()
            }
        }

        override fun guiClickHandler(e: InventoryClickEvent) {
            val selectedWaypoints: MutableSet<String> = if (removalContainer.containsKey(player.uniqueId.toString())) {
                removalContainer[player.uniqueId.toString()]!!
            } else {
                HashSet()
            }

            val currentItem: ItemStack = e.currentItem!!
            val currentItemMeta: ItemMeta = currentItem.itemMeta!!
            val nItemHighlight = NItemHighlight(NNamespaceKeys.NEON_BUTTON_HIGHLIGHT.key)
            val persistentDataContainer: PersistentDataContainer = currentItemMeta.persistentDataContainer

            when (currentItem.type) {
                /* Waypoint button */
                Material.BEACON -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    Handler.getNWaypointsData().forEach { waypointData ->
                        val nWaypoints = NWaypoints(waypointData)
                        val goldWaypointName = "${ChatColor.GOLD}${nWaypoints.waypointName}"
                        val waypointsDetails: MutableList<String> = currentItemMeta.lore!!

                        if (!currentItemMeta.displayName.equals(goldWaypointName, true)) return@forEach

                        if (!currentItemMeta.hasEnchant(nItemHighlight)) {
                            currentItemMeta.addEnchant(nItemHighlight, 0, true)
                            waypointsDetails.add("${ChatColor.GREEN}${ChatColor.BOLD}Selected!")

                            selectedWaypoints.add(nWaypoints.waypointName)
                        } else {
                            currentItemMeta.removeEnchant(nItemHighlight)
                            waypointsDetails.remove("${ChatColor.GREEN}${ChatColor.BOLD}Selected!")
                            selectedWaypoints.remove(nWaypoints.waypointName)
                        }

                        currentItemMeta.lore = waypointsDetails
                        currentItem.itemMeta = currentItemMeta
                    }

                    if (!removalContainer.containsKey(player.uniqueId.toString())) {
                        removalContainer[player.uniqueId.toString()] = selectedWaypoints
                    }
                }

                /* Navigation button */
                Material.SPECTRAL_ARROW -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    when (currentItemMeta.displayName) {
                        previousButtonDisplayName -> {
                            isClicked = true

                            if (pageIndex == 0) return

                            pageIndex--
                            super.openGUI()

                            val contents: Array<ItemStack> = super.getInventory().contents

                            contents.forEach { item: ItemStack? ->
                                if (item == null) return@forEach

                                if (item.type != Material.BEACON) return

                                val itemMeta: ItemMeta = item.itemMeta!!

                                selectedWaypoints.forEach { selectedWaypoint: String ->
                                    if (itemMeta.displayName.substring(2).equals(selectedWaypoint, true)) {
                                        val waypointsDetails: MutableList<String> = itemMeta.lore!!

                                        if (!itemMeta.hasEnchant(nItemHighlight)) {
                                            itemMeta.addEnchant(nItemHighlight, 0, true)
                                            waypointsDetails.add("${ChatColor.GREEN}${ChatColor.BOLD}Selected!")
                                        } else {
                                            itemMeta.removeEnchant(nItemHighlight)
                                            waypointsDetails.remove("${ChatColor.GREEN}${ChatColor.BOLD}Selected!")
                                        }

                                        itemMeta.lore = waypointsDetails
                                        item.itemMeta = itemMeta
                                    }
                                }
                            }
                        }

                        nextButtonDisplayName -> {
                            isClicked = true

                            if ((itemIndex + 1) >= Handler.getNWaypointsData().size) return

                            pageIndex++
                            super.openGUI()

                            val contents: Array<ItemStack> = super.getInventory().contents

                            contents.forEach { item: ItemStack? ->
                                if (item == null) return@forEach

                                if (item.type != Material.BEACON) return

                                val itemMeta: ItemMeta = item.itemMeta!!

                                selectedWaypoints.forEach { selectedWaypoint: String ->
                                    if (itemMeta.displayName.substring(2).equals(selectedWaypoint, true)) {
                                        val waypointsDetails: MutableList<String> = itemMeta.lore!!

                                        if (!itemMeta.hasEnchant(nItemHighlight)) {
                                            itemMeta.addEnchant(nItemHighlight, 0, true)
                                            waypointsDetails.add("${ChatColor.GREEN}${ChatColor.BOLD}Selected!")
                                        } else {
                                            itemMeta.removeEnchant(nItemHighlight)
                                            waypointsDetails.remove("${ChatColor.GREEN}${ChatColor.BOLD}Selected!")
                                        }

                                        itemMeta.lore = waypointsDetails
                                        item.itemMeta = itemMeta
                                    }
                                }
                            }
                        }
                    }
                }

                /* Close button */
                Material.BARRIER -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    if (currentItemMeta.displayName != closeButtonDisplayName) return
                    player.closeInventory()
                }

                /* Remove button */
                Material.BLAZE_POWDER -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    if (currentItemMeta.displayName != removeButtonDisplayName) return

                    if (selectedWaypoints.isEmpty()) {
                        player.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.RED}You must select at least one waypoint to delete!"))
                        return
                    }

                    player.closeInventory()

                    val tempStringContainer = ArrayList<String>()
                    val stringBuilder = StringBuilder()

                    selectedWaypoints.forEach { waypoint ->
                        removalContainer.forEach { container ->
                            if (!container.key.equals(player.uniqueId.toString(), true)) {
                                removalContainer[container.key]!!.remove(waypoint)
                            }
                        }

                        Handler.waypointDataContainer.forEach { waypointData ->
                            if (!waypointData.key.equals(player.uniqueId.toString(), true)) {
                                Handler.waypointDataContainer[waypointData.key]!!.remove(waypoint)
                            }
                        }

                        tempStringContainer.add("${ChatColor.GRAY}'${ChatColor.GOLD}$waypoint${ChatColor.GRAY}'")
                        tempStringContainer.add("${ChatColor.RED}, ")
                        Handler.removeWaypoint(waypoint)
                    }

                    if (tempStringContainer.last().equals("${ChatColor.RED}, ", true)) {
                        tempStringContainer[tempStringContainer.lastIndex] = " "
                    }

                    tempStringContainer.forEach { tempString: String ->
                        stringBuilder.append(tempString)
                    }

                    NConstructor.plugin.server.broadcastMessage(CommandSyntax.createSyntaxMessage("${ChatColor.RED}The " +
                            "waypoint(s): ${stringBuilder}${ChatColor.RED}has been removed by${ChatColor.WHITE} ${player.name}${ChatColor.RED}!"))
                }

                else -> {
                    return
                }
            }
        }

        /* Set event handler */
        fun setEventHandler(e: InventoryClickEvent) {
            val player: Player = e.whoClicked as Player

            if (e.view.title != GUIHandlerRemoval(NGUI.Handler.getNGUI(player)).getGUIName()) return

            val clickedInventory: Inventory = e.clickedInventory ?: return

            val inventoryHolder: InventoryHolder = clickedInventory.holder!!

            if (clickedInventory == player.inventory) e.isCancelled = true

            if (inventoryHolder !is NGUIConstructor) return

            e.isCancelled = true

            if (e.currentItem == null) return

            val nGuiConstructor: NGUIConstructor = inventoryHolder
            nGuiConstructor.guiClickHandler(e)
        }

    }
}