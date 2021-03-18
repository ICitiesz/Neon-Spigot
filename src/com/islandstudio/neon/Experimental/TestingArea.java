package com.islandstudio.neon.Experimental;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.New.Initialization.FolderManager.FolderHandler;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class TestingArea {
    private static Plugin plugin = MainCore.getPlugin(MainCore.class);

    public static final File testFile = new File(FolderHandler.getBetaFolder(), "server_config-Beta.json");

    public static void getDataFolder(Player player) {
        for (File fileName : Objects.requireNonNull(plugin.getDataFolder().listFiles())) {
            player.sendMessage(fileName.getName());
        }


    }

    public static void playSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1);
        player.spawnParticle(Particle.PORTAL, player.getLocation(), 600);
    }

    public static void test() throws Exception {
        File file = new File(FolderHandler.getBetaFolder(), "iWaypoint_Global.json");

        if (!file.exists()) {
            boolean create = file.createNewFile();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JSONParser jsonParser = new JSONParser();

        FileReader fileReader = new FileReader(file);

        JSONObject jsonObject_1 = (JSONObject) jsonParser.parse(fileReader);
        JSONObject jsonObject_2 = new JSONObject();
        JSONObject jsonObject_3 = new JSONObject();

        JSONArray jsonArray_1 = (JSONArray) jsonObject_1.get("Waypoints");
        JSONArray jsonArray_2 = new JSONArray();

        jsonObject_3.put("Yaw", 0);
        jsonObject_3.put("Pitch", 0);
        jsonObject_3.put("Dimension", "NORMAL");
        jsonObject_3.put("Position-X", 0);
        jsonObject_3.put("Position-Y", 0);
        jsonObject_3.put("Position-Z", 0);

        jsonArray_2.add(jsonObject_3);

        jsonObject_2.put("Test_2", jsonArray_2);

        jsonArray_1.add(jsonObject_2);

        String result = gson.toJson(jsonObject_1);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        bufferedWriter.write(result);
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public static void initialize() throws Exception {
        if (!testFile.exists()) {
            boolean isDataFolderCreated = testFile.createNewFile();

            if (isDataFolderCreated) {
                System.out.println("Folder \"\\" + testFile.getName() + "\"\\ has been created!");
            } else {
                System.out.println("Failed to created folder \"\\" + testFile.getName() + "\"\\!");
            }
        }
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void readFileData(Player player) {
        try {
            FileReader fileReader = new FileReader(testFile);
            JSONParser jsonParser = new JSONParser();

            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
            JSONArray jsonArray = (JSONArray) jsonObject.get("Hub_Location");
            JSONObject inObj;
            String result = null;

            for (int i = 0; i < jsonArray.size(); i++) {
                inObj = (JSONObject) jsonArray.get(i);

                if (inObj.get("Dimension").equals("Nether")) {
                    result = inObj.toString();
                }
            }


            assert result != null;
            player.sendMessage(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void modifyFileData(Player player) {
        try {
            FileReader fileReader = new FileReader(testFile);
            JSONParser jsonParser = new JSONParser();

            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
            JSONArray jsonArray = (JSONArray) jsonObject.get("Hub_Location");
            JSONObject inObj;


            for (int i = 0; i < jsonArray.size(); i++) {
                inObj = (JSONObject) jsonArray.get(i);

                if (inObj.get("Dimension").equals("Nether")) {
                    inObj.replace("X", 22);
                }
            }
            String result = gson.toJson(jsonObject);

            FileOutputStream fileOutputStream = new FileOutputStream(testFile);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            assert result != null;
            bufferedWriter.write(result);
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setFileData() {
        JSONObject settings = new JSONObject();
        settings.put("PVP", true);
        settings.put("ChatLogging", true);
        settings.put("TNT_Protection", 0);

        JSONArray jsonArray = new JSONArray();

        settings.put("Hub_Location", jsonArray);

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(settings.toString());

        String result = gson.toJson(jsonElement);

        if (!testFile.exists()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(testFile);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                boolean isFileCreated = testFile.createNewFile();

                bufferedWriter.write(result);
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(testFile);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                bufferedWriter.write(result);
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
