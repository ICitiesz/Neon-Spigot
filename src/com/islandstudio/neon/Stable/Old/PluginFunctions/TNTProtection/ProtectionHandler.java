package com.islandstudio.neon.Stable.Old.PluginFunctions.TNTProtection;

import com.islandstudio.neon.Stable.New.Utilities.ServerCfgHandler;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class ProtectionHandler {
    public static void detectExplosion(EntityExplodeEvent e) throws IOException, ParseException {
        if (ServerCfgHandler.getValue().get("TNT_Protection").equals(1L) || ServerCfgHandler.getValue().get("TNT_Protection").equals(2L)) {
            if (e.getEntityType().equals(EntityType.MINECART_TNT)) {
                e.setCancelled(true);
            } else if (e.getEntityType().equals(EntityType.PRIMED_TNT)) {
                e.setCancelled(true);
            }
        }
    }

    public static void setNoDamage(EntityDamageByEntityEvent e) throws IOException, ParseException {
        if (ServerCfgHandler.getValue().get("TNT_Protection").equals(1L) || ServerCfgHandler.getValue().get("TNT_Protection").equals(2L)) {
            if (e.getDamager().getType().equals(EntityType.MINECART_TNT) || (e.getDamager().getType().equals(EntityType.PRIMED_TNT))) {
                e.setCancelled(true);
                e.setDamage(0);
            }
        }
    }
}
