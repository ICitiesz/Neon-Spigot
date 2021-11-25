package com.islandstudio.neon.Deprecated.PluginFeatures.RankSystem;

import com.islandstudio.neon.Stable.New.features.iRank.ServerRanks;
import com.islandstudio.neon.Stable.New.Utilities.ProfileHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Objects;

@Deprecated
public class RankTags {
    private final static Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();

    public static void init() {
        for (ServerRanks serverRanks : ServerRanks.values()) {
            Team team = scoreboard.registerNewTeam(serverRanks.toString());
            team.setPrefix(serverRanks.getPrefix());
        }
    }

    public static void setRankTags() throws IOException, ParseException {
        for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
            for (ServerRanks serverRanks : ServerRanks.values()) {
                switch (serverRanks) {
                    case OWNER: {
                        if (ServerRanks.OWNER.toString().equalsIgnoreCase((String) ProfileHandler.getValue(onlinePlayers).get("Rank"))) {
                            Objects.requireNonNull(scoreboard.getTeam(serverRanks.toString())).addEntry(onlinePlayers.getName());
                        }
                        break;
                    }

                    case VIP_PLUS: {
                        if (ServerRanks.VIP_PLUS.toString().equalsIgnoreCase((String) ProfileHandler.getValue(onlinePlayers).get("Rank"))) {
                            Objects.requireNonNull(scoreboard.getTeam(serverRanks.toString())).addEntry(onlinePlayers.getName());
                        }
                        break;
                    }

                    case VIP: {
                        if (ServerRanks.VIP.toString().equalsIgnoreCase((String) ProfileHandler.getValue(onlinePlayers).get("Rank"))) {
                            Objects.requireNonNull(scoreboard.getTeam(serverRanks.toString())).addEntry(onlinePlayers.getName());
                        }
                        break;
                    }

                    case MEMBER: {
                        if (ServerRanks.MEMBER.toString().equalsIgnoreCase((String) ProfileHandler.getValue(onlinePlayers).get("Rank"))) {
                            Objects.requireNonNull(scoreboard.getTeam(serverRanks.toString())).addEntry(onlinePlayers.getName());
                        }
                        break;
                    }

                    default: {
                        break;
                    }
                }

                onlinePlayers.setScoreboard(scoreboard);
            }
        }
    }
}
