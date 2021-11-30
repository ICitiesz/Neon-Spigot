package com.islandstudio.neon.Stable.New.Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islandstudio.neon.Stable.New.Initialization.FolderManager.FolderList;
import com.islandstudio.neon.Experimental.PVPHandler;
import com.islandstudio.neon.Stable.New.iCommand.SyntaxHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;

public class ServerCFGHandler {
    private final ClassLoader classLoader = this.getClass().getClassLoader();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final JSONParser jsonParser = new JSONParser();

    public static void init() throws IOException, ParseException {
        ServerCFGHandler serverCFGHandler = new ServerCFGHandler();

        StringBuilder stringBuilder = new StringBuilder();

        createNewFiles();

        FileReader fileReader = new FileReader(getServerConfigFile());
        BufferedReader bufferedReader_1 = new BufferedReader(fileReader);
        long dataOutLength = bufferedReader_1.lines().toArray().length;

        if (dataOutLength == 0) {
            InputStream inputStream = serverCFGHandler.classLoader.getResourceAsStream("resources/Server_Configuration.json");

            fileReader.close();
            bufferedReader_1.close();

            if (inputStream != null) {
                BufferedReader bufferedReader_2 = new BufferedReader(new InputStreamReader(inputStream));
                Object[] dataIn = bufferedReader_2.lines().toArray();

                if (dataIn.length != 0) {
                    FileOutputStream fileOutputStream = new FileOutputStream(getServerConfigFile());
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                    for (Object data : dataIn) {
                        stringBuilder.append(data);
                    }

                    JSONObject jsonObject = (JSONObject) jsonParser.parse(stringBuilder.toString());

                    bufferedWriter.write(gson.toJson(jsonObject));
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
            updateConfigElement();
        }
    }

    @SuppressWarnings("unchecked")
    private static void updateConfigElement() throws IOException, ParseException {
        FileReader fileReader = new FileReader(getServerConfigFile());

        JSONObject sourceElement = getSourceConfigElement();
        if (sourceElement == null) return;
        JSONObject targetElement = getValue();

        FileOutputStream fileOutputStream = new FileOutputStream(getServerConfigFile());
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        sourceElement.keySet().forEach(key -> {
            if (!targetElement.containsKey(key)) {
                targetElement.putIfAbsent(key, sourceElement.get(key));
            }
        });

        targetElement.keySet().removeIf(key -> !sourceElement.containsKey(key));

        bufferedWriter.write(gson.toJson(targetElement));
        bufferedWriter.close();
        fileOutputStream.close();
        fileReader.close();
    }

    private static JSONObject getSourceConfigElement() throws IOException, ParseException {
        ServerCFGHandler serverCFGHandler = new ServerCFGHandler();
        StringBuilder stringBuilder = new StringBuilder();

        InputStream inputStream = serverCFGHandler.classLoader.getResourceAsStream("resources/Server_Configuration.json");

        if (inputStream == null) return null;

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Object[] resourceData = bufferedReader.lines().toArray();

        if (resourceData.length == 0) return null;

        for (Object data : resourceData) {
            stringBuilder.append(data);
        }

        bufferedReader.close();
        inputStream.close();

        return (JSONObject) jsonParser.parse(stringBuilder.toString());
    }

    private static void createNewFiles() throws IOException {
        File file = getServerConfigFile();

        if (!FolderList.FOLDER_A.getFolder().exists()) {
            boolean createFolder = FolderList.FOLDER_A.getFolder().mkdirs();
        }

        if (!file.exists()) {
            boolean createFile = file.createNewFile();
        }
    }

    public static File getServerConfigFile() {
        return new File(FolderList.FOLDER_A.getFolder(), "Server_Configuration.json");
    }

    public static JSONObject getValue() throws IOException, ParseException {
        FileReader fileReader = new FileReader(getServerConfigFile());
        return (JSONObject) jsonParser.parse(fileReader);
    }

    public static ArrayList<String> fetchConfigs() throws IOException, ParseException {
        ArrayList<String> configs = new ArrayList<>();

        FileReader file = new FileReader(getServerConfigFile());
        JSONObject jsonObject = (JSONObject) jsonParser.parse(file);

        for (Object key : jsonObject.keySet()) {
            configs.add((String) key);
        }
        return configs;
    }

    public static void setValue(String setting, String value, Player player) throws Exception {
        JSONObject jsonObject = getValue();

        if (jsonObject != null) {
            FileOutputStream fileOutputStream = new FileOutputStream(getServerConfigFile());
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            switch (setting) {
                case "PVP": {
                    if (value.equalsIgnoreCase("true")) {
                        if (!jsonObject.get(setting).equals("true")) {
                            jsonObject.replace(setting, value.toLowerCase());

                            PVPHandler.setPVP(Boolean.parseBoolean(value.toLowerCase()));
                            player.sendMessage(ChatColor.RED + "PVP has been enabled!");
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

                case "iWaypoints-Cross_Dimension": {
                    if (value.equalsIgnoreCase("true")) {
                        if (!jsonObject.get(setting).equals("true")) {
                            jsonObject.replace(setting, Boolean.parseBoolean(value.toLowerCase()));

                            player.sendMessage(ChatColor.GREEN + "Cross Dimension for iWaypoints has been enabled!");
                            player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "Cross Dimension for iWaypoints already enabled!");
                        }
                    } else if (value.equalsIgnoreCase("false")) {
                        if (!jsonObject.get(setting).equals("false")) {
                            jsonObject.replace(setting, Boolean.parseBoolean(value.toLowerCase()));

                            player.sendMessage(ChatColor.GREEN + "Cross Dimension for iWaypoints has been disabled!");
                            player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "Cross Dimension for iWaypoints already disabled!");
                        }
                    } else {
                        SyntaxHandler.sendSyntax(player, 1);
                    }
                    break;
                }

                case "iCutter": {
                    if (value.equalsIgnoreCase("true")) {
                        if (!jsonObject.get(setting).equals("true")) {
                            jsonObject.replace(setting, Boolean.parseBoolean(value.toLowerCase()));

                            player.sendMessage(ChatColor.GREEN + "iCutter has been enabled!");
                            player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                            player.sendMessage(ChatColor.YELLOW + "Please reload the server to apply the effect!");
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "iCutter already enabled!");
                        }
                    } else  if (value.equalsIgnoreCase("false")) {
                        if (!jsonObject.get(setting).equals("false")) {
                            jsonObject.replace(setting, Boolean.parseBoolean(value.toLowerCase()));

                            player.sendMessage(ChatColor.GREEN + "iCutter has been disabled!");
                            player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                            player.sendMessage(ChatColor.YELLOW + "Please reload the server to apply the effect!");
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "iCutter already disabled!");
                        }
                    } else {
                        SyntaxHandler.sendSyntax(player, 1);
                    }
                    break;
                }

                case "iSmelter": {
                    if (value.equalsIgnoreCase("true")) {
                        if (!jsonObject.get(setting).equals("true")) {
                            jsonObject.replace(setting, Boolean.parseBoolean(value.toLowerCase()));

                            player.sendMessage(ChatColor.GREEN + "iSmelter has been enabled!");
                            player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                            player.sendMessage(ChatColor.YELLOW + "Please reload the server to apply the effect!");
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "iSmelter already enabled!");
                        }
                    } else if (value.equalsIgnoreCase("false")) {
                        if (!jsonObject.get(setting).equals("false")) {
                            jsonObject.replace(setting, Boolean.parseBoolean(value.toLowerCase()));

                            player.sendMessage(ChatColor.GREEN + "iSmelter has been disabled!");
                            player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                            player.sendMessage(ChatColor.YELLOW + "Please reload the server to apply the effect!");
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "iSmelter already disabled!");
                        }
                    } else {
                        SyntaxHandler.sendSyntax(player, 1);
                    }
                    break;
                }

                case "iHarvest": {
                    if (value.equalsIgnoreCase("true")) {
                        if (!jsonObject.get(setting).equals("true")) {
                            jsonObject.replace(setting, Boolean.parseBoolean(value.toLowerCase()));

                            player.sendMessage(ChatColor.GREEN + "iHarvest has been enabled!");
                            player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                            player.sendMessage(ChatColor.YELLOW + "Please reload the server to apply the effect!");
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "iHarvest already enabled!");
                        }
                    } else if (value.equalsIgnoreCase("false")) {
                        if (!jsonObject.get(setting).equals("false")) {
                            jsonObject.replace(setting, Boolean.parseBoolean(value.toLowerCase()));

                            player.sendMessage(ChatColor.GREEN + "iHarvest has been disabled!");
                            player.sendMessage(ChatColor.GREEN + "Server settings has been updated!");
                            player.sendMessage(ChatColor.YELLOW + "Please reload the server to apply the effect!");
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "iHarvest already disabled!");
                        }
                    } else {
                        SyntaxHandler.sendSyntax(player, 1);
                    }
                    break;
                }

                default: {
                    SyntaxHandler.sendSyntax(player, 1);
                    break;
                }
            }

            String result = gson.toJson(jsonObject);

            bufferedWriter.write(result);
            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }
}
