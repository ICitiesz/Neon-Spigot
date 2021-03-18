package com.islandstudio.neon.Experimental;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PlayerInfo {
    private static double health;
    private static int food;
    private static org.bukkit.Location location;
    private static HashMap<Integer, ItemStack> items = new HashMap<>();

    public PlayerInfo(double health, int food, org.bukkit.Location location, HashMap<Integer, ItemStack> items) {
        PlayerInfo.health = health;
        PlayerInfo.food = food;
        PlayerInfo.location = location;
        PlayerInfo.items = items;
    }

    public double getHealth() {
        return health;
    }

    public int getFood() {
        return food;
    }

    public org.bukkit.Location getLocation() {
        return location;
    }

    public HashMap<Integer, ItemStack> getItems() {
        return items;
    }

}
