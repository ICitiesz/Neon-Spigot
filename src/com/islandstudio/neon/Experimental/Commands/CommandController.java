package com.islandstudio.neon.Experimental.Commands;

import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.New.Command.CommandAlias;
import com.islandstudio.neon.Stable.New.Command.SyntaxHandler;
import com.islandstudio.neon.Stable.New.GUI.Interfaces.iWaypoints.IWaypoints;
import com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem.RankHandler;
import com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem.ServerRanks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CommandController implements Listener, TabExecutor {
    /* Class 1: Main Class for Command
     *  Enum: Store commands
     *
     *
     */
    private static final String commandPrefix = "neon";

    public static void init() {
        Objects.requireNonNull(((Plugin) MainCore.getPlugin(MainCore.class)).getServer().getPluginCommand(commandPrefix)).setExecutor(new CommandController());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase(commandPrefix)) {
                Player player = (Player) sender;

                if (args.length > 0) {
                    Arrays.stream(CMDAlias.values()).forEach(cmdAlias -> {
                        if (args[0].equalsIgnoreCase(cmdAlias.getCommandAlias())) {
                            switch (cmdAlias) {
                                case RANK: {
                                    switch (args.length) {
                                        case 3: {
                                            String playerName = args[2];
                                            Collection<Player> players = (Collection<Player>) Bukkit.getServer().getOnlinePlayers();

                                            if (args[1].equalsIgnoreCase("remove")) {
                                                if (players.parallelStream().anyMatch(onlinePlayer -> playerName.equals(onlinePlayer.getName()))) {
                                                    players.parallelStream().forEach(onlinePlayer -> {
                                                        if (playerName.equals(onlinePlayer.getName())) {
                                                            try {
                                                                RankHandler.removeRank(player, onlinePlayer);
                                                            } catch (IOException | ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    player.sendMessage(ChatColor.RED + "Can't find player '" + ChatColor.WHITE + playerName + ChatColor.RED + "'!");
                                                }
                                            } else {
                                                player.sendMessage("Invalid or missing argument!");
                                            }
                                            break;
                                        }

                                        case 4: {
                                            String playerName = args[2];
                                            String rankName = args[3];
                                            Collection<Player> players = (Collection<Player>) Bukkit.getServer().getOnlinePlayers();

                                            if (args[1].equalsIgnoreCase("set")) {
                                                if (players.parallelStream().anyMatch(onlinePlayer -> playerName.equals(onlinePlayer.getName()))) {
                                                    if (Arrays.stream(ServerRanks.values()).anyMatch(rank -> rankName.equalsIgnoreCase(rank.name()))) {
                                                        players.parallelStream().forEach(onlinePlayer -> {
                                                            if (playerName.equals(onlinePlayer.getName())) {
                                                                try {
                                                                    RankHandler.setRank(player, onlinePlayer, rankName, playerName);
                                                                } catch (IOException | ParseException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        player.sendMessage(ChatColor.RED + "No such rank as '" + ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + "'!");
                                                    }
                                                } else {
                                                    player.sendMessage(ChatColor.RED + "Can't find player '" + ChatColor.WHITE + playerName + ChatColor.RED + "'!");
                                                }
                                            }
                                            break;
                                        }

                                        default: {
                                            player.sendMessage("Invalid or missing argument!");
                                        }
                                    }
                                    break;
                                }

                                case WAYPOINTS: {
                                    IWaypoints.setCommandHandler(args, player);
                                    break;
                                }
                            }
                        }
                    });

                    if (Arrays.stream(CMDAlias.values()).noneMatch(cmdAlias -> cmdAlias.getCommandAlias().equalsIgnoreCase(args[0]))) {
                        player.sendMessage("Sorry, there are no such command as '" + args[0] + "'!" );
                    }
                } else {
                    player.sendMessage("Please type '/neon help <pageNumber>' to show all the available commands!" );
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase(commandPrefix)) {
                if (args.length == 1) {
                    return Arrays.stream(CMDAlias.values()).sorted().map(CMDAlias::getCommandAlias).collect(Collectors.toList());
                }

                if (args.length > 1) {
                    for (CMDAlias cmdAlias : CMDAlias.values()) {
                        if (args[0].equalsIgnoreCase(cmdAlias.getCommandAlias())) {
                            switch (cmdAlias) {
                                case WAYPOINTS: {
                                    switch (args.length) {
                                        case 2: {
                                            List<String> waypointOptions = new ArrayList<>();
                                            waypointOptions.add("add");
                                            waypointOptions.add("remove");
                                            return waypointOptions;
                                        }

                                        case 3: {
                                            if (args[1].equalsIgnoreCase("remove")) {
                                                try {
                                                    IWaypoints.waypointData.put(((Player) sender).getUniqueId().toString(), IWaypoints.getWaypointData());
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                return IWaypoints.getWaypointNames((Player) sender);
                                            }
                                        }
                                    }
                                }

                                case RANK: {
                                    if (player.isOp()) {
                                        switch (args.length) {
                                            case 2: {
                                                List<String> rankOptions = new ArrayList<>();
                                                rankOptions.add("set");
                                                rankOptions.add("remove");
                                                return rankOptions;
                                            }

                                            case 3: {
                                                return Bukkit.getServer().getOnlinePlayers().parallelStream().map(Player::getName).collect(Collectors.toList());
                                            }

                                            case 4: {
                                                if (args[1].equalsIgnoreCase("set")) {
                                                    return Arrays.stream(ServerRanks.values()).map(ServerRanks::name).collect(Collectors.toList());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }
}
