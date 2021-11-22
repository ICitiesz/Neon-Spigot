package com.islandstudio.neon.Experimental.Hologram;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class IHologram {
    public static void createHologram(World world, Location location) {
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setVisible(false);
        armorStand.setCustomName("Hello");
        armorStand.setCustomNameVisible(true);
    }
}
