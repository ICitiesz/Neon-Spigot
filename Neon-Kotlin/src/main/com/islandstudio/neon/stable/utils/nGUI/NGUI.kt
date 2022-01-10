package com.islandstudio.neon.stable.utils.nGUI

import org.bukkit.entity.Player

class NGUI(private var guiOwner: Player) {

    fun getGUIOwner(): Player {
        return guiOwner
    }

    fun setGUIOwner(guiOwner: Player) {
        this.guiOwner = guiOwner
    }

    object Handler {
        val nGUIContainer: HashMap<Player, NGUI> = HashMap()

        fun getNGUI(player: Player): NGUI {
            if (nGUIContainer.containsKey(player)) {
                return nGUIContainer[player]!!
            }

            nGUIContainer[player] = NGUI(player)

            return nGUIContainer[player]!!
        }

    }
}