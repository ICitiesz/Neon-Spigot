package com.islandstudio.neon.stable.secondary.iCommand;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public interface CommandHandler {
    /**
     * Set the command handler for the particular feature.
     *
     * @param commander The player who perform the command.
     * @param args The command arguments.
     */
    void setCommandHandler(Player commander, String[] args);

    /**
     * Set the tab completion for the particular command.
     *
     * @param commander The player who perfom the command.
     * @param args The command arguments.
     * @return A list of valid command arguments.
     */
    default List<String> tabCompletion(Player commander, String[] args) {
        return new ArrayList<>();
    }
}
