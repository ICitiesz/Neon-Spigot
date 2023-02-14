package com.islandstudio.neon.stable.utils;

import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;

public class IReflector {
    private static final String version = IConstructor.getPlugin().getServer().getClass().getPackage().getName().split("\\.")[3];

    public static Class<?> getNameSpaceClass(String className) throws ClassNotFoundException {
        if (IConstructor.getVersion().equalsIgnoreCase("1.17")) {
            return Class.forName("net.minecraft." + className);
        } else {
            return Class.forName("net.minecraft.server." + version + "." + className);
        }
    }
}
