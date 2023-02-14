package com.islandstudio.neon.stable.secondary.iTNT;

import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.primary.iServerConfig.IServerConfig;
import com.sun.istack.internal.NotNull;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPlaceEvent;

public class ITNT {
    private final static Long tntProtectionLevel = Handler.getTNTProtectionLevel();

    public static void cancelTNTBlockPlacement(BlockPlaceEvent e) {
        if (!tntProtectionLevel.equals(2L)) return;

        if (!e.getBlockPlaced().getType().equals(Material.TNT)) return;

        e.setCancelled(true);
    }

    public static void cancelMinecartTNTPlacement(EntityPlaceEvent e) {
        if (!tntProtectionLevel.equals(2L)) return;

        if (!e.getEntityType().equals(EntityType.MINECART_TNT)) return;

        e.setCancelled(true);
    }

    public static void cancelTNTExplosion(EntityExplodeEvent e) {
        if (!(tntProtectionLevel.equals(1L) || tntProtectionLevel.equals(2L))) return;

        if (!(e.getEntityType().equals(EntityType.MINECART_TNT) || e.getEntityType().equals(EntityType.PRIMED_TNT))) return;

        e.setCancelled(true);
    }

    public static void cancelTNTDamageEntity(EntityDamageByEntityEvent e) {
        if (!(tntProtectionLevel.equals(1L) || tntProtectionLevel.equals(2L))) return;

        if (!(e.getDamager().getType().equals(EntityType.MINECART_TNT) || e.getDamager().getType().equals(EntityType.PRIMED_TNT))) return;

        e.setCancelled(true);
        e.setDamage(0);
    }

    public static class Handler {
        /**
         * Initialization for iTNT.
         */
        public static void init() {
            final EventController iTNTEventController = new EventController();

            if (tntProtectionLevel == 1L || tntProtectionLevel == 2L) {
                IConstructor.enableEvent(iTNTEventController);
            }

            if ((tntProtectionLevel == 0L)) {
                IConstructor.disableEvent(iTNTEventController);
            }
        }

        @NotNull
        private static Long getTNTProtectionLevel() {
            return (Long) IServerConfig.getExternalServerConfigValue("TNT_Protection");
        }
    }

    private static class EventController implements Listener {
        @EventHandler
        private void onBlockPlacement(BlockPlaceEvent e) {
            cancelTNTBlockPlacement(e);
        }

        @EventHandler
        private void onEntityPlacement(EntityPlaceEvent e) {
            cancelMinecartTNTPlacement(e);
        }

        @EventHandler
        private void onEntityExplode(EntityExplodeEvent e) {
            cancelTNTExplosion(e);
        }

        @EventHandler
        private void onEntityDamagedByEntity(EntityDamageByEntityEvent e) {
            cancelTNTDamageEntity(e);
        }
    }
}
