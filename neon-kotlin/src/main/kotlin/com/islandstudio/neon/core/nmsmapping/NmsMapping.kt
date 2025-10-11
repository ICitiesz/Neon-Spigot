package com.islandstudio.neon.core.nmsmapping

import com.islandstudio.neon.core.nmsmapping.type.*
import java.util.*


abstract class NmsMapping {
    protected val nmsFields: EnumMap<NmsField, String> = EnumMap(NmsField::class.java)
    protected val nmsMethods: EnumMap<NmsMethod, String> = EnumMap(NmsMethod::class.java)
    protected val nmsConstructors: EnumMap<NmsConstructor, String> = EnumMap(NmsConstructor::class.java)
    protected val nmsClasses: EnumMap<NmsClass, String> = EnumMap(NmsClass::class.java)

    protected fun registerMapping(nmsData: Map<String, String>) {
        val nmsObjectType = NmsObjectType.entries.find { x -> x.name == nmsData["Nms Object Type"] } ?: return
        val objectName = nmsData["Object Name"] ?: return
        val remappedName = nmsData["Remapped Name"] ?: return

        when(nmsObjectType) {
            NmsObjectType.NmsField -> {
                NmsField.entries.find { x -> x.name == objectName }?.let {
                    nmsFields[it] = remappedName
                }
            }

            NmsObjectType.NmsMethod -> {
                NmsMethod.entries.find { x -> x.name == objectName }?.let {
                    nmsMethods[it] = remappedName
                }
            }

            NmsObjectType.NmsConstructor -> {
                NmsConstructor.entries.find { x -> x.name == objectName }?.let {
                    nmsConstructors[it] = remappedName
                }
            }

            NmsObjectType.NmsClass -> {
                NmsClass.entries.find { x -> x.name == objectName }?.let {
                    nmsClasses[it] = remappedName
                }
            }
        }
    }

    protected fun getNmsField(nmsField: NmsField): String = nmsFields[nmsField] ?: ""

    protected fun getNmsMethod(nmsMethod: NmsMethod): String = nmsMethods[nmsMethod] ?: ""

    protected fun getNmsConstructor(nmsConstructor: NmsConstructor): String = nmsConstructors[nmsConstructor] ?: ""

    protected fun getNmsClass(nmsClass: NmsClass): String = nmsClasses[nmsClass] ?: ""
}