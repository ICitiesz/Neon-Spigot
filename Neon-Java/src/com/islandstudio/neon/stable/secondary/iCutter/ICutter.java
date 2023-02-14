package com.islandstudio.neon.stable.secondary.iCutter;

import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.primary.iServerConfig.IServerConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.ArrayList;
import java.util.Collections;

public class ICutter {
    public static class Handler {
        /**
         * Initialization for iCutter.
         */
        public static void init()  {
            /* Check if the iCutter is enabled */
            if (!(Boolean) IServerConfig.getExternalServerConfigValue("iCutter")) return;

            for (WoodPlanks woodPlanks : WoodPlanks.values()) {
                ItemStack result;
                NamespacedKey namespacedKey;
                StonecuttingRecipe stonecuttingRecipe;

                if (woodPlanks.getWoodType() == null) continue;

                for (Material woodItems : getWoodItems()) {
                    result = new ItemStack(woodItems);
                    namespacedKey = result.getType().getKey();

                    if (woodPlanks.getWoodType().name().split("_")[0].equalsIgnoreCase(woodItems.name().split("_")[0])) {
                        if (woodItems.name().endsWith("SLAB")) {
                            result = new ItemStack(woodItems, 2);
                        }

                        stonecuttingRecipe = new StonecuttingRecipe(namespacedKey, result, woodPlanks.getWoodType());
                        IConstructor.getPlugin().getServer().addRecipe(stonecuttingRecipe);
                    }
                }

                Material stick = Material.getMaterial("STICK");
                Material ladder = Material.getMaterial("LADDER");
                Material bowl = Material.getMaterial("BOWL");

                if (stick != null) {
                    result = new ItemStack(stick, 2);
                    namespacedKey = new NamespacedKey(IConstructor.getPlugin(), woodPlanks.name().toLowerCase() + "_stick");
                    stonecuttingRecipe = new StonecuttingRecipe(namespacedKey, result, woodPlanks.getWoodType());
                    IConstructor.getPlugin().getServer().addRecipe(stonecuttingRecipe);
                }

                if (ladder != null) {
                    result = new ItemStack(ladder, 1);
                    namespacedKey = new NamespacedKey(IConstructor.getPlugin(), woodPlanks.name().toLowerCase() + "_ladder");
                    stonecuttingRecipe = new StonecuttingRecipe(namespacedKey, result, woodPlanks.getWoodType());
                    IConstructor.getPlugin().getServer().addRecipe(stonecuttingRecipe);
                }

                if (bowl != null) {
                    result = new ItemStack(bowl, 2);
                    namespacedKey = new NamespacedKey(IConstructor.getPlugin(), woodPlanks.name().toLowerCase() + "_bowl");
                    stonecuttingRecipe = new StonecuttingRecipe(namespacedKey, result, woodPlanks.getWoodType());
                    IConstructor.getPlugin().getServer().addRecipe(stonecuttingRecipe);
                }
            }
        }
    }

    private static ArrayList<Material> getWoodItems() {
        ArrayList<Material> woodItems = new ArrayList<>();

        for (WoodItems item : WoodItems.values()) {
            woodItems.add(item.getSlab());
            woodItems.add(item.getStairs());
            woodItems.add(item.getFence());
            woodItems.add(item.getDoor());
            woodItems.add(item.getButton());
            woodItems.add(item.getTrapDoor());
            woodItems.add(item.getFenceGate());
            woodItems.add(item.getPressurePlate());
            woodItems.add(item.getSign());
            woodItems.add(item.getBoat());
        }

        woodItems.removeAll(Collections.singleton(null));

        return woodItems;
    }
}
