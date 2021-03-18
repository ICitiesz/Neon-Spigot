package com.islandstudio.neon.Experimental.Teleportation;

import com.google.gson.*;
import com.islandstudio.neon.Stable.Old.Initialization.FolderManager.FolderList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeleportHandler {
    public static Map<String, String> names = new TreeMap<>();
    public static Map<String, Float> pitches = new TreeMap<>();
    public static Map<String, Double> posXs = new TreeMap<>();
    public static Map<String, Double> posYs = new TreeMap<>();
    public static Map<String, Double> posZs = new TreeMap<>();
    public static Map<String, String> dimensions = new TreeMap<>();
    public static Map<String, Float> yaws = new TreeMap<>();

    public static void add(String name, Player player) {
        Map<String, Object> format = new TreeMap<>();
        String specialChars = "<\\(\\[\\{=\\\\\\$\\^\\|]}\\)\\?\\*\\+\\.!>";
        Pattern pattern = Pattern.compile("[" + specialChars + "]");
        Matcher matcher = pattern.matcher(name);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jsonParser = new JsonParser();

        Location location = player.getLocation();

        float yaw = location.getYaw();
        float pitch = location.getPitch();
        int posX = location.getBlockX();
        int posY = location.getBlockY();
        int posZ = location.getBlockZ();

        World.Environment dimension = Objects.requireNonNull(location.getWorld()).getEnvironment();

        if (!matcher.find()) {
            File file = new File(FolderList.getFolder_2b_3, name + ".json");

            if (!file.exists()) {
                format.put("Name", name);
                format.put("Yaw", yaw);
                format.put("Pitch", pitch);
                format.put("Dimension", dimension);
                format.put("Position-X", posX);
                format.put("Position-Y", posY);
                format.put("Position-Z", posZ);

                JsonElement jsonElement = jsonParser.parse(format.toString());

                String result = gson.toJson(jsonElement);

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                    boolean isFileCreated = file.createNewFile();

                    bufferedWriter.write(result);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                format.clear();
            } else {
                player.sendRawMessage("This name already exist, please try another name!");
            }
        } else {
            player.sendRawMessage("The name must not contain these special character! -> \"<([{\\\\^-=$!|]})?*+.>\"");
        }


    }

    public static void remove(String name) {
        File[] listFiles = FolderList.getFolder_2b_3.listFiles();

        if (listFiles != null) {
            for (File file : listFiles) {
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
                    String fileName = file.getName();

                    if (fileName.equalsIgnoreCase(name + ".json")) {
                        boolean isFileDeleted = file.delete();
                    }
                }
            }
        }
    }

    public static String getName() {
        File[] listFiles = FolderList.getFolder_2b_3.listFiles();

        if (listFiles != null) {
            for (File file : listFiles) {
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
                    JSONParser jsonParser = new JSONParser();

                    try {
                        Object obj = jsonParser.parse(new FileReader(file));
                        JSONObject jsonObject = (JSONObject) obj;

                        names.put(file.getName(), (String) jsonObject.get("Name"));
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return names.get(getFileName());
    }

    public static Float getPitch() {
        File[] listFiles = FolderList.getFolder_2b_3.listFiles();

        if (listFiles != null) {
            for (File file : listFiles) {
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
                    JSONParser jsonParser = new JSONParser();

                    try {
                        Object obj = jsonParser.parse(new FileReader(file));
                        JSONObject jsonObject = (JSONObject) obj;
                        Object pitch = jsonObject.get("Pitch");

                        pitches.put(file.getName(), ((Double) pitch).floatValue());
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return pitches.get(getFileName());
    }

    public static Float getYaw() {
        File[] listFiles = FolderList.getFolder_2b_3.listFiles();

        if (listFiles != null) {
            for (File file : listFiles) {
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
                    JSONParser jsonParser = new JSONParser();

                    try {
                        Object obj = jsonParser.parse(new FileReader(file));
                        JSONObject jsonObject = (JSONObject) obj;
                        Object yaw = jsonObject.get("Yaw");

                        yaws.put(file.getName(), ((Double) yaw).floatValue());
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return yaws.get(getFileName());
    }

    public static Double getPosX() {
        File[] listFiles = FolderList.getFolder_2b_3.listFiles();

        if (listFiles != null) {
            for (File file : listFiles) {
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
                    JSONParser jsonParser = new JSONParser();

                    try {
                        Object obj = jsonParser.parse(new FileReader(file));
                        JSONObject jsonObject = (JSONObject) obj;
                        Object posX = jsonObject.get("Position-X");

                        posXs.put(file.getName(), ((Long) posX).doubleValue());
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return posXs.get(getFileName());
    }

    public static Double getPosY() {
        File[] listFiles = FolderList.getFolder_2b_3.listFiles();

        if (listFiles != null) {
            for (File file : listFiles) {
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
                    JSONParser jsonParser = new JSONParser();

                    try {
                        Object obj = jsonParser.parse(new FileReader(file));
                        JSONObject jsonObject = (JSONObject) obj;
                        Object posY = jsonObject.get("Position-Y");

                        posYs.put(file.getName(), ((Long) posY).doubleValue());
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return posYs.get(getFileName());
    }

    public static Double getPosZ() {
        File[] listFiles = FolderList.getFolder_2b_3.listFiles();

        if (listFiles != null) {
            for (File file : listFiles) {
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
                    JSONParser jsonParser = new JSONParser();

                    try {
                        Object obj = jsonParser.parse(new FileReader(file));
                        JSONObject jsonObject = (JSONObject) obj;
                        Object posZ = jsonObject.get("Position-Z");

                        posZs.put(file.getName(), ((Long) posZ).doubleValue());
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return posZs.get(getFileName());
    }

    public static String getDimension() {
        File[] listFiles = FolderList.getFolder_2b_3.listFiles();

        if (listFiles != null) {
            for (File file : listFiles) {
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
                    JSONParser jsonParser = new JSONParser();

                    try {
                        Object obj = jsonParser.parse(new FileReader(file));
                        JSONObject jsonObject = (JSONObject) obj;
                        Object dimension = jsonObject.get("Dimension");

                        dimensions.put(file.getName(), (String) dimension);
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return dimensions.get(getFileName());
    }

    public static String getFileName() {
        File[] listFiles = FolderList.getFolder_2b_3.listFiles();

        if (listFiles != null) {
            for (File file : listFiles) {
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
                    return file.getName();
                }
            }
        }
        return null;
    }

    public static void clearMap() {
        if (names.size() > 0) {
            names.clear();
        }

        if (yaws.size() > 0) {
            yaws.clear();
        }

        if (dimensions.size() > 0) {
            dimensions.clear();
        }

        if (pitches.size() > 0) {
            pitches.clear();
        }

        if (posXs.size() > 0) {
            posXs.clear();
        }

        if (posYs.size() > 0) {
            posYs.clear();
        }

        if (posZs.size() > 0) {
            posZs.clear();
        }
    }

}
