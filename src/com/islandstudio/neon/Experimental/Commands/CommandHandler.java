package com.islandstudio.neon.Experimental.Commands;

import com.islandstudio.neon.Stable.New.Utilities.NMS_Class_Version;
import com.islandstudio.neon.Stable.New.Command.CommandAlias;
import com.islandstudio.neon.Stable.New.Command.CommandCore;
import com.islandstudio.neon.MainCore;
import org.bukkit.Bukkit;

public final class CommandHandler {
    private static final CommandCore cmd = new CommandCore();

    private static Object getPluginCommand;
    private static Object getServer;
    private static Object setExecutor;
    private static Class<?> commandExecutor;

    static {
        try {
            Object plugin = NMS_Class_Version.getBukkitClass("plugin.java.JavaPlugin").getMethod("getPlugin", Class.class).invoke(null, MainCore.class);
            getServer = plugin.getClass().getMethod("getServer").invoke(plugin);
            commandExecutor = NMS_Class_Version.getBukkitClass("command.CommandExecutor");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initialize() throws Exception {
        for (CommandAlias cmds : CommandAlias.values()) {
            getPluginCommand = getServer.getClass().getMethod("getPluginCommand", String.class).invoke(getServer, cmds.getCommandAlias());
            setExecutor = getPluginCommand.getClass().getDeclaredMethod("setExecutor", commandExecutor).invoke(getPluginCommand, cmd);
        }

        CommandCore commandCore = new CommandCore();
        Bukkit.getServer().getPluginCommand("test").setExecutor(commandCore);
    }
    //private static final Commands cmd_beta = new Commands();

    /*public static void initializeBeta() {
        plugin.getServer().getPluginCommand(cmd_beta.cmd_1).setExecutor(cmd_beta);
        plugin.getServer().getPluginCommand(cmd_beta.cmd_2).setExecutor(cmd_beta);
        plugin.getServer().getPluginCommand(cmd_beta.cmd_3).setExecutor(cmd_beta);
        //plugin.getServer().getPluginCommand(cmd_beta.cmd_4).setExecutor(cmd_beta);
        plugin.getServer().getPluginCommand(cmd_beta.cmd_test).setExecutor(cmd_beta);
        plugin.getServer().getPluginCommand("ldl").setExecutor(cmd_beta);
    }*/
}
