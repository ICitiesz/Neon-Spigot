package com.islandstudio.neon.Deprecated.Command;

import com.islandstudio.neon.Experimental.GlowingConfiguration;
import com.islandstudio.neon.Experimental.InvisibleConfiguration;
import com.islandstudio.neon.Experimental.LastDeadLocation;
import com.islandstudio.neon.Experimental.ReflectedClass;
import com.islandstudio.neon.Stable.New.Command.SyntaxHandler;
import com.islandstudio.neon.MainCore;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;

@Deprecated
public class Commands implements Listener, CommandExecutor {
    public final String cmd_1 = "hidden";
    public final String cmd_2 = "highlight";
    public final String cmd_3 = "mute";
    public final String cmd_4 = "";
    public final String cmd_test = "test";

    public final CommandCore commandCore = new CommandCore();

    public static boolean isGlow;

    @Override
    public boolean onCommand(CommandSender sender_beta, Command cmd_beta, String label_beta, String[] args_beta) {

        if (sender_beta instanceof Player) {
            if (cmd_beta.getName().equalsIgnoreCase(cmd_test)) {
                Player player = (Player) sender_beta;

                if (player.isOp()) {

                    if (args_beta.length == 2) {
                        String text = args_beta[1];

                    }

                    try {
                        ReflectedClass.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    SyntaxHandler.sendSyntax(player, 2);
                }

                /*TeleportHandler.names.clear();
                TeleportHandler.pitches.clear();
                TeleportHandler.posXs.clear();
                TeleportHandler.posYs.clear();
                TeleportHandler.posZs.clear();
                TeleportHandler.dimensions.clear();
                TeleportHandler.yaws.clear();*/
            }

            if (cmd_beta.getName().equalsIgnoreCase(cmd_4)) {


                return true;
            }

            if (cmd_beta.getName().equalsIgnoreCase(cmd_3)) {
                Player player = (Player) sender_beta;

                if (args_beta.length == 2) {
                    String option = args_beta[0];
                    String playerName = args_beta[1];

                    if (option.equalsIgnoreCase("on")) {
                        for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                            if (playerName.equalsIgnoreCase(onlinePlayers.getName())) {
                                /*if (PlayerDataHandler.getPlugin != null) {
                                    File folder = new File(PlayerDataHandler.getPlugin.getDataFolder(), "players data");
                                    File file = new File(folder, onlinePlayers.getUniqueId() + ".sv");

                                    if (! PlayerDataHandler.getData(file).getBoolean("isMuted")) {
                                        PlayerDataHandler.setData(file).set("isMuted", true);
                                        PlayerDataHandler.setData(file);

                                        player.sendRawMessage(commands.prefix_2 + onlinePlayers.getName() + " has been muted!");
                                    } else {
                                        player.sendRawMessage(commands.prefix_2 + onlinePlayers.getName() + " already muted!");
                                    }


                                }*/
                            }
                        }
                    } else if (option.equalsIgnoreCase("off")) {

                    } else {
                        //player.sendRawMessage(commands.prefix_2 + commands.prefix_4);
                    }
                } else {
                    //player.sendRawMessage(commands.prefix_2 + commands.prefix_4);
                }

            }

            if (cmd_beta.getName().equalsIgnoreCase(cmd_1)) {
                Player player = (Player) sender_beta;

                if (player.isOp()) {
                    if (args_beta.length == 1) {
                        String option = args_beta[0];
                        EntityPlayer hider = ((CraftPlayer) player).getHandle();

                        if (option.equalsIgnoreCase("on")) {
                            player.setSilent(true);
                            player.setCollidable(false);
                            InvisibleConfiguration.isHidden = true;
                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                if (!onlinePlayers.isOp()) {
                                    onlinePlayers.hidePlayer(MainCore.getPlugin(MainCore.class), player);
                                    EntityPlayer seeker = ((CraftPlayer) onlinePlayers).getHandle();
                                    //seeker.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, hider));
                                }
                            }

                        } else if (option.equalsIgnoreCase("off")) {
                            player.setSilent(false);
                            player.setCollidable(true);
                            InvisibleConfiguration.isHidden = false;

                            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                                if (!onlinePlayers.isOp()) {
                                    onlinePlayers.showPlayer(MainCore.getPlugin(MainCore.class), player);
                                    EntityPlayer seeker = ((CraftPlayer) onlinePlayers).getHandle();
                                    //seeker.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, hider));
                                    player.sendMessage("Hidden off");
                                }
                            }
                        }

                    } else {
                        CommandCore commandCore = new CommandCore();
                        player.sendMessage(commandCore.prefix + ChatColor.RED + "Invalid Argument!!");
                    }
                } else {
                    SyntaxHandler.sendSyntax(player, 2);
                }

                return true;
            }

            if (cmd_beta.getName().equalsIgnoreCase("ldl")) {
                Player senderPlayer = (Player) sender_beta;

                Player player = LastDeadLocation.getDeadPlayer();
                HashMap<String, Object> locations = LastDeadLocation.playerLocation();

                if (locations.get("dimension") != null) {
                    float yaw = (float) locations.get("yaw");
                    float pitch = (float) locations.get("pitch");
                    double posX = ((Integer) locations.get("posX")).doubleValue();
                    double posY = ((Integer) locations.get("posY")).doubleValue();
                    double posZ = ((Integer) locations.get("posZ")).doubleValue();
                    World world = (World) locations.get("dimension");

                    player.teleport(new Location(world, posX + 0.5, posY, posZ + 0.5, yaw, pitch));
                } else {
                    player.sendRawMessage(ChatColor.YELLOW + "This is internal command, nothing has been executed!");
                }

                return true;
            }

            if (cmd_beta.getName().equalsIgnoreCase(cmd_2)) {
                Player player = (Player) sender_beta;
                Player player1 = Bukkit.getServer().getPlayer("iDefault");

                if (args_beta.length == 1) {
                    String option = args_beta[0];
                    //String name =  args_beta[1];
                    GlowingConfiguration configuration = new GlowingConfiguration();

                    /*if (option.equalsIgnoreCase("on")) {
                        isGlow = true;
                        //glow.setGlow(player1, player);
                        configuration.addGlow(player1, player);
                    } else if (option.equalsIgnoreCase("off")) {
                        isGlow = false;
                        //glow.setGlow(player1, player);
                        configuration.addGlow(player1, player);
                    }*/
                }
            }
        }


        return false;
    }
}
