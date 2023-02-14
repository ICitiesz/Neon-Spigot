package com.islandstudio.neon.stable.secondary.iGameMode;

import com.islandstudio.neon.stable.secondary.iCommand.CommandHandler;
import com.islandstudio.neon.stable.secondary.iCommand.CommandSyntax;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IGameMode {
    public static class Handler implements CommandHandler {

        @Override
        public void setCommandHandler(Player commander, String[] args) {
            if (!commander.isOp()) {
                commander.sendMessage(CommandSyntax.INVALID_PERMISSION.getSyntaxMessage());
                return;
            }

            if (args.length == 1) {
                commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GREEN + "Your gamemode is currently set to: "
                        + ChatColor.GOLD + StringUtils.capitalize(commander.getGameMode().name().toLowerCase())));
                return;
            }

            if (args.length != 2) {
                commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                return;
            }

            final String value = args[1];
            final String gameModeChangeMsg = ChatColor.GREEN + "Your gamemode has been updated to ";

            switch (value) {
                case "0": {
                    setGameMode(commander, value);

                    commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(
                            gameModeChangeMsg + ChatColor.GOLD + "Survival" + ChatColor.GREEN + "!"));
                    break;
                }

                case "1": {
                    setGameMode(commander, value);

                    commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(
                            gameModeChangeMsg + ChatColor.GOLD + "Creative" + ChatColor.GREEN + "!"));
                    break;
                }

                case "2": {
                    setGameMode(commander, value);

                    commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(
                            gameModeChangeMsg + ChatColor.GOLD + "Adventure" + ChatColor.GREEN + "!"));
                    break;
                }

                case "3": {
                    setGameMode(commander, value);

                    commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(
                            gameModeChangeMsg + ChatColor.GOLD + "Spectator" + ChatColor.GREEN + "!"));
                    break;
                }

                default: {
                    commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                }
            }
        }

        @Override
        public List<String> tabCompletion(Player commander, String[] args) {
            final ArrayList<String> BLANK_LIST = new ArrayList<>();

            if (!commander.isOp()) return BLANK_LIST;

            if (args.length != 2) return BLANK_LIST;

            return Arrays.stream(new String[]{"0", "1", "2", "3"}).filter(value -> value.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }
    }

    public static void setGameMode(Player player, String gameModeValue) {
        GameMode gameMode = null;

        switch (gameModeValue) {
            case "0": {
                gameMode = GameMode.SURVIVAL;
                break;
            }

            case "1": {
                gameMode = GameMode.CREATIVE;
                break;
            }

            case "2": {
                gameMode = GameMode.ADVENTURE;
                break;
            }

            case "3": {
                gameMode = GameMode.SPECTATOR;
                break;
            }
        }

        if (gameMode == null) return;
        player.setGameMode(gameMode);
    }
}
