package com.islandstudio.neon.stable.secondary.iWaypoints;

import com.islandstudio.neon.stable.primary.iServerConfig.IServerConfig;
import com.islandstudio.neon.stable.utils.iGUI.IGUI;
import com.islandstudio.neon.stable.utils.iGUI.IGUIConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class GUIHandlerMain extends GUIBuilderMain {
    protected static boolean isNavigating = false;

    private final Player player = iGUI.getGUIOwner();
    private static final Object isCrossDimension = IServerConfig.getExternalServerConfigValue("iWaypoints-Cross_Dimension");

    public GUIHandlerMain(IGUI iGUI) {
        super(iGUI);
    }

    @Override
    public String getGUIName() {
        return ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "--------" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "iWaypoints" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "---------";
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

            /* Getting all waypoint details */
            final String waypointName = ChatColor.GOLD + iWaypoints.getWaypointName();
            final int waypointBlockX = iWaypoints.getWaypointBlockX();
            final int waypointBlockY = iWaypoints.getWaypointBlockY();
            final int waypointBlockZ = iWaypoints.getWaypointBlockZ();
            final String waypointDimension = iWaypoints.getWaypointDimension();

            /* Adding waypoint details such as coordinate and dimension to the body. */
            waypointDetails.add(ChatColor.GRAY + "Coordinate: " + ChatColor.AQUA + waypointBlockX
                    + ChatColor.GRAY + ", " + ChatColor.AQUA + waypointBlockY + ChatColor.GRAY + ", "
                    + ChatColor.AQUA + waypointBlockZ);

            waypointDetails.add(ChatColor.GRAY + "Dimension: " + waypointDimension);

            if (!(boolean) isCrossDimension) {
                if (!iWaypoints.canTeleportOverDimension(player)) {
                    waypointDetails.add(ChatColor.YELLOW + "Teleport over dimension has been disabled!");
                }
            }

            if (waypointMeta == null) return;

            /* Setting display name */
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
        Map<String, JSONObject> waypointData = IWaypoints.Handler.getWaypointDataFromSession(player);

        final String ERR_MSG = "An error occurred while trying to built iWaypoints Main GUI: ";

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null) throw new NullPointerException(ERR_MSG + "Clicked Item is Null!");

        ItemMeta clickedItemMeta = clickedItem.getItemMeta();
        if (clickedItemMeta == null) throw new NullPointerException(ERR_MSG + "Clicked Item Meta is Null!");

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

                    Location waypointLocation = iWaypoints.getWaypointLocation();

                    waypointLocation.setX(iWaypoints.getWaypointBlockX() + 0.5);
                    waypointLocation.setZ(iWaypoints.getWaypointBlockZ() + 0.5);

                    if (player.getLocation().getWorld() == null) return;

                    if (!(boolean) isCrossDimension) {
                        if (!player.getLocation().getWorld().getEnvironment().equals(Objects.requireNonNull(waypointLocation.getWorld()).getEnvironment())) {
                            e.setCancelled(true);
                            return;
                        }
                    }

                    IWaypoints.teleportToWaypoint(player, waypointName, waypointLocation);

                    e.setCancelled(true);
                });

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
                }

                if (clickedItemDisplayName.equals(nextButtonDisplayName)) {
                    if ((itemIndex + 1) >= waypointData.size()) return;

                    isNavigating = true;

                    pageIndex++;
                    super.openGUI();
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
        }
    }

    public static void setEventHandler(InventoryClickEvent e) {
        final String GUI_NAME = e.getView().getTitle();

        if (!GUI_NAME.equals(new GUIHandlerMain(IGUI.Handler.getIGUI((Player) e.getWhoClicked())).getGUIName())) return;

        Inventory gui = e.getClickedInventory();

        if (gui == null) return;

        InventoryHolder inventoryHolder = gui.getHolder();

        if (gui.equals(e.getWhoClicked().getInventory())) e.setCancelled(true);

        if (!(inventoryHolder instanceof IGUIConstructor)) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;

        ((IGUIConstructor) inventoryHolder).setGUIClickHandler(e);
    }
}
