package com.islandstudio.neon.stable.secondary.iRegen;

import com.islandstudio.neon.stable.secondary.iCommand.CommandHandler;
import com.islandstudio.neon.stable.secondary.iCommand.CommandSyntax;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class IRegen {
    public static class Handler implements CommandHandler {

        @Override
        public void setCommandHandler(Player commander, String[] args) {
            if (!commander.isOp()) {
                commander.sendMessage(CommandSyntax.INVALID_PERMISSION.getSyntaxMessage());
                return;
            }

            switch (args.length) {
                case 1: {
                    regenerateHealth(commander);
                    regenerateFood(commander);

                    commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GREEN
                            + "Both health and food saturation has been regenerated!"));
                    return;
                }

                case 2: {
                    if (args[1].equalsIgnoreCase("health")) {
                       regenerateHealth(commander);

                       commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GREEN
                               + "Health has been regenerated!"));
                       return;
                    }

                    if (args[1].equalsIgnoreCase("food")) {
                        regenerateFood(commander);

                        commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GREEN
                                + "Food saturation has been regenerated!"));
                        return;
                    }
                }

                default: {
                    commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                }
            }
        }

        @Override
        public List<String> tabCompletion(Player commander, String[] args) {
            if (args.length != 2) return CommandHandler.super.tabCompletion(commander, args);

            return Arrays.stream(new String[]{"health", "food"}).filter(value -> value.toLowerCase()
                    .startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }
    }

    /**
     * Regenerate player health.
     *
     * @param player The player.
     */
    private static void regenerateHealth(Player player) {
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
    }

    /**
     * Regenerate player food and food saturation.
     *
     * @param player The player.
     */
    private static void regenerateFood(Player player) {
        player.setFoodLevel(20);
        player.setSaturation(20);
    }
}
