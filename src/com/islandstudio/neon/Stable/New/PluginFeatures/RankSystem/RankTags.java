package com.islandstudio.neon.Stable.New.PluginFeatures.RankSystem;

import com.islandstudio.neon.Stable.New.Utilities.NMS_Class_Version;
import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.New.Utilities.ProfileHandler;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

public class RankTags {
    private static Object getNewScoreboard;
    private static Object getBukkitVersion;
    private static Object getOnlineMode;
    private static Object getOnlinePlayers;

   static {
       try {
           Object getScoreboardManager = NMS_Class_Version.getBukkitClass("Bukkit").getDeclaredMethod("getScoreboardManager").invoke(NMS_Class_Version.getBukkitClass("Bukkit"));
           Object plugin = NMS_Class_Version.getBukkitClass("plugin.java.JavaPlugin").getMethod("getPlugin", Class.class).invoke(null, MainCore.class);
           Object getServer = plugin.getClass().getMethod("getServer").invoke(plugin);
           getNewScoreboard = getScoreboardManager.getClass().getMethod("getNewScoreboard").invoke(getScoreboardManager);
           getBukkitVersion = getServer.getClass().getMethod("getBukkitVersion").invoke(getServer);
           getOnlineMode = getServer.getClass().getMethod("getOnlineMode").invoke(getServer);
           getOnlinePlayers = getServer.getClass().getMethod("getOnlinePlayers").invoke(getServer);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

   private static final String version = (String) getBukkitVersion;
   private static final boolean isOnlineMode = (boolean) getOnlineMode;
   private static final Collection<? extends Player> players = (Collection<? extends Player>) getOnlinePlayers;

   public static void test() throws ClassNotFoundException, IOException, ParseException {
       //System.out.println(com.islandstudio.neon.Stable_Release.NewImplementation.Utilities.PlayerDataHandler.getValue(Bukkit.getPlayer("ICities")).get("Rank"));
   }


    private final static Scoreboard scoreboard = (Scoreboard) getNewScoreboard;

    public static Scoreboard getScoreboard() {
        return scoreboard;
    }

    public static void initialize() {
        for (ServerRanks serverRanks : ServerRanks.values()) {
            Team team = scoreboard.registerNewTeam(serverRanks.toString());
            team.setPrefix(serverRanks.getPrefix());
        }
    }

    public static void setRankTags() throws IOException, ParseException {
        for (Player onlinePlayers : players) {
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
//                for (Integer index : LastDeadLocation.getScore().keySet()) {
//                    Score score = LastDeadLocation.getScore().get(index);
//                    score.setScore(index);
//
//                }
}
