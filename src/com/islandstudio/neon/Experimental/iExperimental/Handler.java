package com.islandstudio.neon.Experimental.iExperimental;

import com.islandstudio.neon.Stable.New.features.GUI.Initialization.GUIConstructor;
import com.islandstudio.neon.Stable.New.features.GUI.Initialization.GUIUtility;
import com.islandstudio.neon.Stable.New.features.GUI.Initialization.GUIUtilityHandler;
import com.islandstudio.neon.Stable.New.features.GUI.Initialization.GlowingItemEffect;
import com.islandstudio.neon.Stable.New.Utilities.INamespaceKeys;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class Handler extends Builder {
    private final JSONObject sourceFeatures = IExperimental.getSource();
    private final JSONObject clientFeatures = IExperimental.getClient();
    private final List<String> featureName = (List<String>) sourceFeatures.keySet().stream().sorted().collect(Collectors.toList());

    private final Player player = guiUtility.getOwner();

    public Handler(GUIUtility guiUtility) throws IOException, ParseException {
        super(guiUtility);
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "" + ChatColor.MAGIC + "-------" + ChatColor.GOLD + ChatColor.BOLD + "iExperimental" + ChatColor.YELLOW + ChatColor.MAGIC + "-------";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void setItems() {
        addButtons();

        if (sourceFeatures == null || sourceFeatures.size() == 0) return;

        for (int i = 0; i < super.max; i++) {
            ArrayList<String> detail = new ArrayList<>();
            itemIndex = super.max * pageIndex + i;

            if (itemIndex >= sourceFeatures.size()) break;
            if (featureName.get(itemIndex) == null) return;

            ItemStack experiment = new ItemStack(Material.BIRCH_SIGN);

            JSONArray jsonArray_1 = (JSONArray) sourceFeatures.get(featureName.get(itemIndex));
            JSONObject jsonObject_1 = (JSONObject) jsonArray_1.get(0);

            JSONArray jsonArray_2 = (JSONArray) clientFeatures.get(featureName.get(itemIndex));
            JSONObject jsonObject_2 = (JSONObject) jsonArray_2.get(0);

            if ((boolean) jsonObject_2.get("is_enabled")) {
                detail.add(ChatColor.GRAY + "Status: " + ChatColor.GREEN + "Enabled" + "!");
                experiment.addUnsafeEnchantment(new GlowingItemEffect(INamespaceKeys.NEON_BUTTON_GLOW.getKey()), 0);
            } else {
                detail.add(ChatColor.GRAY + "Status: " + ChatColor.RED + "Disabled" + "!");
            }

            detail.add("");

            for (Object key : jsonObject_1.keySet()) {
                if (key.equals("description")) {
                    ArrayList<String> description = new ArrayList<>(Arrays.asList(((String) jsonObject_1.get(key)).split(" ")));
                    ArrayList<Collection<String>> modifiedDescription = new ArrayList<>();
                    ArrayList<String> splicedWords = new ArrayList<>();

                    for (String word : description) {
                        if (splicedWords.size() == 7) {
                            modifiedDescription.add(splicedWords);
                            splicedWords = new ArrayList<>();
                        }

                        splicedWords.add(word);

                        if ((description.size() - description.indexOf(word)) == 1) {
                            modifiedDescription.add(splicedWords);
                        }
                    }

                    detail.add(ChatColor.GRAY + WordUtils.capitalize((String) key) + ": ");

                    for (Collection<String> collection : modifiedDescription) {
                        detail.add(ChatColor.GREEN + String.join(" ", collection));
                    }
                }

                if (key.equals("conflict")) {
                    detail.add("");

                    if (((String) jsonObject_1.get(key)).equalsIgnoreCase("none")) {
                        detail.add(ChatColor.GRAY + WordUtils.capitalize((String) key) + ": " + ChatColor.YELLOW + ((String) jsonObject_1.get(key)).toUpperCase());
                    } else {
                        detail.add(ChatColor.GRAY + WordUtils.capitalize((String) key) + ": " + ChatColor.RED + jsonObject_1.get(key));
                    }
                }
            }

            ItemMeta experimentMeta = experiment.getItemMeta();

            if (experimentMeta == null) return;

            experimentMeta.setDisplayName(ChatColor.GOLD + featureName.get(itemIndex));
            experimentMeta.setLore(detail);

            experimentMeta.getPersistentDataContainer().set(INamespaceKeys.NEON_BUTTON.getKey(), PersistentDataType.STRING, INamespaceKeys.NEON_BUTTON.getKey().toString());

            experiment.setItemMeta(experimentMeta);
            inventory.addItem(experiment);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void clickHandler(InventoryClickEvent e) {
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null) return;

        ItemMeta clickedItemMeta = clickedItem.getItemMeta();
        GlowingItemEffect glowingItemEffect = new GlowingItemEffect(INamespaceKeys.NEON_BUTTON_GLOW.getKey());

        if (clickedItemMeta == null) return;

        switch (clickedItem.getType()) {
            case BIRCH_SIGN: {
                String clickedItemName = clickedItemMeta.getDisplayName();

                if (sourceFeatures.keySet().stream().anyMatch(featureName -> clickedItemName.equalsIgnoreCase(ChatColor.GOLD + (String) featureName))) {
                    List<String> lore = clickedItemMeta.getLore();

                    if (lore == null) return;

                    String status_1 = ChatColor.GRAY + "Status: " + ChatColor.GREEN + "Enabled!";
                    String status_2 = ChatColor.GRAY + "Status: " + ChatColor.RED + "Disabled!";

                    JSONArray jsonArray = (JSONArray) clientFeatures.get(clickedItemName.substring(2));
                    JSONObject jsonObject = (JSONObject) jsonArray.get(0);


                    lore.forEach(status -> {
                        if (status.contains(status_1)) {
                            jsonObject.replace("is_enabled", false);
                            lore.set(lore.indexOf(status), status_2);
                            clickedItemMeta.removeEnchant(glowingItemEffect);
                        } else if (status.contains(status_2)) {
                            jsonObject.replace("is_enabled", true);
                            lore.set(lore.indexOf(status), status_1);
                            clickedItemMeta.addEnchant(glowingItemEffect, 0, true);
                        }
                    });

                    clickedItemMeta.setLore(lore);
                    clickedItem.setItemMeta(clickedItemMeta);
                }
                break;
            }

            case LEVER: {
                try {
                    IExperimental.save(clientFeatures);
                } catch (IOException err) {
                    err.printStackTrace();
                }

                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Please reload the server to apply the effect.");
                break;
            }

            case SPECTRAL_ARROW: {
                break;
            }

            case BARRIER: {
                if (!Objects.requireNonNull(clickedItemMeta.getPersistentDataContainer().get(INamespaceKeys.NEON_BUTTON.getKey(),
                        PersistentDataType.STRING)).equalsIgnoreCase(INamespaceKeys.NEON_BUTTON.getKey().toString())
                        && !clickedItemMeta.getDisplayName().equalsIgnoreCase(ChatColor.RED + "Close")) return;
                player.closeInventory();
                break;
            }
        }
    }

    public static void setEventHandler(InventoryClickEvent e) throws IOException, ParseException {
        if (!e.getView().getTitle().equalsIgnoreCase(new Handler(GUIUtilityHandler.getGUIUtility((Player) e.getWhoClicked())).getName())) return;

        Inventory inventory = e.getClickedInventory();

        if (inventory == null) return;

        InventoryHolder inventoryHolder = inventory.getHolder();

        if (inventory.equals(e.getWhoClicked().getInventory())) e.setCancelled(true);

        if (inventoryHolder instanceof GUIConstructor) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;

            GUIConstructor guiConstructor = (GUIConstructor) inventoryHolder;
            guiConstructor.clickHandler(e);
        }
    }
}
