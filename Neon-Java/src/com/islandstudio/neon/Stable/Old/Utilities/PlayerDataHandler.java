package com.islandstudio.neon.Stable.Old.Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.islandstudio.neon.Stable.New.Utilities.NamespaceVersion;
import com.islandstudio.neon.MainCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class PlayerDataHandler {
    private static FileConfiguration fileConfiguration;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final JSONParser jsonParser = new JSONParser();

    private static Object getBukkitVersion;
    private static Object getOnlineMode;

    static {
        try {
            Object plugin = NamespaceVersion.getBukkitClass("plugin.java.JavaPlugin").getMethod("getPlugin", Class.class).invoke(null, MainCore.class);
            Object getServer = plugin.getClass().getMethod("getServer").invoke(plugin);
            getOnlineMode = getServer.getClass().getMethod("getOnlineMode").invoke(getServer);
            getBukkitVersion = getServer.getClass().getMethod("getBukkitVersion").invoke(getServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String VERSION = ((String) getBukkitVersion).split("\\.")[0] + "." + ((String) getBukkitVersion).split("\\.")[1];
    private static final boolean IS_ONLINE_MODE = (boolean) getOnlineMode;

    public static void initialize(Player player) {
        Map<String, Object> defaultData = new TreeMap<>();
        defaultData.put("Name", player.getName());
        defaultData.put("UUID", player.getUniqueId().toString());

        if (player.isOp()) {
            defaultData.put("Rank", "OWNER");
        } else {
            defaultData.put("Rank", "MEMBER");
        }

        defaultData.put("isMuted", false);

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(defaultData.toString());

        String result = gson.toJson(jsonElement);

        if (getFile(player) != null) {
            if (!Objects.requireNonNull(getFile(player)).exists()) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(Objects.requireNonNull(getFile(player)));
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                    boolean isFileCreated = Objects.requireNonNull(getFile(player)).createNewFile();

                    bufferedWriter.write(result);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        defaultData.clear();
    }

    public static File getFile(Player player) {
        switch (VERSION) {
            case "1.14": {
                if (IS_ONLINE_MODE) {
                    return new File(com.islandstudio.neon.Deprecated.Initiallization.FolderManager.FolderList.dataFolder_1_a_1, player.getUniqueId() + ".json");
                } else {
                    return new File(com.islandstudio.neon.Deprecated.Initiallization.FolderManager.FolderList.dataFolder_1_b_1, player.getUniqueId() + ".json");
                }


            }

            case "1.15": {
                if (IS_ONLINE_MODE) {
                    return new File(com.islandstudio.neon.Deprecated.Initiallization.FolderManager.FolderList.dataFolder_2_a_1, player.getUniqueId() + ".json");
                } else {
                    return new File(com.islandstudio.neon.Deprecated.Initiallization.FolderManager.FolderList.dataFolder_2_b_1, player.getUniqueId() + ".json");
                }
            }

            case "1.16": {
                if (IS_ONLINE_MODE) {
                    return new File(com.islandstudio.neon.Deprecated.Initiallization.FolderManager.FolderList.dataFolder_3_a_1, player.getUniqueId() + ".json");
                } else {
                    return new File(com.islandstudio.neon.Deprecated.Initiallization.FolderManager.FolderList.dataFolder_3_b_1, player.getUniqueId() + ".json");
                }
            }

            default: {
                break;
            }
        }

        return null;
    }

    public static JSONObject getValue(Player player) throws IOException, ParseException {
        if (getFile(player) != null) {
            FileReader fileReader = new FileReader(Objects.requireNonNull(getFile(player)));
            return (JSONObject) jsonParser.parse(fileReader);
        }
        return null;
    }

    public static void setValue(Player player, String key, String value) throws IOException, ParseException {
        JSONObject jsonObject = getValue(player);

        if (getFile(player) != null) {
            if (jsonObject != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(Objects.requireNonNull(getFile(player)));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                if (!jsonObject.get(key).equals(value)) {
                    jsonObject.replace(key, value);

                    String result = gson.toJson(jsonObject);
                    bufferedWriter.write(result);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            }
        }
    }
}
