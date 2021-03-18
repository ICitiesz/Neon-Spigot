package com.islandstudio.neon.Experimental;

import com.islandstudio.neon.Stable.New.GUI.Interfaces.iWaypoints.Handler;
import com.islandstudio.neon.Stable.New.GUI.Interfaces.iWaypoints.Handler_Removal;
import com.islandstudio.neon.MainCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.plugin.Plugin;

public class Events implements Listener {
    //Commands commands_ = new Commands();
    InvisibleConfiguration invisibleConfiguration = new InvisibleConfiguration();
    Plugin plug = MainCore.getPlugin(MainCore.class);

    @EventHandler
    public void playerDamageEvent(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {

        }

        if (e.isCancelled()) {
            e.setCancelled(false);
        }

        if (e.getDamager().getName().equalsIgnoreCase("ICities")) {
            /*if (commands_beta.isEnabled) {

            }*/
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        InvisibleConfiguration configuration = new InvisibleConfiguration();
        configuration.interactDetection();
    }

    @EventHandler
    public void onBroadcastMessage(BroadcastMessageEvent e) {

    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent e) throws Exception {
        Player player = e.getEntity();
        LastDeadLocation.playerLocation().clear();
        LastDeadLocation.sendLocation(player);
        LastDeadLocation.setDeadPlayer(player);
    }

    @EventHandler
    public final void onInventoryClick(InventoryClickEvent e) {
        String invName = e.getView().getTitle();

        Handler.eventHandling(e);
        Handler_Removal.eventHandling(e);
    }



    /*@EventHandler
    public final void inventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory clickedInventory = e.getClickedInventory();
        Inventory playerInventory = player.getInventory();
        ItemStack itemStack = e.getCurrentItem();

        if (e.getView().getTitle().equalsIgnoreCase(TeleportGUI.guiName)) {
            if (clickedInventory != null) {
                e.setCancelled(true);
            }

            if (itemStack == null || !itemStack.hasItemMeta()) {
                e.setCancelled(true);
            }

            if (itemStack != null) {
                if (itemStack.getType() == Material.MAP) {
                    File[] listFiles = FolderList.getFolder_2b_3.listFiles();

                    if (listFiles != null) {
                        for (File file : listFiles) {
                            if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
                                if ((itemStack.getItemMeta()).getDisplayName().equalsIgnoreCase(ChatColor.GOLD + TeleportHandler.names.get(file.getName()))) {
                                    String fileName = file.getName();

                                    float yaw = TeleportHandler.getYaw();
                                    float pitch = TeleportHandler.getPitch();

                                    double posX = TeleportHandler.getPosX();
                                    double posY = TeleportHandler.getPosY();
                                    double posZ = TeleportHandler.getPosZ();

                                    World world = player.getWorld();

                                    Location location = new Location(world, posX + 0.5, posY, posZ + 0.5, yaw, pitch);

                                    if (player.getLocation().getWorld().getEnvironment().toString().equalsIgnoreCase(TeleportHandler.getDimension())) {
                                        player.teleport(location);
                                    }

                                    e.setCancelled(true);

                                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plug, () -> {
                                        player.closeInventory();
                                        TeleportHandler.clearMap();
                                    }, 0L);
                                }
                            }
                        }
                    }


                }
            }
        }


    }*/
}
