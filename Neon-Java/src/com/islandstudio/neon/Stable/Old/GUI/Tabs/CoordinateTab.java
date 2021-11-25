package com.islandstudio.neon.Stable.Old.GUI.Tabs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.*;

public class CoordinateTab implements Listener {
    public static void initiateManager(Player player) {
        Location playerLocation = player.getLocation();
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("X", "Dummy", "Dummy_2");
        String name = ChatColor.WHITE + "" + ChatColor.RESET + "scLine1";

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.GREEN + "Player Coordinate");

        Team team_1 = scoreboard.registerNewTeam("playerPosition");
        team_1.addEntry(ChatColor.WHITE + "" + ChatColor.RESET);
        team_1.setPrefix("scLine1");
        Score score_2 = objective.getScore("HI");
        score_2.setScore(0);

        Score score_1 = objective.getScore("Hey!");
        score_1.setScore(0 );
        player.setScoreboard(scoreboard);
    }

}
