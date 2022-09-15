package com.islandstudio.neon.Stable.primary.iProfile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islandstudio.neon.Stable.primary.iFolder.IFolder;
import com.islandstudio.neon.Stable.primary.iFolder.FolderList;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.Arrays;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class IProfile {
    private final ClassLoader classLoader = this.getClass().getClassLoader();

    private static final JSONParser jsonParser = new JSONParser();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Create a player profile when new player joins the server.
     *
     * @param player The player who joined the server.
     */
    @SuppressWarnings("unchecked")
    public static void createPlayerProfile(Player player)  {
        createProfileFile(player);

        try {
            File playerProfile = getPlayerProfile(player);
            BufferedReader bufferedReader_1 = new BufferedReader(new FileReader(playerProfile));
            long clientFileLineCount = bufferedReader_1.lines().count();

            bufferedReader_1.close();

            if (clientFileLineCount == 0) {
                FileOutputStream fileOutputStream = new FileOutputStream(playerProfile);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                JSONObject profileElements = getSourceProfileElement();

                profileElements.replace("Name", player.getName());
                profileElements.replace("UUID", player.getUniqueId().toString());

                if (player.isOp()) {
                    profileElements.replace("Rank", "OWNER");
                }

                bufferedWriter.write(gson.toJson(profileElements));
                bufferedWriter.close();
                fileOutputStream.close();
            }

            updateProfileElement(player);
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Get value from player profile.
     *
     * @param player The given player.
     * @return Value from player profile.
     */
    public static JSONObject getValue(Player player) {
        try {
            return (JSONObject) jsonParser.parse(new FileReader(getPlayerProfile(player)));
        } catch (IOException | ParseException e) {
            if (e instanceof IOException) {
                System.out.println("ERROR: Can't find '" + getPlayerProfile(player).getName() + "'!");
            } else {
                System.out.println("ERROR: Can't parse '" + getPlayerProfile(player).getName() + "'!");
            }
        }

        return new JSONObject();
    }

    /**
     * Set value to player profile.
     *
     * @param player The given player.
     * @param key The element to be set.
     * @param value The value to be set.
     */
    @SuppressWarnings("unchecked")
    public static void setValue(Player player, String key, String value) {
        JSONObject jsonObject = getValue(player);

        if (jsonObject.isEmpty()) {
            System.out.println("ERROR: Can't set value due to empty profile elements!");
            return;
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(getPlayerProfile(player));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            if (jsonObject.get(key).equals(value)) return;

            jsonObject.replace(key, value);

            bufferedWriter.write(gson.toJson(jsonObject));
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("ERROR: Can't set value due to " + e.getMessage());
        }
    }

    /**
     * Get profile elements from source file.
     *
     * @return Profile elements.
     */
    private static JSONObject getSourceProfileElement() {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = new IProfile().classLoader.getResourceAsStream("resources/player_.json");

        if (inputStream == null) {
            throw new NullPointerException("ERROR: Can't load 'player_.json' from source!");
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Object[] profileElements = bufferedReader.lines().toArray();

        Arrays.stream(profileElements).forEach(stringBuilder::append);

        try {
            return (JSONObject) jsonParser.parse(stringBuilder.toString());
        } catch (ParseException e) {
            System.out.println("ERROR: Can't parse 'player_.json' from source!");
        }

        return new JSONObject();
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
     * Update profile elements.
     *
     * @param player The given player.
     */
    @SuppressWarnings("unchecked")
    private static void updateProfileElement(Player player) {
        try {
            FileReader fileReader = new FileReader(getPlayerProfile(player));
            JSONObject sourceProfileElement = getSourceProfileElement();
            JSONObject clientProfileElement = (JSONObject) jsonParser.parse(fileReader);

            sourceProfileElement.keySet().forEach(key -> {
                if (!clientProfileElement.containsKey(key)) clientProfileElement.putIfAbsent(key, sourceProfileElement.get(key));
            });

            clientProfileElement.keySet().removeIf(key -> !sourceProfileElement.containsKey(key));

            FileOutputStream fileOutputStream = new FileOutputStream(getPlayerProfile(player));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            bufferedWriter.write(gson.toJson(clientProfileElement));

            bufferedWriter.close();
            fileOutputStream.close();
            fileReader.close();
        } catch (IOException | ParseException e) {
            if (e instanceof IOException) {
                System.out.println("ERROR: Can't find '" + getPlayerProfile(player).getName() + "'!");
            } else {
                System.out.println("ERROR: Can't parse '" + getPlayerProfile(player).getName() + "'!");
            }
        }
    }

    /**
     * Create player profile file.
     *
     * @param player The given player.
     */
    private static void createProfileFile(Player player) {
        File playerFolder = getPlayerFolder(player);
        File playerProfile = getPlayerProfile(player);

        IFolder.createNewFile(playerProfile, playerFolder);
    }
}
