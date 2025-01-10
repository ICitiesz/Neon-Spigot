package com.islandstudio.neon.shared.utils.serialization

import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*
import javax.imageio.ImageIO

object ObjectSerializer {
    /**
     * Serialize object into Base64 encoded string.
     * @param T Object
     * @param obj The object to be serialized.
     * @return The Base64 encoded string.
     */
    fun <T> serializeToBase64(obj: T): String {
        return byteArrayToBase64(serializeToByteArray<T>(obj))
    }

    /**
     * Deserialize Based64 encoded string into object.
     *
     * @param T Object
     * @param base64EncodedObj The Base64 encoded string.
     * @return The object.
     */
    fun <T> deserialzeFromBase64(base64EncodedObj: String): T {
        return deserialzeFromByteArray(base64ToByteArray(base64EncodedObj))
    }

    fun <T> serializeToByteArray(obj: T): ByteArray {
        return ByteArrayOutputStream().use { byteArrayOutputStream ->
            val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)

            objectOutputStream.use {
                it.writeObject(obj)
                it.flush()
            }

            byteArrayOutputStream.toByteArray()
        }
    }

    fun <T> deserialzeFromByteArray(byteArray: ByteArray): T {
        return ByteArrayInputStream(byteArray).use { byteArrayInputStream ->
            val objetInputStream = ObjectInputStream(byteArrayInputStream)

            objetInputStream.use {
                it.readObject() as T
            }
        }
    }

    /**
     * Serialize bukkit object into Base64 encoded string.
     *
     * @param T Bukkit object
     * @param bukkitObj The bukkit object to be serialized.
     * @return The Base64 encoded string.
     */
    fun <T> serializeBukkitToBase64(bukkitObj: T): String {
        return byteArrayToBase64(serialzeBukkitToByteArray<T>(bukkitObj))
    }

    /**
     * Deserialize Based64 encoded string into bukkit object.
     *
     * @param T Bukkit object
     * @param base64EncodedBukkitObj The Base64 encoded string.
     * @return The bukkit object
     */
    fun <T> deserializeBukkitFromBase64(base64EncodedBukkitObj: String): T {
        return deserializeBukkitFromByteArray<T>(base64ToByteArray(base64EncodedBukkitObj))
    }

    fun <T> serialzeBukkitToByteArray(bukkitObj: T): ByteArray {
        return ByteArrayOutputStream().use { byteArrayOutputStream ->
            val objectOutputStream = BukkitObjectOutputStream(byteArrayOutputStream)

            objectOutputStream.use {
                it.writeObject(bukkitObj)
                it.flush()
            }

            byteArrayOutputStream.toByteArray()
        }
    }

    fun <T> deserializeBukkitFromByteArray(bukkitByteArray: ByteArray): T {
        return ByteArrayInputStream(bukkitByteArray).use { byteArrayInputStream ->
            val objetInputStream = BukkitObjectInputStream(byteArrayInputStream)

            objetInputStream.use {
                it.readObject() as T
            }
        }
    }

    fun serializeImageToByteArray(image: BufferedImage, imageFormat: String): ByteArray {
        return ByteArrayOutputStream().use {
            ImageIO.write(image, imageFormat, it)

            it.toByteArray()
        }
    }

    fun deserializeImageFromByteArray(byteArray: ByteArray): BufferedImage {
        return ByteArrayInputStream(byteArray).use {
            ImageIO.read(it)
        }
    }

    fun byteArrayToBase64(byteArray: ByteArray): String {
        return Base64.getEncoder().encodeToString(byteArray)
    }

    fun base64ToByteArray(base64EncodedByteArray: String): ByteArray {
        return Base64.getDecoder().decode(base64EncodedByteArray)
    }
}