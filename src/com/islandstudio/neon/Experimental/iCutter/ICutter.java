package com.islandstudio.neon.Experimental.iCutter;

import com.islandstudio.neon.Experimental.iExperimental.IExperimental;
import com.islandstudio.neon.MainCore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class ICutter {
    private static final Plugin plugin = MainCore.getPlugin(MainCore.class);

    public static void init() throws IOException, ParseException {

        if (((JSONObject) ((JSONArray) IExperimental.getClient().get("iCutter")).get(0)).get("is_enabled").equals(false)) return;

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
                    plugin.getServer().addRecipe(stonecuttingRecipe);
                }
            }

            Material stick = Material.getMaterial("STICK");
            Material ladder = Material.getMaterial("LADDER");
            Material bowl = Material.getMaterial("BOWL");

            if (stick != null) {
                result = new ItemStack(stick, 2);
                namespacedKey = new NamespacedKey(plugin, woodPlanks.name().toLowerCase() + "_stick");
                stonecuttingRecipe = new StonecuttingRecipe(namespacedKey, result, woodPlanks.getWoodType());
                plugin.getServer().addRecipe(stonecuttingRecipe);
            }

            if (ladder != null) {
                result = new ItemStack(ladder, 1);
                namespacedKey = new NamespacedKey(plugin, woodPlanks.name().toLowerCase() + "_ladder");
                stonecuttingRecipe = new StonecuttingRecipe(namespacedKey, result, woodPlanks.getWoodType());
                plugin.getServer().addRecipe(stonecuttingRecipe);
            }

            if (bowl != null) {
                result = new ItemStack(bowl, 2);
                namespacedKey = new NamespacedKey(plugin, woodPlanks.name().toLowerCase() + "_bowl");
                stonecuttingRecipe = new StonecuttingRecipe(namespacedKey, result, woodPlanks.getWoodType());
                plugin.getServer().addRecipe(stonecuttingRecipe);
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
