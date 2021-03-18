package com.islandstudio.neon.Experimental.ServerHub;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islandstudio.neon.Experimental.TestingArea;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HubHandler {
    public static final File testFile = TestingArea.testFile;
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void setHub(Player player) {
        try {
            FileReader fileReader = new FileReader(testFile);
            JSONParser jsonParser = new JSONParser();

            JSONObject jsonObject_1 = (JSONObject) jsonParser.parse(fileReader);
            JSONArray jsonArray = (JSONArray) jsonObject_1.get("Hub_Location");
            JSONObject jsonObject_2;
            JSONObject jsonObject_3 = new JSONObject();

            Map<JSONObject, String> hub_list = new HashMap<>();

            if (jsonArray.size() > 0) {
                for (Object hubs : jsonArray) {
                    jsonObject_2 = (JSONObject) hubs;
                    hub_list.put(jsonObject_2, (String) jsonObject_2.get("Dimension"));
                }

                if (!hub_list.containsValue(player.getLocation().getWorld().getEnvironment().name())) {
                    jsonObject_3.put("X", player.getLocation().getBlockX());
                    jsonObject_3.put("Y", player.getLocation().getBlockY());
                    jsonObject_3.put("Z", player.getLocation().getBlockZ());
                    jsonObject_3.put("Dimension", player.getLocation().getWorld().getEnvironment().name());
                    jsonArray.add(jsonObject_3);
                    player.sendMessage("Hub saved! 1");
                } else {
                    player.sendMessage("Contains existing value!");
                }
            } else {
                jsonObject_3.put("X", player.getLocation().getBlockX());
                jsonObject_3.put("Y", player.getLocation().getBlockY());
                jsonObject_3.put("Z", player.getLocation().getBlockZ());
                jsonObject_3.put("Dimension", player.getLocation().getWorld().getEnvironment().name());

                jsonArray.add(jsonObject_3);
                player.sendMessage("Hub saved! 2");
            }

            String result = gson.toJson(jsonObject_1);

            FileOutputStream fileOutputStream = new FileOutputStream(testFile);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            bufferedWriter.write(result);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getHub() {

    }

    public static void remove(Player player) {
        try {
            FileReader fileReader = new FileReader(testFile);
            JSONParser jsonParser = new JSONParser();

            JSONObject jsonObject_1 = (JSONObject) jsonParser.parse(fileReader);
            JSONArray jsonArray = (JSONArray) jsonObject_1.get("Hub_Location");
            JSONObject jsonObject_2;

            Map<String, Integer> hub_list = new HashMap<>();

            if (jsonArray.size() > 0) {
                for (Object hubs : jsonArray) {
                    jsonObject_2 = (JSONObject) hubs;
                    hub_list.put((String) jsonObject_2.get("Dimension"), jsonArray.indexOf(hubs));
                }

                if (hub_list.containsKey(player.getLocation().getWorld().getEnvironment().name())) {
                    int index = hub_list.get(player.getLocation().getWorld().getEnvironment().name());

                    jsonArray.remove(index);
                    player.sendMessage("Hub removed!");
                } else {
                    player.sendMessage("Nothing to remove!");
                }
            } else {
                player.sendMessage("Nothing to remove!");
            }

            String result = gson.toJson(jsonObject_1);

            FileOutputStream fileOutputStream = new FileOutputStream(testFile);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            bufferedWriter.write(result);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
