package com.islandstudio.neon.stable.utils.reflection

import com.islandstudio.neon.stable.primary.nConstructor.NConstructor
import org.simpleyaml.configuration.MemorySection
import org.simpleyaml.configuration.file.YamlFile
import org.simpleyaml.utils.SupplierIO
import java.util.*

object NReflector {
    private val loadedNMSMapping: EnumMap<NMSRemapped.MappingType, NMSMapping> = EnumMap(NMSRemapped.MappingType::class.java)
    object Handler {
        fun run() {
            loadNMSMapping()
        }

        private fun loadNMSMapping() {
            val yamlFile= YamlFile.loadConfiguration(SupplierIO.Reader { this.javaClass.classLoader
                .getResourceAsStream("resources/NMSMapping.yml")!!.reader() })

            yamlFile.getValues(false).forEach {
                val nmsMapping = NMSMapping(it)

                loadedNMSMapping[nmsMapping.mappingType] = nmsMapping

            }
        }
    }

    /**
     * Get nms mapping.
     *
     * @param mappingType The MappingType constant.
     * @return NMSMapping object.
     */
    fun getMapping(mappingType: NMSRemapped.MappingType, remappedName: String): String {
        if (mappingType == NMSRemapped.MappingType.UNKOWNN) return ""

        return loadedNMSMapping[mappingType]!!.getRemapped(remappedName)
    }

    /**
     * Get the Minecraft namespace class
     *
     * @param className The class name. (String)
     * @return The Minecraft namespace class. (Class)
     */
    fun getNamespaceClass(className: String): Class<*> {
        return Class.forName("net.minecraft.$className")
    }

    fun getCraftBukkitClass(className: String): Class<*> {
        val craftBukkitVersion = NConstructor.plugin.server.javaClass.name.split(".")[3]

        return Class.forName("org.bukkit.craftbukkit.${craftBukkitVersion}.$className")
    }

    /**
     * Get the Neon class
     *
     * @param className The class name. (String)
     * @return The Neon class. (Class)
     */
    fun getNClassName(className: String): Class<*> {
        return Class.forName("com.islandstudio.$className")
    }

    data class NMSMapping(private val nmsMappingsData: Map.Entry<String, Any>) {
        val mappingType: NMSRemapped.MappingType = when (nmsMappingsData.key) {
            "fields" -> { NMSRemapped.MappingType.FIELD }
            "methods" -> { NMSRemapped.MappingType.METHOD }
            else -> { NMSRemapped.MappingType.UNKOWNN }
        }

        private val mappingDetails: HashMap<String, String> = HashMap()

        init {
            val version: String = NConstructor.getRawVersion()

            (nmsMappingsData.value as MemorySection).getValues(false).entries.forEach {
                (it.value as MemorySection).getValues(false).forEach InnerFE@ { remappedObj ->
                    @Suppress("UNCHECKED_CAST")
                    if (version !in (remappedObj.value as MemorySection).get("version") as ArrayList<String>) return@InnerFE

                    mappingDetails[it.key] = remappedObj.key
                }
            }
        }

        fun getRemapped(remappedName: String): String = mappingDetails[remappedName]!!
    }
}