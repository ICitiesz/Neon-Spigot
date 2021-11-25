package com.islandstudio.neon.Experimental.iCutter;

import org.bukkit.Material;

public enum WoodItems {
    OAK(Material.getMaterial("OAK_STAIRS"), Material.getMaterial("OAK_SLAB"),
            Material.getMaterial("OAK_FENCE"), Material.getMaterial("OAK_DOOR"),
            Material.getMaterial("OAK_BUTTON"), Material.getMaterial("OAK_TRAPDOOR"),
            Material.getMaterial("OAK_FENCE_GATE"), Material.getMaterial("OAK_PRESSURE_PLATE"),
            Material.getMaterial("OAK_SIGN"), Material.getMaterial("OAK_BOAT")),

    SPRUCE(Material.getMaterial("SPRUCE_STAIRS"), Material.getMaterial("SPRUCE_SLAB"),
            Material.getMaterial("SPRUCE_FENCE"), Material.getMaterial("SPRUCE_DOOR"),
            Material.getMaterial("SPRUCE_BUTTON"), Material.getMaterial("SPRUCE_TRAPDOOR"),
            Material.getMaterial("SPRUCE_FENCE_GATE"), Material.getMaterial("SPRUCE_PRESSURE_PLATE"),
            Material.getMaterial("SPRUCE_SIGN"), Material.getMaterial("SPRUCE_BOAT")),

    BIRCH(Material.getMaterial("BIRCH_STAIRS"), Material.getMaterial("BIRCH_SLAB"),
            Material.getMaterial("BIRCH_FENCE"), Material.getMaterial("BIRCH_DOOR"),
            Material.getMaterial("BIRCH_BUTTON"), Material.getMaterial("BIRCH_TRAPDOOR"),
            Material.getMaterial("BIRCH_FENCE_GATE"), Material.getMaterial("BIRCH_PRESSURE_PLATE"),
            Material.getMaterial("BIRCH_SIGN"), Material.getMaterial("BIRCH_BOAT")),

    JUNGLE(Material.getMaterial("JUNGLE_STAIRS"), Material.getMaterial("JUNGLE_SLAB"),
            Material.getMaterial("JUNGLE_FENCE"), Material.getMaterial("JUNGLE_DOOR"),
            Material.getMaterial("JUNGLE_BUTTON"), Material.getMaterial("JUNGLE_TRAPDOOR"),
            Material.getMaterial("JUNGLE_FENCE_GATE"), Material.getMaterial("JUNGLE_PRESSURE_PLATE"),
            Material.getMaterial("JUNGLE_SIGN"), Material.getMaterial("JUNGLE_BOAT")),

    ACACIA(Material.getMaterial("ACACIA_STAIRS"), Material.getMaterial("ACACIA_SLAB"),
            Material.getMaterial("ACACIA_FENCE"), Material.getMaterial("ACACIA_DOOR"),
            Material.getMaterial("ACACIA_BUTTON"), Material.getMaterial("ACACIA_TRAPDOOR"),
            Material.getMaterial("ACACIA_FENCE_GATE"), Material.getMaterial("ACACIA_PRESSURE_PLATE"),
            Material.getMaterial("ACACIA_SIGN"), Material.getMaterial("ACACIA_BOAT")),

    DARK_OAK(Material.getMaterial("DARK_OAK_STAIRS"), Material.getMaterial("DARK_OAK_SLAB"),
            Material.getMaterial("DARK_OAK_FENCE"), Material.getMaterial("DARK_OAK_DOOR"),
            Material.getMaterial("DARK_OAK_BUTTON"), Material.getMaterial("DARK_OAK_TRAPDOOR"),
            Material.getMaterial("DARK_OAK_FENCE_GATE"), Material.getMaterial("DARK_OAK_PRESSURE_PLATE"),
            Material.getMaterial("DARK_OAK_SIGN"), Material.getMaterial("DARK_OAK_BOAT")),

    WARPED(Material.getMaterial("WARPED_STAIRS"), Material.getMaterial("WARPED_SLAB"),
            Material.getMaterial("WARPED_FENCE"), Material.getMaterial("WARPED_DOOR"),
            Material.getMaterial("WARPED_BUTTON"), Material.getMaterial("WARPED_TRAPDOOR"),
            Material.getMaterial("WARPED_FENCE_GATE"), Material.getMaterial("WARPED_PRESSURE_PLATE"),
            Material.getMaterial("WARPED_SIGN"), null),

    CRIMSON(Material.getMaterial("CRIMSON_STAIRS"), Material.getMaterial("CRIMSON_SLAB"),
            Material.getMaterial("CRIMSON_FENCE"), Material.getMaterial("CRIMSON_DOOR"),
            Material.getMaterial("CRIMSON_BUTTON"), Material.getMaterial("CRIMSON_TRAPDOOR"),
            Material.getMaterial("CRIMSON_FENCE_GATE"), Material.getMaterial("CRIMSON_PRESSURE_PLATE"),
            Material.getMaterial("CRIMSON_SIGN"), null);

    private final Material stairs;
    private final Material slab;
    private final Material fence;
    private final Material door;
    private final Material button;
    private final Material trapDoor;
    private final Material fenceGate;
    private final Material pressurePlate;
    private final Material sign;
    private final Material boat;

    WoodItems(Material stairs, Material slab, Material fence, Material door, Material button, Material trapDoor, Material fenceGate, Material pressurePlate, Material sign, Material boat) {
        this.stairs = stairs;
        this.slab = slab;
        this.fence = fence;
        this.door = door;
        this.button = button;
        this.trapDoor = trapDoor;
        this.fenceGate = fenceGate;
        this.pressurePlate = pressurePlate;
        this.sign = sign;
        this.boat = boat;
    }

    public Material getStairs() {
        return stairs;
    }

    public Material getSlab() {
        return slab;
    }

    public Material getFence() {
        return fence;
    }

    public Material getDoor() {
        return door;
    }

    public Material getButton() {
        return button;
    }

    public Material getTrapDoor() {
        return trapDoor;
    }

    public Material getFenceGate() {
        return fenceGate;
    }

    public Material getPressurePlate() {
        return pressurePlate;
    }

    public Material getSign() {
        return sign;
    }

    public Material getBoat() {
        return boat;
    }
}


