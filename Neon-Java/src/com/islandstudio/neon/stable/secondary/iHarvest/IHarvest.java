package com.islandstudio.neon.stable.secondary.iHarvest;

import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.primary.iServerConfig.IServerConfig;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class IHarvest {

    public static class Handler {
        /**
         * Initialization for iHarvest
         */
        public static void init() {
            final EventController eventController = new EventController();

            if (!(Boolean) IServerConfig.getExternalServerConfigValue("iHarvest")) {
                IConstructor.disableEvent(eventController);
                return;
            }

            IConstructor.enableEvent(eventController);
        }
    }

    /**
     * Crop harvest operation.
     * @param block The crop.
     * @param heldItem Current using tool.
     * @param hasItem Using bare hand or using tool.
     */
    private static void harvest(Block block, ItemStack heldItem, boolean hasItem) {
        ItemStack unplantable = null;
        ItemStack plantable = null;
        World world = block.getWorld();
        Location location = block.getLocation();

        int itemAmount = 0;

        if (hasItem) {
            if (heldItem == null) return;

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
                    if (!((itemAmount - 1) > 0)) return;

                    plantable.setAmount(itemAmount - 1);
                    world.dropItemNaturally(location, plantable);
                    break;
                }
            }
        }
    }

    private static class EventController implements Listener {
        @EventHandler
        private void onPlayerInteract(PlayerInteractEvent e) {
            Block block = e.getClickedBlock();
            ItemStack heldItem = e.getItem();

            if (e.getHand() != EquipmentSlot.HAND) return;

            if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

            Ageable ageable;
            
            if (block == null) return;
            
            switch (block.getType()) {
                case WHEAT:
                case CARROTS:
                case POTATOES:
                case BEETROOTS:
                case NETHER_WART: {
                    ageable = (Ageable) block.getBlockData();

                    if (ageable.getAge() != ageable.getMaximumAge()) return;

                    harvest(block, heldItem, e.hasItem());
                    block.getWorld().playSound(block.getLocation().add(0.5, 0, 0.5), Sound.ITEM_CROP_PLANT, 1f, 1f);
                    ageable.setAge(0);
                    block.setBlockData(ageable);
                }
            }
        }
    }
}
