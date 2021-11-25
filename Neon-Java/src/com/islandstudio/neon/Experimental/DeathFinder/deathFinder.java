package com.islandstudio.neon.Experimental.DeathFinder;

import com.islandstudio.neon.MainCore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class deathFinder {
    private static Plugin plugin = MainCore.getPlugin(MainCore.class);
    private static final NamespacedKey PLAYER_UUID = new NamespacedKey(plugin, "PlayerUUID");
    private static final NamespacedKey PLAYER_NAME = new NamespacedKey(plugin, "PlayerName");

    protected static Location playerLastPos;

    public static void removeData(Player player) {
        Location location = player.getLocation();
        Block block = location.getBlock();

        if (block.getType().equals(Material.ENDER_CHEST)) {
            TileState tileState = (TileState) block.getState();
            PersistentDataContainer dataContainer = tileState.getPersistentDataContainer();

            if (dataContainer.has(PLAYER_UUID, PersistentDataType.STRING)) {
                player.sendMessage("Removed value: " + Objects.requireNonNull(dataContainer.get(new NamespacedKey(plugin, "PlayerUUID"), PersistentDataType.STRING)));
                dataContainer.remove(PLAYER_UUID);
                tileState.update();
                player.sendMessage("Value removed!");
            } else {
                player.sendMessage("No such value to remove!");
            }

            player.sendMessage(tileState.getType().name());
        }
    }

    public static void readData(Player player) {
        Location location = player.getLocation();
        Block block = location.getBlock();

        if (block.getType().equals(Material.ENDER_CHEST)) {
            TileState tileState = (TileState) block.getState();
            PersistentDataContainer dataContainer = tileState.getPersistentDataContainer();

            if (dataContainer.has(PLAYER_UUID, PersistentDataType.STRING)) {
                player.sendMessage("Stored value: " + Objects.requireNonNull(dataContainer.get(PLAYER_UUID, PersistentDataType.STRING)));
            } else {
                player.sendMessage("No such value!");
                player.sendMessage(tileState.getType().name());
            }
        }
    }

    public static void movementSession(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location playerLocation_1 = player.getLocation().subtract(0, 1, 0);
        Location playerLocation_2 = player.getLocation();
        Block playerBlock = playerLocation_1.getBlock();

        if (!playerBlock.getType().equals(Material.LAVA) && !playerLocation_2.getBlock().getType().equals(Material.LAVA)
                && !playerBlock.getType().isAir() && player.isOnGround()) {
            playerLastPos = playerLocation_1.add(0, 1, 0);
        }
    }

    public static void deathSession(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Location playerLocation_1 = player.getLocation();
        Block playerBlock = playerLocation_1.getBlock();

        double playerY = Math.ceil(playerLocation_1.getY());
        double playerYBlock = playerLocation_1.getBlockY();

        // Check if is it a valid place to put ender chest //
        if ((playerY > playerYBlock && !playerBlock.getType().isAir() && !playerBlock.getType().equals(Material.LAVA))
                || (!playerBlock.getType().isAir() && !playerBlock.getType().equals(Material.WATER) && !playerBlock.getType().equals(Material.LAVA) && playerBlock.isPassable())) {

            Location playerLocation_2;

            if (playerBlock.getType().name().contains("BANNER")) {
                playerLocation_2 = playerLocation_1.add(0, 2, 0);
            } else {
                playerLocation_2 = playerLocation_1.add(0, 1, 0);
            }

            Block playerUpperBlock = playerLocation_2.getBlock();
            playerUpperBlock.setType(Material.ENDER_CHEST);

            //  Assign data to the ender chest. //
            if (playerUpperBlock.getType().equals(Material.ENDER_CHEST)) {
                setData(playerUpperBlock, player);
            } else {
                player.sendMessage(ChatColor.YELLOW + "[DEBUG] " + ChatColor.RED + "Not an ender chest!");
            }

            player.sendMessage(ChatColor.YELLOW + "[DEBUG] " + ChatColor.GREEN + "Block Changed! (Level 1)");
        } else {
            if (playerBlock.getType().equals(Material.LAVA)) {
                Block lastPosBlock_1 = playerLastPos.getBlock();

                double lastPosY = Math.ceil(playerLastPos.getY());
                double lastPosBlockY = playerLastPos.getBlockY();

                if ((lastPosY > lastPosBlockY && ! lastPosBlock_1.getType().equals(Material.LAVA))
                        || (! lastPosBlock_1.getType().isSolid() && !lastPosBlock_1.getType().isAir() && ! lastPosBlock_1.getType().equals(Material.WATER) && !lastPosBlock_1.getType().equals(Material.LAVA))) {

                    Block lastPosBlock_2 = playerLastPos.add(0, 1, 0).getBlock();

                    lastPosBlock_2. setType(Material.ENDER_CHEST);

                    if (lastPosBlock_2.getType().equals(Material.ENDER_CHEST)) {
                        setData(lastPosBlock_2, player);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "[DEBUG] " + ChatColor.RED + "Not an ender chest!");
                    }
                } else {
                    lastPosBlock_1.setType(Material.ENDER_CHEST);

                    if (lastPosBlock_1.getType().equals(Material.ENDER_CHEST)) {
                        setData(lastPosBlock_1, player);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "[DEBUG] " + ChatColor.RED + "Not an ender chest!");
                    }
                }
            }
        }
    }

    public static void respawnSession(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
    }

    public static void testEvent4(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        ItemStack itemStack = new ItemStack(Material.COMPASS);
        ItemMeta itemMeta = itemStack.getItemMeta();


        //player.getInventory().setItemInMainHand(new ItemStack(Material.COMPASS));
    }

    public static void chestInteraction(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block clickedBlock = e.getClickedBlock();
        Action action = e.getAction();
        ItemStack item = e.getItem();

        if (clickedBlock != null) {
            if (clickedBlock.getType().equals(Material.ENDER_CHEST)) {
                TileState enderChest = (TileState) clickedBlock.getState();
                PersistentDataContainer dataContainer = enderChest.getPersistentDataContainer();

                if (Objects.requireNonNull(dataContainer.get(PLAYER_UUID, PersistentDataType.STRING)).equalsIgnoreCase(player.getUniqueId().toString())) {
                    if (!e.hasItem()) {
                        if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                            //e.setCancelled(true);
                        } else if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                            //e.setCancelled(true);
                            player.sendMessage("Please use the given compass to open it!");
                        }
                    } else {
                        if (item != null) {
                            if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                                e.setCancelled(true);
                            } else if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
                                if (item.equals(new ItemStack(Material.COMPASS))) {
                                    e.setCancelled(false);
                                } else {
                                    e.setCancelled(true);
                                    player.sendMessage("Please use the given compass to open it!");
                                }
                            }
                        }
                    }
                } else {
                    //e.setCancelled(true);
                    player.sendMessage("That is not yours!");
                }
            }
        }
    }

    private static void setData(Block targetBlock, Player player) {
        TileState tileState = (TileState) targetBlock.getState();
        PersistentDataContainer dataContainer = tileState.getPersistentDataContainer();

        if (!dataContainer.has(PLAYER_UUID, PersistentDataType.STRING)) {
            dataContainer.set(PLAYER_UUID, PersistentDataType.STRING, player.getUniqueId().toString());

            tileState.update();

            player.sendMessage(ChatColor.YELLOW + "[DEBUG] " + ChatColor.GREEN + "Value stored!");
            player.sendMessage(ChatColor.YELLOW + "[DEBUG] " + ChatColor.WHITE + "Stored value: " + ChatColor.GREEN + dataContainer.get(PLAYER_UUID, PersistentDataType.STRING));
        } else {
            player.sendMessage("An existing value stored!");
            player.sendMessage("Stored value: " + Objects.requireNonNull(dataContainer.get(PLAYER_UUID, PersistentDataType.STRING)));
        }
    }

    public static void test2(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        ItemStack itemStack = playerInventory.getItemInMainHand();
        ItemMeta itemMeta = itemStack.getItemMeta();



        player.sendMessage(player.getCompassTarget().toString());
        player.sendMessage(player.getWorld().getSpawnLocation().toString());
    }

}
