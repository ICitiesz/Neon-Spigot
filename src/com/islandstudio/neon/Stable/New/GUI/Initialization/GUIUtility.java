package com.islandstudio.neon.Stable.New.GUI.Initialization;

import org.bukkit.entity.Player;

public class GUIUtility {
    private Player owner;

    public GUIUtility(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player newOwner) {
        owner = newOwner;
    }
}
