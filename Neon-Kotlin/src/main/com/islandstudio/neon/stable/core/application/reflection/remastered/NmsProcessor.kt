package com.islandstudio.neon.stable.core.application.reflection.remastered

import com.islandstudio.neon.stable.core.application.init.AppInitializer
import com.islandstudio.neon.stable.core.io.resource.NeonResources
import com.islandstudio.neon.stable.core.io.resource.ResourceManager
import org.dhatim.fastexcel.reader.ReadableWorkbook
import org.dhatim.fastexcel.reader.Sheet

class NmsProcessor {
    companion object {
        private val nmsMapping: ArrayList<NmsObject> = arrayListOf()

        /**
         * Init NmsProcessor
         */
        fun run() {
            val nmsMappingFileStream = ResourceManager().getNeonResourceAsStream(NeonResources.NEON_NMS_MAPPING)
            val nmsMappingAsExcel = ReadableWorkbook(nmsMappingFileStream)

            nmsMappingFileStream.use {
                nmsMappingAsExcel.use { excel ->
                    excel.sheets.forEach { sheet ->
                        prepareNmsMapping(sheet)
                    }
                }
            }
        }

        fun getNmsMapping(): ArrayList<NmsObject> = nmsMapping

        /**
         * Get NMS object by mapping type & object name.
         */
        internal fun getNmsObject(mappingType: MappingType, objectName: String): NmsObject? {
            return getNmsMapping()
                .filter { nmsObj -> nmsObj.mappingType == mappingType }
                .find { nmsObj -> nmsObj.objectName == objectName }
        }

        /**
         * Prepare and laod NMS mapping.
         *
         */
        private fun prepareNmsMapping(excelSheet: Sheet) {
            excelSheet.openStream().use { rows ->
                val filteredMapping = rows.filter { row -> row.rowNum != 1 }
                    .filter { row ->
                        val versionsValue = row.getCell(2).text.split(",").map { v -> v.trim() }

                        AppInitializer.serverVersion in versionsValue
                    }
                    .map<NmsObject> { row ->
                        val objectName = row.getCell(0).text
                        val remappedName = row.getCell(1).text
                        val versions = row.getCell(2).text.split(",").map { v -> v.trim() }

                        NmsObject(
                            MappingType.valueOfMappingType(excelSheet.name),
                            objectName,
                            remappedName,
                            versions.first { v -> v == AppInitializer.serverVersion }
                        )
                    }.toList()

                nmsMapping.addAll(filteredMapping)
            }
        }
    }

    /**
     * Get the Minecraft class
     *
     * @param className The class name. (String)
     * @return The Minecraft class. (Class)
     */
    // TODO: Error handling needed
    fun getMcClass(className: String): Class<*>? {
        return runCatching {
            Class.forName("net.minecraft.$className")
        }.getOrElse {
            null
        }
    }

    fun hasMcClass(className: String): Boolean {
        return getMcClass(className) != null
    }

    /**
     * Get the Neon class
     *
     * @param className The class name. (String)
     * @return The Neon class. (Class)
     */
    fun getNeonClass(className: String): Class<*> {
        return Class.forName("com.islandstudio.$className")
    }


}