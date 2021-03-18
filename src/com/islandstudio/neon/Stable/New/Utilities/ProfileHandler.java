package com.islandstudio.neon.Stable.New.Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islandstudio.neon.Stable.New.Initialization.FolderManager.FolderHandler;
import com.islandstudio.neon.Stable.New.Initialization.FolderManager.FolderList;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.*;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ProfileHandler {
    private final ClassLoader classLoader = this.getClass().getClassLoader();

    private static final JSONParser jsonParser = new JSONParser();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void init(Player player) throws Exception {
        ProfileHandler profileHandler = new ProfileHandler();

        StringBuilder stringBuilder = new StringBuilder();

        createNewFiles(player);

        File playerProfile = getPlayerProfile(player);
        FileReader fileReader = new FileReader(playerProfile);
        BufferedReader bufferedReader_1 = new BufferedReader(fileReader);
        long dataOutLength = bufferedReader_1.lines().toArray().length;

        if (dataOutLength == 0) {
            InputStream inputStream = profileHandler.classLoader.getResourceAsStream("Resources/player_.json");

            fileReader.close();
            bufferedReader_1.close();

            if (inputStream != null) {
                BufferedReader bufferedReader_2 = new BufferedReader(new InputStreamReader(inputStream));
                Object[] dataIn = bufferedReader_2.lines().toArray();

                if (dataIn.length != 0) {
                    FileOutputStream fileOutputStream = new FileOutputStream(playerProfile);
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                    for (Object data : dataIn) {
                        stringBuilder.append(data);
                    }

                    JSONObject jsonObject = (JSONObject) jsonParser.parse(stringBuilder.toString());

                    jsonObject.replace("Name", player.getName());
                    jsonObject.replace("UUID", player.getUniqueId().toString());

                    if (player.isOp()) {
                        jsonObject.replace("Rank", "OWNER");
                    }

                    String result = gson.toJson(jsonObject);

                    bufferedWriter.write(result);
                    bufferedWriter.flush();
                    bufferedWriter.close();

                    bufferedReader_2.close();
                    inputStream.close();

                    fileOutputStream.close();
                }
            }
        } else {
            fileReader.close();
            bufferedReader_1.close();
        }
    }

    public static void createNewFiles(Player player) throws IOException {
        File playerFolder = getPlayerFolder(player);
        File playerProfile = getPlayerProfile(player);
        File mainFolder = FolderList.FOLDER_B.getFolder();

        if (!mainFolder.exists()) {
            boolean createFolder = mainFolder.mkdirs();
        }

        if (!playerFolder.exists()) {
            boolean createFolder = playerFolder.mkdirs();
        }

        if (!playerProfile.exists()) {
            boolean createFile = playerProfile.createNewFile();
        }
    }

    public static File getPlayerProfile(Player player) {
        return new File(getPlayerFolder(player), "profile_" + player.getUniqueId().toString() + ".json");
    }

    public static File getPlayerFolder(Player player) {
        return new File(FolderHandler.getDataFolder(), FolderHandler.getVersion() + "\\" + FolderHandler.getMode() + "\\Server_Data\\Player_Data" + "\\" + "player_" + player.getUniqueId().toString());
    }

    public static JSONObject getValue(Player player) throws IOException, ParseException {
        FileReader fileReader = new FileReader(getPlayerProfile(player));
        return (JSONObject) jsonParser.parse(fileReader);
    }

    public static void setValue(Player player, String key, String value) throws IOException, ParseException {
        JSONObject jsonObject = getValue(player);

        if (jsonObject != null) {
            FileOutputStream fileOutputStream = new FileOutputStream(getPlayerProfile(player));
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
