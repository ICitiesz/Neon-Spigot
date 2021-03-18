package com.islandstudio.neon.Stable.Deprecated.PluginFeatures.RankSystem;

import com.islandstudio.neon.Stable.Old.Initialization.FolderManager.FolderList;
import com.islandstudio.neon.Stable.Deprecated.Utilities.PlayerDataHandler;
import com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem.ServerRanks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.util.Objects;

public class RankTags {
    private final static Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();

    public static Scoreboard getScoreboard() {
        return scoreboard;
    }

    public static void initialize() {
        for (ServerRanks serverRanks : ServerRanks.values()) {
            Team team = scoreboard.registerNewTeam(serverRanks.toString());
            team.setPrefix(serverRanks.getPrefix());
        }
    }

    public static void setRankTags() {
        if (Bukkit.getServer().getOnlineMode()) {
            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                File file = new File(FolderList.getFolder_2a_1, onlinePlayers.getUniqueId() + ".sv");

                for (ServerRanks serverRanks : ServerRanks.values()) {
                    switch (serverRanks) {
                        case OWNER: {
                            if (ServerRanks.OWNER.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                                Objects.requireNonNull(scoreboard.getTeam(serverRanks.toString())).addEntry(onlinePlayers.getName());
                            }
                            break;
                        }

                        case VIP_PLUS: {
                            if (ServerRanks.VIP_PLUS.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                                Objects.requireNonNull(scoreboard.getTeam(serverRanks.toString())).addEntry(onlinePlayers.getName());
                            }
                            break;
                        }

                        case VIP: {
                            if (ServerRanks.VIP.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                                Objects.requireNonNull(scoreboard.getTeam(serverRanks.toString())).addEntry(onlinePlayers.getName());
                            }
                            break;
                        }

                        case MEMBER: {
                            if (ServerRanks.MEMBER.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                                Objects.requireNonNull(scoreboard.getTeam(serverRanks.toString())).addEntry(onlinePlayers.getName());
                            }
                            break;
                        }
                    }
                }

                onlinePlayers.setScoreboard(scoreboard);
            }
        } else {
            for (Player onlinePlayers : Bukkit.getServer().getOnlinePlayers()) {
                File file = new File(FolderList.getFolder_2b_1, onlinePlayers.getUniqueId() + ".sv");

                for (ServerRanks serverRanks : ServerRanks.values()) {
                    switch (serverRanks) {
                        case OWNER: {
                            if (ServerRanks.OWNER.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                                Objects.requireNonNull(scoreboard.getTeam(serverRanks.toString())).addEntry(onlinePlayers.getName());
                            }
                            break;
                        }

                        case VIP_PLUS: {
                            if (ServerRanks.VIP_PLUS.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                                Objects.requireNonNull(scoreboard.getTeam(serverRanks.toString())).addEntry(onlinePlayers.getName());
                            }
                            break;
                        }

                        case VIP: {
                            if (ServerRanks.VIP.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                                Objects.requireNonNull(scoreboard.getTeam(serverRanks.toString())).addEntry(onlinePlayers.getName());
                            }
                            break;
                        }

                        case MEMBER: {
                            if (ServerRanks.MEMBER.toString().equalsIgnoreCase(PlayerDataHandler.getData(file).getString("Rank"))) {
                                Objects.requireNonNull(scoreboard.getTeam(serverRanks.toString())).addEntry(onlinePlayers.getName());
                            }
                            break;
                        }
                    }
                }


//                for (Integer index : LastDeadLocation.getScore().keySet()) {
//                    Score score = LastDeadLocation.getScore().get(index);
//                    score.setScore(index);
//
//                }

                onlinePlayers.setScoreboard(scoreboard);
            }
        }


    }

}
