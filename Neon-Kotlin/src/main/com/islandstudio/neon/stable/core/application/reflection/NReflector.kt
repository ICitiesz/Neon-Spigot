package com.islandstudio.neon.stable.core.application.reflection

import com.islandstudio.neon.stable.core.application.reflection.mapping.MappingType
import com.islandstudio.neon.stable.core.application.reflection.mapping.NMSMapping
import org.simpleyaml.configuration.file.YamlFile
import org.simpleyaml.utils.SupplierIO
import java.util.*

object NReflector {
    private val loadedNMSMapping: EnumMap<MappingType, NMSMapping.Mapping> = EnumMap(MappingType::class.java)

    object Handler {
        fun run() {
            loadNMSMapping()
        }

        private fun loadNMSMapping() {
            val yamlFile= YamlFile.loadConfiguration(SupplierIO.Reader { this.javaClass.classLoader
                .getResourceAsStream("resources/application/reflection/NMSMapping.yml")!!.reader() })

            yamlFile.getValues(false).forEach {
                val nmsMapping = NMSMapping.Mapping(it)

                loadedNMSMapping[nmsMapping.mappingType] = nmsMapping
            }
        }
    }

    /**
     * Get the Minecraft class
     *
     * @param className The class name. (String)
     * @return The Minecraft class. (Class)
     */
    fun getMcClass(className: String): Class<*>? {
        return runCatching {
            Class.forName("net.minecraft.$className")
        }.getOrElse {
            null
        }
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

    fun hasNamespaceClass(className: String): Boolean {
        return getMcClass(className) != null
    }

    /**
     * Get nms mapping.
     *
     * @param mappingType The MappingType constant.
     * @return NMSMapping object.
     */
    internal fun getMapping(mappingType: MappingType, mappingName: String): String {
        if (mappingType == MappingType.UNKOWNN) return ""

        return loadedNMSMapping[mappingType]!!.getRemappedValue(mappingName)
    }
}