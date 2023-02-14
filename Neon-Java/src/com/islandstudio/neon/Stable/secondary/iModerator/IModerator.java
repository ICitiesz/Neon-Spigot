package com.islandstudio.neon.stable.secondary.iModerator;

import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.secondary.iCommand.CommandHandler;
import com.islandstudio.neon.stable.secondary.iCommand.CommandSyntax;
import com.islandstudio.neon.stable.secondary.iProfile.IProfile;
import com.islandstudio.neon.stable.secondary.iProfile.ProfileProperty;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IModerator {
    public static class Handler implements CommandHandler {
        /**
         * Initialization for iModerator.
         */
        public static void init() {
            IConstructor.enableEvent(new EventController());
        }

        @Override
        public void setCommandHandler(Player commander, String[] args) {
            if (!commander.isOp()) {
                commander.sendMessage(CommandSyntax.INVALID_PERMISSION.getSyntaxMessage());
                return;
            }

            if (args.length == 1) {
                boolean isModerating = (boolean) IProfile.getProfileValueByProperty(commander, ProfileProperty.IS_MODERATING.getPropertyName());

                String isModeratingText = ChatColor.RED + "disabled!";

                if (isModerating) {
                    isModeratingText = ChatColor.GREEN + "enabled!";
                }

                commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GREEN + "Your moderator status is currently "
                        + isModeratingText));
                return;
            }

            if (args.length != 2) {
                commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                return;
            }

            if (args[1].equalsIgnoreCase("on")) {
                IProfile.setProfileValueByProperty(commander, ProfileProperty.IS_MODERATING.getPropertyName(), true);
                enableModerator(commander);

                commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GREEN + "Moderation enabled!"));
                return;
            }

            if (args[1].equalsIgnoreCase("off")) {
                IProfile.setProfileValueByProperty(commander, ProfileProperty.IS_MODERATING.getPropertyName(), false);
                disableModerator(commander, null);

                commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.RED + "Moderation disabled!"));
            }
        }

        @Override
        public List<String> tabCompletion(Player commander, String[] args) {
            if (!commander.isOp()) return CommandHandler.super.tabCompletion(commander, args);

            if (args.length != 2) return CommandHandler.super.tabCompletion(commander, args);

            return Arrays.stream(new String[]{"on", "off"}).filter(value -> value.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }
    }

    /**
     * Enable moderator mode for the player.
     *
     * @param player The player to enable moderator mode.
     */
    private static void enableModerator(Player player) {
        switch (player.getGameMode()) {
            case CREATIVE:
            case SPECTATOR:
            case SURVIVAL:
            case ADVENTURE: {
                player.setInvulnerable(true);
                player.setCollidable(false);

                IConstructor.getPlugin().getServer().getScheduler().runTaskLater(IConstructor.getPlugin(), () -> {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                }, 1);
                break;
            }
        }
    }


    /**
     * Disable moderator mode for the player.
     *
     * @param player The player to disable moderator mode.
     */
    private static void disableModerator(Player player, GameMode newGameMode) {
        if (newGameMode == null) {
            switch (player.getGameMode()) {
                case CREATIVE:
                case SPECTATOR:{
                    player.setInvulnerable(false);
                    player.setCollidable(true);
                    break;
                }

                case SURVIVAL:
                case ADVENTURE: {
                    player.setInvulnerable(false);
                    player.setCollidable(true);

                    IConstructor.getPlugin().getServer().getScheduler().runTaskLater(IConstructor.getPlugin(), () -> {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                    }, 1);
                    break;
                }
            }

            return;
        }

        switch (newGameMode) {
            case CREATIVE:
            case SPECTATOR:{
                player.setInvulnerable(false);
                player.setCollidable(true);

                IConstructor.getPlugin().getServer().getScheduler().runTaskLater(IConstructor.getPlugin(), () -> {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                }, 1);
                break;
            }

            case SURVIVAL:
            case ADVENTURE: {
                player.setInvulnerable(false);
                player.setCollidable(true);

                IConstructor.getPlugin().getServer().getScheduler().runTaskLater(IConstructor.getPlugin(), () -> {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                }, 1);
                break;
            }
        }
    }

    /**
     * Check if the player is moderating.
     *
     * @param player The player to check.
     * @return True if the player is moderating, false otherwise.
     */
    private static boolean isModerating(Player player) {
        return (boolean) IProfile.getProfileValueByProperty(player, ProfileProperty.IS_MODERATING.getPropertyName());
    }

    private static class EventController implements Listener {
        @EventHandler
        private void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
            Player player = e.getPlayer();

            if (!player.isOp()) return;

            if (isModerating(player)) {
                enableModerator(player);
                return;
            }

            disableModerator(player, e.getNewGameMode());
        }

        @EventHandler
        private void onPlayerJoinServer(PlayerJoinEvent e) {
            Player player = e.getPlayer();

            if (!player.isOp()) return;

            if (isModerating(player)) {
                enableModerator(player);
                return;
            }

            disableModerator(player, null);
        }
    }
}
