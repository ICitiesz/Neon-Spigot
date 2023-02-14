package com.islandstudio.neon.stable.secondary.iWaypoints;

import com.islandstudio.neon.stable.secondary.iCommand.CommandSyntax;
import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.utils.iGUI.IGUI;
import com.islandstudio.neon.stable.utils.iGUI.IGUIConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.json.simple.JSONObject;

import java.util.*;

public class GUIHandlerRemoval extends GUIBuilderRemoval {
    public static boolean isNavigating = false; // Used to avoid remove player from gui session even though player just navigating the GUI.
    public static final Map<UUID, Set<String>> pendingRemovalContainer = new HashMap<>();

    private final Player player = iGUI.getGUIOwner();
    private static final String SELECTED_TEXT = ChatColor.GREEN + "" + ChatColor.BOLD + "Selected!";

    public GUIHandlerRemoval(IGUI IGUI) {
        super(IGUI);
    }

    @Override
    public String getGUIName() {
        return ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "----" + ChatColor.DARK_PURPLE + ChatColor.BOLD
                + "iWaypoints " + ChatColor.RED + ChatColor.BOLD + "Removal" + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "----";
    }

    @Override
    public int getGUISlots() {
        return 54;
    }

    @Override
    public void setGUIButtons() {
        ArrayList<Map.Entry<String, JSONObject>> waypointData = new ArrayList<>(IWaypoints.Handler.getWaypointDataFromSession(player).entrySet());
        
        maxPage = (int) Math.ceil((double) waypointData.size() / (double) maxItemPerPage);

        addGUIButtons(); /* Add navigation and control buttons */

        for (int i = 0; i < super.maxItemPerPage; i++) {
            itemIndex = super.maxItemPerPage * pageIndex + i;

            if (itemIndex >= waypointData.size()) break;

            ItemStack waypoint = new ItemStack(Material.BEACON);
            ItemMeta waypointMeta = waypoint.getItemMeta();

            ArrayList<String> waypointDetails = new ArrayList<>();
            IWaypoints iWaypoints = new IWaypoints(waypointData.get(itemIndex));

            String waypointName = ChatColor.GOLD + iWaypoints.getWaypointName();
            int waypointBlockX = iWaypoints.getWaypointBlockX();
            int waypointBlockY = iWaypoints.getWaypointBlockY();
            int waypointBlockZ = iWaypoints.getWaypointBlockZ();
            String waypointDimension = iWaypoints.getWaypointDimension();

            /* Adding waypoint details such as coordinate and dimension to the body. */
            waypointDetails.add(ChatColor.GRAY + "Coordinate: " + ChatColor.AQUA + waypointBlockX
                    + ChatColor.GRAY + ", " + ChatColor.AQUA + waypointBlockY + ChatColor.GRAY + ", "
                    + ChatColor.AQUA + waypointBlockZ);

            waypointDetails.add(ChatColor.GRAY + "Dimension: " + waypointDimension);

            if (waypointMeta == null) return;

            /* Setting display name and body content */
            waypointMeta.setDisplayName(waypointName);
            waypointMeta.setLore(waypointDetails);

            /* Setting button id key */
            waypointMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

            waypoint.setItemMeta(waypointMeta);

            gui.addItem(waypoint);
        }
    }

    @Override
    public void setGUIClickHandler(InventoryClickEvent e) {
        Set<String> playerSelectedWaypoints;
        Map<String, JSONObject> waypointData = IWaypoints.Handler.getWaypointDataFromSession(player);

        /* Check if pendingRemovalContainer contains a set of player selected waypoints.
        * If true, continue using it, else create a new set.
        * */
        if (pendingRemovalContainer.containsKey(player.getUniqueId())) {
            playerSelectedWaypoints = pendingRemovalContainer.get(player.getUniqueId());
        } else {
            playerSelectedWaypoints = new HashSet<>();
        }

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null) throw new NullPointerException("An error occurred while trying to built iWaypoints Removal GUI: Clicked Item is Null!");

        ItemMeta clickedItemMeta = clickedItem.getItemMeta();
        if (clickedItemMeta == null) throw new NullPointerException("An error occurred while trying to built iWaypoints Removal GUI: Clicked Item Meta is Null!");

        PersistentDataContainer persistentDataContainer = clickedItemMeta.getPersistentDataContainer();

        switch (clickedItem.getType()) {
            /* Waypoint button */
            case BEACON: {
                if (!persistentDataContainer.has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                final String clickedItemDisplayName = clickedItemMeta.getDisplayName().substring(2);

                waypointData.entrySet().forEach(waypoint -> {
                    IWaypoints iWaypoints = new IWaypoints(waypoint);
                    final String waypointName = iWaypoints.getWaypointName();

                    if (!clickedItemDisplayName.equals(waypointName)) return;

                    ArrayList<String> bodyContent = (ArrayList<String>) clickedItemMeta.getLore();

                    if (bodyContent == null) throw new NullPointerException("An error occurred while trying to built iWaypoints Removal GUI: Item body content is Null!");

                    /* Select & deselect waypoint operation */
                    if (!clickedItemMeta.hasEnchant(BUTTON_HIGHLIGHTER)) {
                        clickedItemMeta.addEnchant(BUTTON_HIGHLIGHTER, 0, true);
                        bodyContent.add(SELECTED_TEXT);

                        playerSelectedWaypoints.add(waypointName);
                    } else {
                        clickedItemMeta.removeEnchant(BUTTON_HIGHLIGHTER);
                        bodyContent.remove(SELECTED_TEXT);

                        playerSelectedWaypoints.remove(waypointName);
                    }

                    clickedItemMeta.setLore(bodyContent);
                    clickedItem.setItemMeta(clickedItemMeta);
                });

                final ItemStack[] guiContent = super.getInventory().getContents();

                updateTotalSelected(guiContent, playerSelectedWaypoints);

                /* Add the set of player selected waypoints to the pendingRemovalContainer if it doesn't exist */
                if (!pendingRemovalContainer.containsKey(player.getUniqueId())) {
                    pendingRemovalContainer.put(player.getUniqueId(), playerSelectedWaypoints);
                }

                break;
            }

            /* Navigation button */
            case SPECTRAL_ARROW: {
                if (!persistentDataContainer.has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                final String clickedItemDisplayName = clickedItemMeta.getDisplayName();

                if (clickedItemDisplayName.equals(previousButtonDisplayName)) {
                    if (pageIndex == 0) return;

                    isNavigating = true;

                    pageIndex--;
                    super.openGUI();

                    final ItemStack[] guiContent = super.getInventory().getContents();

                    updateTotalSelected(guiContent, playerSelectedWaypoints);
                    updateSelectedWaypoint(guiContent, playerSelectedWaypoints, player);
                }

                if (clickedItemDisplayName.equals(nextButtonDisplayName)) {
                    if ((itemIndex + 1) >= waypointData.size()) return;

                    isNavigating = true;

                    pageIndex++;
                    super.openGUI();

                    final ItemStack[] guiContent = super.getInventory().getContents();

                    updateTotalSelected(guiContent, playerSelectedWaypoints);
                    updateSelectedWaypoint(guiContent, playerSelectedWaypoints, player);
                }

                break;
            }

            /* Close GUI button */
            case BARRIER: {
                if (!persistentDataContainer.has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                if (!clickedItemMeta.getDisplayName().equals(closeButtonDisplayName)) return;

                player.closeInventory();
                break;
            }

            /* Clear selection button */
            case NAME_TAG: {
                if (!persistentDataContainer.has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                if (!clickedItemMeta.getDisplayName().equals(clearSelectionButtonDisplayName)) return;

                if (playerSelectedWaypoints.isEmpty()) return;

                playerSelectedWaypoints.clear();

                final ItemStack[] guiContent = super.gui.getContents();
                
                updateTotalSelected(guiContent, playerSelectedWaypoints);
                updateSelectedWaypoint(guiContent, playerSelectedWaypoints, player);
                break;
            }

            /* Remove waypoint button */
            case BLAZE_POWDER: {
                if (!persistentDataContainer.has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                if (!clickedItemMeta.getDisplayName().equals(removeButtonDisplayName)) return;

                if (playerSelectedWaypoints.isEmpty()) return;

                ArrayList<String> tempStringContainer = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder();

                /* Update the waypoint data to the latest by removing the selected waypoint(s) from
                * the other player's gui session and pending removal
                */
                playerSelectedWaypoints.forEach(waypoint -> {
                    pendingRemovalContainer.keySet().forEach(playerUUID -> {
                        if (playerUUID.equals(player.getUniqueId())) return;

                        pendingRemovalContainer.get(playerUUID).remove(waypoint);
                    });

                    IWaypoints.Handler.getGUISession().forEach((playerUUID, sessionWaypointData) -> {
                        if (playerUUID.equals(player.getUniqueId())) return;

                        IWaypoints.Handler.getWaypointDataFromSession(player).remove(waypoint);
                    });

                    /* Temporary store removed waypoints for later announcement */
                    tempStringContainer.add(ChatColor.GRAY + "'" + ChatColor.GOLD + waypoint
                        + ChatColor.GRAY + "'");
                    tempStringContainer.add(ChatColor.RED + ", ");

                    IWaypoints.Handler.removeWaypoint(waypoint);
                });

                player.closeInventory();

                tempStringContainer.forEach(string -> {
                    /* Remove the comma [,] from the string if the last piece of string contains it */
                    if (tempStringContainer.get(tempStringContainer.size() - 1).equals(ChatColor.RED + ", ")) {
                        tempStringContainer.set(tempStringContainer.size() - 1, "");
                    }

                    stringBuilder.append(string);
                });


                IConstructor.getPlugin().getServer().broadcastMessage(CommandSyntax.Handler.createSyntaxMessage(
                        ChatColor.WHITE + player.getName() + ChatColor.RED + " removed these waypoint(s) from iWaypoints: "
                        + stringBuilder
                ));

                break;
            }
        }
    }

    public static void setEventHandler(InventoryClickEvent e) {
        final String GUI_NAME = e.getView().getTitle();

        if (!GUI_NAME.equals(new GUIHandlerRemoval(IGUI.Handler.getIGUI((Player) e.getWhoClicked())).getGUIName())) return;

        Inventory gui = e.getClickedInventory();

        if (gui == null) return;

        InventoryHolder inventoryHolder = gui.getHolder();

        if (gui.equals(e.getWhoClicked().getInventory())) e.setCancelled(true);

        if (!(inventoryHolder instanceof IGUIConstructor)) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;

        ((IGUIConstructor) inventoryHolder).setGUIClickHandler(e);

    }

    /**
     * Update total selected waypoints to the 'Remove' button.
     *
     * @param guiContent iWaypoint GUI content.
     * @param playerSelectedWaypoints The player selected waypoints.
     */
    private static void updateTotalSelected(ItemStack[] guiContent, Set<String> playerSelectedWaypoints) {
        final String ERR_MSG = "An error occurred while trying to built iWaypoints Removal GUI: ";

        Arrays.stream(guiContent).forEach(item -> {
            if (item == null) return;

            if (item.getType().equals(Material.NAME_TAG)) {
                ItemMeta itemMeta = item.getItemMeta();

                if (itemMeta == null) throw new NullPointerException(ERR_MSG + "Item Meta is Null!");

                if (!itemMeta.getDisplayName().equals(clearSelectionButtonDisplayName)) return;

                if (!itemMeta.getPersistentDataContainer().has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                ArrayList<String> buttonLore = (ArrayList<String>) itemMeta.getLore();

                if (buttonLore == null) throw new NullPointerException(ERR_MSG + "Button lore is Null!");

                buttonLore.set(0, ChatColor.GRAY + "Total selected: " + ChatColor.GREEN + playerSelectedWaypoints.size());

                itemMeta.setLore(buttonLore);
                item.setItemMeta(itemMeta);
            }

            if (item.getType().equals(Material.BLAZE_POWDER)) {
                ItemMeta itemMeta = item.getItemMeta();

                if (itemMeta == null) throw new NullPointerException(ERR_MSG + "Item Meta is Null!");

                if (!itemMeta.getDisplayName().equals(removeButtonDisplayName)) return;

                if (!itemMeta.getPersistentDataContainer().has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                ArrayList<String> buttonLore = (ArrayList<String>) itemMeta.getLore();

                if (buttonLore == null) throw new NullPointerException(ERR_MSG + "Button lore is Null!");

                if (playerSelectedWaypoints.isEmpty()) {
                    buttonLore.set(0, removeButtonLore.get(0));
                } else {
                    buttonLore.set(0, ChatColor.RED + "They will be gone FOREVER!");
                }

                itemMeta.setLore(buttonLore);
                item.setItemMeta(itemMeta);
            }
        });
    }

    /**
     * Re-highlight player selected waypoint from page to page.
     *
     * @param guiContent iWaypoint GUI content.
     * @param playerSelectedWaypoints The player selected waypoints.
     * @param player The player who using the GUI.
     */
    private static void updateSelectedWaypoint(ItemStack[] guiContent, Set<String> playerSelectedWaypoints, Player player) {
        final boolean isPlayerSelectedWaypointsEmpty = playerSelectedWaypoints.isEmpty();
        final String ERR_MSG = "An error occurred while trying to built iWaypoints Removal GUI: ";

        Arrays.stream(guiContent).forEach(item -> {
            if (item == null) return;

            if (!item.getType().equals(Material.BEACON)) return;

            ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta == null) throw new NullPointerException(ERR_MSG + "Item Meta is Null!");

            final String itemDisplayName = itemMeta.getDisplayName().substring(2);

            IWaypoints.Handler.getWaypointDataFromSession(player).keySet().forEach(waypointName -> {
                if (!itemDisplayName.equals(waypointName)) return;

                if (!itemMeta.getPersistentDataContainer().has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                ArrayList<String> bodyContent = (ArrayList<String>) itemMeta.getLore();

                if (bodyContent == null) throw new NullPointerException(ERR_MSG + "Item body content is Null!");

                if (isPlayerSelectedWaypointsEmpty) {
                    if (!itemMeta.hasEnchant(BUTTON_HIGHLIGHTER)) return;

                    itemMeta.removeEnchant(BUTTON_HIGHLIGHTER);
                    bodyContent.remove(SELECTED_TEXT);

                    itemMeta.setLore(bodyContent);
                    item.setItemMeta(itemMeta);

                    return;
                }

                /* Below are the operation where re-highlight the selected waypoint from page to page */
                playerSelectedWaypoints.forEach(selectedWaypoint -> {
                    if (!itemDisplayName.equals(selectedWaypoint)) return;

                    if (!itemMeta.hasEnchant(BUTTON_HIGHLIGHTER)) {
                        itemMeta.addEnchant(BUTTON_HIGHLIGHTER, 0, true);
                        bodyContent.add(SELECTED_TEXT);
                    } else {
                        itemMeta.removeEnchant(BUTTON_HIGHLIGHTER);
                        bodyContent.remove(SELECTED_TEXT);
                    }

                    itemMeta.setLore(bodyContent);
                    item.setItemMeta(itemMeta);
                });
            });
        });
    }
}
