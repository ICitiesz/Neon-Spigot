package com.islandstudio.neon.Stable.Deprecated.Utilities;

import com.google.gson.*;
import com.islandstudio.neon.Experimental.PVPHandler;
import com.islandstudio.neon.Stable.New.Command.SyntaxHandler;
import com.islandstudio.neon.Stable.Old.Initialization.FolderManager.FolderList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class SettingsHandler {
    private static final boolean isOnlineMode = Bukkit.getServer().getOnlineMode();
    private static final File offlineFile = new File(FolderList.getFolder_1_b, "Server_Configuration.json");
    private static final File onlineFile = new File(FolderList.getFolder_1_a, "Server_Configuration.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final JSONParser jsonParser = new JSONParser();

    public static void initialize() {
        Map<String, Object> settings = new TreeMap<>();
        settings.put("PVP", true);
        settings.put("ChatLogging", true);
        settings.put("TNT_Protection", 0);

        JsonParser jsonParser_2 = new JsonParser();
        JsonElement jsonElement = jsonParser_2.parse(settings.toString());

        String result = gson.toJson(jsonElement);

        if (!onlineFile.exists()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(onlineFile);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                boolean isOnlineFileCreated = onlineFile.createNewFile();

                bufferedWriter.write(result);
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!offlineFile.exists()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(offlineFile);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                boolean isOfflineFileCreated = offlineFile.createNewFile();

                bufferedWriter.write(result);
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        settings.clear();
    }

    public static ArrayList<String> getSettings() throws IOException, ParseException {
        ArrayList<String> settings = new ArrayList<>();

        if (isOnlineMode) {
            for (Object key : getValue().keySet()) {
                settings.add((String) key);
            }
        } else {
            for (Object key : getValue().keySet()) {
                settings.add((String) key);
            }
        }
        return settings;
    }

    public static FileReader fetchSettings() throws IOException {
        if (isOnlineMode) {
            return new FileReader(onlineFile);
        } else {
            return new FileReader(offlineFile);
        }
    }

    public static JSONObject getValue() throws IOException, ParseException {
        return (JSONObject) jsonParser.parse(fetchSettings());
    }

   public static void setValue(String setting, String value, Player player) throws IOException, ParseException {
        JSONObject jsonObject = getValue();

       if (isOnlineMode) {
           switch (setting) {
               case "PVP": {
                   if (Boolean.parseBoolean(value)) {
                       if (!jsonObject.get(setting).equals(Boolean.parseBoolean(value))) {
                           try {
                               FileOutputStream fileOutputStream = new FileOutputStream(onlineFile);
                               BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                               jsonObject.replace(setting, Boolean.parseBoolean(value));
                               String result = gson.toJson(jsonObject);

                               bufferedWriter.write(result);
                               bufferedWriter.flush();
                               bufferedWriter.close();

                               PVPHandler.setPVP(Boolean.parseBoolean(value));

                               player.sendRawMessage(ChatColor.GREEN + "PVP has been enabled!");
                               player.sendRawMessage(ChatColor.GREEN + "Server settings has been updated!");
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       } else if (jsonObject.get(setting).equals(Boolean.parseBoolean(value))) {
                           player.sendRawMessage(ChatColor.YELLOW + "PVP already enabled!");
                       }
                   } else if (!Boolean.parseBoolean(value)) {
                       if (!jsonObject.get(setting).equals(Boolean.parseBoolean(value))) {
                           try {
                               FileOutputStream fileOutputStream = new FileOutputStream(onlineFile);
                               BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                               jsonObject.replace(setting, Boolean.parseBoolean(value));
                               String result = gson.toJson(jsonObject);

                               bufferedWriter.write(result);
                               bufferedWriter.flush();
                               bufferedWriter.close();

                               PVPHandler.setPVP(Boolean.parseBoolean(value));

                               player.sendRawMessage(ChatColor.GREEN + "PVP has been disabled!");
                               player.sendRawMessage(ChatColor.GREEN + "Server settings has been updated!");
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       } else if (jsonObject.get(setting).equals(Boolean.parseBoolean(value))) {
                           player.sendRawMessage(ChatColor.YELLOW + "PVP already disabled!");
                       }
                   }
                   break;
               }
               case "ChatLogging": {
                   if (Boolean.parseBoolean(value)) {
                       if (!jsonObject.get(setting).equals(Boolean.parseBoolean(value))) {
                           try {
                               FileOutputStream fileOutputStream = new FileOutputStream(onlineFile);
                               BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                               jsonObject.replace(setting, Boolean.parseBoolean(value));
                               String result = gson.toJson(jsonObject);

                               bufferedWriter.write(result);
                               bufferedWriter.flush();
                               bufferedWriter.close();

                               player.sendRawMessage(ChatColor.GREEN + "Chat Logging has been enabled!");
                               player.sendRawMessage(ChatColor.YELLOW + "Please reload the server to apply the effect!");
                               player.sendRawMessage(ChatColor.GREEN + "Server settings has been updated!");
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       } else if (jsonObject.get(setting).equals(Boolean.parseBoolean(value))) {
                           player.sendRawMessage(ChatColor.YELLOW + "ChatLogging already enabled!");
                       }

                   } else if (!Boolean.parseBoolean(value)) {
                       if (!jsonObject.get(setting).equals(Boolean.parseBoolean(value))) {
                           try {
                               FileOutputStream fileOutputStream = new FileOutputStream(onlineFile);
                               BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                               jsonObject.replace(setting, Boolean.parseBoolean(value));
                               String result = gson.toJson(jsonObject);

                               bufferedWriter.write(result);
                               bufferedWriter.flush();
                               bufferedWriter.close();

                               player.sendRawMessage(ChatColor.GREEN + "Chat Logging has been disabled!");
                               player.sendRawMessage(ChatColor.YELLOW + "Please reload the server to apply the effect!");
                               player.sendRawMessage(ChatColor.GREEN + "Server settings has been updated!");
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       } else if (jsonObject.get(setting).equals(Boolean.parseBoolean(value))) {
                           player.sendRawMessage(ChatColor.YELLOW + "ChatLogging already disabled!");
                       }
                   } else {
                       SyntaxHandler.sendSyntax(player, 1);
                   }
                   break;
               }

               case "TNT_Protection": {
                   switch (value) {
                       case "0": {
                           if (!jsonObject.get(setting).equals(Long.parseLong(value))) {
                               try {
                                   FileOutputStream fileOutputStream = new FileOutputStream(onlineFile);
                                   BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                                   jsonObject.replace(setting, Long.parseLong(value));
                                   String result = gson.toJson(jsonObject);

                                   bufferedWriter.write(result);
                                   bufferedWriter.flush();
                                   bufferedWriter.close();

                                   player.sendRawMessage(ChatColor.GREEN + "TNT Protection has been disabled!");
                                   player.sendRawMessage(ChatColor.GREEN + "Server settings has been updated!");
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                           } else if (jsonObject.get(setting).equals(Long.parseLong(value))) {
                               player.sendRawMessage(ChatColor.YELLOW + "TNT Protection already disabled!");
                           }
                           break;
                       }

                       case "1": {
                           if (!jsonObject.get(setting).equals(Long.parseLong(value))) {
                               try {
                                   FileOutputStream fileOutputStream = new FileOutputStream(onlineFile);
                                   BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                                   jsonObject.replace(setting, Long.parseLong(value));
                                   String result = gson.toJson(jsonObject);

                                   bufferedWriter.write(result);
                                   bufferedWriter.flush();
                                   bufferedWriter.close();

                                   player.sendRawMessage(ChatColor.GREEN + "TNT Protection has been set to level 1!");
                                   player.sendRawMessage(ChatColor.GREEN + "Server settings has been updated!");
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                           } else if (jsonObject.get(setting).equals(Long.parseLong(value))) {
                               player.sendRawMessage(ChatColor.YELLOW + "TNT Protection already at Level 1!");
                           }
                           break;
                       }

                       case "2": {
                           if (!jsonObject.get(setting).equals(Long.parseLong(value))) {
                               try {
                                   FileOutputStream fileOutputStream = new FileOutputStream(onlineFile);
                                   BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                                   jsonObject.replace(setting, Long.parseLong(value));
                                   String result = gson.toJson(jsonObject);

                                   bufferedWriter.write(result);
                                   bufferedWriter.flush();
                                   bufferedWriter.close();

                                   player.sendRawMessage(ChatColor.GREEN + "TNT Protection has been set to level 2!");
                                   player.sendRawMessage(ChatColor.GREEN + "Server settings has been updated!");
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                           } else if (jsonObject.get(setting).equals(Long.parseLong(value))) {
                               player.sendRawMessage(ChatColor.YELLOW + "TNT Protection already at Level 2!");
                           }
                           break;
                       }

                       default: {
                           SyntaxHandler.sendSyntax(player, 1);
                           break;
                       }
                   }
                   break;
               }

               default: {
                   SyntaxHandler.sendSyntax(player, 1);
                   break;
               }
           }
       } else {
           switch (setting) {
               case "PVP": {
                   if (value.equalsIgnoreCase("true")) {
                       if (!jsonObject.get(setting).equals("true")) {
                           try {
                               FileOutputStream fileOutputStream = new FileOutputStream(offlineFile);
                               BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                               jsonObject.replace(setting, value.toLowerCase());
                               String result = gson.toJson(jsonObject);

                               bufferedWriter.write(result);
                               bufferedWriter.flush();
                               bufferedWriter.close();

                               PVPHandler.setPVP(Boolean.parseBoolean(value.toLowerCase()));

                               player.sendRawMessage(ChatColor.GREEN + "PVP has been enabled!");
                               player.sendRawMessage(ChatColor.GREEN + "Server settings has been updated!");
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       } else if (jsonObject.get(setting).equals("true")) {
                           player.sendMessage(ChatColor.YELLOW + "PVP already enabled!");
                       }
                   } else if (value.equalsIgnoreCase("false")) {
                       if (!jsonObject.get(setting).equals("false")) {
                           try {
                               FileOutputStream fileOutputStream = new FileOutputStream(offlineFile);
                               BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                               jsonObject.replace(setting, value.toLowerCase());
                               String result = gson.toJson(jsonObject);

                               bufferedWriter.write(result);
                               bufferedWriter.flush();
                               bufferedWriter.close();

                               PVPHandler.setPVP(Boolean.parseBoolean(value.toLowerCase()));

                               player.sendMessage(ChatColor.GREEN + "PVP has been disabled!");
                               player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       } else if (jsonObject.get(setting).equals("false")) {
                           player.sendRawMessage(ChatColor.YELLOW + "PVP already disabled!");
                       }
                   } else {
                       SyntaxHandler.sendSyntax(player, 1);
                   }
                   break;
               }

               case "ChatLogging": {
                   if (value.equalsIgnoreCase("true")) {
                       if (!jsonObject.get(setting).equals("true")) {
                           try {
                               FileOutputStream fileOutputStream = new FileOutputStream(offlineFile);
                               BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                               jsonObject.replace(setting, value.toLowerCase());
                               String result = gson.toJson(jsonObject);

                               bufferedWriter.write(result);
                               bufferedWriter.flush();
                               bufferedWriter.close();

                               player.sendMessage(ChatColor.GREEN + "Chat Logging has been enabled!");
                               player.sendMessage(ChatColor.YELLOW + "Please reload the server to apply the effect!");
                               player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       } else {
                           player.sendMessage(ChatColor.YELLOW + "Chat Logging already enabled!");
                       }
                   } else if (value.equalsIgnoreCase("false")) {
                       if (!jsonObject.get(setting).equals("false")) {
                           try {
                               FileOutputStream fileOutputStream = new FileOutputStream(offlineFile);
                               BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                               jsonObject.replace(setting, value.toLowerCase());
                               String result = gson.toJson(jsonObject);

                               bufferedWriter.write(result);
                               bufferedWriter.flush();
                               bufferedWriter.close();

                               player.sendMessage(ChatColor.GREEN + "Chat Logging has been disabled!");
                               player.sendMessage(ChatColor.YELLOW + "Please reload the server to apply the effect!");
                               player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       } else {
                           player.sendMessage(ChatColor.YELLOW + "Chat Logging already disabled!");
                       }
                   } else {
                       SyntaxHandler.sendSyntax(player, 1);
                   }
                   break;
               }

               case "TNT_Protection": {
                   switch (value) {
                       case "0": {
                           if (!jsonObject.get(setting).equals(Long.parseLong(value))) {
                               try {
                                   FileOutputStream fileOutputStream = new FileOutputStream(offlineFile);
                                   BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                                   jsonObject.replace(setting, Long.parseLong(value));
                                   String result = gson.toJson(jsonObject);

                                   bufferedWriter.write(result);
                                   bufferedWriter.flush();
                                   bufferedWriter.close();

                                   player.sendRawMessage(ChatColor.GREEN + "TNT Protection has been disabled!");
                                   player.sendRawMessage(ChatColor.GREEN + "Server settings has been updated!");
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                           } else if (jsonObject.get(setting).equals(Long.parseLong(value))) {
                               player.sendRawMessage(ChatColor.YELLOW + "TNT Protection already disabled!");
                           }
                           break;
                       }

                       case "1": {
                           if (!jsonObject.get(setting).equals(Long.parseLong(value))) {
                               try {
                                   FileOutputStream fileOutputStream = new FileOutputStream(offlineFile);
                                   BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                                   jsonObject.replace(setting, Long.parseLong(value));
                                   String result = gson.toJson(jsonObject);

                                   bufferedWriter.write(result);
                                   bufferedWriter.flush();
                                   bufferedWriter.close();

                                   player.sendRawMessage(ChatColor.GREEN + "TNT Protection has been set to level 1!");
                                   player.sendRawMessage(ChatColor.GREEN + "Server settings has been updated!");
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                           } else if (jsonObject.get(setting).equals(Long.parseLong(value))) {
                               player.sendRawMessage(ChatColor.YELLOW + "TNT Protection already at Level 1!");
                           }
                           break;
                       }

                       case "2": {
                           if (!jsonObject.get(setting).equals(Long.parseLong(value))) {
                               try {
                                   FileOutputStream fileOutputStream = new FileOutputStream(offlineFile);
                                   BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                                   jsonObject.replace(setting, Long.parseLong(value));
                                   String result = gson.toJson(jsonObject);

                                   bufferedWriter.write(result);
                                   bufferedWriter.flush();
                                   bufferedWriter.close();

                                   player.sendRawMessage(ChatColor.GREEN + "TNT Protection has been set to level 2!");
                                   player.sendRawMessage(ChatColor.GREEN + "Server settings has been updated!");
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                           } else if (jsonObject.get(setting).equals(Long.parseLong(value))) {
                               player.sendRawMessage(ChatColor.YELLOW + "TNT Protection already at Level 2!");
                           }
                           break;
                       }

                       default: {
                           SyntaxHandler.sendSyntax(player, 1);
                           break;
                       }
                   }
                   break;
               }

               default: {
                   SyntaxHandler.sendSyntax(player, 1);
                   break;
               }
           }

       }
    }

}
