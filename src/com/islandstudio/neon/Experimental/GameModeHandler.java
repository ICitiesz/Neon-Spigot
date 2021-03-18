package com.islandstudio.neon.Experimental;

import com.islandstudio.neon.Stable.New.Command.CommandCore;
import com.islandstudio.neon.Stable.New.Command.SyntaxHandler;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeHandler {
    private static final boolean MODERATION = CommandCore.moderation;

    public static void setSurvival(Player player) {
        if (player.isOp()) {
            if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
                player.setGameMode(GameMode.SURVIVAL);
                if (MODERATION) {
                    player.setAllowFlight(true);

                    if (!player.isFlying()) {
                        player.setFlying(true);
                    }
                }

                player.sendRawMessage(ChatColor.GREEN + "Your gamemode has been updated to survival mode!");
            } else {
                player.sendRawMessage(ChatColor.YELLOW + "You are in survival mode now!");
            }
        } else {
            SyntaxHandler.sendSyntax(player, 2);
        }


    }

    public static void setCreative(Player player) {
        if (player.isOp()) {
            if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                player.setGameMode(GameMode.CREATIVE);
                player.sendRawMessage(ChatColor.GREEN + "Your gamemode has been updated to creative mode!");
            } else {
                player.sendRawMessage(ChatColor.YELLOW + "You are in creative mode now!");
            }
        } else {
            SyntaxHandler.sendSyntax(player, 2);
        }
    }

    public static void setAdventure(Player player) {
        if (player.isOp()) {
            if (!player.getGameMode().equals(GameMode.ADVENTURE)) {
                player.setGameMode(GameMode.ADVENTURE);
                player.sendRawMessage(ChatColor.GREEN + "Your gamemode has been updated to adventure mode!");

                if (MODERATION) {
                    player.setAllowFlight(true);

                    if (!player.isFlying()) {
                        player.setFlying(true);
                    }
                }

            } else {
                player.sendRawMessage(ChatColor.YELLOW + "You are in adventure mode now!");
            }
        } else {
            SyntaxHandler.sendSyntax(player, 2);
        }


    }

    public static void setSpectator(Player player) {
        if (player.isOp()) {
            if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendRawMessage(ChatColor.GREEN + "Your gamemode has been updated to spectator mode!");
            } else {
                player.sendRawMessage(ChatColor.YELLOW + "You are in spectator mode now!");
            }
        } else {
            SyntaxHandler.sendSyntax(player, 2);
        }


    }
}
