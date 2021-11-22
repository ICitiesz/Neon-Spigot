package com.islandstudio.neon.Stable.New.features.GUI.Initialization;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class GUIUtilityHandler {
    public static final HashMap<Player, GUIUtility> utilityHM = new HashMap<>();

    public static GUIUtility getGUIUtility(Player player) {
        GUIUtility guiUtility;

        if (utilityHM.containsKey(player)) {
            return utilityHM.get(player);
        } else {
            guiUtility = new GUIUtility(player);
            utilityHM.put(player, guiUtility);

            return guiUtility;
        }
    }
}
