package com.islandstudio.neon.Experimental.OreExcavation;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class OreExc {
    public static void blockBreak(BlockBreakEvent e) {
        HashSet<Block> savedBlock = new HashSet<>();
        Block block = e.getBlock();

        if (e.getPlayer().isSneaking()) {
            getBlocks(block, block.getType(), savedBlock);
        }

        for (Block blocks : savedBlock) {
            blocks.breakNaturally();
        }

        savedBlock.clear();

//        System.out.println(e.getBlock().getRelative(BlockFace.UP));
//        System.out.println(block.getBoundingBox());
//        System.out.println(block.getType());
    }

    private static void getBlocks(Block block, Material material , HashSet<Block> blockList) {
        if (block.getType().equals(material) && !blockList.contains(block) && blockList.size() < 10) {
            blockList.add(block);

            for (BlockFace blockFace : BlockFace.values()) {
                if (!blockFace.equals(BlockFace.SELF)) {
                    getBlocks(block.getRelative(blockFace), material, blockList);
                }
            }
        }
    }

    public static void playerInteract(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            ItemStack itemStack = e.getItem();

            if (itemStack != null) {
                //System.out.println(itemStack.getType());

                switch (itemStack.getType().name()) {
                    case "WOODEN_PICKAXE":

                    case "STONE_PICKAXE":

                    case "GOLDEN_PICKAXE":

                    case "IRON_PICKAXE":

                    case "DIAMOND_PICKAXE":

                    case "NETHERITE_PICKAXE": {
                        for (BlockFace blockFace : BlockFace.values()) {

                        }

                        break;
                    }
                }
            }
        }
    }
}
