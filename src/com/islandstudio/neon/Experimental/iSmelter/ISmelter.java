package com.islandstudio.neon.Experimental.iSmelter;

import com.islandstudio.neon.MainCore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ISmelter {
    private static final Plugin plugin = MainCore.getPlugin(MainCore.class);

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

    public static void init() {
        String[] colors = {"WHITE", "ORANGE", "MAGENTA", "LIGHT_BLUE", "YELLOW", "LIME", "PINK", "GRAY",
                          "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK"};

        String[] woodType = {"OAK", "SPRUCE", "BIRCH", "JUNGLE", "ACACIA", "DARK_OAK", "WARPED", "CRIMSON"};

        for (Material input : Material.values()) {
            for (Material output : Material.values()) {
                String inputName = input.name();
                String outputName = output.name();

                if (inputName.equalsIgnoreCase("SAND") || inputName.equalsIgnoreCase("RED_SAND")) {
                    if (outputName.equalsIgnoreCase("GLASS")) {
                        addRecipe(input, output, null);
                    }
                }

                if (inputName.equalsIgnoreCase("COBBLESTONE")) {
                    if (outputName.equalsIgnoreCase("STONE")) {
                        addRecipe(input, output, null);
                    }
                }

                if ((inputName.equalsIgnoreCase("SANDSTONE") && outputName.equalsIgnoreCase("SMOOTH_SANDSTONE"))
                   || (inputName.equalsIgnoreCase("RED_SANDSTONE") && outputName.equalsIgnoreCase("SMOOTH_RED_SANDSTONE"))) {
                    addRecipe(input, output, null);
                }

                if (inputName.equalsIgnoreCase("STONE")) {
                    if (outputName.equalsIgnoreCase("SMOOTH_STONE")) {
                        addRecipe(input, output, null);
                    }
                }

                if (inputName.equalsIgnoreCase("QUARTZ_BLOCK")) {
                    if (outputName.equalsIgnoreCase("SMOOTH_QUARTZ")) {
                        addRecipe(input, output, null);
                    }
                }

                if (inputName.equalsIgnoreCase("CLAY_BALL")) {
                    if (outputName.equalsIgnoreCase("BRICK")) {
                        addRecipe(input, output, null);
                    }
                }

                if (inputName.equalsIgnoreCase("NETHERRACK")) {
                    if (outputName.equalsIgnoreCase("NETHER_BRICK")) {
                        addRecipe(input, output, null);
                    }
                }

                if (inputName.equalsIgnoreCase("NETHER_BRICKS")) {
                    if (outputName.equalsIgnoreCase("CRACKED_NETHER_BRICKS")) {
                        addRecipe(input, output, null);
                    }
                }

                if (inputName.equalsIgnoreCase("CLAY")) {
                    if (outputName.equalsIgnoreCase("TERRACOTTA")) {
                        addRecipe(input, output, null);
                    }
                }

                if (inputName.equalsIgnoreCase("STONE_BRICKS")) {
                    if (outputName.equalsIgnoreCase("CRACKED_STONE_BRICKS")) {
                        addRecipe(input, output, null);
                    }
                }

                for (String color : colors) {
                    if (inputName.equalsIgnoreCase(color + "_TERRACOTTA")) {
                        if (outputName.equalsIgnoreCase(color + "_GLAZED_TERRACOTTA")) {
                            addRecipe(input, output, null);
                        }
                    }
                }

                if (inputName.equalsIgnoreCase("CACTUS")) {
                    if (outputName.equalsIgnoreCase("GREEN_DYE")) {
                        addRecipe(input, output, null);
                    }
                }

                for (String woodName : woodType) {
                    if (inputName.equalsIgnoreCase(woodName + "_WOOD") || inputName.equalsIgnoreCase(woodName + "_LOG")
                       || inputName.equalsIgnoreCase("STRIPPED_" + woodName + "_WOOD") || inputName.equalsIgnoreCase("STRIPPED_" + woodName + "_LOG")) {
                        if (outputName.equalsIgnoreCase("CHARCOAL")) {
                            addRecipe(input, output, woodName);
                        }
                    }
                }

                if (inputName.equalsIgnoreCase("CHORUS_FRUIT")) {
                    if (outputName.equalsIgnoreCase("POPPED_CHORUS_FRUIT")) {
                        addRecipe(input, output, null);
                    }
                }

                if (inputName.equalsIgnoreCase("WET_SPONGE")) {
                    if (outputName.equalsIgnoreCase("SPONGE")) {
                        addRecipe(input, output, null);
                    }
                }

                if (inputName.equalsIgnoreCase("SEA_PICKLE")) {
                    if (outputName.equalsIgnoreCase("LIME_DYE")) {
                        addRecipe(input, output, null);
                    }
                }
            }
        }
    }

    private static void addRecipe(Material input, Material output, String woodName) {
        String inputName = input.name();
        ItemStack itemStack = new ItemStack(output);
        NamespacedKey namespacedKey = itemStack.getType().getKey();
        List<Recipe> recipes = plugin.getServer().getRecipesFor(new ItemStack(output));

        float exp;
        int cookingTime;

        FurnaceRecipe furnaceRecipe = null;

        for (Recipe recipe : recipes) {
            if (recipe.toString().contains("FurnaceRecipe")) {
                furnaceRecipe = (FurnaceRecipe) recipe;
            }
        }

        if (inputName.equalsIgnoreCase("SAND")) {
            namespacedKey = new NamespacedKey(plugin, inputName.toLowerCase() + "_glass");
        } else if (inputName.equalsIgnoreCase("RED_SAND")) {
            namespacedKey = new NamespacedKey(plugin, inputName.toLowerCase() + "_glass");
        }

        if (woodName != null) {
            if (inputName.equalsIgnoreCase(woodName + "_WOOD")) {
                namespacedKey = new NamespacedKey(plugin,  inputName.toLowerCase() + "_charcoal");
            } else if (inputName.equalsIgnoreCase(woodName + "_LOG")) {
                namespacedKey = new NamespacedKey(plugin, inputName.toLowerCase() + "_charcoal");
            } else if (inputName.equalsIgnoreCase("STRIPPED_" + woodName + "_WOOD")) {
                namespacedKey = new NamespacedKey(plugin, inputName.toLowerCase() + "_charcoal");
            } else if (inputName.equalsIgnoreCase("STRIPPED_" + woodName + "_LOG")) {
                namespacedKey = new NamespacedKey(plugin, inputName.toLowerCase() + "_charcoal");
            }
        }

        if (furnaceRecipe == null) return;

        exp = furnaceRecipe.getExperience();
        cookingTime = furnaceRecipe.getCookingTime() / 2;

        BlastingRecipe blastingRecipe = new BlastingRecipe(namespacedKey, itemStack, input, exp, cookingTime);
        plugin.getServer().addRecipe(blastingRecipe);
    }
}
