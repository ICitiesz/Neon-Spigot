package com.islandstudio.neon.stable.utils

import com.islandstudio.neon.stable.primary.nConstructor.NConstructor

object NReflector {
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
}