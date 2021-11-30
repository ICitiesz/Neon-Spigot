package com.islandstudio.neon.Stable.New.features.iHarvest;

import com.islandstudio.neon.Stable.New.Utilities.ServerCFGHandler;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class IHarvest {
    private static final ArrayList<Player> players = new ArrayList<>();

    public static void addPlayer(Player player) throws IOException, ParseException {
        boolean isEnabled = (Boolean) ServerCFGHandler.getValue().get("iHarvest");

        if (isEnabled) {
            if (!players.contains(player)) {
                players.add(player);
            }
        } else {
            if (players.size() > 0) {
                players.clear();
            }
        }
    }

    public static void removePlayer(Player player) {
        players.remove(player);
    }

    public static void setEventHandler(PlayerInteractEvent e) {
        if (!players.contains(e.getPlayer())) return;

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
                            block.getWorld().playSound(block.getLocation().add(0.5, 0, 0.5), Sound.ITEM_CROP_PLANT, 1f, 1f);
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
