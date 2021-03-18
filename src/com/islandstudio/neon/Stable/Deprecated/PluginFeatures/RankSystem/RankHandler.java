package com.islandstudio.neon.Stable.Deprecated.PluginFeatures.RankSystem;

import com.islandstudio.neon.Stable.New.Utilities.NMS_Class_Version;
import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.Old.Initialization.FolderManager.FolderList;
import com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem.ServerRanks;
import com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem.ServerRanksDefaultState;
import com.islandstudio.neon.Stable.Old.Utilities.ChatLogger;
import com.islandstudio.neon.Stable.Deprecated.Utilities.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.UUID;

public class RankHandler {
    public static ArrayList<String> rankNames = new ArrayList<>();

    private static final Plugin plugin = MainCore.getPlugin(MainCore.class);
    private static final boolean isOnlineMode = Bukkit.getServer().getOnlineMode();

    public static void giveRank(Player player, Player onlinePlayers, String rankName, String playerName) {
        String modifiedString = ChatColor.RED + "The player " + ChatColor.WHITE + playerName + ChatColor.RED
                + " already have rank " + ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + " !";

        if (isOnlineMode) {
            File file = new File(FolderList.getFolder_2a_1, onlinePlayers.getUniqueId() + ".sv");

            for (ServerRanks serverRanks : ServerRanks.values()) {
                rankNames.add(serverRanks.toString());

                if (rankName.equalsIgnoreCase(serverRanks.toString())) {
                    if (!rankName.equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                        if (!onlinePlayers.isOp()) {
                            if (!rankName.equalsIgnoreCase("OWNER")) {
                                PlayerDataHandler.setData(file).set("Rank", rankName.toUpperCase());
                                PlayerDataHandler.saveData(file);
                                RankTags.setRankTags();
                                player.sendRawMessage(ChatColor.GREEN + "Rank successfully set!");
                            } else {
                                player.sendRawMessage(ChatColor.YELLOW + "You can't set Owner rank to people who are not Server Operator!");
                            }
                        }
                    } else {
                        player.sendRawMessage(modifiedString);
                    }
                }
            }
        } else {
            File file = new File(FolderList.getFolder_2b_1, onlinePlayers.getUniqueId() + ".sv");

            for (ServerRanks serverRanks : ServerRanks.values()) {
                rankNames.add(serverRanks.toString());

                if (rankName.equalsIgnoreCase(serverRanks.toString())) {
                    if (!rankName.equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                        if (!onlinePlayers.isOp()) {
                            if (!rankName.equalsIgnoreCase("OWNER")) {
                                PlayerDataHandler.setData(file).set("Rank", rankName.toUpperCase());
                                PlayerDataHandler.saveData(file);
                                RankTags.setRankTags();
                                player.sendRawMessage(ChatColor.GREEN + "Rank successfully set!");
                            } else {
                                player.sendRawMessage(ChatColor.YELLOW + "You can't set Owner rank to people who are not Server Operator!");
                            }
                        }
                    } else {
                        player.sendRawMessage(modifiedString);
                    }
                }
            }
        }
    }

    public static void giveRank(Player player, Player onlinePlayers, String rankName) {
        String modifiedString = ChatColor.RED + "You already have " + ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + " rank!";

        if (isOnlineMode) {
            File file = new File(FolderList.getFolder_2a_1, onlinePlayers.getUniqueId() + ".sv");

            if (!plugin.getConfig().getBoolean("Static_Owner_Rank")) {
                for (ServerRanks serverRanks : ServerRanks.values()) {
                    rankNames.add(serverRanks.toString());

                    if (rankName.equalsIgnoreCase(serverRanks.toString())) {
                        if (!rankName.equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                            PlayerDataHandler.setData(file).set("Rank", rankName.toUpperCase());
                            PlayerDataHandler.saveData(file);
                            RankTags.setRankTags();
                            player.sendRawMessage(ChatColor.GREEN + "Rank successfully set!");
                        } else {
                            player.sendRawMessage(modifiedString);
                        }
                    }
                }
            } else {
                for (ServerRanks serverRanks : ServerRanks.values()) {
                    rankNames.add(serverRanks.toString());

                    if (rankName.equalsIgnoreCase(serverRanks.toString())) {
                        if (rankName.equalsIgnoreCase("OWNER")) {
                            if (!rankName.equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                                PlayerDataHandler.setData(file).set("Rank", rankName.toUpperCase());
                                PlayerDataHandler.saveData(file);
                                RankTags.setRankTags();
                                player.sendRawMessage(ChatColor.GREEN + "Rank successfully set!");
                            } else {
                                player.sendRawMessage(modifiedString);
                            }
                        } else {
                            player.sendRawMessage(ChatColor.RED + "You can't change your OWNER rank!");
                        }
                    }
                }
            }
        } else {
            File file = new File(FolderList.getFolder_2b_1, onlinePlayers.getUniqueId() + ".sv");

            if (!plugin.getConfig().getBoolean("Static_Owner_Rank")) {
                for (ServerRanks serverRanks : ServerRanks.values()) {
                    rankNames.add(serverRanks.toString());

                    if (rankName.equalsIgnoreCase(serverRanks.toString())) {
                        if (!rankName.equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                            PlayerDataHandler.setData(file).set("Rank", rankName.toUpperCase());
                            PlayerDataHandler.saveData(file);
                            RankTags.setRankTags();
                            player.sendRawMessage(ChatColor.GREEN + "Rank successfully set!");
                        } else {
                            player.sendRawMessage(modifiedString);
                        }
                    }
                }
            } else {
                for (ServerRanks serverRanks : ServerRanks.values()) {
                    rankNames.add(serverRanks.toString());

                    if (rankName.equalsIgnoreCase(serverRanks.toString())) {
                        if (rankName.equalsIgnoreCase("OWNER")) {
                            if (!rankName.equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                                PlayerDataHandler.setData(file).set("Rank", rankName.toUpperCase());
                                PlayerDataHandler.saveData(file);
                                RankTags.setRankTags();
                                player.sendRawMessage(ChatColor.GREEN + "Rank successfully set!");
                            } else {
                                player.sendRawMessage(modifiedString);
                            }
                        } else {
                            player.sendRawMessage(ChatColor.RED + "You can't change your OWNER rank!");
                        }
                    }
                }
            }
        }
    }

    public static void removeRank(Player player, Player onlinePlayers) {
        if (isOnlineMode) {
            File file = new File(FolderList.getFolder_2a_1, onlinePlayers.getUniqueId() + ".sv");
            String rankName = PlayerDataHandler.getData(file).getString("Rank");

            if ("VIP".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))
                    || "VIP_PLUS".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                PlayerDataHandler.setData(file).set("Rank", "MEMBER");
                PlayerDataHandler.saveData(file);
                RankTags.setRankTags();
                if (rankName != null) {
                    player.sendRawMessage(ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + " rank has been removed from " + ChatColor.WHITE + onlinePlayers.getName());
                }
            } else if ("OWNER".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                player.sendRawMessage(ChatColor.RED + "You can't remove your Owner rank!");
            } else if ("MEMBER".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                player.sendRawMessage(ChatColor.YELLOW + "Minimum rank must be a Member!");
            }
        } else {
            File file = new File(FolderList.getFolder_2b_1, onlinePlayers.getUniqueId() + ".sv");

            String rankName = PlayerDataHandler.getData(file).getString("Rank");

            if ("VIP".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))
                    || "VIP_PLUS".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                PlayerDataHandler.setData(file).set("Rank", "MEMBER");
                PlayerDataHandler.saveData(file);
                RankTags.setRankTags();
                if (rankName != null) {
                    player.sendRawMessage(ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + " rank has been removed from " + ChatColor.WHITE + onlinePlayers.getName());
                }
            } else if ("OWNER".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                player.sendRawMessage(ChatColor.RED + "You can't remove your Owner rank!");
            } else if ("MEMBER".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                player.sendRawMessage(ChatColor.YELLOW + "Minimum rank must be a Member!");
            }
        }
    }

    public static void removeRank(Player player) {
        if (isOnlineMode) {
            File file = new File(FolderList.getFolder_2a_1, player.getUniqueId() + ".sv");

            if (!plugin.getConfig().getBoolean("Static_Owner_Rank")) {
                String rankName = PlayerDataHandler.getData(file).getString("Rank");

                if ("OWNER".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank")) || "VIP".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))
                        || "VIP_PLUS".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                    PlayerDataHandler.setData(file).set("Rank", "MEMBER");
                    PlayerDataHandler.saveData(file);
                    RankTags.setRankTags();
                    if (rankName != null) {
                        player.sendRawMessage(ChatColor.RED + "Your " + ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + " rank has been removed!");
                    }
                } else if ("MEMBER".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                    player.sendRawMessage(ChatColor.YELLOW + "Minimum rank must be a Member!");
                }
            } else {
                player.sendRawMessage(ChatColor.RED + "You can't remove your Owner rank!");
            }
        } else {
            File file = new File(FolderList.getFolder_2b_1, player.getUniqueId() + ".sv");

            if (!plugin.getConfig().getBoolean("Static_Owner_Rank")) {
                String rankName = PlayerDataHandler.getData(file).getString("Rank");

                if ("OWNER".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank")) || "VIP".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))
                        || "VIP_PLUS".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                    PlayerDataHandler.setData(file).set("Rank", "MEMBER");
                    PlayerDataHandler.saveData(file);
                    RankTags.setRankTags();
                    if (rankName != null) {
                        player.sendRawMessage(ChatColor.RED + "Your " + ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + " rank has been removed!");
                    }
                } else if ("MEMBER".equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                    player.sendRawMessage(ChatColor.YELLOW + "Minimum rank must be a Member!");
                }
            } else {
                player.sendRawMessage(ChatColor.RED + "You can't remove your Owner rank!");
            }
        }
    }

    public static void setChatTag(Player player, String playerName, String messages) throws IOException {
        if (isOnlineMode) {
            File file = new File(FolderList.getFolder_2a_1, player.getUniqueId() + ".sv");

            for (ServerRanks serverRanks : ServerRanks.values()) {
                switch (serverRanks) {
                    case OWNER: {
                        if (ServerRanks.OWNER.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                try {
                                    sendChat(serverRanks.getPrefix(), playerName, messages, onlinePlayers);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            ChatLogger.add(ServerRanksDefaultState.OWNER.getPrefix(), playerName, messages);
                        }
                        break;
                    }

                    case VIP_PLUS: {
                        if (ServerRanks.VIP_PLUS.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                try {
                                    sendChat(serverRanks.getPrefix(), playerName, messages, onlinePlayers);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            ChatLogger.add(ServerRanksDefaultState.VIP_PLUS.getPrefix(), playerName, messages);
                        }
                        break;
                    }

                    case VIP: {
                        if (ServerRanks.VIP.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                try {
                                    sendChat(serverRanks.getPrefix(), playerName, messages, onlinePlayers);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            ChatLogger.add(ServerRanksDefaultState.VIP.getPrefix(), playerName, messages);
                        }
                        break;
                    }

                    case MEMBER: {
                        if (ServerRanks.MEMBER.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                try {
                                    sendChat(serverRanks.getPrefix(), playerName, messages, onlinePlayers);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            ChatLogger.add(ServerRanksDefaultState.MEMBER.getPrefix(), playerName, messages);
                        }
                        break;
                    }
                }
            }
        } else {
            File file = new File(FolderList.getFolder_2b_1, player.getUniqueId() + ".sv");

            for (ServerRanks serverRanks : ServerRanks.values()) {
                switch (serverRanks) {
                    case OWNER: {
                        if (ServerRanks.OWNER.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                try {
                                    sendChat(serverRanks.getPrefix(), playerName, messages, onlinePlayers);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            ChatLogger.add(ServerRanksDefaultState.OWNER.getPrefix(), playerName, messages);
                        }
                        break;
                    }

                    case VIP_PLUS: {
                        if (ServerRanks.VIP_PLUS.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                try {
                                    sendChat(serverRanks.getPrefix(), playerName, messages, onlinePlayers);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            ChatLogger.add(ServerRanksDefaultState.VIP_PLUS.getPrefix(), playerName, messages);
                        }
                        break;
                    }

                    case VIP: {
                        if (ServerRanks.VIP.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                try {
                                    sendChat(serverRanks.getPrefix(), playerName, messages, onlinePlayers);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            ChatLogger.add(ServerRanksDefaultState.VIP.getPrefix(), playerName, messages);
                        }
                        break;
                    }

                    case MEMBER: {
                        if (ServerRanks.MEMBER.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                try {
                                    sendChat(serverRanks.getPrefix(), playerName, messages, onlinePlayers);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            ChatLogger.add(ServerRanksDefaultState.MEMBER.getPrefix(), playerName, messages);
                        }
                        break;
                    }
                }
            }
        }
    }

    private static void sendChat(String rank, String playerName, String messages, Player onlinePlayer) throws Exception {
        for (Class<?> declaredClass : NMS_Class_Version.getNMSClass("IChatBaseComponent").getDeclaredClasses()) {
            if (declaredClass.getSimpleName().equalsIgnoreCase("ChatSerializer")) {
                Object chatMsg = declaredClass.getMethod("a", String.class).invoke(null, "{\"text\":\"" + rank + ChatColor.WHITE + playerName + " > " + messages + "\"}");
                Object chatType = NMS_Class_Version.getNMSClass("ChatMessageType").getField("CHAT").get(null);

                Constructor<?> constructor = NMS_Class_Version.getNMSClass("PacketPlayOutChat").getConstructor(NMS_Class_Version.getNMSClass("IChatBaseComponent"), NMS_Class_Version.getNMSClass("ChatMessageType"), UUID.class);
                Object packet = constructor.newInstance(chatMsg, chatType, onlinePlayer.getUniqueId());

                sendPacket(onlinePlayer, packet);
            }
        }
    }

    private static void sendPacket(Player onlinePlayer, Object packet) throws Exception {
        Object handler = onlinePlayer.getClass().getMethod("getHandle").invoke(onlinePlayer);
        Object playerConnection = handler.getClass().getField("playerConnection").get(handler);
        playerConnection.getClass().getMethod("sendPacket", NMS_Class_Version.getNMSClass("Packet")).invoke(playerConnection, packet);
    }

}
