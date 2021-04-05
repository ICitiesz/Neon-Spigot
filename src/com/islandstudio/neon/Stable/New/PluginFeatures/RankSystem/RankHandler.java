package com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem;

import com.islandstudio.neon.Stable.New.Utilities.NamespaceVersion;
import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.New.Utilities.ProfileHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class RankHandler {
    public static ArrayList<String> rankNames = new ArrayList<>();

    private static final Plugin plugin = MainCore.getPlugin(MainCore.class);
    private static final String VERSION = plugin.getServer().getBukkitVersion().split("\\.")[0] + "." + plugin.getServer().getBukkitVersion().split("\\.")[1];

    public static void setRank(Player commander, Player onlinePlayers, String rankName, String playerName) throws IOException, ParseException {
        for (ServerRanks serverRanks : ServerRanks.values()) {
            rankNames.add(serverRanks.toString());

            if (rankName.equalsIgnoreCase(serverRanks.toString())) {
                if (!rankName.equalsIgnoreCase((String) ProfileHandler.getValue(onlinePlayers).get("Rank"))) {
                    if (rankName.equalsIgnoreCase(ServerRanks.OWNER.toString())) {
                        if (onlinePlayers.isOp()) {
                            ProfileHandler.setValue(onlinePlayers, "Rank", rankName.toUpperCase());
                            RankTags.setRankTags();
                            commander.sendMessage(ChatColor.GREEN + "Rank successfully set!");
                        } else {
                            commander.sendMessage(ChatColor.YELLOW + "You can't set Owner rank to people who are not Server Operator!");
                        }
                    } else {
                        ProfileHandler.setValue(onlinePlayers, "Rank", rankName.toUpperCase());
                        RankTags.setRankTags();
                        commander.sendMessage(ChatColor.GREEN + "Rank successfully set!");
                    }
                } else {
                    String syntax = ChatColor.RED + "The player " + ChatColor.WHITE + playerName + ChatColor.RED
                            + " already have rank " + ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + "!";

                    if (rankName.equalsIgnoreCase(ServerRanks.OWNER.toString())) {
                        if (onlinePlayers.isOp()) {
                            if (onlinePlayers.equals(commander)) {
                                onlinePlayers.sendMessage(ChatColor.RED + "You already have " + ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + " rank!");
                            } else {
                                onlinePlayers.sendMessage(syntax);
                            }
                        }
                    } else {
                        if (onlinePlayers.equals(commander)) {
                            onlinePlayers.sendMessage(ChatColor.RED + "You already have " + ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + " rank!");
                        } else {
                            onlinePlayers.sendMessage(syntax);
                        }
                    }
                }
            }
        }
    }

    public static void removeRank(Player commander, Player onlinePlayers) throws IOException, ParseException {
        String rankName = (String) ProfileHandler.getValue(onlinePlayers).get("Rank");

        if (rankName.equalsIgnoreCase(ServerRanks.VIP.toString()) || rankName.equalsIgnoreCase(ServerRanks.VIP_PLUS.toString())
                || rankName.equalsIgnoreCase(ServerRanks.OWNER.toString())) {
            ProfileHandler.setValue(onlinePlayers, "Rank", ServerRanks.MEMBER.toString());
            RankTags.setRankTags();

            if (onlinePlayers.isOp() && rankName.equalsIgnoreCase(ServerRanks.OWNER.toString())) {
                onlinePlayers.sendMessage(ChatColor.RED + "Your " + ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + " rank has been removed!");
            } else {
                commander.sendMessage(ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + " rank has been removed from " + ChatColor.WHITE + onlinePlayers.getName());
            }
        } else if (rankName.equalsIgnoreCase(ServerRanks.MEMBER.toString())) {
            commander.sendMessage(ChatColor.YELLOW + "Minimum rank must be a Member!");
        }
    }

    public static void setChatTag(Player player, String playerName, String messages) throws IOException, ParseException {
        String rankName = (String) ProfileHandler.getValue(player).get("Rank");

        for (ServerRanks serverRanks : ServerRanks.values()) {
            switch (serverRanks) {
                case OWNER: {
                    if (ServerRanks.OWNER.toString().equalsIgnoreCase(rankName)) {
                        for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                            try {
                                sendChat(serverRanks.getPrefix(), playerName, messages, onlinePlayers);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        //ChatLogger.add(ServerRanksDefaultState.OWNER.getPrefix(), playerName, messages);
                    }
                    break;
                }

                case VIP_PLUS: {
                    if (ServerRanks.VIP_PLUS.toString().equalsIgnoreCase(rankName)) {
                        for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                            try {
                                sendChat(serverRanks.getPrefix(), playerName, messages, onlinePlayers);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        //ChatLogger.add(ServerRanksDefaultState.VIP_PLUS.getPrefix(), playerName, messages);
                    }
                    break;
                }

                case VIP: {
                    if (ServerRanks.VIP.toString().equalsIgnoreCase(rankName)) {
                        for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                            try {
                                sendChat(serverRanks.getPrefix(), playerName, messages, onlinePlayers);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        //ChatLogger.add(ServerRanksDefaultState.VIP.getPrefix(), playerName, messages);
                    }
                    break;
                }

                case MEMBER: {
                    if (ServerRanks.MEMBER.toString().equalsIgnoreCase(rankName)) {
                        for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                            try {
                                sendChat(serverRanks.getPrefix(), playerName, messages, onlinePlayers);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        //ChatLogger.add(ServerRanksDefaultState.MEMBER.getPrefix(), playerName, messages);
                    }
                    break;
                }
            }
        }
    }

    private static void sendChat(String rank, String playerName, String messages, Player onlinePlayer) throws Exception {
        for (Class<?> declaredClass : NamespaceVersion.getNameSpaceClass("IChatBaseComponent").getDeclaredClasses()) {
            if (declaredClass.getSimpleName().equalsIgnoreCase("ChatSerializer")) {
                Object chatMessage = declaredClass.getMethod("a", String.class).invoke(null, "{\"text\":\"" + rank + ChatColor.WHITE + playerName + " > " + charactersEscape(messages) + "\"}");
                Object chatType = NamespaceVersion.getNameSpaceClass("ChatMessageType").getField("CHAT").get(null);
                Object packet = null;

                if (VERSION.equalsIgnoreCase("1.16") || VERSION.equalsIgnoreCase("1.15")) {
                    Constructor<?> constructor = NamespaceVersion.getNameSpaceClass("PacketPlayOutChat").getConstructor(NamespaceVersion.getNameSpaceClass("IChatBaseComponent"), NamespaceVersion.getNameSpaceClass("ChatMessageType"), UUID.class);
                    packet = constructor.newInstance(chatMessage, chatType, onlinePlayer.getUniqueId());
                } else if (VERSION.equalsIgnoreCase("1.14")) {
                    Constructor<?> constructor = NamespaceVersion.getNameSpaceClass("PacketPlayOutChat").getConstructor(NamespaceVersion.getNameSpaceClass("IChatBaseComponent"), NamespaceVersion.getNameSpaceClass("ChatMessageType"));
                    packet = constructor.newInstance(chatMessage, chatType);
                }

                sendPacket(onlinePlayer, packet);
            }
        }
    }

    private static void sendPacket(Player onlinePlayer, Object packet) throws Exception {
        Object handler = onlinePlayer.getClass().getMethod("getHandle").invoke(onlinePlayer);
        Object playerConnection = handler.getClass().getField("playerConnection").get(handler);
        playerConnection.getClass().getMethod("sendPacket", NamespaceVersion.getNameSpaceClass("Packet")).invoke(playerConnection, packet);
    }

    private static String charactersEscape(String message) {
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
