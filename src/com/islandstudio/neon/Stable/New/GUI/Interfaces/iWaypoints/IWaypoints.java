package com.islandstudio.neon.Stable.New.GUI.Interfaces.iWaypoints;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islandstudio.neon.Stable.New.GUI.Initialization.GUIUtilityHandler;
import com.islandstudio.neon.Stable.New.Initialization.FolderManager.FolderList;
import com.islandstudio.neon.Stable.New.Command.SyntaxHandler;
import org.bukkit.*;

import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.*;

public class IWaypoints {
    public static Map<String, Map<String, JSONObject>> waypointData = new TreeMap<>();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final JSONParser jsonParser = new JSONParser();
    private final ClassLoader classLoader = this.getClass().getClassLoader();

    public static void init() throws Exception {
        IWaypoints iWaypoints = new IWaypoints();

        StringBuilder stringBuilder = new StringBuilder();

        createNewFiles();

        FileReader fileReader = new FileReader(getWaypointFile());
        BufferedReader bufferedReader_1 = new BufferedReader(fileReader);
        long dataOutLength = bufferedReader_1.lines().toArray().length;

        if (dataOutLength == 0) {
            InputStream inputStream = iWaypoints.classLoader.getResourceAsStream("Resources/iWaypoints.json");

            fileReader.close();
            bufferedReader_1.close();

            if (inputStream != null) {
                BufferedReader bufferedReader_2 = new BufferedReader(new InputStreamReader(inputStream));
                Object[] dataIn = bufferedReader_2.lines().toArray();

                if (dataIn.length != 0) {
                    FileOutputStream fileOutputStream = new FileOutputStream(getWaypointFile());
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
        }

    }

    private static void createNewFiles() throws Exception {
        File file = getWaypointFile();

        if (!FolderList.FOLDER_D.getFolder().exists()) {
            boolean createFolder = FolderList.FOLDER_D.getFolder().mkdirs();
        }

        if (!file.exists()) {
            boolean createFile = file.createNewFile();
        }
    }

    public static File getWaypointFile() {
        return new File(FolderList.FOLDER_D.getFolder(), "iWaypoints-Global.json");
    }

    public static void add(String name, Player player) throws Exception {
        File file = getWaypointFile();

        if (!file.exists()) {
            init();
        }

        if (name.contains("\\") || name.contains("\"")) {
            player.sendMessage(ChatColor.RED + "The name must not contain character(s) " + ChatColor.GOLD + "\\ " + ChatColor.RED + "or " + ChatColor.GOLD +  "\" " + ChatColor.RED  + "!");
        } else {
            if (getWaypointNames(player) != null) {
                if (!Objects.requireNonNull(getWaypointNames(player)).contains(name)) {
                    FileReader fileReader = new FileReader(file);
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();

                    JSONObject jsonObject_1 = (JSONObject) new JSONParser().parse(fileReader);
                    JSONObject jsonObject_2 = new JSONObject();
                    JSONObject jsonObject_3 = new JSONObject();

                    JSONArray jsonArray_1 = (JSONArray) jsonObject_1.get("Waypoints");
                    JSONArray jsonArray_2 = new JSONArray();

                    jsonObject_3.put("Location", locationSerialize(player));

                    jsonArray_2.add(jsonObject_3);

                    jsonObject_2.put(name, jsonArray_2);

                    jsonArray_1.add(jsonObject_2);

                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

                    bufferedWriter.write(String.valueOf(gson.toJson(jsonObject_1)));
                    bufferedWriter.flush();
                    bufferedWriter.close();

                    player.sendMessage(ChatColor.GREEN + "The waypoint has been saved as " + ChatColor.GRAY + "'" + ChatColor.GOLD + name + ChatColor.GRAY + "'" + ChatColor.GREEN + "!");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "The given name already exist! Please try another one!");
                }
            }
        }
    }

    public static void remove(String waypointName) throws Exception {
        FileReader fileReader = new FileReader(getWaypointFile());

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject_1 = (JSONObject) jsonParser.parse(fileReader);
        JSONArray jsonArray = (JSONArray) jsonObject_1.get("Waypoints");

        int index = 0;

        for (Object object : jsonArray) {
            if (((JSONObject) object).containsKey(waypointName)) {
                index = jsonArray.indexOf(object);
            }
        }

        jsonArray.remove(index);

        fileReader.close();

        FileOutputStream fileOutputStream = new FileOutputStream(getWaypointFile());
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        bufferedWriter.write(String.valueOf(gson.toJson(jsonObject_1)));
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public static Map<String, JSONObject> getWaypointData() throws Exception {
        FileReader fileReader = new FileReader(getWaypointFile());
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        Map<String, JSONObject> data = new TreeMap<>();

        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) ((JSONObject) jsonParser.parse(bufferedReader)).get("Waypoints");

        fileReader.close();
        bufferedReader.close();

        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;

            for (Object key : jsonObject.keySet()) {
                JSONArray jsonArray_2 = (JSONArray) jsonObject.get(key);
                JSONObject jsonObject_2 = (JSONObject) jsonArray_2.get(0);

                data.put((String) key, jsonObject_2);
            }
        }

        return data;
    }

    public static ArrayList<String> getWaypointNames(Player player) {
        try {
            return new ArrayList<>(waypointData.get(player.getUniqueId().toString()).keySet());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDimension(Player player, String waypointName) throws Exception {
        Location location = locationDeserialize(player, waypointName);

        switch (Objects.requireNonNull(location.getWorld()).getEnvironment().toString()) {
            case "NORMAL": {
                return ChatColor.GREEN + "Over World";
            }

            case "NETHER": {
                return ChatColor.DARK_RED + "Nether";
            }

            case "THE_END": {
                return ChatColor.DARK_PURPLE + "The End";
            }

            default: {
                return null;
            }
        }
    }

    public static String getAvailability(Player player, String waypointName) throws Exception {
        Location location = locationDeserialize(player, waypointName);

        if (player.getLocation().getWorld() != null) {
            if (player.getLocation().getWorld().getEnvironment().toString().equalsIgnoreCase(Objects.requireNonNull(location.getWorld()).getEnvironment().toString())) {
                return ChatColor.GREEN + "Available!";
            } else {
                return ChatColor.RED + "Unavailable!";
            }
        }
        return null;
    }

    public static void teleport(Player player, Location location, String waypointNameGold, int posX, int posY, int posZ) {
        player.spawnParticle(Particle.DRAGON_BREATH, player.getLocation(), 700);
        player.teleport(location);

        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1);
        player.spawnParticle(Particle.PORTAL, player.getLocation(), 700);
        player.sendMessage(ChatColor.GREEN + "You have been teleported to " + waypointNameGold + ChatColor.GRAY + ", " + ChatColor.AQUA + posX + ", " + posY + ", " + posZ + ChatColor.GREEN + " !");
    }

    public static void setCommandHandler(String[] args, Player player) {
        if (!player.isSleeping()) {
            switch (args.length) {
                case 0: {
                    try {
                        waypointData.put(player.getUniqueId().toString(), getWaypointData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    new Handler(GUIUtilityHandler.getGUIUtility(player)).open();
                    break;
                }

                case 1: {
                    if (args[0].equalsIgnoreCase("remove")) {
                        try {
                            waypointData.put(player.getUniqueId().toString(), getWaypointData());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new Handler_Removal(GUIUtilityHandler.getGUIUtility(player)).open();
                    } else {
                        SyntaxHandler.sendSyntax(player, 1);
                    }
                    break;
                }

                case 2: {
                    if (args[0].equalsIgnoreCase("add")) {
                        try {
                            waypointData.put(player.getUniqueId().toString(), getWaypointData());
                            IWaypoints.add(args[1], player);
                            waypointData.remove(player.getUniqueId().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        try {
                            IWaypoints.remove(args[1]);
                            waypointData.remove(player.getUniqueId().toString());
                            Bukkit.getServer().broadcastMessage(ChatColor.RED + "The waypoint " + ChatColor.GRAY + "'" + ChatColor.GOLD + args[1] + ChatColor.GRAY + "'" + ChatColor.RED + " has been removed by " + ChatColor.WHITE + player.getName() + ChatColor.RED + " !");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        SyntaxHandler.sendSyntax(player, 1);
                    }
                    break;
                }

                default: {
                    SyntaxHandler.sendSyntax(player, 1);
                }
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "You can't use iWaypoints while you're sleeping!");
        }
    }

    public static Location locationDeserialize(Player player, String waypointName) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode((String) waypointData.get(player.getUniqueId().toString()).get(waypointName).get("Location")));
        BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

        return (Location) bukkitObjectInputStream.readObject();
    }

    private static String locationSerialize(Player player) throws Exception {
        Location location = player.getLocation();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);

        bukkitObjectOutputStream.writeObject(location);
        bukkitObjectOutputStream.flush();

        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }
}
