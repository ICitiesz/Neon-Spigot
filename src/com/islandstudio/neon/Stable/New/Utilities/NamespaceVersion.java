package com.islandstudio.neon.Stable.New.Utilities;

public class NamespaceVersion {

    private static Object getServer;

    static {
        try {
            getServer = getBukkitClass("Bukkit").getDeclaredMethod("getServer").invoke(getBukkitClass("Bukkit"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String version = getServer.getClass().getPackage().getName().split("\\.")[3];

    public static Class<?> getNameSpaceClass(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + version + "." + className);
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
}
