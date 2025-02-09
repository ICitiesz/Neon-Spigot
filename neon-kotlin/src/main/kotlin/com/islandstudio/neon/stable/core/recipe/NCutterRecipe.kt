package com.islandstudio.neon.stable.core.recipe

import com.islandstudio.neon.stable.core.recipe.component.AbstractRecipeHolder

sealed class NCutterRecipe(keyName: String): AbstractRecipeHolder(keyName) {
    companion object: RecipeHolderHandler<NCutterRecipe>(NCutterRecipe::class);

    /* Wooden Stairs */
    data object OakStairs: NCutterRecipe("nCutter.recipe.oak_stairs.key") {
        override val result: NMaterial = NMaterial.OAK_STAIRS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_PLANKS
        )
    }

    data object SpruceStairs: NCutterRecipe("nCutter.recipe.spruce_stairs.key") {
        override val result: NMaterial = NMaterial.SPRUCE_STAIRS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.SPRUCE_PLANKS
        )
    }

    data object BirchStairs: NCutterRecipe("nCutter.recipe.birch_stairs.key") {
        override val result: NMaterial = NMaterial.BIRCH_STAIRS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BIRCH_PLANKS
        )
    }

    data object JungleStairs: NCutterRecipe("nCutter.recipe.jungle_stairs.key") {
        override val result: NMaterial = NMaterial.JUNGLE_STAIRS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.JUNGLE_PLANKS
        )
    }

    data object AcaciaStairs: NCutterRecipe("nCutter.recipe.acacia_stairs.key") {
        override val result: NMaterial = NMaterial.ACACIA_STAIRS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.ACACIA_PLANKS
        )
    }

    data object DarkOakStairs: NCutterRecipe("nCutter.recipe.dark_oak_stairs.key") {
        override val result: NMaterial = NMaterial.DARK_OAK_STAIRS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.DARK_OAK_PLANKS
        )
    }

    data object WarpedStairs: NCutterRecipe("nCutter.recipe.warped_stairs.key") {
        override val result: NMaterial = NMaterial.WARPED_STAIRS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.WARPED_PLANKS
        )
    }

    data object CrimsonStairs: NCutterRecipe("nCutter.recipe.crimson_stairs.key") {
        override val result: NMaterial = NMaterial.CRIMSON_STAIRS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CRIMSON_PLANKS
        )
    }

    data object MangroveStairs: NCutterRecipe("nCutter.recipe.mangrove_stairs.key") {
        override val result: NMaterial = NMaterial.MANGROVE_STAIRS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.MANGROVE_PLANKS
        )
    }

    data object BambooStairs: NCutterRecipe("nCutter.recipe.bamboo_stairs.key") {
        override val result: NMaterial = NMaterial.BAMBOO_STAIRS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BAMBOO_PLANKS
        )
    }

    data object BambooMosaicStairs: NCutterRecipe("nCutter.recipe.bamboo_mosaic_stairs.key") {
        override val result: NMaterial = NMaterial.BAMBOO_MOSAIC_STAIRS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BAMBOO_MOSAIC
        )
    }

    data object CherryStairs: NCutterRecipe("nCutter.recipe.cherry_stairs.key") {
        override val result: NMaterial = NMaterial.CHERRY_STAIRS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CHERRY_PLANKS
        )
    }

    /* Wooden Slab */
    data object OakSlab: NCutterRecipe("nCutter.recipe.oak_slab.key") {
        override val result: NMaterial = NMaterial.OAK_SLAB
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_PLANKS
        )
    }

    data object SpruceSlab: NCutterRecipe("nCutter.recipe.spruce_slab.key") {
        override val result: NMaterial = NMaterial.SPRUCE_SLAB
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.SPRUCE_PLANKS
        )
    }

    data object BirchSlab: NCutterRecipe("nCutter.recipe.birch_slab.key") {
        override val result: NMaterial = NMaterial.BIRCH_SLAB
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BIRCH_PLANKS
        )
    }

    data object JungleSlab: NCutterRecipe("nCutter.recipe.jungle_slab.key") {
        override val result: NMaterial = NMaterial.JUNGLE_SLAB
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.JUNGLE_PLANKS
        )
    }

    data object AcaciaSlab: NCutterRecipe("nCutter.recipe.acacia_slab.key") {
        override val result: NMaterial = NMaterial.ACACIA_SLAB
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.ACACIA_PLANKS
        )
    }

    data object DarkOakSlab: NCutterRecipe("nCutter.recipe.dark_oak_slab.key") {
        override val result: NMaterial = NMaterial.DARK_OAK_SLAB
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.DARK_OAK_PLANKS
        )
    }

    data object WarpedSlab: NCutterRecipe("nCutter.recipe.warped_slab.key") {
        override val result: NMaterial = NMaterial.WARPED_SLAB
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.WARPED_PLANKS
        )
    }

    data object CrimsonSlab: NCutterRecipe("nCutter.recipe.crimson_slab.key") {
        override val result: NMaterial = NMaterial.CRIMSON_SLAB
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CRIMSON_PLANKS
        )
    }

    data object MangroveSlab: NCutterRecipe("nCutter.recipe.mangrove_slab.key") {
        override val result: NMaterial = NMaterial.MANGROVE_SLAB
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.MANGROVE_PLANKS
        )
    }

    data object BambooSlab: NCutterRecipe("nCutter.recipe.bamboo_slab.key") {
        override val result: NMaterial = NMaterial.BAMBOO_SLAB
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BAMBOO_PLANKS
        )
    }

    data object BambooMosaicSlab: NCutterRecipe("nCutter.recipe.bamboo_mosaic_slab.key") {
        override val result: NMaterial = NMaterial.BAMBOO_MOSAIC_SLAB
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BAMBOO_MOSAIC
        )
    }

    data object CherrySlab: NCutterRecipe("nCutter.recipe.cherry_slab.key") {
        override val result: NMaterial = NMaterial.CHERRY_SLAB
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CHERRY_PLANKS
        )
    }

    /* Wooden Fence */
    data object OakFence: NCutterRecipe("nCutter.recipe.oak_fence.key") {
        override val result: NMaterial = NMaterial.OAK_FENCE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_PLANKS
        )
    }

    data object SpruceFence: NCutterRecipe("nCutter.recipe.spruce_fence.key") {
        override val result: NMaterial = NMaterial.SPRUCE_FENCE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.SPRUCE_PLANKS
        )
    }

    data object BirchFence: NCutterRecipe("nCutter.recipe.birch_fence.key") {
        override val result: NMaterial = NMaterial.BIRCH_FENCE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BIRCH_PLANKS
        )
    }

    data object JungleFence: NCutterRecipe("nCutter.recipe.jungle_fence.key") {
        override val result: NMaterial = NMaterial.JUNGLE_FENCE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.JUNGLE_PLANKS
        )
    }

    data object AcaciaFence: NCutterRecipe("nCutter.recipe.acacia_fence.key") {
        override val result: NMaterial = NMaterial.ACACIA_FENCE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.ACACIA_PLANKS
        )
    }

    data object DarkOakFence: NCutterRecipe("nCutter.recipe.dark_oak_fence.key") {
        override val result: NMaterial = NMaterial.DARK_OAK_FENCE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.DARK_OAK_PLANKS
        )
    }

    data object WarpedFence: NCutterRecipe("nCutter.recipe.warped_fence.key") {
        override val result: NMaterial = NMaterial.WARPED_FENCE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.WARPED_PLANKS
        )
    }

    data object CrimsonFence: NCutterRecipe("nCutter.recipe.crimson_fence.key") {
        override val result: NMaterial = NMaterial.CRIMSON_FENCE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CRIMSON_PLANKS
        )
    }

    data object MangroveFence: NCutterRecipe("nCutter.recipe.mangrove_fence.key") {
        override val result: NMaterial = NMaterial.MANGROVE_FENCE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.MANGROVE_PLANKS
        )
    }

    data object BambooFence: NCutterRecipe("nCutter.recipe.bamboo_fence.key") {
        override val result: NMaterial = NMaterial.BAMBOO_FENCE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BAMBOO_PLANKS
        )
    }

    data object CherryFence: NCutterRecipe("nCutter.recipe.cherry_fence.key") {
        override val result: NMaterial = NMaterial.CHERRY_FENCE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CHERRY_PLANKS
        )
    }

    /* Wooden Fence Gate */
    data object OakFenceGate: NCutterRecipe("nCutter.recipe.oak_fence_gate.key") {
        override val result: NMaterial = NMaterial.OAK_FENCE_GATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_PLANKS
        )
    }

    data object SpruceFenceGate: NCutterRecipe("nCutter.recipe.spruce_fence_gate.key") {
        override val result: NMaterial = NMaterial.SPRUCE_FENCE_GATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.SPRUCE_PLANKS
        )
    }

    data object BirchFenceGate: NCutterRecipe("nCutter.recipe.birch_fence_gate.key") {
        override val result: NMaterial = NMaterial.BIRCH_FENCE_GATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BIRCH_PLANKS
        )
    }

    data object JungleFenceGate: NCutterRecipe("nCutter.recipe.jungle_fence_gate.key") {
        override val result: NMaterial = NMaterial.JUNGLE_FENCE_GATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.JUNGLE_FENCE_GATE
        )
    }

    data object AcaciaFenceGate: NCutterRecipe("nCutter.recipe.acacia_fence_gate.key") {
        override val result: NMaterial = NMaterial.ACACIA_FENCE_GATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.ACACIA_PLANKS
        )
    }

    data object DarkOakFenceGate: NCutterRecipe("nCutter.recipe.dark_oak_fence_gate.key") {
        override val result: NMaterial = NMaterial.DARK_OAK_FENCE_GATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.DARK_OAK_PLANKS
        )
    }

    data object WarpedFenceGate: NCutterRecipe("nCutter.recipe.warped_fence_gate.key") {
        override val result: NMaterial = NMaterial.WARPED_FENCE_GATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.WARPED_FENCE_GATE
        )
    }

    data object CrimsonFenceGate: NCutterRecipe("nCutter.recipe.crimson_fence_gate.key") {
        override val result: NMaterial = NMaterial.CRIMSON_FENCE_GATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CRIMSON_PLANKS
        )
    }

    data object MangroveFenceGate: NCutterRecipe("nCutter.recipe.mangrove_fence_gate.key") {
        override val result: NMaterial = NMaterial.MANGROVE_FENCE_GATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.MANGROVE_PLANKS
        )
    }

    data object BambooFenceGate: NCutterRecipe("nCutter.recipe.bamboo_fence_gate.key") {
        override val result: NMaterial = NMaterial.BAMBOO_FENCE_GATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BAMBOO_PLANKS
        )
    }

    data object CherryFenceGate: NCutterRecipe("nCutter.recipe.cherry_fence_gate.key") {
        override val result: NMaterial = NMaterial.CHERRY_FENCE_GATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CHERRY_PLANKS
        )
    }

    /* Wooden Door */
    data object OakDoor: NCutterRecipe("nCutter.recipe.oak_door.key") {
        override val result: NMaterial = NMaterial.OAK_DOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_PLANKS
        )
    }

    data object SpruceDoor: NCutterRecipe("nCutter.recipe.spruce_door.key") {
        override val result: NMaterial = NMaterial.SPRUCE_DOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.SPRUCE_PLANKS
        )
    }

    data object BirchDoor: NCutterRecipe("nCutter.recipe.birch_door.key") {
        override val result: NMaterial = NMaterial.BIRCH_DOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BIRCH_PLANKS
        )
    }

    data object JungleDoor: NCutterRecipe("nCutter.recipe.jungle_door.key") {
        override val result: NMaterial = NMaterial.JUNGLE_DOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.JUNGLE_PLANKS
        )
    }

    data object AcaciaDoor: NCutterRecipe("nCutter.recipe.acacia_door.key") {
        override val result: NMaterial = NMaterial.ACACIA_DOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.ACACIA_PLANKS
        )
    }

    data object DarkOakDoor: NCutterRecipe("nCutter.recipe.dark_oak_door.key") {
        override val result: NMaterial = NMaterial.DARK_OAK_DOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.DARK_OAK_PLANKS
        )
    }

    data object WarpedDoor: NCutterRecipe("nCutter.recipe.warped_door.key") {
        override val result: NMaterial = NMaterial.WARPED_DOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.WARPED_PLANKS
        )
    }

    data object CrimsonDoor: NCutterRecipe("nCutter.recipe.crimson_door.key") {
        override val result: NMaterial = NMaterial.CRIMSON_DOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CRIMSON_PLANKS
        )
    }

    data object MangroveDoor: NCutterRecipe("nCutter.recipe.mangrove_door.key") {
        override val result: NMaterial = NMaterial.MANGROVE_DOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.MANGROVE_PLANKS
        )
    }

    data object BambooDoor: NCutterRecipe("nCutter.recipe.bamboo_door.key") {
        override val result: NMaterial = NMaterial.BAMBOO_DOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BAMBOO_PLANKS
        )
    }

    data object CherryDoor: NCutterRecipe("nCutter.recipe.cherry_door.key") {
        override val result: NMaterial = NMaterial.CHERRY_DOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CHERRY_PLANKS
        )
    }

    /* Wooden Trapdoor */
    data object OakTrapdoor: NCutterRecipe("nCutter.recipe.oak_trapdoor.key") {
        override val result: NMaterial = NMaterial.OAK_TRAPDOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_PLANKS
        )
    }

    data object SpruceTrapdoor: NCutterRecipe("nCutter.recipe.spruce_trapdoor.key") {
        override val result: NMaterial = NMaterial.SPRUCE_TRAPDOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.SPRUCE_PLANKS
        )
    }

    data object BirchTrapdoor: NCutterRecipe("nCutter.recipe.birch_trapdoor.key") {
        override val result: NMaterial = NMaterial.BIRCH_TRAPDOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BIRCH_PLANKS
        )
    }

    data object JungleTrapdoor: NCutterRecipe("nCutter.recipe.jungle_trapdoor.key") {
        override val result: NMaterial = NMaterial.JUNGLE_TRAPDOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.JUNGLE_PLANKS
        )
    }

    data object AcaciaTrapdoor: NCutterRecipe("nCutter.recipe.acacia_trapdoor.key") {
        override val result: NMaterial = NMaterial.ACACIA_TRAPDOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.ACACIA_PLANKS
        )
    }

    data object DarkOakTrapdoor: NCutterRecipe("nCutter.recipe.dark_oak_trapdoor.key") {
        override val result: NMaterial = NMaterial.DARK_OAK_TRAPDOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.DARK_OAK_PLANKS
        )
    }

    data object WarpedTrapdoor: NCutterRecipe("nCutter.recipe.warped_trapdoor.key") {
        override val result: NMaterial = NMaterial.WARPED_TRAPDOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.WARPED_PLANKS
        )
    }

    data object CrimsonTrapdoor: NCutterRecipe("nCutter.recipe.crimson_trapdoor.key") {
        override val result: NMaterial = NMaterial.CRIMSON_TRAPDOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CRIMSON_PLANKS
        )
    }

    data object MangroveTrapdoor: NCutterRecipe("nCutter.recipe.mangrove_trapdoor.key") {
        override val result: NMaterial = NMaterial.MANGROVE_TRAPDOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.MANGROVE_PLANKS
        )
    }

    data object BambooTrapdoor: NCutterRecipe("nCutter.recipe.bamboo_trapdoor.key") {
        override val result: NMaterial = NMaterial.BAMBOO_TRAPDOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BAMBOO_PLANKS
        )
    }

    data object CherryTrapdoor: NCutterRecipe("nCutter.recipe.cherry_trapdoor.key") {
        override val result: NMaterial = NMaterial.CHERRY_TRAPDOOR
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CHERRY_PLANKS
        )
    }

    /* Wooden Button */
    data object OakButton: NCutterRecipe("nCutter.recipe.oak_button.key") {
        override val result: NMaterial = NMaterial.OAK_BUTTON
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_PLANKS
        )
    }

    data object SpruceButton: NCutterRecipe("nCutter.recipe.spruce_button.key") {
        override val result: NMaterial = NMaterial.SPRUCE_BUTTON
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.SPRUCE_PLANKS
        )
    }

    data object BirchButton: NCutterRecipe("nCutter.recipe.birch_button.key") {
        override val result: NMaterial = NMaterial.BIRCH_BUTTON
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BIRCH_PLANKS
        )
    }

    data object JungleButton: NCutterRecipe("nCutter.recipe.jungle_button.key") {
        override val result: NMaterial = NMaterial.JUNGLE_BUTTON
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.JUNGLE_PLANKS
        )
    }

    data object AcaciaButton: NCutterRecipe("nCutter.recipe.acacia_button.key") {
        override val result: NMaterial = NMaterial.ACACIA_BUTTON
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.ACACIA_PLANKS
        )
    }

    data object DarkOakButton: NCutterRecipe("nCutter.recipe.dark_oak_button.key") {
        override val result: NMaterial = NMaterial.DARK_OAK_BUTTON
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.DARK_OAK_PLANKS
        )
    }

    data object WarpedButton: NCutterRecipe("nCutter.recipe.warped_button.key") {
        override val result: NMaterial = NMaterial.WARPED_BUTTON
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.WARPED_PLANKS
        )
    }

    data object CrimsonButton: NCutterRecipe("nCutter.recipe.crimson_button.key") {
        override val result: NMaterial = NMaterial.CRIMSON_BUTTON
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CRIMSON_PLANKS
        )
    }

    data object MangroveButton: NCutterRecipe("nCutter.recipe.mangrove_button.key") {
        override val result: NMaterial = NMaterial.MANGROVE_BUTTON
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.MANGROVE_PLANKS
        )
    }

    data object BambooButton: NCutterRecipe("nCutter.recipe.bamboo_button.key") {
        override val result: NMaterial = NMaterial.BAMBOO_BUTTON
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BAMBOO_PLANKS
        )
    }

    data object CherryButton: NCutterRecipe("nCutter.recipe.cherry_button.key") {
        override val result: NMaterial = NMaterial.CHERRY_BUTTON
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CHERRY_PLANKS
        )
    }

    /* Wooden Pressure Plate */
    data object OakPressurePlate: NCutterRecipe("nCutter.recipe.oak_pressure_plate.key") {
        override val result: NMaterial = NMaterial.OAK_PRESSURE_PLATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_PLANKS
        )
    }

    data object SprucePressurePlate: NCutterRecipe("nCutter.recipe.spruce_pressure_plate.key") {
        override val result: NMaterial = NMaterial.SPRUCE_PRESSURE_PLATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.SPRUCE_PLANKS
        )
    }

    data object BirchPressurePlate: NCutterRecipe("nCutter.recipe.birch_pressure_plate.key") {
        override val result: NMaterial = NMaterial.BIRCH_PRESSURE_PLATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BIRCH_PLANKS
        )
    }

    data object JunglePressurePlate: NCutterRecipe("nCutter.recipe.jungle_pressure_plate.key") {
        override val result: NMaterial = NMaterial.JUNGLE_PRESSURE_PLATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.JUNGLE_PLANKS
        )
    }

    data object AcaciaPressurePlate: NCutterRecipe("nCutter.recipe.acacia_pressure_plate.key") {
        override val result: NMaterial = NMaterial.ACACIA_PRESSURE_PLATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.ACACIA_PLANKS
        )
    }

    data object DarkOakPressurePlate: NCutterRecipe("nCutter.recipe.dark_oak_pressure_plate.key") {
        override val result: NMaterial = NMaterial.DARK_OAK_PRESSURE_PLATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.DARK_OAK_PLANKS
        )
    }

    data object WarpedPressurePlate: NCutterRecipe("nCutter.recipe.warped_pressure_plate.key") {
        override val result: NMaterial = NMaterial.WARPED_PRESSURE_PLATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.WARPED_PLANKS
        )
    }

    data object CrimsonPressurePlate: NCutterRecipe("nCutter.recipe.crimson_pressure_plate.key") {
        override val result: NMaterial = NMaterial.CRIMSON_PRESSURE_PLATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CRIMSON_PLANKS
        )
    }

    data object MangrovePressurePlate: NCutterRecipe("nCutter.recipe.mangrove_pressure_plate.key") {
        override val result: NMaterial = NMaterial.MANGROVE_PRESSURE_PLATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.MANGROVE_PLANKS
        )
    }

    data object BambooPressurePlate: NCutterRecipe("nCutter.recipe.bamboo_pressure_plate.key") {
        override val result: NMaterial = NMaterial.BAMBOO_PRESSURE_PLATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BAMBOO_PLANKS
        )
    }

    data object CherryPressurePlate: NCutterRecipe("nCutter.recipe.cherry_pressure_plate.key") {
        override val result: NMaterial = NMaterial.CHERRY_PRESSURE_PLATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CHERRY_PLANKS
        )
    }

    /* Wooden Sign */
    data object OakSign: NCutterRecipe("nCutter.recipe.oak_sign.key") {
        override val result: NMaterial = NMaterial.OAK_SIGN
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_PLANKS
        )
    }

    data object SpruceSign: NCutterRecipe("nCutter.recipe.spruce_sign.key") {
        override val result: NMaterial = NMaterial.SPRUCE_SIGN
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.SPRUCE_PLANKS
        )
    }

    data object BirchSign: NCutterRecipe("nCutter.recipe.birch_sign.key") {
        override val result: NMaterial = NMaterial.BIRCH_SIGN
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BIRCH_PLANKS
        )
    }

    data object JungleSign: NCutterRecipe("nCutter.recipe.jungle_sign.key") {
        override val result: NMaterial = NMaterial.JUNGLE_SIGN
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.JUNGLE_SIGN
        )
    }

    data object AcaciaSign: NCutterRecipe("nCutter.recipe.acacia_sign.key") {
        override val result: NMaterial = NMaterial.ACACIA_SIGN
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.ACACIA_PLANKS
        )
    }

    data object DarkOakSign: NCutterRecipe("nCutter.recipe.dark_oak_sign.key") {
        override val result: NMaterial = NMaterial.DARK_OAK_SIGN
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.DARK_OAK_PLANKS
        )
    }

    data object WarpedSign: NCutterRecipe("nCutter.recipe.warped_sign.key") {
        override val result: NMaterial = NMaterial.WARPED_SIGN
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.WARPED_PLANKS
        )
    }

    data object CrimsonSign: NCutterRecipe("nCutter.recipe.crimson_sign.key") {
        override val result: NMaterial = NMaterial.CRIMSON_SIGN
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CRIMSON_PLANKS
        )
    }

    data object MangroveSign: NCutterRecipe("nCutter.recipe.mangrove_sign.key") {
        override val result: NMaterial = NMaterial.MANGROVE_SIGN
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.MANGROVE_PLANKS
        )
    }

    data object BambooSign: NCutterRecipe("nCutter.recipe.bamboo_sign.key") {
        override val result: NMaterial = NMaterial.BAMBOO_SIGN
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BAMBOO_PLANKS
        )
    }

    data object CherrySign: NCutterRecipe("nCutter.recipe.cherry_sign.key") {
        override val result: NMaterial = NMaterial.CHERRY_SIGN
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CHERRY_PLANKS
        )
    }

    /* Wooden Boat/Raft */
    data object OakBoat: NCutterRecipe("nCutter.recipe.oak_boat.key") {
        override val result: NMaterial = NMaterial.OAK_BOAT
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_PLANKS
        )
    }

    data object SpruceBoat: NCutterRecipe("nCutter.recipe.spruce_boat.key") {
        override val result: NMaterial = NMaterial.SPRUCE_BOAT
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.SPRUCE_PLANKS
        )
    }

    data object BirchBoat: NCutterRecipe("nCutter.recipe.birch_boat.key") {
        override val result: NMaterial = NMaterial.BIRCH_BOAT
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BIRCH_PLANKS
        )
    }

    data object JungleBoat: NCutterRecipe("nCutter.recipe.jungle_boat.key") {
        override val result: NMaterial = NMaterial.JUNGLE_BOAT
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.JUNGLE_PLANKS
        )
    }

    data object AcaciaBoat: NCutterRecipe("nCutter.recipe.acacia_boat.key") {
        override val result: NMaterial = NMaterial.ACACIA_BOAT
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.ACACIA_PLANKS
        )
    }

    data object DarkOakBoat: NCutterRecipe("nCutter.recipe.dark_oak_boat.key") {
        override val result: NMaterial = NMaterial.DARK_OAK_BOAT
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.DARK_OAK_PLANKS
        )
    }

    data object MangroveBoat: NCutterRecipe("nCutter.recipe.mangrove_boat.key") {
        override val result: NMaterial = NMaterial.MANGROVE_BOAT
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.MANGROVE_PLANKS
        )
    }

    data object BambooRaft: NCutterRecipe("nCutter.recipe.bamboo_raft.key") {
        override val result: NMaterial = NMaterial.BAMBOO_RAFT
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BAMBOO_PLANKS
        )
    }

    data object CherryBoat: NCutterRecipe("nCutter.recipe.cherry_boat.key") {
        override val result: NMaterial = NMaterial.CHERRY_BOAT
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CHERRY_PLANKS
        )
    }

    /* Wooden Items */
    data object Stick: NCutterRecipe("nCutter.recipe.stick.key") {
        override val result: NMaterial = NMaterial.STICK
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_PLANKS, NMaterial.SPRUCE_PLANKS, NMaterial.BIRCH_PLANKS,
            NMaterial.JUNGLE_PLANKS, NMaterial.ACACIA_PLANKS, NMaterial.DARK_OAK_PLANKS, NMaterial.WARPED_PLANKS,
            NMaterial.CRIMSON_PLANKS, NMaterial.MANGROVE_PLANKS, NMaterial.BAMBOO_PLANKS, NMaterial.CRIMSON_PLANKS
        )
    }

    data object Ladder: NCutterRecipe("nCutter.recipe.ladder.key") {
        override val result: NMaterial = NMaterial.LADDER
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_PLANKS, NMaterial.SPRUCE_PLANKS, NMaterial.BIRCH_PLANKS,
            NMaterial.JUNGLE_PLANKS, NMaterial.ACACIA_PLANKS, NMaterial.DARK_OAK_PLANKS, NMaterial.WARPED_PLANKS,
            NMaterial.CRIMSON_PLANKS, NMaterial.MANGROVE_PLANKS, NMaterial.BAMBOO_PLANKS, NMaterial.CRIMSON_PLANKS
        )
    }

    data object Bowl: NCutterRecipe("nCutter.recipe.bowl.key") {
        override val result: NMaterial = NMaterial.BOWL
        override val resultAmount: Int = 2
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_PLANKS, NMaterial.SPRUCE_PLANKS, NMaterial.BIRCH_PLANKS,
            NMaterial.JUNGLE_PLANKS, NMaterial.ACACIA_PLANKS, NMaterial.DARK_OAK_PLANKS, NMaterial.WARPED_PLANKS,
            NMaterial.CRIMSON_PLANKS, NMaterial.MANGROVE_PLANKS, NMaterial.BAMBOO_PLANKS, NMaterial.CRIMSON_PLANKS
        )
    }
}