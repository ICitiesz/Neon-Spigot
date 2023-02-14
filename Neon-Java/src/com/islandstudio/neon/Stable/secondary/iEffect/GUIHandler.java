package com.islandstudio.neon.stable.secondary.iEffect;

import com.islandstudio.neon.stable.secondary.iCommand.CommandSyntax;
import com.islandstudio.neon.stable.utils.iGUI.IGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class GUIHandler extends GUIBuilder {

    public GUIHandler(IGUI iGUI) {
        super(iGUI);
    }

    @Override
    public String getGUIName() {
        return ChatColor.YELLOW + ChatColor.BOLD.toString() + "-------- iEffect --------";
    }

    @Override
    public int getGUISlots() {
        return 9;
    }

    @Override
    public void setGUIButtons() {
        addGUIButtons();
    }

    @Override
    public void setGUIClickHandler(InventoryClickEvent e) {
        final ItemStack CLICKED_BUTTON = e.getCurrentItem();

        if (CLICKED_BUTTON == null) return;

        final ItemMeta CLICKED_BUTTON_META = CLICKED_BUTTON.getItemMeta();

        if (CLICKED_BUTTON_META == null) return;

        final PersistentDataContainer CLICKED_BUTTON_DATA_CONTAINER = CLICKED_BUTTON_META.getPersistentDataContainer();
        final Player PLAYER = (Player) e.getWhoClicked();
        final int EFFECT_DURATION = Integer.MAX_VALUE;
        final String[] HASTE_EFFECTS = new String[]{HASTE_1, HASTE_2, HASTE_3};

        switch (CLICKED_BUTTON.getType()) {
            case GOLDEN_PICKAXE: {
                if (!CLICKED_BUTTON_DATA_CONTAINER.has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                for (String hasteEffect : HASTE_EFFECTS) {
                    if (!CLICKED_BUTTON_META.getDisplayName().equalsIgnoreCase(hasteEffect)) continue;

                    if (CLICKED_BUTTON_META.getDisplayName().equalsIgnoreCase(HASTE_1)) {
                        PLAYER.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, EFFECT_DURATION, 150, true, true));
                    }

                    if (CLICKED_BUTTON_META.getDisplayName().equalsIgnoreCase(HASTE_2)) {
                        PLAYER.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, EFFECT_DURATION, 300, true, true));
                    }

                    if (CLICKED_BUTTON_META.getDisplayName().equalsIgnoreCase(HASTE_3)) {
                        PLAYER.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, EFFECT_DURATION, 600, true, true));
                    }

                    PLAYER.closeInventory();
                    PLAYER.sendMessage(CommandSyntax.Handler.createSyntaxMessage(hasteEffect + ChatColor.GREEN + " has been applied!"));
                }

                break;
            }

            case MILK_BUCKET: {
                if (!CLICKED_BUTTON_DATA_CONTAINER.has(BUTTON_ID_KEY, PersistentDataType.STRING)) return;

                if (!PLAYER.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                    PLAYER.closeInventory();
                    PLAYER.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "Effect may removed or not exist!"));
                    return;
                }

                if (!(Objects.requireNonNull(PLAYER.getPotionEffect(PotionEffectType.FAST_DIGGING)).getAmplifier() == 150
                        || Objects.requireNonNull(PLAYER.getPotionEffect(PotionEffectType.FAST_DIGGING)).getAmplifier() == 300
                        || Objects.requireNonNull(PLAYER.getPotionEffect(PotionEffectType.FAST_DIGGING)).getAmplifier() == 600)) {
                    PLAYER.closeInventory();
                    PLAYER.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.RED + "Invalid effect supplier! No effect removed!"));
                    return;
                }

                String hasteLevel = "";

                if (Objects.requireNonNull(PLAYER.getPotionEffect(PotionEffectType.FAST_DIGGING)).getAmplifier() == 150) {
                    hasteLevel = HASTE_1;
                }

                if (Objects.requireNonNull(PLAYER.getPotionEffect(PotionEffectType.FAST_DIGGING)).getAmplifier() == 300) {
                    hasteLevel = HASTE_2;
                }
                if (Objects.requireNonNull(PLAYER.getPotionEffect(PotionEffectType.FAST_DIGGING)).getAmplifier() == 600) {
                    hasteLevel = HASTE_3;
                }

                PLAYER.removePotionEffect(PotionEffectType.FAST_DIGGING);
                PLAYER.closeInventory();
                PLAYER.sendMessage(CommandSyntax.Handler.createSyntaxMessage(hasteLevel + ChatColor.YELLOW + " has been removed!"));
                break;
            }

            default: {
                break;
            }
        }
    }
}
