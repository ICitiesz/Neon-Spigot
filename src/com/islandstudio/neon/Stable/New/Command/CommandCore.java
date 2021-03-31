package com.islandstudio.neon.Stable.New.Command;

import com.islandstudio.neon.Stable.New.GUI.Interfaces.iWaypoints.IWaypoints;
import com.islandstudio.neon.Experimental.GameModes;
import com.islandstudio.neon.Experimental.GameModeHandler;
import com.islandstudio.neon.Stable.New.Utilities.ServerCfgHandler;
import com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem.RankHandler;
import com.islandstudio.neon.Stable.Deprecated.Utilities.PlayerDataHandler;
import com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem.ServerRanks;
import com.islandstudio.neon.Stable.New.GUI.Interfaces.EffectsManager.EffectsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandCore implements Listener, TabExecutor {
    public static boolean moderation = false;

    //Prefix
    public static final String prefix = ChatColor.WHITE + "[" + ChatColor.AQUA + "SERVER" + ChatColor.WHITE + "] ";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {

            if (cmd.getName().equalsIgnoreCase(CommandAlias.CMD_1.getCommandAlias())) {
                Player player = (Player) sender;

                if (player.isOp()) {
                    if (args.length == 1) {
                        String option = args[0];

                        if (option.equalsIgnoreCase("on")) {
                            if (!player.isInvulnerable()) {
                                player.setInvulnerable(true);
                                player.setAllowFlight(true);
                                if (!player.isCollidable()) {
                                    player.setCollidable(false);
                                }
                                if (! moderation) {
                                    moderation = true;
                                }
                                player.sendMessage(ChatColor.GREEN + "Moderation enabled!");
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "Moderation already enabled!");
                            }
                        } else if (option.equalsIgnoreCase("off")) {
                            if (player.isInvulnerable()) {
                                player.setInvulnerable(false);
                                player.setAllowFlight(false);
                                if (player.isCollidable()) {
                                    player.setCollidable(true);
                                }
                                if (moderation) {
                                    moderation = false;
                                }
                                player.sendMessage(ChatColor.RED + "Moderation disabled!");
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "Moderation already enabled!");
                            }
                        } else {
                            SyntaxHandler.sendSyntax(player, 1);
                        }
                    } else {
                        SyntaxHandler.sendSyntax(player, 1);
                    }
                } else {
                    SyntaxHandler.sendSyntax(player, 2);
                }
                return true;
            }

            if (cmd.getName().equalsIgnoreCase(CommandAlias.CMD_2.getCommandAlias())) {
                Player player = (Player) sender;

                /*if (player.isOp()) {
                    if (args.length == 2) {
                        String gameModeValue = args[0];
                        String target = args[1];

                        if (gameModeValue.equalsIgnoreCase(String.valueOf(GameModes.SURVIVAL_MODE.getGameModeValue()))) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                if (target.equalsIgnoreCase(onlinePlayers.getName())) {
                                    GameModeHandler.setSurvival(onlinePlayers);
                                }
                            }
                        } else if (gameModeValue.equalsIgnoreCase(String.valueOf(GameModes.CREATIVE_MODE.getGameModeValue()))) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                if (target.equalsIgnoreCase(onlinePlayers.getName())) {
                                    GameModeHandler.setCreative(onlinePlayers);
                                }
                            }
                        } else if (gameModeValue.equalsIgnoreCase(String.valueOf(GameModes.ADVENTURE_MODE.getGameModeValue()))) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                if (target.equalsIgnoreCase(onlinePlayers.getName())) {
                                    GameModeHandler.setAdventure(onlinePlayers);
                                }
                            }
                        } else if (gameModeValue.equalsIgnoreCase(String.valueOf(GameModes.SPECTATOR_MODE.getGameModeValue()))) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                if (target.equalsIgnoreCase(onlinePlayers.getName())) {
                                    GameModeHandler.setSpectator(onlinePlayers);
                                }
                            }
                        } else {
                            player.sendRawMessage("Invalid gamemode!");
                        }
                    }
                }*/

                if (player.isOp()) {
                    if (args.length == 1) {
                        String value = args[0];

                        if (value.equalsIgnoreCase(String.valueOf(GameModes.SURVIVAL_MODE.getGameModeValue()))) {
                            GameModeHandler.setSurvival(player);
                        } else if (value.equalsIgnoreCase(String.valueOf(GameModes.CREATIVE_MODE.getGameModeValue()))) {
                            GameModeHandler.setCreative(player);
                        } else if (value.equalsIgnoreCase(String.valueOf(GameModes.ADVENTURE_MODE.getGameModeValue()))) {
                            GameModeHandler.setAdventure(player);
                        } else if (value.equalsIgnoreCase(String.valueOf(GameModes.SPECTATOR_MODE.getGameModeValue()))) {
                            GameModeHandler.setSpectator(player);
                        } else {
                            SyntaxHandler.sendSyntax(player, 1);
                        }
                    } else {
                        SyntaxHandler.sendSyntax(player, 1);
                    }
                } else {
                    SyntaxHandler.sendSyntax(player, 2);
                }
                return true;
            }

            if (cmd.getName().equalsIgnoreCase(CommandAlias.CMD_3.getCommandAlias())) {
                Player player = (Player) sender;
                String playerRank = PlayerDataHandler.getData(PlayerDataHandler.getDataFile(player)).getString("Rank");

                if (player.isOp() || ServerRanks.OWNER.toString().equalsIgnoreCase(playerRank)) {
                    if (player.getFoodLevel() < 20 || (player.getSaturation() < 20)) {
                        player.setFoodLevel(20);
                        player.setSaturation(20);
                    } else if (player.getHealth() < 20) {
                        player.setHealth(20);
                    } else {
                        player.sendRawMessage(ChatColor.YELLOW + "Regen only available when your health level or food level is below 20!!");
                    }
                } else {
                    SyntaxHandler.sendSyntax(player, 2);
                }

            }

            if (cmd.getName().equalsIgnoreCase(CommandAlias.CMD_4.getCommandAlias())) {
                Player player = (Player) sender;
                String playerRank = PlayerDataHandler.getData(PlayerDataHandler.getDataFile(player)).getString("Rank");
                EffectsManager effectsManager = new EffectsManager();

                if (player.isOp() || ServerRanks.OWNER.toString().equalsIgnoreCase(playerRank) || ServerRanks.VIP_PLUS.toString().equalsIgnoreCase(playerRank)) {
                    if (!player.isSleeping()) {
                        effectsManager.openEffectManager(player);
                    } else {
                        player.sendRawMessage(ChatColor.YELLOW + "You can't use Effects Manager while you're sleeping!");
                    }
                } else {
                    player.sendRawMessage(ChatColor.RED + "You need a higher rank to do that!");
                }
                return true;
            }

            if (cmd.getName().equalsIgnoreCase(CommandAlias.CMD_5.getCommandAlias())) {
                Player player = (Player) sender;

                if (player.isOp()) {
                    if (args.length == 3) {
                        String option = args[0];
                        String playerName = args[1];
                        String rankName = args[2];
                        ArrayList<String> playerNames = new ArrayList<>();
                        ArrayList<String> rankNames = RankHandler.rankNames;

                        if (option.equalsIgnoreCase("set")) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                playerNames.add(onlinePlayers.getName());

                                if (playerName.equalsIgnoreCase(onlinePlayers.getName())) {
                                    try {
                                        RankHandler.setRank(player, onlinePlayers, rankName, playerName);
                                    } catch (IOException | ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            if (!playerNames.contains(playerName)) {
                                player.sendMessage(ChatColor.RED + "Can't find player " + ChatColor.WHITE + playerName + ChatColor.RED + " !");
                            } else if (!rankNames.contains(rankName.toUpperCase())) {
                                player.sendMessage(ChatColor.RED + "No such rank as " + ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + " !");
                            }

                            playerNames.clear();
                            rankNames.clear();
                        } else {
                            SyntaxHandler.sendSyntax(player, 1);
                        }
                    } else if (args.length == 2) {
                        String option = args[0];
                        String playerName = args[1];

                        if (option.equalsIgnoreCase("remove")) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                if (playerName.equalsIgnoreCase(onlinePlayers.getName())) {
                                    try {
                                        com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem.RankHandler.removeRank(player, onlinePlayers);
                                    } catch (IOException | ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            SyntaxHandler.sendSyntax(player, 1);
                        }
                    } else {
                        SyntaxHandler.sendSyntax(player, 1);
                    }
                } else {
                    SyntaxHandler.sendSyntax(player, 2);
                }
                return true;
            }

            if (cmd.getName().equalsIgnoreCase(CommandAlias.CMD_6.getCommandAlias())){
                Player player = (Player) sender;

                if (player.isOp()) {
                    if (args.length == 1) {
                        String setting = args[0];

                        switch (setting) {
                            case "PVP":

                            case "TNT_Protection":

                            case "ChatLogging": {
                                player.sendMessage(ChatColor.YELLOW + "Please provide a value!");
                                break;
                            }

                            default: {
                                SyntaxHandler.sendSyntax(player, 1);
                                break;
                            }
                        }
                    } else if (args.length == 2) {
                        String setting = args[0];
                        String value = args[1];

                        try {
                            ServerCfgHandler.setValue(setting, value, player);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Please select a configuration to modify!");
                    }
                } else {
                    SyntaxHandler.sendSyntax(player, 2);
                }
                return true;
            }

            if (cmd.getName().equalsIgnoreCase(CommandAlias.CMD_7.getCommandAlias())) {
                IWaypoints.setCommandHandler(args, (Player) sender);
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("test")) {
                Player player = (Player) sender;

                if (args.length == 1) {
                    String value = args[0];

                    //TestingArea.test(value);

//                    if (value.equalsIgnoreCase("remove")) {
//                        //deathFinder.removeData(player);
//                        HubHandler.remove(player);
//                    } else if (value.equalsIgnoreCase("read")) {
//                        //TestingArea.readFileData(player);
//                    } else if (value.equalsIgnoreCase("edit")) {
//                        //TestingArea.modifyFileData(player);
//                    } else if (value.equalsIgnoreCase("set")) {
//                        HubHandler.setHub(player);
//                    }
                } else {
                    //Test.readData(player);
                    //Test.ftTest(player);
                    //deathFinder.test2(player);
                    //TestingArea.getDataFolder(player);
                    //TestingArea.setFileData();

                    try {
                        //TestingArea.initialize();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase(CommandAlias.CMD_5.getCommandAlias())) {
                List<String> option = new ArrayList<>();
                List<String> playerNames = new ArrayList<>();
                List<String> ranks = new ArrayList<>();

                if (args.length == 1) {
                    option.add("set");
                    option.add("remove");
                    return option;
                }

                if (args.length == 2) {
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        playerNames.add(player.getName());
                    }
                    return playerNames;
                }

                if (args.length == 3) {
                    for (ServerRanks serverRanks : ServerRanks.values()) {
                        ranks.add(serverRanks.name());
                    }
                    return ranks;
                }
            }

            if (cmd.getName().equalsIgnoreCase(CommandAlias.CMD_6.getCommandAlias())) {
                if (args.length == 1) {
                    try {
                        return ServerCfgHandler.fetchConfigs();
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (args.length == 2) {
                    String value = args[0];

                    List<String> values = new ArrayList<>();

                    switch (value) {
                        case "PVP":

                        case "ChatLogging" : {
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

            if (cmd.getName().equalsIgnoreCase(CommandAlias.CMD_7.getCommandAlias())) {
                switch (args.length) {
                    case 1: {
                        List<String> options = new ArrayList<>();

                        options.add("add");
                        options.add("remove");

                        return options;
                    }

                    case 2: {
                        if (args[0].equalsIgnoreCase("remove")) {
                            return IWaypoints.getWaypointNames();
                        }
                    }
                }
            }

            /*if (cmd.getName().equalsIgnoreCase(new Commands().cmd_1)) {
                if (args.length == 1) {
                    String firstArgs = args[0];

                    level.add("0");
                    level.add("1");
                    level.add("2");

                    completion.addAll(level);

                    if (!firstArgs.equalsIgnoreCase("")) {

                        if (!level.contains(firstArgs)) {
                            completion.removeAll(level);
                        } else {
                            completion.addAll(level);
                        }
                    }
                } else if (args.length > 1) {
                    completion.removeAll(level);
                }
            } else if (cmd.getName().equalsIgnoreCase(new Commands().cmd_test_1)) {
                completion.add("Test1");
                completion.add("Test2");
            }*/
        }

        return null;
    }
}
