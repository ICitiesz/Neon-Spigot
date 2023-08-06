package com.islandstudio.neon.stable.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

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