package com.islandstudio.neon.stable.secondary.iSmelter;

import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.primary.iServerConfig.IServerConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ISmelter {
    /* Input[Output]:
    *   Sand/Red Sand[Glass], Cobblestone[Stone], Sandstone[Smooth Sandstone], Red Sandstone[Smooth Red Sandstone],
    *   Stone[Smooth Stone], Block of Quartz[Smooth Quartz], Clay ball[Brick], Netherrack[Nether Brick],
    *   Nether Bricks[Cracked Nether Bricks], Clay[Terracotta], Stone Bricks[Cracked Stone Bricks],
    *   Dyed Terracotta[Glazed Terracotta], Cactus[Green Dye], Charcoal[Log, Stripped Log, Stripped Wood],
    *   Chorus Fruit[Popped Chorus Fruit], Wet Sponge[Sponge], Sea Pickle[Lime Dye]
    *
    *   Exp is same as original
    *   Cooking time is original / 2 [200 ticks (10 sec) / 2 = 100 ticks (5 sec)]
    *
    */

    public static class Handler {
        /**
         * Initialization for iSmelter
         *
         */
        public static void init() {
            final Object isISmelterEnabled = IServerConfig.getExternalServerConfigValue("iSmelter");

            if (!(Boolean) isISmelterEnabled) return;

            for (String key : getSmeltable().keySet()) {
                FurnaceRecipe furnaceRecipe = null;

                Material inputMaterial = getSmeltable().get(key).get(0);
                Material outputMaterial = getSmeltable().get(key).get(1);

                ItemStack output = new ItemStack(outputMaterial);
                NamespacedKey namespacedKey = output.getType().getKey();

                List<Recipe> recipes = IConstructor.getPlugin().getServer().getRecipesFor(new ItemStack(outputMaterial));

                float exp;
                int cookingTime;

                for (Recipe recipe : recipes) {
                    if (recipe.toString().contains("FurnaceRecipe")) {
                        furnaceRecipe = (FurnaceRecipe) recipe;
                    }
                }

                if (key.startsWith("SAND") || key.startsWith("RED_SAND")) {
                    namespacedKey = new NamespacedKey(IConstructor.getPlugin(), inputMaterial.name().toLowerCase() + "_glass");
                }

                if (key.endsWith("CHARCOAL")) {
                    namespacedKey = new NamespacedKey(IConstructor.getPlugin(), key.toLowerCase());
                }

                if (furnaceRecipe == null) return;

                exp = furnaceRecipe.getExperience();
                cookingTime = (furnaceRecipe.getCookingTime() / 2);

                BlastingRecipe blastingRecipe = new BlastingRecipe(namespacedKey, output, inputMaterial, exp, cookingTime);
                IConstructor.getPlugin().getServer().addRecipe(blastingRecipe);
            }
        }
    }

    private static HashMap<String, ArrayList<Material>> getSmeltable() {
        HashMap<String, ArrayList<Material>> combinedSmeltable = new HashMap<>();

        for (Smeltable smeltable : Smeltable.values()) {
            ArrayList<Material> smeltableItems = new ArrayList<>();

            if (smeltable.getInput() != null && smeltable.getOutput() != null) {
                smeltableItems.add(smeltable.getInput());
                smeltableItems.add(smeltable.getOutput());
                combinedSmeltable.put(smeltable.name(), smeltableItems);
            }
        }

        return combinedSmeltable;
    }
}
