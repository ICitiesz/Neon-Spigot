package com.islandstudio.neon.Stable.New.Utilities;

import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.New.Initialization.PluginConstructor;
import org.bukkit.plugin.Plugin;

public class NamespaceVersion {

    private static Object getServer;
    private static final Plugin plugin = MainCore.getPlugin(MainCore.class);

//    static {
//        try {
//            getServer = getBukkitClass("Bukkit").getDeclaredMethod("getServer").invoke(getBukkitClass("Bukkit"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private static final String version = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];

    public static Class<?> getNameSpaceClass(String className) throws ClassNotFoundException {
        if (PluginConstructor.getVersion().equalsIgnoreCase("1.17")) {
            return Class.forName("net.minecraft." + className);
        } else {
            return Class.forName("net.minecraft.server." + version + "." + className);
        }
    }

    public static Class<?> getBukkitClass(String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit." + className);
    }

    public static Class<?> getCraftBukkitClass(String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + version + "." + className );
    }

    public static Class<?> getMDClass(String className) throws ClassNotFoundException {
        return Class.forName("net.md_5.bungee." + className);
    }

    public static Class<?> getNameSpaceClassNew(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft." + className);
    }
}
