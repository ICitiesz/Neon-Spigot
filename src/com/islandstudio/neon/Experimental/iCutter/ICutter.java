package com.islandstudio.neon.Experimental.iCutter;

import com.islandstudio.neon.MainCore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.TreeMap;

public class ICutter {
    private static final Plugin plugin = MainCore.getPlugin(MainCore.class);

    public static void init() {
        String[] woodType = {"OAK", "SPRUCE", "BIRCH", "JUNGLE", "ACACIA", "DARK_OAK", "WARPED", "CRIMSON"};

        Map<String, Material> inputs = new TreeMap<>();

        for (Material material : Material.values()) {
            String materialName = material.name();

            for (String woodName : woodType) {
                if (materialName.equalsIgnoreCase(woodName + "_PLANKS")) {
                    if (!inputs.containsKey(woodName)) {
                        inputs.put(woodName, material);
                    }
                }

                if (materialName.equalsIgnoreCase(woodName + "_STAIRS")) {
                    addRecipe(material, inputs, woodName);
                }

                if (materialName.equalsIgnoreCase(woodName + "_SLAB")) {
                    addRecipe(material, inputs, woodName);
                }

                if (materialName.equalsIgnoreCase(woodName + "_FENCE")) {
                    addRecipe(material, inputs, woodName);
                }

                if (materialName.equalsIgnoreCase(woodName + "_DOOR")) {
                    addRecipe(material, inputs, woodName);
                }

                if (materialName.equalsIgnoreCase(woodName + "_BUTTON")) {
                    addRecipe(material, inputs, woodName);
                }

                if (materialName.equalsIgnoreCase(woodName + "_TRAPDOOR")) {
                    addRecipe(material, inputs, woodName);
                }

                if (materialName.equalsIgnoreCase(woodName + "_FENCE_GATE")) {
                    addRecipe(material, inputs, woodName);
                }

                if (materialName.equalsIgnoreCase(woodName + "_PRESSURE_PLATE")) {
                    addRecipe(material, inputs, woodName);
                }

                if (materialName.equalsIgnoreCase(woodName + "_SIGN")) {
                    addRecipe(material, inputs, woodName);
                }

                if (materialName.equalsIgnoreCase("STICK")) {
                    addRecipe(material, inputs, woodName);
                }

                if (materialName.equalsIgnoreCase("LADDER")) {
                    addRecipe(material, inputs, woodName);

                }
            }
        }

        inputs.clear();
    }

    private static void addRecipe(Material material, Map<String, Material> inputs, String woodName) {
        String materialName = material.name();
        ItemStack itemStack = new ItemStack(material);
        NamespacedKey namespacedKey = itemStack.getType().getKey();

        StonecuttingRecipe cuttingRecipe = new StonecuttingRecipe(namespacedKey, itemStack, inputs.get(woodName));

        if (materialName.equalsIgnoreCase(woodName + "_STAIRS")) {
            cuttingRecipe.setGroup("wooden_stairs");
        }

        if (materialName.equalsIgnoreCase(woodName + "_SLAB")) {
            itemStack = new ItemStack(material, 2);
            cuttingRecipe = new StonecuttingRecipe(namespacedKey, itemStack, inputs.get(woodName));
            cuttingRecipe.setGroup("wooden_slab");
        }

        if (materialName.equalsIgnoreCase(woodName + "_FENCE")) {
            cuttingRecipe.setGroup("wooden_fence");
        }

        if (materialName.equalsIgnoreCase(woodName + "_DOOR")) {
            cuttingRecipe.setGroup("wooden_door");
        }

        if (materialName.equalsIgnoreCase(woodName + "_BUTTON")) {
            cuttingRecipe.setGroup("wooden_button");
        }

        if (materialName.equalsIgnoreCase(woodName + "_TRAPDOOR")) {
            cuttingRecipe.setGroup("wooden_trapdoor");
        }

        if (materialName.equalsIgnoreCase(woodName + "_FENCE_GATE")) {
            cuttingRecipe.setGroup("wooden_fence_gate");
        }

        if (materialName.equalsIgnoreCase(woodName + "_PRESSURE_PLATE")) {
            cuttingRecipe.setGroup("wooden_pressure_plate");
        }

        if (materialName.equalsIgnoreCase("STICK")) {
            itemStack = new ItemStack(material, 2);
            namespacedKey = new NamespacedKey(plugin, woodName + "_stick");
            cuttingRecipe = new StonecuttingRecipe(namespacedKey, itemStack, inputs.get(woodName));
            cuttingRecipe.setGroup("sticks");
        }

        if (materialName.equalsIgnoreCase("LADDER")) {
            itemStack = new ItemStack(material, 1);
            namespacedKey = new NamespacedKey(plugin, woodName + "_ladder");
            cuttingRecipe = new StonecuttingRecipe(namespacedKey, itemStack, inputs.get(woodName));
            cuttingRecipe.setGroup("ladder");
        }

        if (materialName.equalsIgnoreCase(woodName + "_SIGN")) {
            cuttingRecipe.setGroup("sign");
        }

        plugin.getServer().addRecipe(cuttingRecipe);
    }
}
