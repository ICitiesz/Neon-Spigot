package com.islandstudio.neon.stable.secondary.iRank;

import com.islandstudio.neon.stable.secondary.iCommand.CommandHandler;
import com.islandstudio.neon.stable.secondary.iCommand.CommandSyntax;
import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.secondary.iProfile.IProfile;
import com.islandstudio.neon.stable.secondary.iProfile.ProfileProperty;
import com.islandstudio.neon.stable.utils.IReflector;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class IRank {
    private static final Plugin plugin = IConstructor.getPlugin();
    private static final Scoreboard scoreboard = Objects.requireNonNull(plugin.getServer().getScoreboardManager()).getNewScoreboard();

    public static class Handler implements CommandHandler {
        /**
         * Initialization for iRank.
         *
         */
        public static void init() {
            for (ServerRank serverRank : ServerRank.values()) {
                scoreboard.registerNewTeam(serverRank.name()).setPrefix(serverRank.getPrefix());
            }

            IConstructor.enableEvent(new EventController());
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setCommandHandler(Player commander, String[] args) {
            if (!commander.isOp()) {
                commander.sendMessage(CommandSyntax.INVALID_PERMISSION.getSyntaxMessage());
                return;
            }

            Collection<Player> onlinePlayers = (Collection<Player>) plugin.getServer().getOnlinePlayers();

            switch (args.length) {
                case 3: {
                    String playerName = args[2];

                    /* Check if the option is "remove" */
                    if (!args[1].equalsIgnoreCase("remove")) {
                        commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                        return;
                    }

                    /* Check if the given player name exist */
                    if (onlinePlayers.parallelStream().noneMatch(onlinePlayer -> playerName.equals(onlinePlayer.getName()))) {
                        commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.RED + "No such player as '" + ChatColor.WHITE + playerName + ChatColor.RED + "'!"));
                        return;
                    }

                    final Player TARGET = plugin.getServer().getPlayer(playerName);
                    assert TARGET != null;

                    String targetCurrentRank = (String) IProfile.getProfileValueByProperty(TARGET, ProfileProperty.RANK.getPropertyName());

                    if (targetCurrentRank.equalsIgnoreCase(ServerRank.MEMBER.name())) {
                        commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "The target player already have the lowest rank [Member]!"));
                        return;
                    }

                    IRank.removePlayerRank(TARGET);

                    commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.RED + "The '"
                            + ChatColor.WHITE + targetCurrentRank.toUpperCase() + ChatColor.RED + "' rank has been removed from '"
                            + ChatColor.WHITE + TARGET.getName() + ChatColor.RED + "'!"));
                    TARGET.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.RED + "Your '" + ChatColor.WHITE
                            + targetCurrentRank.toUpperCase() + ChatColor.RED + "' rank has been removed!"));
                    return;
                }

                case 4: {
                    String playerName = args[2];
                    String rankName = args[3];

                    /* Check if the option is "set" */
                    if (!args[1].equalsIgnoreCase("set")) {
                        commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                        return;
                    }

                    /* Check if the given player name exist */
                    if (onlinePlayers.parallelStream().noneMatch(onlinePlayer -> playerName.equals(onlinePlayer.getName()))) {
                        commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.RED + "No such player as '" + ChatColor.WHITE + playerName + ChatColor.RED + "'!"));
                        return;
                    }

                    /* Check if the given rank name exist */
                    if (Arrays.stream(ServerRank.values()).noneMatch(rank -> rankName.equalsIgnoreCase(rank.name()))) {
                        commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.RED + "No such rank as '" + ChatColor.WHITE + rankName + ChatColor.RED + "'!"));
                        return;
                    }

                    final Player TARGET = plugin.getServer().getPlayer(playerName);
                    assert TARGET != null;

                    /* Check if the given rankName match the "Rank" value in the player profile */
                    if (!rankName.equalsIgnoreCase((String) IProfile.getProfileValueByProperty(TARGET, ProfileProperty.RANK.getPropertyName()))) {
                        /* Owner rank for Server Operator */
                        if (rankName.equalsIgnoreCase(ServerRank.OWNER.name()) && !TARGET.isOp()) {
                            commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "The '" + rankName.toUpperCase() + "' rank is unavailable for this player!"));
                            return;
                        }

                        IRank.setPlayerRank(rankName, TARGET);
                        commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GREEN + "Rank successfully set!"));
                        return;
                    }

                    commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.RED + "The player '" + ChatColor.WHITE + playerName + ChatColor.RED
                            + "' already have the existing rank '" + ChatColor.WHITE + rankName.toUpperCase() + ChatColor.RED + "'!"));
                    return;
                }

                default: {
                    commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                }
            }
        }

        @Override
        public List<String> tabCompletion(Player commander, String[] args) {
            final List<String> BLANK_LIST = CommandHandler.super.tabCompletion(commander, args);

            if (!commander.isOp()) return BLANK_LIST;

            final String SET_RANK = "set";
            final String REMOVE_RANK = "remove";

            switch (args.length) {
                case 2: {
                    return Arrays.stream(new String[]{SET_RANK, REMOVE_RANK}).filter(value -> value.toLowerCase()
                            .startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                }

                case 3: {
                    if (!(args[1].equalsIgnoreCase(SET_RANK) || args[1].equalsIgnoreCase(REMOVE_RANK))) return BLANK_LIST;

                    return plugin.getServer().getOnlinePlayers().parallelStream().map(Player::getName)
                            .filter(playerName -> playerName.startsWith(args[2])).collect(Collectors.toList());
                }

                case 4: {
                    if (!(args[1].equalsIgnoreCase(SET_RANK) && !args[2].isEmpty())) return BLANK_LIST;

                    return Arrays.stream(ServerRank.values()).map(ServerRank::name).filter(serverRankName -> serverRankName
                            .toLowerCase().startsWith(args[3].toLowerCase())).collect(Collectors.toList());
                }

                default: {
                    return BLANK_LIST;
                }
            }
        }
    }

    /**
     * Update rank tag that show beside the player name.
     *
     */
    public static void updatePlayerRankTag() {
        plugin.getServer().getOnlinePlayers().forEach(target -> {
            String rankName = (String) IProfile.getProfileValueByProperty(target, ProfileProperty.RANK.getPropertyName());

            if (Arrays.stream(ServerRank.values()).noneMatch(rank -> rankName.equalsIgnoreCase(rank.name()))) return;

            Objects.requireNonNull(scoreboard.getTeam(rankName.toUpperCase())).addEntry(target.getName());
            target.setScoreboard(scoreboard);
        });
    }

    /**
     * Set the player rank by given rank name and player.
     *
     * @param rankName The rank name that need to be set.
     * @param target The target player.
     */
    public static void setPlayerRank(String rankName, Player target) {
        IProfile.setProfileValueByProperty(target, ProfileProperty.RANK.getPropertyName(), rankName.toUpperCase());
        updatePlayerRankTag();
    }

    /**
     * Remove player rank by given player.
     *
     * @param target The target player.
     */
    public static void removePlayerRank(Player target) {
        IProfile.setProfileValueByProperty(target, ProfileProperty.RANK.getPropertyName(), ServerRank.MEMBER.name());
        updatePlayerRankTag();
    }

    /**
     * Send processed message.
     *
     * @param player The player who sent the message.
     * @param messages The filtered chat messages.
     */
    public static void sendMessage(Player player, String messages) {
        String rankName = (String) IProfile.getProfileValueByProperty(player, ProfileProperty.RANK.getPropertyName());
        String playerName = player.getName();

        if (Arrays.stream(ServerRank.values()).noneMatch(rank -> rankName.equalsIgnoreCase(rank.name()))) return;

        plugin.getServer().getOnlinePlayers().parallelStream().forEach(onlinePlayers -> processMessage(ServerRank
                .valueOf(rankName.toUpperCase()).getPrefix(), playerName, messages, onlinePlayers));
    }

    /**
     * Message processing using server protocol.
     *
     * @param rank The rank name.
     * @param playerName The player name.
     * @param messages The chat messages from player.
     * @param onlinePlayer Server player that are online.
     */
    private static void processMessage(String rank, String playerName, String messages, Player onlinePlayer) {
        String VERSION = plugin.getServer().getBukkitVersion().split("\\.")[0] + "." + plugin.getServer().getBukkitVersion().split("\\.")[1];

        try {
            /* Get the ChatSerializer in IChatBaseComponent */
            for (Class<?> declaredClass : IReflector.getNameSpaceClass("IChatBaseComponent").getDeclaredClasses()) {
                if (!declaredClass.getSimpleName().equalsIgnoreCase("ChatSerializer")) return;

                Object chatMessage = declaredClass.getMethod("a", String.class).invoke(null, "{\"text\":\"" + rank + ChatColor.WHITE + playerName + " > " + messageFilter(messages) + "\"}");
                Object chatType = IReflector.getNameSpaceClass("ChatMessageType").getField("CHAT").get(null);
                Object packet;

                if (VERSION.equalsIgnoreCase("1.14")) {
                    Constructor<?> constructor = IReflector.getNameSpaceClass("PacketPlayOutChat")
                            .getConstructor(IReflector.getNameSpaceClass("IChatBaseComponent"), IReflector.getNameSpaceClass("ChatMessageType"));
                    packet = constructor.newInstance(chatMessage, chatType);
                } else {
                    Constructor<?> constructor = IReflector.getNameSpaceClass("PacketPlayOutChat")
                            .getConstructor(IReflector.getNameSpaceClass("IChatBaseComponent"), IReflector.getNameSpaceClass("ChatMessageType"), UUID.class);
                    packet = constructor.newInstance(chatMessage, chatType, onlinePlayer.getUniqueId());
                }

                Object handle = onlinePlayer.getClass().getMethod("getHandle").invoke(onlinePlayer);
                Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

                playerConnection.getClass().getMethod("sendPacket", IReflector.getNameSpaceClass("Packet")).invoke(playerConnection, packet);
            }
        } catch (NoSuchFieldException | ClassNotFoundException | InvocationTargetException | IllegalAccessException |
                 NoSuchMethodException | InstantiationException err) {
            System.out.println("An error occurred while trying to process chat messages through iRank: " + err.getCause());
        }
    }

    /**
     * Filter some characters in the message to avoid parsing error.
     *
     * @param message The chat message from player.
     * @return Filtered chat message.
     */
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

    private static class EventController implements Listener {
        @EventHandler
        private void onPlayerJoin(PlayerJoinEvent e) {
            updatePlayerRankTag();
        }

        @EventHandler
        private void onPlayerChat(AsyncPlayerChatEvent e) {
            e.setCancelled(true);
            sendMessage(e.getPlayer(), e.getMessage());
        }
    }
}
