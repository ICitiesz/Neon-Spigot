package com.islandstudio.neon.Experimental;

import com.islandstudio.neon.Stable.New.Utilities.NamespaceVersion;
import com.islandstudio.neon.Stable.New.Utilities.ServerCfgHandler;
import com.islandstudio.neon.MainCore;
import org.bukkit.World;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class PVPHandler {
    private static Object getWorlds;

    static {
        try {
            Object plugin = NamespaceVersion.getBukkitClass("plugin.java.JavaPlugin").getMethod("getPlugin", Class.class).invoke(null, MainCore.class);
            Object getServer = plugin.getClass().getMethod("getServer").invoke(plugin);
            getWorlds = getServer.getClass().getMethod("getWorlds").invoke(getServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final List<World> worlds = (List<World>) getWorlds;

    public static void init() throws IOException, ParseException {
        for (World world : worlds) {
            if (Objects.requireNonNull(ServerCfgHandler.getValue()).get("PVP").equals(true)) {
                if (!world.getPVP()) {
                    world.setPVP(true);
                }
            } else if (ServerCfgHandler.getValue().get("PVP").equals(false)) {
                if (world.getPVP()) {
                    world.setPVP(false);
                }
            }
        }
    }

    public static void setPVP(boolean value) {
        for (World world : worlds) {
            world.setPVP(value);
        }
    }
}
