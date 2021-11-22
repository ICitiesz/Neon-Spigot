package com.islandstudio.neon.Stable.New.features.iRank;

import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.New.Utilities.NamespaceVersion;
import com.islandstudio.neon.Stable.New.Utilities.ProfileHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

public class IRank {
    private static final Plugin plugin = MainCore.getPlugin(MainCore.class);
    private static final Scoreboard scoreboard = Objects.requireNonNull(plugin.getServer().getScoreboardManager()).getNewScoreboard();

    /* Initialization */
    public static void init() {
        for (ServerRanks serverRanks : ServerRanks.values()) {
            Team team = scoreboard.registerNewTeam(serverRanks.name());
            team.setPrefix(serverRanks.getPrefix());
        }
    }

    /* Update tag */
    public static void updateTag() {
        plugin.getServer().getOnlinePlayers().forEach(target -> {
            try {
                String rankName = (String) ProfileHandler.getValue(target).get("Rank");

                if (Arrays.stream(ServerRanks.values()).noneMatch(rank -> rankName.equalsIgnoreCase(rank.name()))) return;

                Objects.requireNonNull(scoreboard.getTeam(rankName.toUpperCase())).addEntry(target.getName());
                target.setScoreboard(scoreboard);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });
    }

    /* Setting player rank */
    public static void set(Player commander, String rankName, String playerName) throws IOException, ParseException {
        Player target = plugin.getServer().getPlayer(playerName);

        /* Check if the given rankName match the "Rank" value in the player profile */
        if (!rankName.equalsIgnoreCase((String) ProfileHandler.getValue(target).get("Rank"))) {
            if (target == null) return;

            /* Owner rank for Server Operator */
            if (rankName.equalsIgnoreCase(ServerRanks.OWNER.name()) && !target.isOp()) {
                commander.sendMessage(ChatColor.YELLOW + "The '" + rankName.toUpperCase() + "' rank is unavailable for this player!");
                return;
            }

            ProfileHandler.setValue(target, "Rank", rankName.toUpperCase());
            updateTag();
            commander.sendMessage(ChatColor.GREEN + "Rank successfully set!");
            return;
        }

        /* Message need rephrase */
        commander.sendMessage(ChatColor.RED + "The player " + ChatColor.WHITE + playerName + ChatColor.RED
                + " already have an existing rank " + ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + "!");
    }

    /* Remove player rank */
    public static void remove(Player commander, String playerName) throws IOException, ParseException {
        Player target = plugin.getServer().getPlayer(playerName);
        String rankName = (String) ProfileHandler.getValue(target).get("Rank");

        if (rankName.equalsIgnoreCase(ServerRanks.MEMBER.name())) {
            commander.sendMessage(ChatColor.YELLOW + "Minimum rank must be a Member!");
            return;
        }

        ProfileHandler.setValue(target, "Rank", ServerRanks.MEMBER.name());
        updateTag();

        commander.sendMessage(ChatColor.RED + "The '" + ChatColor.GRAY + rankName.toUpperCase() + ChatColor.RED + "' rank has been removed from '" + ChatColor.GRAY + target.getName() + ChatColor.RED + "'!");
        target.sendMessage(ChatColor.RED + "Your '" + ChatColor.GRAY + rankName.toUpperCase() + ChatColor.RED + "' rank has been removed!");
    }

    /* Send processed message */
    public static void sendMessage(Player player, String messages) throws IOException, ParseException {
        String rankName = (String) ProfileHandler.getValue(player).get("Rank");
        String playerName = player.getName();

        if (Arrays.stream(ServerRanks.values()).noneMatch(rank -> rankName.equalsIgnoreCase(rank.name()))) return;

        plugin.getServer().getOnlinePlayers().parallelStream().forEach(onlinePlayers -> {
            try {
                processMessage(ServerRanks.valueOf(rankName.toUpperCase()).getPrefix(), playerName, messages, onlinePlayers);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /* Setting command handler to handle the command execution */
    @SuppressWarnings("unchecked")
    public static void setCommandHandler(Player commander, String[] args, String pluginName) {
        if (!commander.isOp()) {
            commander.sendMessage(ChatColor.RED + "You do not have permission to do this!");
            return;
        }

        Collection<Player> onlinePlayers = (Collection<Player>) plugin.getServer().getOnlinePlayers();

        switch (args.length) {
            case 3: {
                String playerName = args[2];

                /* Check if the option is "remove" */
                if (!args[1].equalsIgnoreCase("remove")) {
                    commander.sendMessage(pluginName + ChatColor.RED + "Invalid or missing argument!");
                    break;
                }

                /* Check if the given player name exist */
                if (onlinePlayers.parallelStream().noneMatch(onlinePlayer -> playerName.equals(onlinePlayer.getName()))) {
                    commander.sendMessage(pluginName + ChatColor.RED + "No such player as '" + ChatColor.WHITE + playerName + ChatColor.RED + "'!");
                    break;
                }

                try {
                    IRank.remove(commander, playerName);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                break;
            }

            case 4: {
                String playerName = args[2];
                String rankName = args[3];

                /* Check if the option is "set" */
                if (!args[1].equalsIgnoreCase("set")) {
                    commander.sendMessage(pluginName + ChatColor.RED + "Invalid or missing argument!");
                    break;
                }

                /* Check if the given player name exist */
                if (onlinePlayers.parallelStream().noneMatch(onlinePlayer -> playerName.equals(onlinePlayer.getName()))) {
                    commander.sendMessage( pluginName + ChatColor.RED + "No such player as '" + ChatColor.WHITE + playerName + ChatColor.RED + "'!");
                    break;
                }

                /* Check if the given rank name exist */
                if (Arrays.stream(ServerRanks.values()).noneMatch(rank -> rankName.equalsIgnoreCase(rank.name()))) {
                    commander.sendMessage(pluginName + ChatColor.RED + "No such rank as '" + ChatColor.WHITE + rankName + ChatColor.RED + "'!");
                    break;
                }

                try {
                    IRank.set(commander, rankName, playerName);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                break;
            }

            default: {
                commander.sendMessage(pluginName + ChatColor.RED + "Invalid or missing argument!");
                break;
            }
        }
    }

    /* Message processing using packet */
    private static void processMessage(String rank, String playerName, String messages, Player onlinePlayer) throws Exception{
        String VERSION = plugin.getServer().getBukkitVersion().split("\\.")[0] + "." + plugin.getServer().getBukkitVersion().split("\\.")[1];

        /* Get the ChatSerializer in IChatBaseComponent */
        for (Class<?> declaredClass : NamespaceVersion.getNameSpaceClass("IChatBaseComponent").getDeclaredClasses()) {
            if (!declaredClass.getSimpleName().equalsIgnoreCase("ChatSerializer")) return;

            Object chatMessage = declaredClass.getMethod("a", String.class).invoke(null, "{\"text\":\"" + rank + ChatColor.WHITE + playerName + " > " + messageFilter(messages) + "\"}");
            Object chatType = NamespaceVersion.getNameSpaceClass("ChatMessageType").getField("CHAT").get(null);
            Object packet;

            if (VERSION.equalsIgnoreCase("1.14")) {
                Constructor<?> constructor = NamespaceVersion.getNameSpaceClass("PacketPlayOutChat").getConstructor(NamespaceVersion.getNameSpaceClass("IChatBaseComponent"), NamespaceVersion.getNameSpaceClass("ChatMessageType"));
                packet = constructor.newInstance(chatMessage, chatType);
            } else {
                Constructor<?> constructor = NamespaceVersion.getNameSpaceClass("PacketPlayOutChat").getConstructor(NamespaceVersion.getNameSpaceClass("IChatBaseComponent"), NamespaceVersion.getNameSpaceClass("ChatMessageType"), UUID.class);
                packet = constructor.newInstance(chatMessage, chatType, onlinePlayer.getUniqueId());
            }

            Object handler = onlinePlayer.getClass().getMethod("getHandle").invoke(onlinePlayer);
            Object playerConnection = handler.getClass().getField("playerConnection").get(handler);
            playerConnection.getClass().getMethod("sendPacket", NamespaceVersion.getNameSpaceClass("Packet")).invoke(playerConnection, packet);
        }
    }

    /* Filter some characters in the message to avoid parsing error */
    private static String messageFilter(String message) {
        Map<Integer, String> subStr = new TreeMap<>();
        StringBuilder stringBuilder = new StringBuilder();

        String esc = "\\";

        for (int i = 0; i < message.length(); i++) {
            subStr.put(i, message.substring(i, i + 1));
        }

        for (int key : subStr.keySet()) {
            if (subStr.get(key).equalsIgnoreCase("\"")) {
                subStr.replace(key, esc + "\"");
            }

            if (subStr.get(key).equalsIgnoreCase("\\")) {
                subStr.replace(key, esc + "\\");
            }

            stringBuilder.append(subStr.get(key));
            message = stringBuilder.toString();
        }

        return message;
    }

}
