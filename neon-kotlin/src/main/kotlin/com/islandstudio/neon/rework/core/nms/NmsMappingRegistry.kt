package com.islandstudio.neon.rework.core.nms

import com.islandstudio.neon.core.nmsmapping.type.*
import java.util.*

class NmsMappingRegistry {
    private val registeredNmsFields = EnumMap<NmsField, String>(NmsField::class.java)
    private val registeredNmsMethods = EnumMap<NmsMethod, String>(NmsMethod::class.java)
    private val registeredNmsConstructors = EnumMap<NmsConstructor, String>(NmsConstructor::class.java)
    private val registeredNmsClasses = EnumMap<NmsClass, String>(NmsClass::class.java)

    internal fun registerMapping(nmsData: Map<String, String>) {
        val nmsObjectType = NmsObjectType.entries.find { x -> x.name == nmsData["Nms Object Type"] } ?: return

        val objectName = nmsData["Object Name"] ?: return
        val remappedName = nmsData["Remapped Name"] ?: return

        when(nmsObjectType) {
            NmsObjectType.NmsField -> {
                NmsField.entries.find { x -> x.name == objectName }?.let {
                    registeredNmsFields[it] = remappedName
                }
            }

            NmsObjectType.NmsMethod -> {
                NmsMethod.entries.find { x -> x.name == objectName }?.let {
                    registeredNmsMethods[it] = remappedName
                }
            }

            NmsObjectType.NmsConstructor -> {
                NmsConstructor.entries.find { x -> x.name == objectName }?.let {
                    registeredNmsConstructors[it] = remappedName
                }
            }

            NmsObjectType.NmsClass -> {
                NmsClass.entries.find { x -> x.name == objectName }?.let {
                    registeredNmsClasses[it] = remappedName
                }
            }
        }
    }

    fun getNmsField(nmsField: NmsField): String = registeredNmsFields[nmsField] ?: ""

    fun getNmsMethod(nmsMethod: NmsMethod): String = registeredNmsMethods[nmsMethod] ?: ""

    fun getNmsConstructor(nmsConstructor: NmsConstructor): String = registeredNmsConstructors[nmsConstructor] ?: ""

    fun getNmsClass(nmsClass: NmsClass): String = registeredNmsClasses[nmsClass] ?: ""
}