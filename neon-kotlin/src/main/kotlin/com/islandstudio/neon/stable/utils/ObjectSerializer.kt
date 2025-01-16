package com.islandstudio.neon.stable.utils

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
     *
     * @param objectToSerialize The object to be serialized.
     * @return The Base64 encoded string.
     */
    fun serializeObjectEncoded(objectToSerialize: Any): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)

        objectOutputStream.writeObject(objectToSerialize)
        objectOutputStream.flush()

        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
    }

    /**
     * Deserialize Based64 encoded string into object.
     *
     * @param serializeObject The Base64 encoded string.
     * @return The object.
     */
    fun deserializeObjectEncoded(serializeObject: String): Any {
        val byteArrayInputStream = ByteArrayInputStream(Base64.getDecoder().decode(serializeObject))
        val objetInputStream = ObjectInputStream(byteArrayInputStream)

        return objetInputStream.readObject()
    }

    fun serializedByteArrayEncoded(byteArray: ByteArray): String {
        return Base64.getEncoder().encodeToString(byteArray)
    }

    fun deserializedByteArrayEncoded(serializeByteArray: String): ByteArray {
        return Base64.getDecoder().decode(serializeByteArray)
    }
    fun serializedImageRaw(imageToSerialize: BufferedImage, imageFormat: String): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(imageToSerialize, imageFormat,byteArrayOutputStream)

        return byteArrayOutputStream.toByteArray()
    }

    fun deserializedImageRaw(serializedImage: ByteArray): BufferedImage {
        val byteArrayInputStream = ByteArrayInputStream(serializedImage)
        return ImageIO.read(byteArrayInputStream)
    }

    /**
     * Serialize bukkit object into Base64 encoded string.
     *
     * @param bukkitObjectToSerialize The object to be serialized.
     * @return The Base64 encoded string.
     */
    fun serializeBukkitObjectEncoded(bukkitObjectToSerialize: Any): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = BukkitObjectOutputStream(byteArrayOutputStream)

        objectOutputStream.writeObject(bukkitObjectToSerialize)
        objectOutputStream.flush()

        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
    }

    /**
     * Deserialize Based64 encoded string into bukkit object.
     *
     * @param serializeBukkitObject The Base64 encoded string.
     * @return The object.
     */
    fun deserializeBukkitObjectEncoded(serializeBukkitObject: String): Any {
        val byteArrayInputStream = ByteArrayInputStream(Base64.getDecoder().decode(serializeBukkitObject))
        val objetInputStream = BukkitObjectInputStream(byteArrayInputStream)

        return objetInputStream.readObject()
    }

    /**
     * Serialize bukkit object into Base64 encoded string.
     *
     * @param bukkitObjectToSerialize The object to be serialized.
     * @return The Base64 encoded string.
     */
    fun serializeBukkitObjectRaw(bukkitObjectToSerialize: Any): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = BukkitObjectOutputStream(byteArrayOutputStream)

        objectOutputStream.writeObject(bukkitObjectToSerialize)
        objectOutputStream.flush()

        return byteArrayOutputStream.toByteArray()
    }

    /**
     * Deserialize Based64 encoded string into bukkit object.
     *
     * @param serializeBukkitObject The Base64 encoded string.
     * @return The object.
     */
    fun deserializeBukkitObjectRaw(serializeBukkitObject: ByteArray): Any {
        val byteArrayInputStream = ByteArrayInputStream(serializeBukkitObject)
        val objetInputStream = ObjectInputStream(byteArrayInputStream)

        return objetInputStream.readObject()
    }

    fun serializeObjectRaw(objectToSerialize: Any): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)

        objectOutputStream.writeObject(objectToSerialize)
        objectOutputStream.flush()

        return byteArrayOutputStream.toByteArray()
    }

    fun deserializeObjectRaw(serializeObject: ByteArray): Any {
        val byteArrayInputStream = ByteArrayInputStream(serializeObject)
        val objetInputStream = ObjectInputStream(byteArrayInputStream)

        return objetInputStream.readObject()
    }
}