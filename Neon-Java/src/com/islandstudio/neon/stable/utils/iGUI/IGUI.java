package com.islandstudio.neon.stable.utils.iGUI;

import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class IGUI {
    private Player guiOwner;

    public IGUI(Player guiOwner) {
        this.guiOwner = guiOwner;
    }

    public Player getGUIOwner() {
        return guiOwner;
    }

    public void setGUIOwner(Player guiOwner) {
        this.guiOwner = guiOwner;
    }

    public static class Handler {
        public static HashMap<Player, IGUI> iGUIContainer = new HashMap<>();

        /**
         * Initialization for iGUI.
         *
         */
        public static void init() {
            IConstructor.enableEvent(new EventController());
        }
        public static IGUI getIGUI(Player player) {
            if (iGUIContainer.containsKey(player)) return iGUIContainer.get(player);

            iGUIContainer.put(player, new IGUI(player));

            return iGUIContainer.get(player);
        }
    }

    private static class EventController implements Listener {
        @EventHandler
        private void onPlayerQuitServer(PlayerQuitEvent e) {
            Handler.iGUIContainer.remove(e.getPlayer());
        }
    }
}
