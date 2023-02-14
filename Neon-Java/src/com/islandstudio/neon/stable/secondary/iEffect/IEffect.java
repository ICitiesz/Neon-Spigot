package com.islandstudio.neon.stable.secondary.iEffect;

import com.islandstudio.neon.stable.secondary.iCommand.CommandHandler;
import com.islandstudio.neon.stable.secondary.iCommand.CommandSyntax;
import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.utils.iGUI.IGUI;
import com.islandstudio.neon.stable.utils.iGUI.IGUIConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public class IEffect {
    public static class Handler implements CommandHandler {
        public static void init() {
            IConstructor.enableEvent(new EventController());
        }

        @Override
        public void setCommandHandler(Player commander, String[] args) {
            if (args.length != 1) {
                commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                return;
            }

            if (!commander.isOp()) {
                commander.sendMessage(CommandSyntax.INVALID_PERMISSION.getSyntaxMessage());
                return;
            }

            if (commander.isSleeping()) {
                commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "You can't use iEffect while you're sleeping!"));
                return;
            }

            new GUIHandler(IGUI.Handler.getIGUI(commander)).openGUI();
        }

        @Override
        public List<String> tabCompletion(Player commander, String[] args) {
            return CommandHandler.super.tabCompletion(commander, args);
        }
    }

    private static class EventController implements Listener {
        @EventHandler
        private void onInventoryClick(InventoryClickEvent e) {
            final Player PLAYER = (Player) e.getWhoClicked();

            if (!e.getView().getTitle().equals(new GUIHandler(IGUI.Handler.getIGUI(PLAYER)).getGUIName())) return;

            final Inventory CLICKED_GUI = e.getClickedInventory();

            if (CLICKED_GUI == null) return;

            final InventoryHolder GUI_HOLDER = CLICKED_GUI.getHolder();

            if (GUI_HOLDER == null) return;

            if (CLICKED_GUI == PLAYER.getInventory()) e.setCancelled(true);

            if (!(GUI_HOLDER instanceof IGUIConstructor)) return;

            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;

            ((IGUIConstructor) GUI_HOLDER).setGUIClickHandler(e);
        }
    }

}
