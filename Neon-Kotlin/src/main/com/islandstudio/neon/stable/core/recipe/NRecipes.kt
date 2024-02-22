package com.islandstudio.neon.stable.core.recipe

import com.islandstudio.neon.stable.utils.identifier.NeonKey
import org.bukkit.NamespacedKey

@Suppress("unused")
enum class NRecipes(val key: NamespacedKey, val result: NMaterial, val resultAmount: Int, val ingredients: HashSet<NMaterial>) {
    /* [nCutter] Wooden Stairs */
    NCUTTER_OAK_STAIRS(
        NeonKey.fromProperty("nCutter.recipe.oak_stairs.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.OAK_STAIRS, 1,
        Handler.registerIngredients(NMaterial.OAK_PLANKS)
    ),
    NCUTTER_SPRUCE_STAIRS(
        NeonKey.fromProperty("nCutter.recipe.spruce_stairs.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SPRUCE_STAIRS, 1,
        Handler.registerIngredients(NMaterial.SPRUCE_PLANKS)
    ),
    NCUTTER_BIRCH_STAIRS(
        NeonKey.fromProperty("nCutter.recipe.birch_stairs.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BIRCH_STAIRS, 1,
        Handler.registerIngredients(NMaterial.BIRCH_PLANKS)
    ),
    NCUTTER_JUNGLE_STAIRS(
        NeonKey.fromProperty("nCutter.recipe.jungle_stairs.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.JUNGLE_STAIRS, 1,
        Handler.registerIngredients(NMaterial.JUNGLE_PLANKS)
    ),
    NCUTTER_ACACIA_STAIRS(
        NeonKey.fromProperty("nCutter.recipe.acacia_stairs.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.ACACIA_STAIRS, 1,
        Handler.registerIngredients(NMaterial.ACACIA_PLANKS)
    ),
    NCUTTER_DARK_OAK_STAIRS(
        NeonKey.fromProperty("nCutter.recipe.dark_oak_stairs.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.DARK_OAK_STAIRS, 1,
        Handler.registerIngredients(NMaterial.DARK_OAK_PLANKS)
    ),
    NCUTTER_WARPED_STAIRS(
        NeonKey.fromProperty("nCutter.recipe.warped_stairs.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.WARPED_STAIRS, 1,
        Handler.registerIngredients(NMaterial.WARPED_PLANKS)
    ),
    NCUTTER_CRIMSON_STAIRS(
        NeonKey.fromProperty("nCutter.recipe.crimson_stairs.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CRIMSON_STAIRS, 1,
        Handler.registerIngredients(NMaterial.CRIMSON_PLANKS)
    ),
    NCUTTER_MANGROVE_STAIRS(
        NeonKey.fromProperty("nCutter.recipe.mangrove_stairs.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.MANGROVE_STAIRS, 1,
        Handler.registerIngredients(NMaterial.MANGROVE_PLANKS)
    ),
    NCUTTER_BAMBOO_STAIRS(
        NeonKey.fromProperty("nCutter.recipe.bamboo_stairs.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BAMBOO_STAIRS, 1,
        Handler.registerIngredients(NMaterial.BAMBOO_PLANKS)
    ),
    NCUTTER_BAMBOO_MOSAIC_STAIRS(
        NeonKey.fromProperty("nCutter.recipe.bamboo_mosaic_stairs.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BAMBOO_MOSAIC_STAIRS, 1,
        Handler.registerIngredients(NMaterial.BAMBOO_MOSAIC)
    ),
    NCUTTER_CHERRY_STAIRS(
        NeonKey.fromProperty("nCutter.recipe.cherry_stairs.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CHERRY_STAIRS, 1,
        Handler.registerIngredients(NMaterial.CHERRY_PLANKS)
    ),

    /* [nCutter] Wooden Slab */
    NCUTTER_OAK_SLAB(
        NeonKey.fromProperty("nCutter.recipe.oak_slab.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.OAK_SLAB, 2,
        Handler.registerIngredients(NMaterial.OAK_PLANKS)
    ),
    NCUTTER_SPRUCE_SLAB(
        NeonKey.fromProperty("nCutter.recipe.spruce_slab.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SPRUCE_SLAB, 2,
        Handler.registerIngredients(NMaterial.SPRUCE_PLANKS)
    ),
    NCUTTER_BIRCH_SLAB(
        NeonKey.fromProperty("nCutter.recipe.birch_slab.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BIRCH_SLAB, 2,
        Handler.registerIngredients(NMaterial.BIRCH_PLANKS)
    ),
    NCUTTER_JUNGLE_SLAB(
        NeonKey.fromProperty("nCutter.recipe.jungle_slab.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.JUNGLE_SLAB, 2,
        Handler.registerIngredients(NMaterial.JUNGLE_PLANKS)
    ),
    NCUTTER_ACACIA_SLAB(
        NeonKey.fromProperty("nCutter.recipe.acacia_slab.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.ACACIA_SLAB, 2,
        Handler.registerIngredients(NMaterial.ACACIA_PLANKS)
    ),
    NCUTTER_DARK_OAK_SLAB(
        NeonKey.fromProperty("nCutter.recipe.dark_oak_slab.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.DARK_OAK_SLAB, 2,
        Handler.registerIngredients(NMaterial.DARK_OAK_PLANKS)
    ),
    NCUTTER_WARPED_SLAB(
        NeonKey.fromProperty("nCutter.recipe.warped_slab.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.WARPED_SLAB, 2,
        Handler.registerIngredients(NMaterial.WARPED_PLANKS)
    ),
    NCUTTER_CRIMSON_SLAB(
        NeonKey.fromProperty("nCutter.recipe.crimson_slab.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CRIMSON_SLAB, 2,
        Handler.registerIngredients(NMaterial.CRIMSON_PLANKS)
    ),
    NCUTTER_MANGROVE_SLAB(
        NeonKey.fromProperty("nCutter.recipe.mangrove_slab.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.MANGROVE_SLAB, 2,
        Handler.registerIngredients(NMaterial.MANGROVE_PLANKS)
    ),
    NCUTTER_BAMBOO_SLAB(
        NeonKey.fromProperty("nCutter.recipe.bamboo_slab.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BAMBOO_SLAB, 2,
        Handler.registerIngredients(NMaterial.BAMBOO_PLANKS)
    ),
    NCUTTER_BAMBOO_MOSAIC_SLAB(
        NeonKey.fromProperty("nCutter.recipe.bamboo_mosaic_slab.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BAMBOO_MOSAIC_SLAB, 2,
        Handler.registerIngredients(NMaterial.BAMBOO_MOSAIC)
    ),
    NCUTTER_CHERRY_SLAB(
        NeonKey.fromProperty("nCutter.recipe.cherry_slab.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CHERRY_SLAB, 2,
        Handler.registerIngredients(NMaterial.CHERRY_PLANKS)
    ),

    /* [nCutter] Wooden Fence */
    NCUTTER_OAK_FENCE(
        NeonKey.fromProperty("nCutter.recipe.oak_fence.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.OAK_FENCE, 1,
        Handler.registerIngredients(NMaterial.OAK_PLANKS)
    ),
    NCUTTER_SPRUCE_FENCE(
        NeonKey.fromProperty("nCutter.recipe.spruce_fence.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SPRUCE_FENCE, 1,
        Handler.registerIngredients(NMaterial.SPRUCE_PLANKS)
    ),
    NCUTTER_BIRCH_FENCE(
        NeonKey.fromProperty("nCutter.recipe.birch_fence.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BIRCH_FENCE, 1,
        Handler.registerIngredients(NMaterial.BIRCH_PLANKS)
    ),
    NCUTTER_JUNGLE_FENCE(
        NeonKey.fromProperty("nCutter.recipe.jungle_fence.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.JUNGLE_FENCE, 1,
        Handler.registerIngredients(NMaterial.JUNGLE_PLANKS)
    ),
    NCUTTER_ACACIA_FENCE(
        NeonKey.fromProperty("nCutter.recipe.acacia_fence.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.ACACIA_FENCE, 1,
        Handler.registerIngredients(NMaterial.ACACIA_PLANKS)
    ),
    NCUTTER_DARK_OAK_FENCE(
        NeonKey.fromProperty("nCutter.recipe.dark_oak_fence.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.DARK_OAK_FENCE, 1,
        Handler.registerIngredients(NMaterial.DARK_OAK_PLANKS)
    ),
    NCUTTER_WARPED_FENCE(
        NeonKey.fromProperty("nCutter.recipe.warped_fence.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.WARPED_FENCE, 1,
        Handler.registerIngredients(NMaterial.WARPED_PLANKS)
    ),
    NCUTTER_CRIMSON_FENCE(
        NeonKey.fromProperty("nCutter.recipe.crimson_fence.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CRIMSON_FENCE, 1,
        Handler.registerIngredients(NMaterial.CRIMSON_PLANKS)
    ),
    NCUTTER_MANGROVE_FENCE(
        NeonKey.fromProperty("nCutter.recipe.mangrove_fence.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.MANGROVE_FENCE, 1,
        Handler.registerIngredients(NMaterial.MANGROVE_PLANKS)
    ),
    NCUTTER_BAMBOO_FENCE(
        NeonKey.fromProperty("nCutter.recipe.bamboo_fence.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BAMBOO_FENCE, 1,
        Handler.registerIngredients(NMaterial.BAMBOO_PLANKS)
    ),
    NCUTTER_CHERRY_FENCE(
        NeonKey.fromProperty("nCutter.recipe.cherry_fence.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CHERRY_FENCE, 1,
        Handler.registerIngredients(NMaterial.CHERRY_PLANKS)
    ),

    /* [nCutter] Wooden Fence Gate */
    NCUTTER_OAK_FENCE_GATE(
        NeonKey.fromProperty("nCutter.recipe.oak_fence_gate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.OAK_FENCE_GATE, 1,
        Handler.registerIngredients(NMaterial.OAK_PLANKS)
    ),
    NCUTTER_SPRUCE_FENCE_GATE(
        NeonKey.fromProperty("nCutter.recipe.spruce_fence_gate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SPRUCE_FENCE_GATE, 1,
        Handler.registerIngredients(NMaterial.SPRUCE_PLANKS)
    ),
    NCUTTER_BIRCH_FENCE_GATE(
        NeonKey.fromProperty("nCutter.recipe.birch_fence_gate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BIRCH_FENCE_GATE, 1,
        Handler.registerIngredients(NMaterial.BIRCH_PLANKS)
    ),
    NCUTTER_JUNGLE_FENCE_GATE(
        NeonKey.fromProperty("nCutter.recipe.jungle_fence_gate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.JUNGLE_FENCE_GATE, 1,
        Handler.registerIngredients(NMaterial.JUNGLE_PLANKS)
    ),
    NCUTTER_ACACIA_FENCE_GATE(
        NeonKey.fromProperty("nCutter.recipe.acacia_fence_gate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.ACACIA_FENCE_GATE, 1,
        Handler.registerIngredients(NMaterial.ACACIA_PLANKS)
    ),
    NCUTTER_DARK_OAK_FENCE_GATE(
        NeonKey.fromProperty("nCutter.recipe.dark_oak_fence_gate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.DARK_OAK_FENCE_GATE, 1,
        Handler.registerIngredients(NMaterial.DARK_OAK_PLANKS)
    ),
    NCUTTER_WARPED_FENCE_GATE(
        NeonKey.fromProperty("nCutter.recipe.warped_fence_gate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.WARPED_FENCE_GATE, 1,
        Handler.registerIngredients(NMaterial.WARPED_PLANKS)
    ),
    NCUTTER_CRIMSON_FENCE_GATE(
        NeonKey.fromProperty("nCutter.recipe.crimson_fence_gate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CRIMSON_FENCE_GATE, 1,
        Handler.registerIngredients(NMaterial.CRIMSON_PLANKS)
    ),
    NCUTTER_MANGROVE_FENCE_GATE(
        NeonKey.fromProperty("nCutter.recipe.mangrove_fence_gate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.MANGROVE_FENCE_GATE, 1,
        Handler.registerIngredients(NMaterial.MANGROVE_PLANKS)
    ),
    NCUTTER_BAMBOO_FENCE_GATE(
        NeonKey.fromProperty("nCutter.recipe.bamboo_fence_gate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BAMBOO_FENCE_GATE, 1,
        Handler.registerIngredients(NMaterial.BAMBOO_PLANKS)
    ),
    NCUTTER_CHERRY_FENCE_GATE(
        NeonKey.fromProperty("nCutter.recipe.cherry_fence_gate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CHERRY_FENCE_GATE, 1,
        Handler.registerIngredients(NMaterial.CHERRY_PLANKS)
    ),

    /* [nCutter] Wooden Door */
    NCUTTER_OAK_DOOR(
        NeonKey.fromProperty("nCutter.recipe.oak_door.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.OAK_DOOR, 1,
        Handler.registerIngredients(NMaterial.OAK_PLANKS)
    ),
    NCUTTER_SPRUCE_DOOR(
        NeonKey.fromProperty("nCutter.recipe.spruce_door.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SPRUCE_DOOR, 1,
        Handler.registerIngredients(NMaterial.SPRUCE_PLANKS)
    ),
    NCUTTER_BIRCH_DOOR(
        NeonKey.fromProperty("nCutter.recipe.birch_door.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BIRCH_DOOR, 1,
        Handler.registerIngredients(NMaterial.BIRCH_PLANKS)
    ),
    NCUTTER_JUNGLE_DOOR(
        NeonKey.fromProperty("nCutter.recipe.jungle_door.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.JUNGLE_DOOR, 1,
        Handler.registerIngredients(NMaterial.JUNGLE_PLANKS)
    ),
    NCUTTER_ACACIA_DOOR(
        NeonKey.fromProperty("nCutter.recipe.acacia_door.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.ACACIA_DOOR, 1,
        Handler.registerIngredients(NMaterial.ACACIA_PLANKS)
    ),
    NCUTTER_DARK_OAK_DOOR(
        NeonKey.fromProperty("nCutter.recipe.dark_oak_door.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.DARK_OAK_DOOR, 1,
        Handler.registerIngredients(NMaterial.DARK_OAK_PLANKS)
    ),
    NCUTTER_WARPED_DOOR(
        NeonKey.fromProperty("nCutter.recipe.warped_door.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.WARPED_DOOR, 1,
        Handler.registerIngredients(NMaterial.WARPED_PLANKS)
    ),
    NCUTTER_CRIMSON_DOOR(
        NeonKey.fromProperty("nCutter.recipe.crimson_door.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CRIMSON_DOOR, 1,
        Handler.registerIngredients(NMaterial.CRIMSON_PLANKS)
    ),
    NCUTTER_MANGROVE_DOOR(
        NeonKey.fromProperty("nCutter.recipe.mangrove_door.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.MANGROVE_DOOR, 1,
        Handler.registerIngredients(NMaterial.MANGROVE_PLANKS)
    ),
    NCUTTER_BAMBOO_DOOR(
        NeonKey.fromProperty("nCutter.recipe.bamboo_door.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BAMBOO_DOOR, 1,
        Handler.registerIngredients(NMaterial.BAMBOO_PLANKS)
    ),
    NCUTTER_CHERRY_DOOR(
        NeonKey.fromProperty("nCutter.recipe.cherry_door.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CHERRY_DOOR, 1,
        Handler.registerIngredients(NMaterial.CHERRY_PLANKS)
    ),

    /* [nCutter] Wooden Trap Door */
    NCUTTER_OAK_TRAPDOOR(
        NeonKey.fromProperty("nCutter.recipe.oak_trapdoor.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.OAK_TRAPDOOR, 1,
        Handler.registerIngredients(NMaterial.OAK_PLANKS)
    ),
    NCUTTER_SPRUCE_TRAPDOOR(
        NeonKey.fromProperty("nCutter.recipe.spruce_trapdoor.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SPRUCE_TRAPDOOR, 1,
        Handler.registerIngredients(NMaterial.SPRUCE_PLANKS)
    ),
    NCUTTER_BIRCH_TRAPDOOR(
        NeonKey.fromProperty("nCutter.recipe.birch_trapdoor.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BIRCH_TRAPDOOR, 1,
        Handler.registerIngredients(NMaterial.BIRCH_PLANKS)
    ),
    NCUTTER_JUNGLE_TRAPDOOR(
        NeonKey.fromProperty("nCutter.recipe.jungle_trapdoor.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.JUNGLE_TRAPDOOR, 1,
        Handler.registerIngredients(NMaterial.JUNGLE_PLANKS)
    ),
    NCUTTER_ACACIA_TRAPDOOR(
        NeonKey.fromProperty("nCutter.recipe.acacia_trapdoor.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.ACACIA_TRAPDOOR, 1,
        Handler.registerIngredients(NMaterial.ACACIA_PLANKS)
    ),
    NCUTTER_DARK_OAK_TRAPDOOR(
        NeonKey.fromProperty("nCutter.recipe.dark_oak_trapdoor.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.DARK_OAK_TRAPDOOR, 1,
        Handler.registerIngredients(NMaterial.DARK_OAK_PLANKS)
    ),
    NCUTTER_WARPED_TRAPDOOR(
        NeonKey.fromProperty("nCutter.recipe.warped_trapdoor.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.WARPED_TRAPDOOR, 1,
        Handler.registerIngredients(NMaterial.WARPED_PLANKS)
    ),
    NCUTTER_CRIMSON_TRAPDOOR(
        NeonKey.fromProperty("nCutter.recipe.crimson_trapdoor.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CRIMSON_TRAPDOOR, 1,
        Handler.registerIngredients(NMaterial.CRIMSON_PLANKS)
    ),
    NCUTTER_MANGROVE_TRAPDOOR(
        NeonKey.fromProperty("nCutter.recipe.mangrove_trapdoor.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.MANGROVE_TRAPDOOR, 1,
        Handler.registerIngredients(NMaterial.MANGROVE_PLANKS)
    ),
    NCUTTER_BAMBOO_TRAPDOOR(
        NeonKey.fromProperty("nCutter.recipe.bamboo_trapdoor.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BAMBOO_TRAPDOOR, 1,
        Handler.registerIngredients(NMaterial.BAMBOO_PLANKS)
    ),
    NCUTTER_CHERRY_TRAPDOOR(
        NeonKey.fromProperty("nCutter.recipe.cherry_trapdoor.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CHERRY_TRAPDOOR, 1,
        Handler.registerIngredients(NMaterial.CHERRY_PLANKS)
    ),

    /* [nCutter] Wooden Button */
    NCUTTER_OAK_BUTTON(
        NeonKey.fromProperty("nCutter.recipe.oak_button.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.OAK_BUTTON, 1,
        Handler.registerIngredients(NMaterial.OAK_PLANKS)
    ),
    NCUTTER_SPRUCE_BUTTON(
        NeonKey.fromProperty("nCutter.recipe.spruce_button.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SPRUCE_BUTTON, 1,
        Handler.registerIngredients(NMaterial.SPRUCE_PLANKS)
    ),
    NCUTTER_BIRCH_BUTTON(
        NeonKey.fromProperty("nCutter.recipe.birch_button.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BIRCH_BUTTON, 1,
        Handler.registerIngredients(NMaterial.BIRCH_PLANKS)
    ),
    NCUTTER_JUNGLE_BUTTON(
        NeonKey.fromProperty("nCutter.recipe.jungle_button.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.JUNGLE_BUTTON, 1,
        Handler.registerIngredients(NMaterial.JUNGLE_PLANKS)
    ),
    NCUTTER_ACACIA_BUTTON(
        NeonKey.fromProperty("nCutter.recipe.acacia_button.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.ACACIA_BUTTON, 1,
        Handler.registerIngredients(NMaterial.ACACIA_PLANKS)
    ),
    NCUTTER_DARK_OAK_BUTTON(
        NeonKey.fromProperty("nCutter.recipe.dark_oak_button.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.DARK_OAK_BUTTON, 1,
        Handler.registerIngredients(NMaterial.DARK_OAK_PLANKS)
    ),
    NCUTTER_WARPED_BUTTON(
        NeonKey.fromProperty("nCutter.recipe.warped_button.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.WARPED_BUTTON, 1,
        Handler.registerIngredients(NMaterial.WARPED_PLANKS)
    ),
    NCUTTER_CRIMSON_BUTTON(
        NeonKey.fromProperty("nCutter.recipe.crimson_button.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CRIMSON_BUTTON, 1,
        Handler.registerIngredients(NMaterial.CRIMSON_PLANKS)
    ),
    NCUTTER_MANGROVE_BUTTON(
        NeonKey.fromProperty("nCutter.recipe.mangrove_button.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.MANGROVE_BUTTON, 1,
        Handler.registerIngredients(NMaterial.MANGROVE_PLANKS)
    ),
    NCUTTER_BAMBOO_BUTTON(
        NeonKey.fromProperty("nCutter.recipe.bamboo_button.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BAMBOO_BUTTON, 1,
        Handler.registerIngredients(NMaterial.BAMBOO_PLANKS)
    ),
    NCUTTER_CHERRY_BUTTON(
        NeonKey.fromProperty("nCutter.recipe.cherry_button.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CHERRY_BUTTON, 1,
        Handler.registerIngredients(NMaterial.CHERRY_PLANKS)
    ),

    /* [nCutter] Wooden Pressure Plate */
    NCUTTER_OAK_PRESSURE_PLATE(
        NeonKey.fromProperty("nCutter.recipe.oak_pressure_plate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.OAK_PRESSURE_PLATE, 1,
        Handler.registerIngredients(NMaterial.OAK_PLANKS)
    ),
    NCUTTER_SPRUCE_PRESSURE_PLATE(
        NeonKey.fromProperty("nCutter.recipe.spruce_pressure_plate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SPRUCE_PRESSURE_PLATE, 1,
        Handler.registerIngredients(NMaterial.SPRUCE_PLANKS)
    ),
    NCUTTER_BIRCH_PRESSURE_PLATE(
        NeonKey.fromProperty("nCutter.recipe.birch_pressure_plate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BIRCH_PRESSURE_PLATE, 1,
        Handler.registerIngredients(NMaterial.BIRCH_PLANKS)
    ),
    NCUTTER_JUNGLE_PRESSURE_PLATE(
        NeonKey.fromProperty("nCutter.recipe.jungle_pressure_plate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.JUNGLE_PRESSURE_PLATE, 1,
        Handler.registerIngredients(NMaterial.JUNGLE_PLANKS)
    ),
    NCUTTER_ACACIA_PRESSURE_PLATE(
        NeonKey.fromProperty("nCutter.recipe.acacia_pressure_plate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.ACACIA_PRESSURE_PLATE, 1,
        Handler.registerIngredients(NMaterial.ACACIA_PLANKS)
    ),
    NCUTTER_DARK_OAK_PRESSURE_PLATE(
        NeonKey.fromProperty("nCutter.recipe.dark_oak_pressure_plate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.DARK_OAK_PRESSURE_PLATE, 1,
        Handler.registerIngredients(NMaterial.DARK_OAK_PLANKS)
    ),
    NCUTTER_WARPED_PRESSURE_PLATE(
        NeonKey.fromProperty("nCutter.recipe.warped_pressure_plate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.WARPED_PRESSURE_PLATE, 1,
        Handler.registerIngredients(NMaterial.WARPED_PLANKS)
    ),
    NCUTTER_CRIMSON_PRESSURE_PLATE(
        NeonKey.fromProperty("nCutter.recipe.crimson_pressure_plate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CRIMSON_PRESSURE_PLATE, 1,
        Handler.registerIngredients(NMaterial.CRIMSON_PLANKS)
    ),
    NCUTTER_MANGROVE_PRESSURE_PLATE(
        NeonKey.fromProperty("nCutter.recipe.mangrove_pressure_plate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.MANGROVE_PRESSURE_PLATE, 1,
        Handler.registerIngredients(NMaterial.MANGROVE_PLANKS)
    ),
    NCUTTER_BAMBOO_PRESSURE_PLATE(
        NeonKey.fromProperty("nCutter.recipe.bamboo_pressure_plate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BAMBOO_PRESSURE_PLATE, 1,
        Handler.registerIngredients(NMaterial.BAMBOO_PLANKS)
    ),
    NCUTTER_CHERRY_PRESSURE_PLATE(
        NeonKey.fromProperty("nCutter.recipe.cherry_pressure_plate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CHERRY_PRESSURE_PLATE, 1,
        Handler.registerIngredients(NMaterial.CHERRY_PLANKS)
    ),

    /* [nCutter] Wooden Sign */
    NCUTTER_OAK_SIGN(
        NeonKey.fromProperty("nCutter.recipe.oak_sign.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.OAK_SIGN, 1,
        Handler.registerIngredients(NMaterial.OAK_PLANKS)
    ),
    NCUTTER_SPRUCE_SIGN(
        NeonKey.fromProperty("nCutter.recipe.spruce_sign.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SPRUCE_SIGN, 1,
        Handler.registerIngredients(NMaterial.SPRUCE_PLANKS)
    ),
    NCUTTER_BIRCH_SIGN(
        NeonKey.fromProperty("nCutter.recipe.birch_sign.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BIRCH_SIGN, 1,
        Handler.registerIngredients(NMaterial.BIRCH_PLANKS)
    ),
    NCUTTER_JUNGLE_SIGN(
        NeonKey.fromProperty("nCutter.recipe.jungle_sign.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.JUNGLE_SIGN, 1,
        Handler.registerIngredients(NMaterial.JUNGLE_PLANKS)
    ),
    NCUTTER_ACACIA_SIGN(
        NeonKey.fromProperty("nCutter.recipe.acacia_sign.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.ACACIA_SIGN, 1,
        Handler.registerIngredients(NMaterial.ACACIA_PLANKS)
    ),
    NCUTTER_DARK_OAK_SIGN(
        NeonKey.fromProperty("nCutter.recipe.dark_oak_sign.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.DARK_OAK_SIGN, 1,
        Handler.registerIngredients(NMaterial.DARK_OAK_PLANKS)
    ),
    NCUTTER_WARPED_SIGN(
        NeonKey.fromProperty("nCutter.recipe.warped_sign.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.WARPED_SIGN, 1,
        Handler.registerIngredients(NMaterial.WARPED_PLANKS)
    ),
    NCUTTER_CRIMSON_SIGN(
        NeonKey.fromProperty("nCutter.recipe.crimson_sign.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CRIMSON_SIGN, 1,
        Handler.registerIngredients(NMaterial.CRIMSON_PLANKS)
    ),
    NCUTTER_MANGROVE_SIGN(
        NeonKey.fromProperty("nCutter.recipe.mangrove_sign.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.MANGROVE_SIGN, 1,
        Handler.registerIngredients(NMaterial.MANGROVE_PLANKS)
    ),
    NCUTTER_BAMBOO_SIGN(
        NeonKey.fromProperty("nCutter.recipe.bamboo_sign.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BAMBOO_SIGN, 1,
        Handler.registerIngredients(NMaterial.BAMBOO_PLANKS)
    ),
    NCUTTER_CHERRY_SIGN(
        NeonKey.fromProperty("nCutter.recipe.cherry_sign.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CHERRY_SIGN, 1,
        Handler.registerIngredients(NMaterial.CHERRY_PLANKS)
    ),

    /* [nCutter] Wooden Boat */
    NCUTTER_OAK_BOAT(
        NeonKey.fromProperty("nCutter.recipe.oak_boat.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.OAK_BOAT, 1,
        Handler.registerIngredients(NMaterial.OAK_PLANKS)
    ),
    NCUTTER_SPRUCE_BOAT(
        NeonKey.fromProperty("nCutter.recipe.spruce_boat.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SPRUCE_BOAT, 1,
        Handler.registerIngredients(NMaterial.SPRUCE_PLANKS)
    ),
    NCUTTER_BIRCH_BOAT(
        NeonKey.fromProperty("nCutter.recipe.birch_boat.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BIRCH_BOAT, 1,
        Handler.registerIngredients(NMaterial.BIRCH_PLANKS)
    ),
    NCUTTER_JUNGLE_BOAT(
        NeonKey.fromProperty("nCutter.recipe.jungle_boat.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.JUNGLE_BOAT, 1,
        Handler.registerIngredients(NMaterial.JUNGLE_PLANKS)
    ),
    NCUTTER_ACACIA_BOAT(
        NeonKey.fromProperty("nCutter.recipe.acacia_boat.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.ACACIA_BOAT, 1,
        Handler.registerIngredients(NMaterial.ACACIA_PLANKS)
    ),
    NCUTTER_DARK_OAK_BOAT(
        NeonKey.fromProperty("nCutter.recipe.dark_oak_boat.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.DARK_OAK_BOAT, 1,
        Handler.registerIngredients(NMaterial.DARK_OAK_PLANKS)
    ),
    NCUTTER_MANGROVE_BOAT(
        NeonKey.fromProperty("nCutter.recipe.mangrove_boat.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.MANGROVE_BOAT, 1,
        Handler.registerIngredients(NMaterial.MANGROVE_PLANKS)
    ),
    NCUTTER_BAMBOO_RAFT(
        NeonKey.fromProperty("nCutter.recipe.bamboo_raft.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BAMBOO_RAFT, 1,
        Handler.registerIngredients(NMaterial.BAMBOO_PLANKS)
    ),
    NCUTTER_CHERRY_BOAT(
        NeonKey.fromProperty("nCutter.recipe.cherry_boat.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CHERRY_BOAT, 1,
        Handler.registerIngredients(NMaterial.CHERRY_PLANKS)
    ),

    /* [nCutter] Wooden Items */
    NCUTTER_STICK(
        NeonKey.fromProperty("nCutter.recipe.stick.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.STICK, 2,
        Handler.registerIngredients(
            NMaterial.OAK_PLANKS, NMaterial.SPRUCE_PLANKS, NMaterial.BIRCH_PLANKS,
            NMaterial.JUNGLE_PLANKS, NMaterial.ACACIA_PLANKS, NMaterial.DARK_OAK_PLANKS, NMaterial.WARPED_PLANKS,
            NMaterial.CRIMSON_PLANKS, NMaterial.MANGROVE_PLANKS, NMaterial.BAMBOO_PLANKS, NMaterial.CHERRY_PLANKS
        )
    ),
    NCUTTER_LADDER(
        NeonKey.fromProperty("nCutter.recipe.ladder.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.LADDER, 1,
        Handler.registerIngredients(
            NMaterial.OAK_PLANKS, NMaterial.SPRUCE_PLANKS, NMaterial.BIRCH_PLANKS,
            NMaterial.JUNGLE_PLANKS, NMaterial.ACACIA_PLANKS, NMaterial.DARK_OAK_PLANKS, NMaterial.WARPED_PLANKS,
            NMaterial.CRIMSON_PLANKS, NMaterial.MANGROVE_PLANKS, NMaterial.BAMBOO_PLANKS, NMaterial.CHERRY_PLANKS
        )
    ),
    NCUTTER_BOWL(
        NeonKey.fromProperty("nCutter.recipe.bowl.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BOWL, 2,
        Handler.registerIngredients(
            NMaterial.OAK_PLANKS,
            NMaterial.SPRUCE_PLANKS,
            NMaterial.BIRCH_PLANKS,
            NMaterial.JUNGLE_PLANKS,
            NMaterial.ACACIA_PLANKS,
            NMaterial.DARK_OAK_PLANKS,
            NMaterial.WARPED_PLANKS,
            NMaterial.CRIMSON_PLANKS,
            NMaterial.MANGROVE_PLANKS,
            NMaterial.BAMBOO_PLANKS,
            NMaterial.CHERRY_PLANKS
        )
    ),

    /* [nSmelter] */
    NSMELTER_GLASS(
        NeonKey.fromProperty("nSmelter.recipe.glass.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.GLASS, 1,
        Handler.registerIngredients(NMaterial.SAND, NMaterial.RED_SAND)
    ),
    NSMELTER_STONE(
        NeonKey.fromProperty("nSmelter.recipe.stone.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.STONE, 1,
        Handler.registerIngredients(NMaterial.COBBLESTONE)
    ),
    NSMELTER_DEEPSLATE(
        NeonKey.fromProperty("nSmelter.recipe.deepslate.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.DEEPSLATE, 1,
        Handler.registerIngredients(NMaterial.COBBLED_DEEPSLATE)
    ),
    NSMELTER_SMOOTH_SANDSTONE(
        NeonKey.fromProperty("nSmelter.recipe.smooth_sandstone.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SMOOTH_SANDSTONE, 1,
        Handler.registerIngredients(NMaterial.SANDSTONE)
    ),
    NSMELTER_SMOOTH_RED_SANDSTONE(
        NeonKey.fromProperty("nSmelter.recipe.smooth_red_sandstone.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SMOOTH_RED_SANDSTONE, 1,
        Handler.registerIngredients(NMaterial.RED_SANDSTONE)
    ),
    NSMELTER_SMOOTH_STONE(
        NeonKey.fromProperty("nSmelter.recipe.smooth_stone.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SMOOTH_STONE, 1,
        Handler.registerIngredients(NMaterial.STONE)
    ),
    NSMELTER_SMOOTH_QUARTZ(
        NeonKey.fromProperty("nSmelter.recipe.smooth_quartz.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SMOOTH_QUARTZ, 1,
        Handler.registerIngredients(NMaterial.QUARTZ_BLOCK)
    ),
    NSMELTER_BRICK(
        NeonKey.fromProperty("nSmelter.recipe.brick.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BRICK, 1,
        Handler.registerIngredients(NMaterial.CLAY_BALL)
    ),
    NSMELTER_NETHER_BRICK(
        NeonKey.fromProperty("nSmelter.recipe.nether_brick.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.NETHER_BRICK, 1,
        Handler.registerIngredients(NMaterial.NETHERRACK)
    ),
    NSMELTER_CRACKED_NETHER_BRICKS(
        NeonKey.fromProperty("nSmelter.recipe.cracked_nether_bricks.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CRACKED_NETHER_BRICKS, 1,
        Handler.registerIngredients(NMaterial.NETHER_BRICKS)
    ),
    NSMELTER_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.CLAY)
    ),
    NSMELTER_CRACKED_STONE_BRICKS(
        NeonKey.fromProperty("nSmelter.recipe.cracked_stone_bricks.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CRACKED_STONE_BRICKS, 1,
        Handler.registerIngredients(NMaterial.STONE_BRICKS)
    ),
    NSMELTER_POPPED_CHORUS_FRUIT(
        NeonKey.fromProperty("nSmelter.recipe.popped_chorus_fruit.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.POPPED_CHORUS_FRUIT, 1,
        Handler.registerIngredients(NMaterial.CHORUS_FRUIT)
    ),
    NSMELTER_SPONGE(
        NeonKey.fromProperty("nSmelter.recipe.sponge.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.SPONGE, 1,
        Handler.registerIngredients(NMaterial.WET_SPONGE)
    ),
    NSMELTER_CHARCOAL(
        NeonKey.fromProperty("nSmelter.recipe.charcoal.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CHARCOAL, 1,
        Handler.registerIngredients(
            NMaterial.OAK_WOOD,
            NMaterial.SPRUCE_WOOD,
            NMaterial.BIRCH_WOOD,
            NMaterial.JUNGLE_WOOD,
            NMaterial.ACACIA_WOOD,
            NMaterial.DARK_OAK_WOOD,
            NMaterial.MANGROVE_WOOD,
            NMaterial.CHERRY_WOOD,
            NMaterial.OAK_LOG,
            NMaterial.SPRUCE_LOG,
            NMaterial.BIRCH_LOG,
            NMaterial.JUNGLE_LOG,
            NMaterial.ACACIA_LOG,
            NMaterial.DARK_OAK_LOG,
            NMaterial.MANGROVE_LOG,
            NMaterial.CHERRY_LOG,
            NMaterial.STRIPPED_OAK_WOOD,
            NMaterial.STRIPPED_SPRUCE_WOOD,
            NMaterial.STRIPPED_BIRCH_WOOD,
            NMaterial.STRIPPED_JUNGLE_WOOD,
            NMaterial.STRIPPED_ACACIA_WOOD,
            NMaterial.STRIPPED_DARK_OAK_WOOD,
            NMaterial.STRIPPED_MANGROVE_WOOD,
            NMaterial.STRIPPED_CHERRY_WOOD,
            NMaterial.STRIPPED_OAK_LOG,
            NMaterial.STRIPPED_SPRUCE_LOG,
            NMaterial.STRIPPED_BIRCH_LOG,
            NMaterial.STRIPPED_JUNGLE_LOG,
            NMaterial.STRIPPED_ACACIA_LOG,
            NMaterial.STRIPPED_DARK_OAK_LOG,
            NMaterial.STRIPPED_MANGROVE_LOG,
            NMaterial.STRIPPED_CHERRY_LOG
        )
    ),

    /* [nSmelter] Ore Block */
    NSMELTER_IRON_BLOCK(
        NeonKey.fromProperty("nSmelter.recipe.iron_block.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.IRON_BLOCK, 1,
        Handler.registerIngredients(NMaterial.RAW_IRON_BLOCK)
    ),
    NSMELTER_COPPER_BLOCK(
        NeonKey.fromProperty("nSmelter.recipe.copper_block.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.COPPER_BLOCK, 1,
        Handler.registerIngredients(NMaterial.RAW_COPPER_BLOCK)
    ),
    NSMELTER_GOLD_BLOCK(
        NeonKey.fromProperty("nSmelter.recipe.gold_block.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.GOLD_BLOCK, 1,
        Handler.registerIngredients(NMaterial.RAW_GOLD_BLOCK)
    ),

    /* [nSmelter] Dye */
    NSMELTER_GREEN_DYE(
        NeonKey.fromProperty("nSmelter.recipe.green_dye.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.GREEN_DYE, 1,
        Handler.registerIngredients(NMaterial.CACTUS)
    ),
    NSMELTER_LIME_DYE(
        NeonKey.fromProperty("nSmelter.recipe.lime_dye.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.LIME_DYE, 1,
        Handler.registerIngredients(NMaterial.SEA_PICKLE)
    ),

    /* [nSmelter] Colored Glazed Terracotta */
    NSMELTER_WHITE_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.white_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.WHITE_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.WHITE_TERRACOTTA)
    ),
    NSMELTER_ORANGE_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.orange_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.ORANGE_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.ORANGE_TERRACOTTA)
    ),
    NSMELTER_MAGENTA_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.magenta_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.MAGENTA_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.MAGENTA_TERRACOTTA)
    ),
    NSMELTER_LIGHT_BLUE_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.light_blue_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.LIGHT_BLUE_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.LIGHT_BLUE_TERRACOTTA)
    ),
    NSMELTER_YELLOW_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.yellow_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.YELLOW_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.YELLOW_TERRACOTTA)
    ),
    NSMELTER_LIME_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.lime_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.LIME_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.LIME_TERRACOTTA)
    ),
    NSMELTER_PINK_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.pink_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.PINK_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.PINK_TERRACOTTA)
    ),
    NSMELTER_GRAY_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.gray_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.GRAY_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.GRAY_TERRACOTTA)
    ),
    NSMELTER_LIGHT_GRAY_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.light_gray_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.LIGHT_GRAY_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.LIGHT_GRAY_TERRACOTTA)
    ),
    NSMELTER_CYAN_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.cyan_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.CYAN_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.CYAN_TERRACOTTA)
    ),
    NSMELTER_PURPLE_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.purple_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.PURPLE_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.PURPLE_TERRACOTTA)
    ),
    NSMELTER_BLUE_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.blue_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BLUE_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.BLUE_TERRACOTTA)
    ),
    NSMELTER_BROWN_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.brown_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BROWN_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.BROWN_TERRACOTTA)
    ),
    NSMELTER_GREEN_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.green_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.GREEN_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.GREEN_TERRACOTTA)
    ),
    NSMELTER_RED_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.red_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.RED_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.RED_TERRACOTTA)
    ),
    NSMELTER_BLACK_GLAZED_TERRACOTTA(
        NeonKey.fromProperty("nSmelter.recipe.black_glazed_terracotta.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BLACK_GLAZED_TERRACOTTA, 1,
        Handler.registerIngredients(NMaterial.BLACK_TERRACOTTA)
    ),

    /* [nBundle] */
    NBUNDLE_BUNDLE(
        NeonKey.fromProperty("nBundle.recipe.bundle.key", NeonKey.NeonKeyType.RECIPE),
        NMaterial.BUNDLE, 1,
        Handler.registerIngredients(NMaterial.STRING, NMaterial.RABBIT_HIDE)
    );

    private object Handler {
        fun registerIngredients(vararg ingredients: NMaterial): HashSet<NMaterial> = ingredients.toHashSet()
    }
}