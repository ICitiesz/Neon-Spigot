package com.islandstudio.neon.Experimental.Commands;

import com.islandstudio.neon.Stable.New.Command.CommandAlias;
import com.islandstudio.neon.Stable.New.Command.CommandCore;
import com.islandstudio.neon.MainCore;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public final class CommandHandler {
    private static final CommandCore cmd = new CommandCore();
    private static final Plugin plugin = MainCore.getPlugin(MainCore.class);

    public static void init() {
        for (CommandAlias cmds : CommandAlias.values()) {
            Objects.requireNonNull(plugin.getServer().getPluginCommand(cmds.getCommandAlias())).setExecutor(cmd);
        }
        Objects.requireNonNull(plugin.getServer().getPluginCommand("test")).setExecutor(cmd);
    }
}
