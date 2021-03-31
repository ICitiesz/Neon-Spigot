package com.islandstudio.neon.Experimental;

import org.bukkit.event.Listener;

public class Events implements Listener {

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
