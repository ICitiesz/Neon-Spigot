package com.islandstudio.neon.attribute

import com.islandstudio.neon.core.nmsmapping.NmsManager
import com.islandstudio.neon.server.ServerGamePacketManager
import com.islandstudio.neon.shared.utils.data.DataUtil
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket
import net.minecraft.world.entity.ai.attributes.Attribute
import org.bukkit.attribute.Attributable
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.function.Consumer

object AttributeManager {
    fun getAttributeOnDirty(attributeInstance: AttributeInstance) {

    }

    fun <T> applyClientEntityAttributeModifier(player: Player, target: T, neonAttribute: NeonAttribute, overwriteIfExist: Boolean = false) where T : Attributable, T : Entity {
        target.getAttribute(neonAttribute.baseAttribute)?.let { targetAttributeInstance ->
            val entityId = target.entityId

            val nmsTargetAttributeInstance = targetAttributeInstance.javaClass.getDeclaredField("handle").run {
                this.isAccessible = true

                DataUtil.asType<net.minecraft.world.entity.ai.attributes.AttributeInstance>(this.get(targetAttributeInstance))
            }

            // TODO: Need add to Nms Mapping
            val nmsOnTriggerAttributeUpdate = nmsTargetAttributeInstance.javaClass.getDeclaredField("h").run {
                this.isAccessible = true

                DataUtil.asType<Consumer<net.minecraft.world.entity.ai.attributes.AttributeInstance>>(this.get(nmsTargetAttributeInstance))
            }

            /* Attribute instance creation */
            val newAttributeInstance = net.minecraft.world.entity.ai.attributes.AttributeInstance::class.java.getConstructor(
                Attribute::class.java, Consumer::class.java)
                .newInstance(NmsManager.toNmsAttribute(neonAttribute.baseAttribute), nmsOnTriggerAttributeUpdate).apply {
                    val attributeModifier = if (!targetAttributeInstance.modifiers.any { x -> x.name == neonAttribute.dataKey.toString() }) {
                        neonAttribute.buildDefault()
                    } else return@apply

                    this.javaClass.getMethod("a", net.minecraft.world.entity.ai.attributes.AttributeInstance::class.java)
                        .invoke(this, nmsTargetAttributeInstance) // replaceFrom()
                    
                    this.javaClass.getMethod("b", net.minecraft.world.entity.ai.attributes.AttributeModifier::class.java)
                        .invoke(this, NmsManager.toNmsAttributeModifier(attributeModifier)) // addTransientModifier()
                }

            val updateAttributePacket = ClientboundUpdateAttributesPacket(
                entityId,
                mutableListOf(newAttributeInstance)
            )

            ServerGamePacketManager.sendServerGamePacket(player, updateAttributePacket)
        }
    }

    fun <T> removeClientEntityAttributeModifier(player: Player, target: T, neonAttribute: NeonAttribute) where T: Attributable, T: Entity {
        target.getAttribute(neonAttribute.baseAttribute)?.let { targetAttributeInstance ->
            val entityId = target.entityId

            val nmsTargetAttributeInstance = targetAttributeInstance.javaClass.getDeclaredField("handle").run {
                this.isAccessible = true

                DataUtil.asType<net.minecraft.world.entity.ai.attributes.AttributeInstance>(this.get(targetAttributeInstance))
            }

            val updateAttributePacket = ClientboundUpdateAttributesPacket(
                entityId,
                mutableListOf(nmsTargetAttributeInstance)
            )

            ServerGamePacketManager.sendServerGamePacket(player, updateAttributePacket)
        }
    }

    fun attachAttributeModifier(target: Attributable, neonAttribute: NeonAttribute, isPermanant: Boolean = false, overwriteIfExist: Boolean = false, custom: NeonAttribute.() -> AttributeModifier? = { null }) {
        target.getAttribute(neonAttribute.baseAttribute)?.let { attributeInstance ->
            if (hasAttributeModifier(target, neonAttribute)) {
                if (!overwriteIfExist) return

                removeAttributeModifier(target, neonAttribute)
            }

            val addTempAttributeModifier: (AttributeModifier) -> Unit = {
                val nmsAttributeModifier = DataUtil.asType<net.minecraft.world.entity.ai.attributes.AttributeModifier>(attributeInstance.javaClass.getMethod("convert", AttributeModifier::class.java)
                    .invoke(null, it))

                val nmsAttributeInstance = attributeInstance.javaClass.getDeclaredField("handle").run {
                    this.isAccessible = true

                    DataUtil.asType<net.minecraft.world.entity.ai.attributes.AttributeInstance>(this.get(attributeInstance))
                }

                // TODO: Need add to Nms Mapping (Add temp attribute modifier)
                nmsAttributeInstance.javaClass.getMethod("b", net.minecraft.world.entity.ai.attributes.AttributeModifier::class.java)
                    .invoke(nmsAttributeInstance, nmsAttributeModifier)
            }

            custom(neonAttribute)?.apply custom@ {
                return@let if (isPermanant) attributeInstance.addModifier(this) else addTempAttributeModifier(this)
            }

            if (isPermanant) {
                attributeInstance.addModifier(neonAttribute.buildDefault())
            } else {
                addTempAttributeModifier(neonAttribute.buildDefault())
            }
        }
    }

    fun hasAttributeModifier(target: Attributable, neonAttribute: NeonAttribute): Boolean {
        return target.getAttribute(neonAttribute.baseAttribute)?.let { attributeInstance ->
            attributeInstance.modifiers.any { x -> x.name == neonAttribute.dataKey.toString() }
        } ?: false
    }

    fun removeAttributeModifier(target: Attributable, neonAttribute: NeonAttribute) {
        target.getAttribute(neonAttribute.baseAttribute)?.let { attributeInstance ->
            attributeInstance.modifiers.find { x -> x.name == neonAttribute.dataKey.toString() }?.let {
                attributeInstance.removeModifier(it)
            }
        }
    }
}