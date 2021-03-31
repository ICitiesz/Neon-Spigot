package com.islandstudio.neon.Stable.Old.Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.islandstudio.neon.Stable.New.Utilities.NamespaceVersion;
import com.islandstudio.neon.Experimental.PVPHandler;
import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.New.Command.SyntaxHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class SettingsHandler {
    private static final File onlineFile_1 = new File(com.islandstudio.neon.Stable.Deprecated.Initiallization.FolderManager.FolderList.serverFolder_1_a, "Server_Configuration.json");
    private static final File offlineFile_1 = new File(com.islandstudio.neon.Stable.Deprecated.Initiallization.FolderManager.FolderList.serverFolder_1_b, "Server_Configuration.json");

    private static final File onlineFile_2 = new File(com.islandstudio.neon.Stable.Deprecated.Initiallization.FolderManager.FolderList.serverFolder_2_a, "Server_Configuration.json");
    private static final File offlineFile_2 = new File(com.islandstudio.neon.Stable.Deprecated.Initiallization.FolderManager.FolderList.serverFolder_2_b, "Server_Configuration.json");

    private static final File onlineFile_3 = new File(com.islandstudio.neon.Stable.Deprecated.Initiallization.FolderManager.FolderList.serverFolder_3_a, "Server_Configuration.json");
    private static final File offlineFile_3 = new File(com.islandstudio.neon.Stable.Deprecated.Initiallization.FolderManager.FolderList.serverFolder_3_b, "Server_Configuration.json");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final JSONParser jsonParser = new JSONParser();

    private static Object getOnlineMode;
    private static Object getBukkitVersion;

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

    private static final boolean IS_ONLINE_MODE = (boolean) getOnlineMode;
    private static final String VERSION = ((String) getBukkitVersion).split("\\.")[0] + "." + ((String) getBukkitVersion).split("\\.")[1];

    public static void initialize() {
        Map<String, Object> settings = new TreeMap<>();
        settings.put("PVP", true);
        settings.put("ChatLogging", true);
        settings.put("TNT_Protection", 0);

        JsonParser jsonParser_2 = new JsonParser();
        JsonElement jsonElement = jsonParser_2.parse(settings.toString());

        String result = gson.toJson(jsonElement);

        if (getFile() != null) {
            if (!getFile().exists()) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(getFile());
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                    boolean isFileCreated = getFile().createNewFile();

                    bufferedWriter.write(result);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        settings.clear();
    }

    public static JSONObject getValue() throws IOException, ParseException {
        if (getFile() != null) {
            FileReader fileReader = new FileReader(getFile());
            return (JSONObject) jsonParser.parse(fileReader);
        }
        return null;
    }

    public static ArrayList<String> getSettings() throws IOException, ParseException {
        ArrayList<String> settings = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();

        if (getFile() != null) {
            FileReader file = new FileReader(getFile());
            JSONObject jObj = (JSONObject) jsonParser.parse(file);

            for (Object key : jObj.keySet()) {
                settings.add((String) key);
            }
            return settings;
        }
        return null;
    }

    public static File getFile() {
        switch (VERSION) {
            case "1.14": {
                if (IS_ONLINE_MODE) {
                    return onlineFile_1;
                } else {
                    return offlineFile_1;
                }
            }

            case "1.15": {
                if (IS_ONLINE_MODE) {
                    return onlineFile_2;
                } else {
                    return offlineFile_2;
                }
            }

            case "1.16": {
                if (IS_ONLINE_MODE) {
                    return onlineFile_3;
                } else {
                    return offlineFile_3;
                }
            }

            default: {
                break;
            }
        }

        return null;
    }

    public static void setValue(String setting, String value, Player player) throws Exception {
        JSONObject jsonObject = getValue();

        try {
            if (getFile() != null) {
                if (jsonObject != null) {
                    //FileOutputStream fileOutputStream = new FileOutputStream(getFile());
                    //BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                    switch (setting) {
                        case "PVP": {
                            if (value.equalsIgnoreCase("true")) {
                                if (!jsonObject.get(setting).equals("true")) {
                                    jsonObject.replace(setting, value.toLowerCase());

                                    PVPHandler.setPVP(Boolean.parseBoolean(value.toLowerCase()));
                                    player.sendMessage(ChatColor.GREEN + "PVP has been enabled!");
                                    player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "PVP already enabled!");
                                }
                            } else if (value.equalsIgnoreCase("false")) {
                                if (!jsonObject.get(setting).equals("false")) {
                                    jsonObject.replace(setting, value.toLowerCase());

                                    PVPHandler.setPVP(Boolean.parseBoolean(value.toLowerCase()));
                                    player.sendMessage(ChatColor.GREEN + "PVP has been disabled!");
                                    player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "PVP already disabled!");
                                }
                            } else {
                                SyntaxHandler.sendSyntax(player, 1);
                            }
                            break;
                        }

                        case "ChatLogging": {
                            if (value.equalsIgnoreCase("true")) {
                                if (!jsonObject.get(setting).equals("true")) {
                                    jsonObject.replace(setting, value.toLowerCase());

                                    player.sendMessage(ChatColor.GREEN + "Chat Logging has been enabled!");
                                    player.sendMessage(ChatColor.YELLOW + "Please reload the server to apply the effect!");
                                    player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "Chat Logging already enabled!");
                                }
                            } else if (value.equalsIgnoreCase("false")) {
                                if (!jsonObject.get(setting).equals("false")) {
                                    jsonObject.replace(setting, value.toLowerCase());

                                    player.sendMessage(ChatColor.GREEN + "Chat Logging has been disabled!");
                                    player.sendMessage(ChatColor.YELLOW + "Please reload the server to apply the effect!");
                                    player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
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
                                        jsonObject.replace(setting, Long.parseLong(value));

                                        player.sendMessage(ChatColor.GREEN + "TNT Protection has been disabled!");
                                        player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                                    } else {
                                        player.sendMessage(ChatColor.YELLOW + "TNT Protection already disabled!");
                                    }
                                    break;
                                }

                                case "1": {
                                    if (!jsonObject.get(setting).equals(Long.parseLong(value))) {
                                        jsonObject.replace(setting, Long.parseLong(value));

                                        player.sendMessage(ChatColor.GREEN + "TNT Protection has been set to level 1!");
                                        player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                                    } else {
                                        player.sendMessage(ChatColor.YELLOW + "TNT Protection already at Level 1!");
                                    }
                                    break;
                                }

                                case "2": {
                                    if (!jsonObject.get(setting).equals(Long.parseLong(value))) {
                                        jsonObject.replace(setting, Long.parseLong(value));

                                        player.sendMessage(ChatColor.GREEN + "TNT Protection has been set to level 2!");
                                        player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                                    } else {
                                        player.sendMessage(ChatColor.YELLOW + "TNT Protection already at Level 2!");
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

                    String result = gson.toJson(jsonObject);

//                    bufferedWriter.write(result);
//                    bufferedWriter.flush();
//                    bufferedWriter.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
