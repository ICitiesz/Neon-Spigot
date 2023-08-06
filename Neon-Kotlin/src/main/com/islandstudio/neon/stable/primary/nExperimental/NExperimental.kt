package com.islandstudio.neon.stable.primary.nExperimental

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.islandstudio.neon.stable.primary.nCommand.CommandSyntax
import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import com.islandstudio.neon.stable.primary.nFolder.FolderList
import com.islandstudio.neon.stable.utils.NItemHighlight
import com.islandstudio.neon.stable.utils.NeonKey
import com.islandstudio.neon.stable.utils.nGUI.NGUI
import com.islandstudio.neon.stable.utils.nGUI.NGUIConstructor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.*
import kotlin.math.ceil

data class NExperimental(private val experimentalData: Map.Entry<Any?, Any?>) {
    private val jsonObject: JSONObject = (experimentalData.value as JSONArray)[0] as JSONObject

    val experimentalName: String = experimentalData.key as String
    val isEnabled: Boolean = jsonObject["is_enabled"] as Boolean
    val experimentalDescription: String = jsonObject["description"] as String
    val conflict: String = jsonObject["conflict"] as String

    object Handler {
        private val classLoader: ClassLoader = NExperimental::class.java.classLoader

        private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        private val jsonParser: JSONParser = JSONParser()

        /**
         * Initialize the nExperimental.
         */
        fun run() {
            createNewFiles()

            val fileReader = FileReader(getNExperimentalFile())
            val clientBufferedReader = BufferedReader(fileReader)
            val clientNExperimentalContentSize: Long = clientBufferedReader.lines().count()

            fileReader.close()
            clientBufferedReader.close()

            if (clientNExperimentalContentSize == 0L) {
                val clientFileOutputStream = FileOutputStream(getNExperimentalFile())
                val clientBufferedWriter = BufferedWriter(OutputStreamWriter(clientFileOutputStream))

                clientBufferedWriter.write(gson.toJson(getSourceElement()))
                clientBufferedWriter.close()
                clientFileOutputStream.close()

                return
            }

            updateElement()

            NConstructor.registerEventProcessor(EventController())
        }

        /* Save the nExperiment config */
        fun save(experimentConfig: JSONObject) {
            val fileOutputStream = FileOutputStream(getNExperimentalFile())
            val bufferedWriter = BufferedWriter(OutputStreamWriter(fileOutputStream))

            bufferedWriter.write(gson.toJson(experimentConfig))
            bufferedWriter.close()
        }

        fun setCommandHandler(commander: Player, args: Array<out String>) {
            if (!commander.isOp) {
                commander.sendMessage(CommandSyntax.INVALID_PERMISSION.syntaxMessage)
                return
            }

            if (args.size > 1) {
                commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.syntaxMessage)
                return
            }

            if (getSourceElement().size == 0) {
                commander.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}There are no experiment features available for testing."))
                return
            }

            GUIHandler(NGUI.Handler.getNGUI(commander)).openGUI()
        }

        /* Update the content of nExperiment.json if any experimental feature added or removed. */
        private fun updateElement() {
            val fileReader = FileReader(getNExperimentalFile())
            val sourceElement: JSONObject = getSourceElement()
            val clientElement: JSONObject = jsonParser.parse(fileReader) as JSONObject

            sourceElement.keys.forEach { key ->
                if (!clientElement.containsKey(key)) clientElement.putIfAbsent(key, sourceElement[key])
            }

            clientElement.keys.removeIf { key -> !sourceElement.containsKey(key) }

            sourceElement.forEach { element ->
                if (clientElement.size > 0) {
                    val nExperimental = NExperimental(element)

                    val sourceExperimentalDetails: JSONObject = (sourceElement[nExperimental.experimentalName] as JSONArray)[0] as JSONObject
                    val clientExperimentalDetails: JSONObject = (clientElement[nExperimental.experimentalName] as JSONArray)[0] as JSONObject

                    if (clientExperimentalDetails["description"] != sourceExperimentalDetails["description"]) clientExperimentalDetails["description"] = sourceExperimentalDetails["description"]
                    if (clientExperimentalDetails["conflict"] != sourceExperimentalDetails["conflict"]) clientExperimentalDetails["conflict"] = sourceExperimentalDetails["conflict"]
                }
            }

            val clientFileOutputStream = FileOutputStream(getNExperimentalFile())
            val clientBufferedWriter = BufferedWriter(OutputStreamWriter(clientFileOutputStream))

            clientBufferedWriter.write(gson.toJson(clientElement))
            clientBufferedWriter.close()
            clientFileOutputStream.close()

            fileReader.close()
        }

        /* Get source element */
        fun getSourceElement(): JSONObject {
            val inputStream: InputStream = classLoader.getResourceAsStream("resources/nExperimental.json")!!
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            val stringBuilder = StringBuilder()

            bufferedReader.lines()!!.toArray().forEach { content: Any ->
                stringBuilder.append(content)
            }

            return jsonParser.parse(stringBuilder.toString()) as JSONObject
        }

        /* Get client element */
        fun getClientElement(): JSONObject {
            return jsonParser.parse(FileReader(getNExperimentalFile())) as JSONObject
        }

        /* Create requires folders and files */
        private fun createNewFiles() {
            val nExperimentalFile: File = getNExperimentalFile()

            if (!FolderList.SERVER_CONFIG_FOLDER.folder.exists()) {
                FolderList.SERVER_CONFIG_FOLDER.folder.mkdirs()
            }

            if (!nExperimentalFile.exists()) {
                nExperimentalFile.createNewFile()
            }
        }

        /* Get the nExperimental.json file */
        private fun getNExperimentalFile(): File {
            return File(FolderList.NEXPERIMENTAL_FOLDER.folder, "nExperimental.json")
        }
    }

    /* GUI handler for nExperiment */
    class GUIHandler(nGUI: NGUI): GUIBuilder(nGUI) {
        private val sourceElement: JSONObject = Handler.getSourceElement()
        private val clientElement: JSONObject = Handler.getClientElement()

        private val player: Player = nGUI.getGUIOwner()

        override fun getGUIName(): String {
            return "${ChatColor.YELLOW}${ChatColor.MAGIC}-------${ChatColor.GOLD}${ChatColor.BOLD}nExperimental${ChatColor.YELLOW}${ChatColor.MAGIC}------"
        }

        override fun getGUISlots(): Int {
            return 54
        }

        @Suppress("UNCHECKED_CAST")
        override fun setGUIButtons() {
            if (sourceElement.size == 0) return

            maxPage = ceil((sourceElement.size.toDouble() / maxItemPerPage.toDouble())).toInt()

            addGUIButtons()

            for (i in 0 until super.maxItemPerPage) {
                itemIndex = super.maxItemPerPage * pageIndex + i

                if (itemIndex >= sourceElement.size) break

                val experimentalDetails: ArrayList<String> = ArrayList()

                val sourceExperimental = sourceElement.entries.elementAt(itemIndex)
                val clientExperimental = clientElement.entries.elementAt(itemIndex)

                val nExperimentalSource = NExperimental(sourceExperimental as Map.Entry<String, Any>)
                val nExperimentalClient = NExperimental(clientExperimental as Map.Entry<String, Any>)

                val experimentFeature = ItemStack(Material.BIRCH_SIGN)
                val experimentFeatureMeta = experimentFeature.itemMeta!!

                /* Is enabled? */
                if (nExperimentalClient.isEnabled) {
                    experimentalDetails.add("${ChatColor.GRAY}Status: ${ChatColor.GREEN}Enabled!")
                    experimentFeatureMeta.addEnchant(NItemHighlight(NeonKey.NamespaceKeys.NEON_BUTTON_HIGHLIGHT.key), 0, true)
                } else {
                    experimentalDetails.add("${ChatColor.GRAY}Status: ${ChatColor.RED}Disabled!")
                }

                experimentalDetails.add("")

                /* Experimental feature description */
                val description: ArrayList<String> = nExperimentalSource.experimentalDescription.split(" ").toCollection(ArrayList())
                val modifiedDescription: ArrayList<Collection<String>> = ArrayList()
                var splicedWords: ArrayList<String> = ArrayList()

                /* Spit up the description into 7 words for a sentence */
                for (word in description) {
                    if (splicedWords.size == 7) {
                        modifiedDescription.add(splicedWords)
                        splicedWords = ArrayList()
                    }

                    splicedWords.add(word)

                    if ((description.size - description.indexOf(word)) == 1) {
                        modifiedDescription.add(splicedWords)
                    }
                }

                experimentalDetails.add("${ChatColor.GRAY}Description: ")

                modifiedDescription.forEach { words ->
                    experimentalDetails.add("${ChatColor.YELLOW}${words.joinToString(" ")}")
                }

                experimentalDetails.add("")

                /* Experimental feature conflict */
                if (nExperimentalSource.conflict.equals("none", true)) {
                    experimentalDetails.add("${ChatColor.GRAY}Conflict: ${ChatColor.GREEN}None!")
                } else {
                    experimentalDetails.add("${ChatColor.GRAY}Conflict: ${ChatColor.RED}${nExperimentalSource.conflict}!")
                }

                experimentFeatureMeta.setDisplayName("${ChatColor.GOLD}${nExperimentalSource.experimentalName}")
                experimentFeatureMeta.lore = experimentalDetails

                experimentFeatureMeta.persistentDataContainer.set(buttonIDKey, PersistentDataType.STRING, buttonIDKey.toString())

                experimentFeature.itemMeta = experimentFeatureMeta

                inventory.addItem(experimentFeature)
            }
        }

        /* Click handler for nExperiment GUI */
        @Suppress("UNCHECKED_CAST")
        override fun setGUIClickHandler(e: InventoryClickEvent) {
            val clickedItem: ItemStack = e.currentItem!!
            val clickedItemMeta: ItemMeta = clickedItem.itemMeta!!
            val persistentDataContainer: PersistentDataContainer = clickedItemMeta.persistentDataContainer

            when (clickedItem.type) {
                /* Experimental feature button */
                Material.BIRCH_SIGN -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    val clickedItemDisplayName: String = clickedItemMeta.displayName

                    Handler.getSourceElement().forEach { element ->
                        val nExperimental = NExperimental(element as Map.Entry<String, Any>)

                        if (("${ChatColor.GOLD}${nExperimental.experimentalName}").equals(clickedItemDisplayName, true)) {
                            val lore: ArrayList<String> = (clickedItemMeta.lore as ArrayList<String>)

                            val status1 = "${ChatColor.GRAY}Status: ${ChatColor.GREEN}Enabled!"
                            val status2 = "${ChatColor.GRAY}Status: ${ChatColor.RED}Disabled!"

                            val experimentalDetails: JSONObject = (clientElement[nExperimental.experimentalName] as JSONArray)[0] as JSONObject

                            lore.forEach { status ->
                                if (status.contains(status1)) {
                                    if (experimentalDetails["is_enabled"] as Boolean) experimentalDetails["is_enabled"] = false
                                    lore[lore.indexOf(status)] = status2
                                    clickedItemMeta.removeEnchant(nItemHighlight)
                                } else if (status.contains(status2)) {
                                    if (!(experimentalDetails["is_enabled"] as Boolean)) experimentalDetails["is_enabled"] = true
                                    lore[lore.indexOf(status)] = status1
                                    clickedItemMeta.addEnchant(nItemHighlight, 0, true)
                                }
                            }

                            clickedItemMeta.lore = lore
                            clickedItem.itemMeta = clickedItemMeta
                        }
                    }
                }

                /* Apply button */
                Material.LEVER -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    if (clickedItemMeta.displayName != applyButtonDisplayName) return

                    Handler.save(clientElement)

                    player.closeInventory()
                    player.sendMessage(CommandSyntax.createSyntaxMessage("${ChatColor.YELLOW}Please reload the server to apply the effects!"))
                }

                /* Navigation button */
                Material.SPECTRAL_ARROW -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    if (clickedItemMeta.displayName.equals(previousButtonDisplayName, true)) {
                        if (pageIndex == 0)  return

                        pageIndex--
                        super.openGUI()
                    } else if (clickedItemMeta.displayName.equals(nextButtonDisplayName, true)) {
                        if ((itemIndex + 1) >= clientElement.size) return

                        pageIndex++
                        super.openGUI()
                    }
                }

                /* Close button */
                Material.BARRIER -> {
                    if (!persistentDataContainer.has(buttonIDKey, PersistentDataType.STRING)) return

                    if (clickedItemMeta.displayName != closeButtonDisplayName) return

                    player.closeInventory()
                }

                else -> {
                    return
                }
            }
        }

        /* Set event handler for nExperiment GUI */
        fun setEventHandler(e: InventoryClickEvent) {
            val player: Player = e.whoClicked as Player

            if (e.view.title != GUIHandler(NGUI.Handler.getNGUI(player)).getGUIName()) return

            val clickedInventory: Inventory = e.clickedInventory ?: return

            val inventoryHolder: InventoryHolder = clickedInventory.holder!!

            if (clickedInventory == player.inventory) e.isCancelled = true

            if (inventoryHolder !is NGUIConstructor) return

            e.isCancelled = true

            if (e.currentItem == null) return

            val nGUIConstructor: NGUIConstructor = inventoryHolder
            nGUIConstructor.setGUIClickHandler(e)
        }

    }

    private class EventController: Listener {
        @EventHandler
        private fun onInventoryClick(e: InventoryClickEvent) {
            GUIHandler(NGUI.Handler.getNGUI(e.whoClicked as Player)).setEventHandler(e)
        }

        @EventHandler
        private fun onInventoryClose(e: InventoryCloseEvent) {
            val inventoryName = e.view.title
            val player = e.player as Player

            if (inventoryName.equals(GUIHandler(NGUI.Handler.getNGUI(player)).getGUIName(), true)) {
                NGUI.Handler.nGUIContainer.remove(player)
            }
        }
    }
}