package com.islandstudio.neon.Stable.New.GUI.Interfaces.iWaypoints;

import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.New.GUI.Initialization.GUIConstructor;
import com.islandstudio.neon.Stable.New.GUI.Initialization.GUIUtility;
import com.islandstudio.neon.Stable.New.GUI.Initialization.GUIUtilityHandler;
import com.islandstudio.neon.Stable.New.Utilities.ServerCfgHandler;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Handler extends Builder {
    private final Player player = guiUtility.getOwner();
    private final Plugin plugin = MainCore.getPlugin(MainCore.class);

    public Handler(GUIUtility guiUtility) {
        super(guiUtility);
    }

    @Override
    public String getName() {
        return ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "--------" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "iWaypoints" + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "---------";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void setItems() {
        addButtons();

        ArrayList<String> waypointNames = IWaypoints.getWaypointNames();
        ArrayList<String> details = new ArrayList<>();

        for (int i = 0; i < super.max; i++) {
            itemIndex = super.max * pageIndex + i;

            if (waypointNames != null) {
                if (itemIndex >= waypointNames.size()) {
                    break;
                }

                if (waypointNames.get(itemIndex) != null) {
                    ItemStack waypoint = new ItemStack(Material.BEACON);
                    ItemMeta waypointMeta = waypoint.getItemMeta();

                    String waypointName = waypointNames.get(itemIndex);

                    try {
                        int posX = (int) (long) IWaypoints.getWaypointData().get(waypointName).get("Position-X");
                        int posY = (int) (long) IWaypoints.getWaypointData().get(waypointName).get("Position-Y");
                        int posZ = (int) (long) IWaypoints.getWaypointData().get(waypointName).get("Position-Z");

                        details.add(ChatColor.GRAY + "Coordinate: " + ChatColor.AQUA + posX + ChatColor.GRAY + ", " + ChatColor.AQUA + posY + ChatColor.GRAY + ", " + ChatColor.AQUA + posZ);
                        details.add(ChatColor.GRAY + "Dimension: " + IWaypoints.getDimension(waypointName));

                        if (ServerCfgHandler.getValue().get("iWaypoints-Cross_Dimension").equals(false)) {
                            details.add(ChatColor.GRAY + "Status: " + IWaypoints.getAvailability(player, waypointName));
                        }

                        if (waypointMeta != null) {
                            waypointMeta.setDisplayName(ChatColor.GOLD + IWaypoints.getWaypointNames().get(itemIndex));
                            waypointMeta.setLore(details);
                        }

                        waypoint.setItemMeta(waypointMeta);

                        inventory.addItem(waypoint);

                        details.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public static boolean isClicked = false;

    @Override
    public void clickHandler(InventoryClickEvent e) {
        ArrayList<String> waypointNames = IWaypoints.getWaypointNames();

        float yaw;
        float pitch;
        double posX;
        double posY;
        double posZ;

        assert waypointNames != null;

        ItemStack currentItem = e.getCurrentItem();
        assert currentItem != null;
        ItemMeta currentItemMeta = currentItem.getItemMeta();

        try {
            for (String waypointName: waypointNames) {
                String waypointNameGold = ChatColor.GOLD + waypointName;

                if (currentItem.getType().equals(Material.BEACON) && currentItemMeta != null && currentItemMeta.getDisplayName().equalsIgnoreCase(waypointNameGold)) {
                    yaw = (float) (double) IWaypoints.getWaypointData().get(waypointName).get("Yaw");
                    pitch = (float) (double) IWaypoints.getWaypointData().get(waypointName).get("Pitch");
                    posX = (int) (long) IWaypoints.getWaypointData().get(waypointName).get("Position-X");
                    posY = (int) (long) IWaypoints.getWaypointData().get(waypointName).get("Position-Y");
                    posZ = (int) (long) IWaypoints.getWaypointData().get(waypointName).get("Position-Z");

                    World world = player.getWorld();

                    Location location_1 = new Location(world, posX + 0.5, posY, posZ + 0.5, yaw, pitch);
                    Location location_2 = IWaypoints.locationDeserialize((String) IWaypoints.getWaypointData().get(waypointName).get("Raw_Location"));

                    if (ServerCfgHandler.getValue().get("iWaypoints-Cross_Dimension").equals(false)) {
                        if (player.getLocation().getWorld() != null) {
                            if (player.getLocation().getWorld().getEnvironment().toString().equalsIgnoreCase((String) IWaypoints.getWaypointData().get(waypointName).get("Dimension"))) {
                                IWaypoints.teleport(player, location_1, waypointNameGold, (int) posX, (int) posY, (int) posZ);
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "iWaypoints-[Cross Dimension] has been restricted!");
                            }
                        }
                    } else {
                        location_2.setX(posX + 0.5);
                        location_2.setY(posY + 0.5);
                        location_2.setZ(posZ + 0.5);
                        location_2.setYaw(yaw);
                        location_2.setPitch(pitch);

                        IWaypoints.teleport(player, location_2, waypointNameGold, (int) posX, (int) posY, (int) posZ);
                    }

                    e.setCancelled(true);

                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, player::closeInventory, 0L);

                    return;
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        switch (currentItem.getType()) {
            case SPECTRAL_ARROW: {
                if (currentItemMeta != null && currentItemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Previous")) {
                    isClicked = true;

                    if (pageIndex == 0) {
                        player.sendMessage(ChatColor.YELLOW + "Already on the first page!");
                    } else {
                        pageIndex = pageIndex - 1;
                        super.open();
                    }
                } else if (currentItemMeta != null && currentItemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Next")) {
                    isClicked = true;

                    if (!((itemIndex + 1) >= waypointNames.size())) {
                        pageIndex = pageIndex + 1;
                        super.open();
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Already on the last page!");
                    }
                }
                break;
            }

            case BARRIER: {
                if (currentItemMeta != null && currentItemMeta.getDisplayName().equalsIgnoreCase(ChatColor.RED + "Close")) {
                    player.closeInventory();
                }
                break;
            }
        }
    }

    public static void setEventHandler(InventoryClickEvent e) {
        String invName = e.getView().getTitle();

        if (invName.equalsIgnoreCase(new Handler(GUIUtilityHandler.getGUIUtility((Player) e.getWhoClicked())).getName())) {
            Inventory inventory = e.getClickedInventory();

            if (inventory != null) {
                InventoryHolder inventoryHolder = inventory.getHolder();

                if (inventory.equals(e.getWhoClicked().getInventory())) {
                    e.setCancelled(true);
                }

                if (inventoryHolder instanceof GUIConstructor) {
                    e.setCancelled(true);

                    if (e.getCurrentItem() == null) {
                        return;
                    }

                    GUIConstructor guiConstructor = (GUIConstructor) inventoryHolder;
                    guiConstructor.clickHandler(e);
                }
            }
        }
    }
}
