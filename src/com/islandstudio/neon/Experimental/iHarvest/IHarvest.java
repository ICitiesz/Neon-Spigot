package com.islandstudio.neon.Experimental.iHarvest;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class IHarvest {
    public static void setEventHandler(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        ItemStack heldItem = e.getItem();

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Ageable ageable;

            if (block != null) {
                switch (block.getType()) {
                    case WHEAT:

                    case CARROTS:

                    case POTATOES:

                    case BEETROOTS:

                    case NETHER_WART: {
                        ageable = (Ageable) block.getBlockData();

                        if (ageable.getAge() == ageable.getMaximumAge()) {
                            harvest(block, heldItem, e.hasItem());

                            ageable.setAge(0);
                            block.setBlockData(ageable);
                        }
                        break;
                    }
                }
            }
        }
    }

    private static void harvest(Block block, ItemStack heldItem, boolean hasItem) {
        ItemStack unplantable = null;
        ItemStack plantable = null;
        World world = block.getWorld();
        Location location = block.getLocation();

        int itemAmount = 0;

        if (hasItem) {
            assert heldItem != null;

            for (ItemStack drops : block.getDrops(heldItem)) {
                switch (drops.getType()) {
                    case WHEAT:

                    case POISONOUS_POTATO:

                    case BEETROOT: {
                        unplantable = drops;
                        break;
                    }

                    case WHEAT_SEEDS:

                    case CARROT:

                    case POTATO:

                    case BEETROOT_SEEDS:

                    case NETHER_WART: {
                        plantable = drops;
                        itemAmount = itemAmount + drops.getAmount();
                        break;
                    }
                }
            }
        } else {
            for (ItemStack drops : block.getDrops()) {
                switch (drops.getType()) {
                    case WHEAT:

                    case POISONOUS_POTATO:

                    case BEETROOT: {
                        unplantable = drops;
                        break;
                    }

                    case WHEAT_SEEDS:

                    case CARROT:

                    case POTATO:

                    case BEETROOT_SEEDS:

                    case NETHER_WART: {
                        plantable = drops;
                        itemAmount = itemAmount + drops.getAmount();
                        break;
                    }
                }
            }
        }

        if (unplantable != null) {
            switch (unplantable.getType()) {
                case WHEAT:

                case POISONOUS_POTATO:

                case BEETROOT: {
                    world.dropItemNaturally(location, unplantable);
                    break;
                }
            }
        }

        if (plantable != null) {
            switch (plantable.getType()) {
                case WHEAT_SEEDS:

                case CARROT:

                case POTATO:

                case BEETROOT_SEEDS:

                case NETHER_WART: {
                    if ((itemAmount - 1) > 0) {
                        plantable.setAmount(itemAmount - 1);
                        world.dropItemNaturally(location, plantable);
                    }
                    break;
                }
            }
        }
    }
}
