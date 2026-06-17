package com.islandstudio.neon.shared.rework.core.initialization

import com.islandstudio.neon.shared.rework.core.initialization.context.IPluginContext
import java.io.File
import java.net.URLClassLoader
import java.security.CodeSource
import java.util.jar.JarFile

class NeonClassLoader(
    private val libraryFiles: Array<File>,
    parent: ClassLoader,
    private val bridgeClassPaths: Array<String>
): URLClassLoader(libraryFiles.map { it.toURI().toURL() }.toTypedArray(), parent) {
    private var jarLibraries: Array<JarFile>? = null

    companion object {
        fun buildClassLoader(pluginContext: IPluginContext, libraryUrls: Array<File>, pluginClassLoader: ClassLoader, bridgeClassPaths: Array<String>): NeonClassLoader {
            pluginContext.getPluginLogger().info("Building class loader......")
            return NeonClassLoader(libraryUrls, pluginClassLoader, bridgeClassPaths)
        }
    }

    private val baseClasses = arrayOf("java.", "javax.", "kotlin.", "org.bukkit.", "net.minecraft.")

    init {
        bridgeClassPaths.plus(arrayOf(
            /*"com.islandstudio.neon.shared.core.initialization.IPluginInitializer",*/
            /*"com.islandstudio.neon.shared.core.initialization.IPluginContext", */
            /*"com.islandstudio.neon.shared.core.initialization.PluginContext",*/
        ))
    }

    public override fun loadClass(name: String, resolve: Boolean): Class<*> {
        synchronized(getClassLoadingLock(name)) {
            findLoadedClass(name)?.let { return it }

            /* Check if the class is base class or exclusive neon class, then let PluginClassLoader handle the class loading */
            if (baseClasses.any { name.startsWith(it) } || this@NeonClassLoader.bridgeClassPaths.any { name == it }) {
                return parent.loadClass(name)
            }

            return try {
                findClass(name)?.let {
                    if (resolve) resolveClass(it)

                    it
                } ?: super.loadClass(name, resolve)
            } catch (e: ClassNotFoundException) {
                super.loadClass(name, resolve)
            }
        }
    }

    override fun findClass(name: String): Class<*>? {
        if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) return null

        val jarFiles = jarLibraries ?: libraryFiles.map { JarFile(it) }.toTypedArray()

        var definedClass: Class<*>? = null

        jarFiles.forEach { jarFile ->
            val entry = jarFile.getJarEntry(name.replace(".", "/") + ".class") ?: return@forEach

            val inputStream = jarFile.getInputStream(entry)
            val bytes = inputStream.readBytes()

            val signers = entry.codeSigners
            val libraryFileURL = libraryFiles.first { libraryFile -> libraryFile.name == jarFile.name.substringAfterLast("\\") }.toURI().toURL()
            val codeSource = CodeSource(libraryFileURL, signers)

            definedClass = defineClass(name, bytes, 0, bytes.size, codeSource)
        }

        return definedClass
    }

    override fun close() {
        try {
            jarLibraries?.let { jarLibs ->
                jarLibs.forEach { try { it.close() } catch (_: Exception) {} }
            }
        } finally {
            try { super.close() } catch (_: Exception) {}
        }
    }
}