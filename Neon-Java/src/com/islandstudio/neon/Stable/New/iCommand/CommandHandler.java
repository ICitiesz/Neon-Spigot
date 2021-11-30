package com.islandstudio.neon.Stable.New.iCommand;

import com.islandstudio.neon.Experimental.GameModeHandler;
import com.islandstudio.neon.Experimental.GameModes;
import com.islandstudio.neon.Stable.New.Utilities.iExperimental.IExperimental;
import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.New.features.EffectsManager.EffectsManager;
import com.islandstudio.neon.Stable.New.features.iWaypoints.IWaypoints;
import com.islandstudio.neon.Stable.New.features.iRank.IRank;
import com.islandstudio.neon.Stable.New.features.iRank.ServerRanks;
import com.islandstudio.neon.Stable.New.Utilities.ProfileHandler;
import com.islandstudio.neon.Stable.New.Utilities.ServerCFGHandler;
import org.bukkit.*;
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

public class CommandHandler implements Listener, TabExecutor {
    private static final String commandPrefix = "neon";
    private static final String pluginName = ChatColor.WHITE + "[" + ChatColor.AQUA + "Neon" + ChatColor.WHITE + "] ";

    public static boolean moderation = false;

    /* Initialization for the commands. */
    public static void init() {
        Objects.requireNonNull(((Plugin) MainCore.getPlugin(MainCore.class)).getServer().getPluginCommand(commandPrefix)).setExecutor(new CommandHandler());
    }

    public static String getPluginName() {
        return pluginName;
    }

    /* Command execution start here. */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getServer().getConsoleSender().sendMessage( pluginName + ChatColor.RED + "The command doesn't support console execution!");
            return true;
        }

        Player commander = (Player) sender;

        if (!cmd.getName().equalsIgnoreCase(commandPrefix)) return true;

        if (!(args.length > 0)) {
            commander.sendMessage(pluginName + ChatColor.YELLOW + "Please type '/neon help <pageNumber>' to show all the available commands!" );
            return true;
        }

        Arrays.stream(Commands.values()).forEach(cmdAlias -> {
            if (!args[0].equalsIgnoreCase(cmdAlias.getCommandAlias())) return;

            switch (cmdAlias) {
                case RANK: {
                    IRank.setCommandHandler(commander, args, pluginName);
                    break;
                }

                case WAYPOINTS: {
                    IWaypoints.setCommandHandler(args, commander);
                    break;
                }

                case EXPERIMENTAL: {
                    if (!commander.isOp()) {
                        SyntaxHandler.sendSyntax(commander, 2);
                        return;
                    }

                    try {
                        IExperimental.open(commander);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }

                    break;
                }

                case GM: {
                    if (!commander.isOp()) {
                        SyntaxHandler.sendSyntax(commander, 2);
                        return;
                    }

                    if (args.length != 2) {
                        SyntaxHandler.sendSyntax(commander, 1);
                        return;
                    }

                    String value = args[1];

                    if (value.equalsIgnoreCase(String.valueOf(GameModes.SURVIVAL_MODE.getGameModeValue()))) {
                        GameModeHandler.setSurvival(commander);
                    } else if (value.equalsIgnoreCase(String.valueOf(GameModes.CREATIVE_MODE.getGameModeValue()))) {
                        GameModeHandler.setCreative(commander);
                    } else if (value.equalsIgnoreCase(String.valueOf(GameModes.ADVENTURE_MODE.getGameModeValue()))) {
                        GameModeHandler.setAdventure(commander);
                    } else if (value.equalsIgnoreCase(String.valueOf(GameModes.SPECTATOR_MODE.getGameModeValue()))) {
                        GameModeHandler.setSpectator(commander);
                    } else {
                        SyntaxHandler.sendSyntax(commander, 1);
                    }
                    break;
                }

                case REGEN: {
                    try {
                        String playerRank2 = (String) ProfileHandler.getValue(commander).get("Rank");

                        if (!commander.isOp() || !playerRank2.equalsIgnoreCase(ServerRanks.OWNER.toString())) {
                            SyntaxHandler.sendSyntax(commander, 2);
                            return;
                        }

                        if (commander.getFoodLevel() <20 && commander.getHealth() < 20) {
                            commander.setFoodLevel(20);
                            commander.setSaturation(20);
                            commander.setHealth(20);
                            commander.sendMessage(ChatColor.GREEN + "Your health and hunger have been filled!");
                        } else if (commander.getFoodLevel() <20) {
                            commander.setFoodLevel(20);
                            commander.setSaturation(20);
                            commander.sendMessage(ChatColor.GREEN + "Your hunger has been filled!");
                        } else if (commander.getHealth() < 0) {
                            commander.setHealth(20);
                            commander.sendMessage(ChatColor.GREEN + "Your health has been filled!");
                        } else {
                            commander.sendMessage(ChatColor.YELLOW + "Regen only available when your health level or food level is below 20!!");
                        }

                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case SERVERCONFIG: {
                    if (!commander.isOp()) {
                        SyntaxHandler.sendSyntax(commander, 2);
                        return;
                    }

                    switch (args.length) {
                        case 2: {
                            switch (args[1]) {
                                case "PVP":

                                case "TNT_Protection":

                                case "iCutter":

                                case "iSmelter":

                                case "iHarvest":

                                case "ChatLogging": {
                                    commander.sendMessage(ChatColor.YELLOW + "Please provide a value!");
                                    break;
                                }

                                default: {
                                    SyntaxHandler.sendSyntax(commander, 1);
                                    break;
                                }
                            }
                            break;
                        }

                        case 3: {
                            try {
                                ServerCFGHandler.setValue(args[1], args[2], commander);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;
                        }

                        default: {
                            commander.sendMessage(ChatColor.YELLOW + "Please select a configuration to modify!");
                            break;
                        }
                    }

                    break;
                }

                case DEBUG: {
                    commander.sendMessage(ChatColor.YELLOW + "Nothing to see here...");
                    break;
                }

                case MOD: {
                    if (!commander.isOp()) {
                        SyntaxHandler.sendSyntax(commander, 2);
                        return;
                    }

                    if (args.length != 2) {
                        SyntaxHandler.sendSyntax(commander, 1);
                        return;
                    }

                    String option = args[1];

                    if (option.equalsIgnoreCase("on")) {
                        if (!commander.isInvulnerable()) {
                            commander.setInvulnerable(true);
                            commander.setAllowFlight(true);
                            if (!commander.isCollidable()) {
                                commander.setCollidable(false);
                            }
                            if (!moderation) {
                                moderation = true;
                            }
                            commander.sendMessage(ChatColor.GREEN + "Moderation enabled!");
                        } else {
                            commander.sendMessage(ChatColor.YELLOW + "Moderation already enabled!");
                        }
                    } else if (option.equalsIgnoreCase("off")) {
                        if (commander.isInvulnerable()) {
                            commander.setInvulnerable(false);
                            commander.setAllowFlight(false);
                            if (commander.isCollidable()) {
                                commander.setCollidable(true);
                            }
                            if (moderation) {
                                moderation = false;
                            }
                            commander.sendMessage(ChatColor.RED + "Moderation disabled!");
                        } else {
                            commander.sendMessage(ChatColor.YELLOW + "Moderation already enabled!");
                        }
                    } else {
                        SyntaxHandler.sendSyntax(commander, 1);
                    }
                    break;
                }

                case EFS: {
                    try {
                        String playerRank = (String) ProfileHandler.getValue(commander).get("Rank");
                        EffectsManager effectsManager = new EffectsManager();

                        if (commander.isOp() || ServerRanks.OWNER.toString().equalsIgnoreCase(playerRank) || ServerRanks.VIP_PLUS.toString().equalsIgnoreCase(playerRank)) {
                            if (commander.isSleeping()) {
                                commander.sendMessage(ChatColor.YELLOW + "You can't use Effects Manager while you're sleeping!");
                                return;
                            }

                            effectsManager.openEffectManager(commander);
                        } else {
                            commander.sendMessage(ChatColor.RED + "You need a higher rank to do that!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }
        });

        if (Arrays.stream(Commands.values()).noneMatch(cmdAlias -> cmdAlias.getCommandAlias().equalsIgnoreCase(args[0]))) {
            commander.sendMessage(pluginName + ChatColor.YELLOW + "Sorry, there are no such command as " + ChatColor.WHITE
                    + "'" + ChatColor.GRAY + args[0] + ChatColor.WHITE + "'" + ChatColor.YELLOW + "!");
        }

        return true;
    }

    /* Show the hints of the command for tab completion. */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "The command doesn't support console execution!");
            return null;
        }

        if (!cmd.getName().equalsIgnoreCase(commandPrefix)) return null;

        Player player = (Player) sender;

        if (args.length == 1) return Arrays.stream(Commands.values()).sorted().map(Commands::getCommandAlias).collect(Collectors.toList());

        if (!(args.length > 1)) return null;

        for (Commands cmds : Commands.values()) {
            if (args[0].equalsIgnoreCase(cmds.getCommandAlias())) {
                switch (cmds) {
                    case WAYPOINTS: {
                        switch (args.length) {
                            case 2: {
                                List<String> waypointOptions = new ArrayList<>();
                                waypointOptions.add("add");
                                waypointOptions.add("remove");
                                return waypointOptions;
                            }

                            case 3: {
                                if (!args[1].equalsIgnoreCase("remove")) return null;

                                try {
                                    IWaypoints.waypointData.put(((Player) sender).getUniqueId().toString(), IWaypoints.getWaypointData());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return IWaypoints.getWaypointNames((Player) sender);
                            }
                        }
                    }

                    case RANK: {
                        if (!player.isOp()) return null;

                        switch (args.length) {
                            case 2: {
                                List<String> rankOptions = new ArrayList<>();
                                rankOptions.add("set");
                                rankOptions.add("remove");
                                return rankOptions;
                            }

                            case 3: {
                                if (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("remove")) {
                                    return Bukkit.getServer().getOnlinePlayers().parallelStream().map(Player::getName).collect(Collectors.toList());
                                }
                            }

                            case 4: {
                                if (args[1].equalsIgnoreCase("set") && !args[2].isEmpty()) {
                                    return Arrays.stream(ServerRanks.values()).map(ServerRanks::name).collect(Collectors.toList());
                                }
                            }
                        }
                    }

                    case GM: {
                        if (!player.isOp()) return null;

                        if (args.length != 2) return null;

                        List<String> gmCode = new ArrayList<>();
                        gmCode.add("0");
                        gmCode.add("1");
                        gmCode.add("2");
                        gmCode.add("3");

                        return gmCode;
                    }

                    case MOD: {
                        if (!player.isOp()) return null;

                        if (args.length != 2) return null;

                        List<String> modOption = new ArrayList<>();
                        modOption.add("on");
                        modOption.add("off");

                        return modOption;
                    }

                    case SERVERCONFIG: {
                        if (!player.isOp()) return null;

                        switch (args.length) {
                            case 2: {
                                try {
                                    return ServerCFGHandler.fetchConfigs();
                                } catch (IOException | ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            case 3: {
                                List<String> values = new ArrayList<>();

                                switch (args[1]) {
                                    case "PVP":

                                    case "ChatLogging" :

                                    case "iCutter":

                                    case "iSmelter":

                                    case "iHarvest":

                                    case "iWaypoints-Cross_Dimension": {
                                        values.add("true");
                                        values.add("false");
                                        break;
                                    }

                                    case "TNT_Protection" : {
                                        values.add("0");
                                        values.add("1");
                                        values.add("2");
                                        break;
                                    }
                                }

                                return values;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }
}
