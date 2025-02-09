package com.islandstudio.neon.stable.core.recipe

import com.islandstudio.neon.stable.core.recipe.component.AbstractRecipeHolder

sealed class NSmelterRecipe(keyName: String): AbstractRecipeHolder(keyName) {
    companion object : RecipeHolderHandler<NSmelterRecipe>(NSmelterRecipe::class);

    /* General Smeltable Items */
    data object Glass: NSmelterRecipe("nSmelter.recipe.glass.key") {
        override val result: NMaterial = NMaterial.GLASS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.SAND, NMaterial.RED_SAND
        )
    }

    data object Stone: NSmelterRecipe("nSmelter.recipe.stone.key") {
        override val result: NMaterial = NMaterial.STONE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.COBBLESTONE
        )
    }

    data object Deepslate: NSmelterRecipe("nSmelter.recipe.deepslate.key") {
        override val result: NMaterial = NMaterial.DEEPSLATE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.COBBLED_DEEPSLATE
        )
    }

    data object SmoothSandstone: NSmelterRecipe("nSmelter.recipe.smooth_sandstone.key") {
        override val result: NMaterial = NMaterial.SMOOTH_SANDSTONE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.SANDSTONE
        )
    }

    data object SmoothRedSandstone: NSmelterRecipe("nSmelter.recipe.smooth_red_sandstone.key") {
        override val result: NMaterial = NMaterial.SMOOTH_RED_SANDSTONE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.RED_SANDSTONE
        )
    }

    data object SmoothStone: NSmelterRecipe("nSmelter.recipe.smooth_stone.key") {
        override val result: NMaterial = NMaterial.SMOOTH_STONE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.STONE
        )
    }

    data object SmoothQuartz: NSmelterRecipe("nSmelter.recipe.smooth_quartz.key") {
        override val result: NMaterial = NMaterial.SMOOTH_QUARTZ
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.QUARTZ_BLOCK
        )
    }

    data object Brick: NSmelterRecipe("nSmelter.recipe.brick.key") {
        override val result: NMaterial = NMaterial.BRICK
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CLAY_BALL
        )
    }

    data object NetherBrick: NSmelterRecipe("nSmelter.recipe.nether_brick.key") {
        override val result: NMaterial = NMaterial.NETHER_BRICK
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.NETHERRACK
        )
    }

    data object CrackedNetherBricks: NSmelterRecipe("nSmelter.recipe.cracked_nether_bricks.key") {
        override val result: NMaterial = NMaterial.CRACKED_NETHER_BRICKS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.NETHER_BRICKS
        )
    }

    data object Terracotta: NSmelterRecipe("nSmelter.recipe.terracotta.key") {
        override val result: NMaterial = NMaterial.TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CLAY
        )
    }

    data object CrackedStoneBricks: NSmelterRecipe("nSmelter.recipe.cracked_stone_bricks.key") {
        override val result: NMaterial = NMaterial.CRACKED_STONE_BRICKS
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.STONE_BRICKS
        )
    }

    data object PoppedChorusFruit: NSmelterRecipe("nSmelter.recipe.popped_chorus_fruit.key") {
        override val result: NMaterial = NMaterial.POPPED_CHORUS_FRUIT
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CHORUS_FRUIT
        )
    }

    data object Sponge: NSmelterRecipe("nSmelter.recipe.sponge.key") {
        override val result: NMaterial = NMaterial.SPONGE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.WET_SPONGE
        )
    }

    data object Charcoal: NSmelterRecipe("nSmelter.recipe.charcoal.key") {
        override val result: NMaterial = NMaterial.CHARCOAL
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.OAK_WOOD, NMaterial.SPRUCE_WOOD, NMaterial.BIRCH_WOOD, NMaterial.JUNGLE_WOOD,
            NMaterial.ACACIA_WOOD, NMaterial.DARK_OAK_WOOD, NMaterial.MANGROVE_WOOD, NMaterial.CHERRY_WOOD,
            NMaterial.OAK_LOG, NMaterial.SPRUCE_LOG, NMaterial.BIRCH_LOG, NMaterial.JUNGLE_LOG, NMaterial.ACACIA_LOG,
            NMaterial.DARK_OAK_LOG, NMaterial.MANGROVE_LOG, NMaterial.CHERRY_LOG, NMaterial.STRIPPED_OAK_WOOD,
            NMaterial.STRIPPED_SPRUCE_WOOD, NMaterial.STRIPPED_BIRCH_WOOD, NMaterial.STRIPPED_JUNGLE_WOOD,
            NMaterial.STRIPPED_ACACIA_WOOD, NMaterial.STRIPPED_DARK_OAK_WOOD, NMaterial.STRIPPED_MANGROVE_WOOD,
            NMaterial.STRIPPED_CHERRY_WOOD, NMaterial.STRIPPED_OAK_LOG, NMaterial.STRIPPED_SPRUCE_LOG,
            NMaterial.STRIPPED_BIRCH_LOG, NMaterial.STRIPPED_JUNGLE_LOG, NMaterial.STRIPPED_ACACIA_LOG,
            NMaterial.STRIPPED_DARK_OAK_LOG, NMaterial.STRIPPED_MANGROVE_LOG, NMaterial.STRIPPED_CHERRY_LOG
        )
    }

    /* Ore Block */
    data object IronBlock: NSmelterRecipe("nSmelter.recipe.iron_block.key") {
        override val result: NMaterial = NMaterial.IRON_BLOCK
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.RAW_IRON_BLOCK
        )
    }

    data object CopperBlock: NSmelterRecipe("nSmelter.recipe.copper_block.key") {
        override val result: NMaterial = NMaterial.COPPER_BLOCK
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.RAW_COPPER_BLOCK
        )
    }

    data object GoldBlock: NSmelterRecipe("nSmelter.recipe.gold_block.key") {
        override val result: NMaterial = NMaterial.GOLD_BLOCK
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.RAW_GOLD_BLOCK
        )
    }

    /* Dye */
    data object GreenDye: NSmelterRecipe("nSmelter.recipe.green_dye.key") {
        override val result: NMaterial = NMaterial.GREEN_DYE
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CACTUS
        )
    }

    data object LimeDye: NSmelterRecipe("nSmelter.recipe.lime_dye.key") {
        override val result: NMaterial = NMaterial.LIME_DYE
        override val ingredients: HashSet<NMaterial> =setIngredients(
            NMaterial.SEA_PICKLE
        )
    }

    /* Colored Glazed Terracotta */
    data object WhiteGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.white_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.WHITE_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.WHITE_TERRACOTTA
        )
    }

    data object OrangeGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.orange_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.ORANGE_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.ORANGE_TERRACOTTA
        )
    }

    data object MagentaGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.magenta_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.MAGENTA_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.MAGENTA_TERRACOTTA
        )
    }

    data object LightBlueGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.light_blue_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.LIGHT_BLUE_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.LIGHT_BLUE_TERRACOTTA
        )
    }

    data object YellowGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.yellow_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.YELLOW_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.YELLOW_TERRACOTTA
        )
    }

    data object LimeGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.lime_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.LIME_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.LIME_TERRACOTTA
        )
    }

    data object PinkGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.pink_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.PINK_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.PINK_TERRACOTTA
        )
    }

    data object GrayGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.gray_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.GRAY_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.GRAY_TERRACOTTA
        )
    }

    data object LightGrayGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.light_gray_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.LIGHT_GRAY_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.LIGHT_GRAY_TERRACOTTA
        )
    }

    data object CyanGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.cyan_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.CYAN_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.CYAN_TERRACOTTA
        )
    }

    data object PurpleGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.purple_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.PURPLE_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.PURPLE_TERRACOTTA
        )
    }

    data object BlueGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.blue_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.BLUE_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BLUE_TERRACOTTA
        )
    }

    data object BrownGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.brown_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.BROWN_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BROWN_TERRACOTTA
        )
    }

    data object GreenGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.green_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.GREEN_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.GREEN_TERRACOTTA
        )
    }

    data object RedGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.red_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.RED_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.RED_TERRACOTTA
        )
    }

    data object BlackGlazedTerracotta: NSmelterRecipe("nSmelter.recipe.black_glazed_terracotta.key") {
        override val result: NMaterial = NMaterial.BLACK_GLAZED_TERRACOTTA
        override val ingredients: HashSet<NMaterial> = setIngredients(
            NMaterial.BLACK_TERRACOTTA
        )
    }
}