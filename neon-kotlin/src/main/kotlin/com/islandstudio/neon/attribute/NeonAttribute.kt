package com.islandstudio.neon.attribute

import com.islandstudio.neon.core.datakey.AbstractDataKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier

sealed class NeonAttribute(val baseAttribute: Attribute, attributeModifierkeyName: String): AbstractDataKey(attributeModifierkeyName) {
    protected abstract val value: Double
    protected abstract val attributeOperation: AttributeModifier.Operation

    fun buildDefault(): AttributeModifier {
        return AttributeModifier(super.dataKey.toString(), this.value, this.attributeOperation)
    }

    fun buildCustom(value: Double, attributeOperation: AttributeModifier.Operation): AttributeModifier {
        return AttributeModifier(super.dataKey.toString(), value, attributeOperation)
    }

    data object DisablePigMovement: NeonAttribute(Attribute.GENERIC_MOVEMENT_SPEED, "neon.attribute.disable_pig_movement") {
        override val value: Double = -1.0
        override val attributeOperation: AttributeModifier.Operation = AttributeModifier.Operation.MULTIPLY_SCALAR_1
    }
}