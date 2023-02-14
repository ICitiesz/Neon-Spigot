package com.islandstudio.neon.stable.utils.iGUI;

import com.islandstudio.neon.stable.utils.INamespaceKeys;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class ButtonHighlighter extends Enchantment {
    public ButtonHighlighter(NamespacedKey key) {
        super(key);
    }

    public static class Handler {
        public static void init() {
            if (Enchantment.getByKey(INamespaceKeys.NEON_BUTTON_HIGHLIGHTER.getKey()) != null) return;

            ButtonHighlighter buttonHighlighter = new ButtonHighlighter(INamespaceKeys.NEON_BUTTON_HIGHLIGHTER.getKey());

            try {
                Field field = Enchantment.class.getDeclaredField("acceptingNew");
                field.setAccessible(true);
                field.set(null, true);

                Enchantment.registerEnchantment(buttonHighlighter);
            } catch (IllegalAccessException | NoSuchFieldException err) {
                if (err instanceof IllegalAccessException) {
                    System.out.println("Error while trying to access field with private modifier!");
                }

                if (err instanceof NoSuchFieldException) {
                    System.out.println("Error while trying to access corresponding field!");
                }
            }
        }
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return false;
    }
}
