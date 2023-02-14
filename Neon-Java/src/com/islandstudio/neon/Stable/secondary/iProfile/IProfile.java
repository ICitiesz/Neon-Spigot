package com.islandstudio.neon.stable.secondary.iProfile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.primary.iFolder.FolderList;
import com.islandstudio.neon.stable.primary.iFolder.IFolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

public class IProfile {
    private final ClassLoader classLoader = this.getClass().getClassLoader();

    private static final JSONParser jsonParser = new JSONParser();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static class Handler {
        /**
         * Initialization for iProfile.
         *
         */
        public static void init() {
            IConstructor.enableEvent(new EventController());
        }

        /**
         * Get player profile folder.
         *
         * @param player The given player.
         * @return Player folder.
         */
        private static File getPlayerFolder(Player player) {
            return new File(FolderList.PLAYER_DATA.getFolder(), "player_" + player.getUniqueId());
        }

        /**
         * Get player profile file.
         *
         * @param player The given player.
         * @return Player profile file.
         */
        private static File getPlayerProfile(Player player) {
            return new File(getPlayerFolder(player), "profile_" + player.getUniqueId() + ".json");
        }

        /**
         * Create player profile file.
         *
         * @param player The given player.
         */
        private static void createProfileFile(Player player) {
            IFolder.createNewFile(getPlayerProfile(player), getPlayerFolder(player));
        }

        /**
         * Create a player profile when new player joins the server.
         *
         * @param player The player who joins the server.
         */
        @SuppressWarnings("unchecked")
        private static void createPlayerProfile(Player player)  {
            createProfileFile(player);

            try {
                BufferedReader externalBufferedReader = new BufferedReader(new FileReader(getPlayerProfile(player)));
                final long externalProfileFileSize = externalBufferedReader.lines().count();

                externalBufferedReader.close();

                if (externalProfileFileSize == 0) {
                    FileOutputStream fileOutputStream = new FileOutputStream(getPlayerProfile(player));
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                    JSONObject profileProperty = getInternalProfileProperty();

                    profileProperty.replace("Name", player.getName());
                    profileProperty.replace("UUID", player.getUniqueId().toString());

                    if (player.isOp()) profileProperty.replace("Rank", "OWNER");

                    bufferedWriter.write(gson.toJson(profileProperty));
                    bufferedWriter.close();
                    fileOutputStream.close();
                }

                updateProfileProperty(player);
            } catch (IOException err) {
                final String ERR_MSG = "An error occurred while trying to create player profile: ";

                if (err instanceof FileNotFoundException) {
                    System.out.println(ERR_MSG + "Is '" + Handler.getPlayerProfile(player).getName() + "' missing?");
                    return;
                }

                System.out.println(ERR_MSG + "Failed to create/close I/O stream!");
            }
        }
    }



    /**
     * Get player profile data by given property name.
     *
     * @param player The target player.
     * @param propertyName The profile property name.
     * @return The profile data by property.
     */
    public static Object getProfileValueByProperty(Player player, String propertyName) {
        JSONObject playerProfileData = getProfileData(player);

        if (playerProfileData.containsKey(propertyName)) return playerProfileData.get(propertyName);

        updateProfileProperty(player);

        playerProfileData = getProfileData(player);

        return playerProfileData.get(propertyName);
    }

    /**
     * Set value to player profile.
     *
     * @param player The given player.
     * @param key The profile property to be set.
     * @param value The value to be set.
     */
    @SuppressWarnings("unchecked")
    public static void setProfileValueByProperty(Player player, String key, Object value) {
        JSONObject jsonObject = getProfileData(player);

        final String ERR_MSG = "An error occurred while trying to set value for player profile: ";

        if (jsonObject.isEmpty()) {
            System.out.println(ERR_MSG + "Can't set value due to empty profile elements!");
            return;
        }

        if (!jsonObject.containsKey(key)) {
            updateProfileProperty(player);

            jsonObject = getProfileData(player);
        }

        try {
            if (jsonObject.get(key).equals(value)) return;

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Handler.getPlayerProfile(player).toPath())));

            jsonObject.replace(key, value);

            bufferedWriter.write(gson.toJson(jsonObject));
            bufferedWriter.close();
        } catch (IOException err) {
            System.out.println(ERR_MSG + "Failed to create an file output stream for '"
                    + Handler.getPlayerProfile(player).getName() + "'!");
        }
    }

    /**
     * Get profile property value from player profile.
     *
     * @param player The given player.
     * @return Profile property value from player profile.
     */
    private static JSONObject getProfileData(Player player) {
        try {
            return (JSONObject) jsonParser.parse(new FileReader(Handler.getPlayerProfile(player)));
        } catch (IOException | ParseException err) {
            final String ERR_MSG = "An error occurred while trying to get property value from player profile: ";

            if (err instanceof IOException) {
                System.out.println(ERR_MSG + "Is '" + Handler.getPlayerProfile(player).getName() + "' missing?");
            }

            if (err instanceof ParseException) {
                System.out.println(ERR_MSG + "Is '" + Handler.getPlayerProfile(player).getName() + "' corrupted?");
            }
        }

        return new JSONObject();
    }

    /**
     * Get profile property from internal resource, 'player_.json'.
     *
     * @return Profile property from internal resource.
     */
    private static JSONObject getInternalProfileProperty() {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = new IProfile().classLoader.getResourceAsStream("resources/player_.json");

        if (inputStream == null) {
            throw new NullPointerException("An error occurred while trying to create input stream for 'player_.json' from internal resource!");
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Object[] profileElements = bufferedReader.lines().toArray();

        Arrays.stream(profileElements).forEach(stringBuilder::append);

        try {
            return (JSONObject) jsonParser.parse(stringBuilder.toString());
        } catch (ParseException err) {
            System.out.println("An error occurred while trying to parse content from internal resource: Is 'player_.json' corrupted!");
        }

        return new JSONObject();
    }

    /**
     * Get profile property from player profile.
     *
     * @param player The player.
     * @return Player profile property.
     */
    private static JSONObject getExternalProfileProperty(Player player) {
        try {
            return (JSONObject) jsonParser.parse(new FileReader(Handler.getPlayerProfile(player)));
        } catch (IOException | ParseException err) {
            final String ERR_MSG = "An error occurred while trying to get player profile property: ";

            if (err instanceof IOException) {
                System.out.println(ERR_MSG + "Is '" + Handler.getPlayerProfile(player).getName() + "' missing?");
            }

            if (err instanceof ParseException) {
                System.out.println(ERR_MSG + "Is '" + Handler.getPlayerProfile(player).getName() + "' corrupted?");
            }
        }

        return new JSONObject();
    }

    /**
     * Update player profile property.
     *
     * @param player The given player.
     */
    @SuppressWarnings("unchecked")
    private static void updateProfileProperty(Player player) {
        try {
            JSONObject internalProfileProperty = getInternalProfileProperty();
            JSONObject externalProfileProperty = getExternalProfileProperty(player);

            internalProfileProperty.keySet().forEach(key -> {
                if (!externalProfileProperty.containsKey(key)) externalProfileProperty.putIfAbsent(key, internalProfileProperty.get(key));
            });

            externalProfileProperty.keySet().removeIf(key -> !internalProfileProperty.containsKey(key));

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Handler.getPlayerProfile(player).toPath())));

            bufferedWriter.write(gson.toJson(externalProfileProperty));
            bufferedWriter.close();
        } catch (IOException err) {
            System.out.println("An error occurred while trying to update player profile property: Is '"
                    + Handler.getPlayerProfile(player).getName() + "' missing?");
        }
    }

    private static class EventController implements Listener {
        @EventHandler
        private void onPlayerJoinServer(PlayerJoinEvent e) {
            Handler.createPlayerProfile(e.getPlayer());
        }

        @EventHandler
        private void onServerLoad(ServerLoadEvent e) {
            if (e.getType() != ServerLoadEvent.LoadType.RELOAD) return;

            IConstructor.getPlugin().getServer().getOnlinePlayers().forEach(IProfile::updateProfileProperty);
        }
    }
}
