package com.islandstudio.neon.Experimental.Teleportation;

import com.islandstudio.neon.Stable.Old.Initialization.FolderManager.FolderList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class TeleportGUI {
    public static final String guiName = ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "--------" + ChatColor.DARK_PURPLE + "Teleportation" + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "--------";

    public static Inventory warpGUI(Player player) {
        Inventory teleportGUI = Bukkit.getServer().createInventory(null, 54, guiName);

        File[] listFiles = FolderList.getFolder_2b_3.listFiles();


        int maxItemPerPage = 45;
        int page = 0;

        if (listFiles != null) {
            ArrayList<File> itemStacks = new ArrayList<>(Arrays.asList(listFiles));


            for (int i = 0; i < maxItemPerPage; i++) {
                int itemIndex = maxItemPerPage * page + i;

                if (itemIndex >= itemStacks.size()) {
                    break;
                }

                if (itemStacks.get(itemIndex) != null) {
                    ItemStack waypoint = new ItemStack(Material.MAP, 1);
                    ItemMeta wpMeta = waypoint.getItemMeta();
                    ItemFlag[] itemFlags = {ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE};
                    ArrayList<String> details = new ArrayList<>();

                    if (TeleportHandler.getDimension().equalsIgnoreCase("NORMAL")) {
                        details.add(ChatColor.GRAY + "Dimension: " + ChatColor.GREEN + TeleportHandler.getDimension().substring(0, 1).toUpperCase() + TeleportHandler.getDimension().substring(1).toLowerCase());
                    } else if (TeleportHandler.getDimension().equalsIgnoreCase("NETHER")) {
                        details.add(ChatColor.GRAY + "Dimension: " + ChatColor.DARK_RED + TeleportHandler.getDimension().substring(0, 1).toUpperCase() + TeleportHandler.getDimension().substring(1).toLowerCase());
                    } else if (TeleportHandler.getDimension().equalsIgnoreCase("THE_END")) {
                        details.add(ChatColor.GRAY + "Dimension: " + ChatColor.DARK_PURPLE + TeleportHandler.getDimension().split("_")[0].substring(0, 1).toUpperCase() + TeleportHandler.getDimension().split("_")[0].substring(1).toLowerCase() + " " + TeleportHandler.getDimension().split("_")[1].substring(0, 1).toUpperCase() + TeleportHandler.getDimension().split("_")[1].substring(1).toLowerCase());
                    }

                    details.add(ChatColor.GRAY + "Coordinate: " + ChatColor.AQUA + TeleportHandler.getPosX().intValue() + ChatColor.GRAY + ", " + ChatColor.AQUA + TeleportHandler.getPosY().intValue() + ChatColor.GRAY + ", " + ChatColor.AQUA + TeleportHandler.getPosZ().intValue());

                    if (Objects.requireNonNull(player.getLocation().getWorld()).getEnvironment().toString().equalsIgnoreCase(TeleportHandler.getDimension())) {
                        details.add(ChatColor.GREEN + "Available!");
                    } else {
                        details.add(ChatColor.RED +"Unavailable!");
                    }

                    if (wpMeta != null) {
                        wpMeta.setDisplayName(ChatColor.GOLD + TeleportHandler.getName());

                        wpMeta.setUnbreakable(true);

                        wpMeta.addItemFlags(itemFlags);

                        wpMeta.setLore(details);
                    }
                    waypoint.setItemMeta(wpMeta);

                    teleportGUI.addItem(waypoint);
                    details.clear();
                }


            }


            for (File file : listFiles) {
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
                    String fileName = file.getName();


                }
            }
        }

        /*for (ItemStack itemStack : itemStacks) {
            items.put(itemIndex, itemStack);
        }*/

        ItemStack nextPage = new ItemStack(Material.SPECTRAL_ARROW);
        ItemStack close = new ItemStack(Material.BARRIER);
        teleportGUI.setItem(50, nextPage);
        teleportGUI.setItem(49, close);

        return teleportGUI;
    }
}
