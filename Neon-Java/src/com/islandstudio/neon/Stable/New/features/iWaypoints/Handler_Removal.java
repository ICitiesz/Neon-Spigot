package com.islandstudio.neon.Stable.New.features.iWaypoints;

import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.New.features.GUI.Initialization.GUIConstructor;
import com.islandstudio.neon.Stable.New.features.GUI.Initialization.GUIUtility;
import com.islandstudio.neon.Stable.New.features.GUI.Initialization.GUIUtilityHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Handler_Removal extends Builder_Removal{
    public static boolean isClicked = false;
    public static final Map<String, ArrayList<String>> removalListSeparator = new TreeMap<>();

    private final Player player = guiUtility.getOwner();
    private final Plugin plugin = MainCore.getPlugin(MainCore.class);

    public Handler_Removal(GUIUtility guiUtility) {
        super(guiUtility);
    }

    @Override
    public String getName() {
        return ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "----" + ChatColor.DARK_PURPLE + ChatColor.BOLD
                + "iWaypoints " + ChatColor.RED + ChatColor.BOLD + "Removal" + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "----";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void setItems() {
        addButtons();

        ArrayList<String> waypointNames = IWaypoints.getWaypointNames(player);
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
                        Location location = IWaypoints.locationDeserialize(player, waypointName);

                        int posX = location.getBlockX();
                        int posY = location.getBlockY();
                        int posZ = location.getBlockZ();

                        details.add(ChatColor.GRAY + "Coordinate: " + ChatColor.AQUA + posX + ChatColor.GRAY + ", " + ChatColor.AQUA + posY + ChatColor.GRAY + ", " + ChatColor.AQUA + posZ);
                        details.add(ChatColor.GRAY + "Dimension: " + IWaypoints.getDimension(player, waypointName));

                        if (waypointMeta != null) {
                            waypointMeta.setDisplayName(ChatColor.GOLD + waypointName);
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

    @Override
    public void clickHandler(InventoryClickEvent e) {
        ArrayList<String> removalList;
        ArrayList<String> waypointNames = IWaypoints.getWaypointNames(player);

        if (removalListSeparator.containsKey(player.getUniqueId().toString())) {
            removalList = removalListSeparator.get(player.getUniqueId().toString());
        } else {
            removalList = new ArrayList<>();
        }

        assert waypointNames != null;

        ItemStack currentItem = e.getCurrentItem();
        assert currentItem != null;
        ItemMeta currentItemMeta = currentItem.getItemMeta();

        for (String waypointName : waypointNames) {
            String waypointNameGold = ChatColor.GOLD + waypointName;

            if (currentItem.getType().equals(Material.BEACON) && currentItemMeta != null && currentItemMeta.getDisplayName().equalsIgnoreCase(waypointNameGold)) {
                if (!currentItem.getEnchantments().containsKey(Enchantment.VANISHING_CURSE)) {
                    currentItem.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);

                    ItemMeta itemMetaNew = currentItem.getItemMeta();
                    assert itemMetaNew != null;
                    List<String> lore = itemMetaNew.getLore();

                    assert lore != null;
                    lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Selected!");
                    itemMetaNew.setLore(lore);
                    itemMetaNew.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    currentItem.setItemMeta(itemMetaNew);

                    removalList.add(itemMetaNew.getDisplayName().substring(2));
                } else {
                    currentItem.removeEnchantment(Enchantment.VANISHING_CURSE);

                    ItemMeta itemMetaNew = currentItem.getItemMeta();
                    assert itemMetaNew != null;
                    List<String> lore = itemMetaNew.getLore();

                    assert lore != null;
                    lore.remove(ChatColor.GREEN + "" + ChatColor.BOLD + "Selected!");
                    itemMetaNew.setLore(lore);
                    itemMetaNew.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    currentItem.setItemMeta(itemMetaNew);

                    removalList.remove(itemMetaNew.getDisplayName().substring(2));
                }

                if (!removalListSeparator.containsKey(player.getUniqueId().toString())) {
                    removalListSeparator.put(player.getUniqueId().toString(), removalList);
                }
            }
        }

        switch (currentItem.getType()) {
            case SPECTRAL_ARROW: {
                if (currentItemMeta != null && currentItemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Previous")) {
                    isClicked = true;

                    if (pageIndex == 0) {
                        return;
                    } else {
                        pageIndex = pageIndex - 1;
                        super.open();

                        ItemStack[] invItems = super.getInventory().getContents();

                        for (ItemStack item : invItems) {
                            if (item != null) {
                                if (item.getType().equals(Material.BEACON)) {
                                    ItemMeta itemMeta = item.getItemMeta();

                                    for (String name : removalList) {
                                        assert itemMeta != null;
                                        if (itemMeta.getDisplayName().substring(2).equalsIgnoreCase(name)) {
                                            item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);

                                            ItemMeta itemMetaNew = item.getItemMeta();

                                            List<String> lore = itemMetaNew.getLore();

                                            assert lore != null;
                                            lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Selected!");
                                            itemMetaNew.setLore(lore);
                                            itemMetaNew.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                                            item.setItemMeta(itemMetaNew);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (currentItemMeta != null && currentItemMeta.getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Next")) {
                    isClicked = true;

                    if (!((itemIndex + 1) >= waypointNames.size())) {
                        pageIndex = pageIndex + 1;
                        super.open();

                        ItemStack[] invItems = super.getInventory().getContents();

                        for (ItemStack item : invItems) {
                            if (item != null) {
                                if (item.getType().equals(Material.BEACON)) {
                                    ItemMeta itemMeta = item.getItemMeta();

                                    for (String name : removalList) {
                                        assert itemMeta != null;
                                        if (itemMeta.getDisplayName().substring(2).equalsIgnoreCase(name)) {
                                            item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);

                                            ItemMeta itemMetaNew = item.getItemMeta();

                                            List<String> lore = itemMetaNew.getLore();

                                            assert lore != null;
                                            lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Selected!");
                                            itemMetaNew.setLore(lore);
                                            itemMetaNew.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                                            item.setItemMeta(itemMetaNew);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        return;
                    }
                }
                break;
            }

            case BARRIER: {
                if (currentItemMeta != null && currentItemMeta.getDisplayName().equalsIgnoreCase(ChatColor.RED + "Close")) {
                    player.closeInventory();

                    waypointNames.clear();
                }
                break;
            }

            case BLAZE_POWDER: {
                if (currentItemMeta != null && currentItemMeta.getDisplayName().equalsIgnoreCase(ChatColor.RED + "Remove")) {
                    if (removalList.isEmpty()) {
                        player.sendMessage(ChatColor.YELLOW + "Please select waypoint(s) to delete!");
                    } else {
                        player.closeInventory();

                        plugin.getServer().broadcastMessage(ChatColor.RED + "The waypoint(s): ");

                        for (String name : removalList) {
                            try {
                                for (String playerUUID : removalListSeparator.keySet()) {
                                    if (!playerUUID.equalsIgnoreCase(player.getUniqueId().toString())) {
                                        removalListSeparator.get(playerUUID).remove(name);
                                    }
                                }

                                for (String playerUUID : IWaypoints.waypointData.keySet()) {
                                    if (!playerUUID.equalsIgnoreCase(player.getUniqueId().toString())) {
                                        IWaypoints.waypointData.get(playerUUID).remove(name);
                                    }
                                }

                                IWaypoints.remove(name);

                                plugin.getServer().broadcastMessage(ChatColor.GRAY + "'" + ChatColor.GOLD + name + ChatColor.GRAY + "'");
                            } catch (Exception err) {
                                err.printStackTrace();
                            }
                        }

                        plugin.getServer().broadcastMessage(ChatColor.RED + "has been removed by " + ChatColor.WHITE + player.getName() + ChatColor.RED + " !");

                        removalListSeparator.remove(player.getUniqueId().toString());
                    }

                }
                break;
            }
        }
    }

    public static void setEventHandler(InventoryClickEvent e) {
        String invName = e.getView().getTitle();

        if (invName.equalsIgnoreCase(new Handler_Removal(GUIUtilityHandler.getGUIUtility((Player) e.getWhoClicked())).getName())) {
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
