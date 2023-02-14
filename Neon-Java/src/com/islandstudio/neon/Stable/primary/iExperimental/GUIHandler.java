package com.islandstudio.neon.stable.primary.iExperimental;

import com.islandstudio.neon.stable.secondary.iCommand.CommandSyntax;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class GUIHandler extends GUIBuilder {
    private final ArrayList<Map.Entry<String, JSONObject>> internalExperimentalData = new ArrayList<>(IExperimental.Handler.getLoadedInternalExperimental().entrySet());

    protected static boolean isNavigating = false;
    private final Player player = iGUI.getGUIOwner();

    public GUIHandler(IGUI IGUI) {
        super(IGUI);
    }

    @Override
    public String getGUIName() {
        return ChatColor.YELLOW + "" + ChatColor.MAGIC + "-------" + ChatColor.GOLD + ChatColor.BOLD + "iExperimental" + ChatColor.YELLOW + ChatColor.MAGIC + "-------";
    }

    @Override
    public int getGUISlots() {
        return 54;
    }

    @Override
    public void setGUIButtons() {
        ArrayList<Map.Entry<String, JSONObject>> modifiableExperimentalData = new ArrayList<>(IExperimental.Handler.getModifiableExperimentalFromSession(player).entrySet());

        maxPage = (int) Math.ceil((double) internalExperimentalData.size() / (double) maxItemPerPage);

        addGUIButtons(); /* Add navigation and control buttons */

        if (internalExperimentalData.isEmpty()) return;

        for (int i = 0; i < super.maxItemPerPage; i++) {
            itemIndex = super.maxItemPerPage * pageIndex + i;

            if (itemIndex >= internalExperimentalData.size()) break;

            ItemStack experimentalFeature = new ItemStack(Material.BIRCH_SIGN);
            ItemMeta experimentMeta = experimentalFeature.getItemMeta();

            ArrayList<String> experimentalFeatureDetail = new ArrayList<>();

            IExperimental internalExperimental = new IExperimental(internalExperimentalData.get(itemIndex));
            IExperimental modifiableExperimental = new IExperimental(modifiableExperimentalData.get(itemIndex));

            /* Getting all experimental feature detail */
            final String experimentalFeatureName = internalExperimental.getExperimentalFeatureName();
            final boolean isEnabled = modifiableExperimental.isEnabled();
            final String description = internalExperimental.getDescription();
            final String conflict = internalExperimental.getConflict();

            if (experimentMeta == null) return;

            /* Adding experimental feature detail as above to the body. */
            if (isEnabled) {
                experimentalFeatureDetail.add(ChatColor.GRAY + "Status: " + ChatColor.GREEN + "Enabled!");
                experimentMeta.addEnchant(BUTTON_HIGHLIGHTER, 0, true);
            }

            if (!isEnabled) {
                experimentalFeatureDetail.add(ChatColor.GRAY + "Status: " + ChatColor.RED + "Disabled!");
                experimentMeta.removeEnchant(BUTTON_HIGHLIGHTER);
            }

            experimentalFeatureDetail.add("");
            experimentalFeatureDetail.add(ChatColor.GRAY + "Description: ");

            descriptionTrimmer(description).forEach(trimmedDescription -> experimentalFeatureDetail
                    .add(ChatColor.YELLOW + trimmedDescription));
            experimentalFeatureDetail.add("");

            if (conflict.equalsIgnoreCase("none")) {
                experimentalFeatureDetail.add(ChatColor.GRAY + "Conflict: " + ChatColor.GREEN + "NONE");
            } else {
                experimentalFeatureDetail.add(ChatColor.GRAY + "Conflict: " + ChatColor.RED + conflict);
            }

            /* Setting display name and body content */
            experimentMeta.setDisplayName(ChatColor.GOLD + experimentalFeatureName);
            experimentMeta.setLore(experimentalFeatureDetail);

            /* Setting button id key */
            experimentMeta.getPersistentDataContainer().set(BUTTON_ID_KEY, PersistentDataType.STRING, BUTTON_ID_KEY.toString());

            experimentalFeature.setItemMeta(experimentMeta);
            gui.addItem(experimentalFeature);
        }
    }

    @Override
    public void setGUIClickHandler(InventoryClickEvent e) {
        final String ERR_MSG = "An error occurred while trying to built iExperimental GUI: ";

        Map<String, JSONObject> experimentalFeature = IExperimental.Handler.getModifiableExperimentalFromSession(player);

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null) throw new NullPointerException(ERR_MSG + "Clicked Item is Null!");

        ItemMeta clickedItemMeta = clickedItem.getItemMeta();
        if (clickedItemMeta == null) throw new NullPointerException(ERR_MSG + "Clicked Item Meta is Null!");

        PersistentDataContainer persistentDataContainer = clickedItemMeta.getPersistentDataContainer();

        switch (clickedItem.getType()) {
            /* Experimental feature button */
            case BIRCH_SIGN: {
                if (!persistentDataContainer.has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                final String clickedItemDisplayName = clickedItemMeta.getDisplayName().substring(2);
                final String statusEnabled = ChatColor.GRAY + "Status: " + ChatColor.GREEN + "Enabled!";
                final String statusDisabled = ChatColor.GRAY + "Status: " + ChatColor.RED + "Disabled!";

                internalExperimentalData.forEach(experimental -> {
                    IExperimental internalExperimental = new IExperimental(experimental);

                    final String experimentalName = internalExperimental.getExperimentalFeatureName();

                    if (!clickedItemDisplayName.equals(experimentalName)) return;

                    ArrayList<String> bodyContent = (ArrayList<String>) clickedItemMeta.getLore();

                    if (bodyContent == null) throw new NullPointerException("An error occurred while trying to built iExperimental GUI: Item body content is Null!");

                    /* Toggle experimental feature operation */
                    if (!clickedItemMeta.hasEnchant(BUTTON_HIGHLIGHTER)) {
                        clickedItemMeta.addEnchant(BUTTON_HIGHLIGHTER, 0, true);

                        if (bodyContent.contains(statusDisabled)) {
                            bodyContent.set(bodyContent.indexOf(statusDisabled), statusEnabled);
                        }

                        experimentalFeature.get(experimentalName).replace("is_enabled", true);
                    } else {
                        clickedItemMeta.removeEnchant(BUTTON_HIGHLIGHTER);

                        if (bodyContent.contains(statusEnabled)) {
                            bodyContent.set(bodyContent.indexOf(statusEnabled), statusDisabled);
                        }

                        experimentalFeature.get(experimentalName).replace("is_enabled", false);
                    }

                    clickedItemMeta.setLore(bodyContent);
                    clickedItem.setItemMeta(clickedItemMeta);
                });
                break;
            }

            /* Apply button */
            case LEVER: {
                if (!persistentDataContainer.has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                if (!clickedItemMeta.getDisplayName().equals(APPLY_BUTTON_DISPLAY_NAME)) return;

                Map<String, JSONObject> modifiedExperimental = IExperimental.Handler.getModifiableExperimentalFromSession(player);

                if (IExperimental.Handler.getShowExperimentalDemo()) {
                    IExperimental.Handler.setModifiableExperimental(modifiedExperimental);

                    player.closeInventory();
                    player.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "This is a demo version of iExperimental GUI, changes were made will only save into memory!"));
                    return;
                }

                IExperimental.saveToggleStatus(modifiedExperimental);
                IExperimental.Handler.setModifiableExperimental(modifiedExperimental);

                player.closeInventory();
                player.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "Please reload the server to apply the effect."));
                break;
            }

            /* Navigation button */
            case SPECTRAL_ARROW: {
                if (!persistentDataContainer.has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                final String clickedItemDisplayName = clickedItemMeta.getDisplayName();

                if (clickedItemDisplayName.equals(PREVIOUS_BUTTON_DISPLAY_NAME)) {
                    if (pageIndex == 0) return;

                    isNavigating = true;

                    pageIndex--;
                    super.openGUI();
                }

                if (clickedItemDisplayName.equals(NEXT_BUTTON_DISPLAY_NAME)) {
                    if ((itemIndex + 1) >= internalExperimentalData.size()) return;

                    isNavigating = true;

                    pageIndex++;
                    super.openGUI();
                }

                break;
            }

            /* Close GUI button */
            case BARRIER: {
                if (!persistentDataContainer.has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                if (!clickedItemMeta.getDisplayName().equals(CLOSE_BUTTON_DISPLAY_NAME)) return;

                player.closeInventory();
                break;
            }
        }
    }

    protected static void setEventHandler(InventoryClickEvent e) {
        final String GUI_NAME = e.getView().getTitle();

        if (!GUI_NAME.equals(new GUIHandler(IGUI.Handler.getIGUI((Player) e.getWhoClicked())).getGUIName())) return;

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
     * Trim down description for better view in GUI.
     *
     * @param description The original untrimmed description.
     * @return The trimmed description.
     */
    private static ArrayList<String> descriptionTrimmer(String description) {
        ArrayList<String> splicedDescription = Arrays.stream(description.split(" ")).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> tempStore = new ArrayList<>();
        ArrayList<String> trimmedDescription = new ArrayList<>();

        for (String word : splicedDescription) {
            if (tempStore.size() == 7) {
                trimmedDescription.add(String.join(" ", tempStore));
                tempStore = new ArrayList<>();
            }

            tempStore.add(word);

            if ((splicedDescription.size() - splicedDescription.indexOf(word)) == 1) {
                trimmedDescription.add(String.join(" ", tempStore));
            }
        }

        return trimmedDescription;
    }
}
