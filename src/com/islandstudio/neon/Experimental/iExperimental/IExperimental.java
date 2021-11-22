package com.islandstudio.neon.Experimental.iExperimental;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islandstudio.neon.Stable.New.features.GUI.Initialization.GUIUtilityHandler;
import com.islandstudio.neon.Stable.New.Initialization.FolderManager.FolderHandler;
import com.islandstudio.neon.Stable.New.Initialization.FolderManager.FolderList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class IExperimental {
    private final ClassLoader classLoader = this.getClass().getClassLoader();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final JSONParser jsonParser = new JSONParser();

    /* Initialization for iExperimental */
    public static void init() throws IOException, ParseException {
        StringBuilder stringBuilder = new StringBuilder();

        createNewFile();

        FileReader fileReader = new FileReader(getFile());
        BufferedReader bufferedReader_1 = new BufferedReader(fileReader);

        ArrayList<String> content = bufferedReader_1.lines().collect(Collectors.toCollection(ArrayList::new));

        long targetDataLength = content.size();

        fileReader.close();
        bufferedReader_1.close();


        /* Check if the target file is empty. */
        if (targetDataLength == 0) {
            InputStream inputStream = new IExperimental().classLoader.getResourceAsStream("resources/iExperimental.json");

            if (inputStream == null) return;

            BufferedReader bufferedReader_2 = new BufferedReader(new InputStreamReader(inputStream));
            Object[] resourceData = bufferedReader_2.lines().toArray();

            if (resourceData.length == 0) return;

            /* Fetch data from resources directory inside .jar file
             * Then, parse it into JSON object and write to the target file.
             */
            for (Object data : resourceData) {
                stringBuilder.append(data);
            }

            FileOutputStream fileOutputStream = new FileOutputStream(getFile());
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            IExperimental iExperimental = new IExperimental();

            JSONObject jsonObject = (JSONObject) iExperimental.jsonParser.parse(stringBuilder.toString());

            bufferedWriter.write(iExperimental.gson.toJson(jsonObject));
            bufferedWriter.close();

            bufferedReader_2.close();
            inputStream.close();

            fileOutputStream.close();

            return;
        }

        update();
    }

    /* Update the content of iExperimental.json if any experimental feature added or removed. */
    @SuppressWarnings("unchecked")
    private static void update() throws IOException, ParseException {
        FileReader fileReader = new FileReader(getFile());
        IExperimental iExperimental = new IExperimental();

        try {
            JSONObject jsonObject_1 = getSource();
            if (jsonObject_1 == null) return;
            JSONObject jsonObject_2 = getClient();

            FileOutputStream fileOutputStream = new FileOutputStream(getFile());
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            jsonObject_1.keySet().forEach(key -> {
                if (!jsonObject_2.containsKey(key)) {
                    jsonObject_2.putIfAbsent(key, jsonObject_1.get(key));
                }

                if (jsonObject_2.keySet().size() > 0) {
                    JSONArray jsonArray_1 = (JSONArray) jsonObject_1.get(key);
                    JSONArray jsonArray_2 = (JSONArray) jsonObject_2.get(key);
                    JSONObject jsonObject_3 = (JSONObject) jsonArray_1.get(0);
                    JSONObject jsonObject_4 = (JSONObject) jsonArray_2.get(0);

                    String innerKey_1 = "description";
                    String innerKey_2 = "conflict";

                    if (!((String) jsonObject_4.get(innerKey_1)).equalsIgnoreCase((String) jsonObject_3.get(innerKey_1))) {
                        jsonObject_4.replace(innerKey_1, jsonObject_3.get(innerKey_1));
                    }

                    if (!((String) jsonObject_4.get(innerKey_2)).equalsIgnoreCase((String) jsonObject_3.get(innerKey_2))) {
                        jsonObject_4.replace(innerKey_2, jsonObject_3.get(innerKey_2));
                    }
                }
            });

            jsonObject_2.keySet().removeIf(key -> !jsonObject_1.containsKey(key));

            bufferedWriter.write(iExperimental.gson.toJson(jsonObject_2));
            bufferedWriter.close();
            fileOutputStream.close();
            fileReader.close();
        } catch (ParseException e) {
            fileReader.close();

            boolean delete = getFile().delete();

            // Temporary error message
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.RED + "File corruption found: iExperiment.json");
            init();
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.RED + "File recreated: iExperiment.json");
        }
    }

    /* Save experiment config */
    public static void save(JSONObject experimentConfig) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(getFile());
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

        IExperimental iExperimental = new IExperimental();

        bufferedWriter.write(iExperimental.gson.toJson(experimentConfig));
        bufferedWriter.close();
    }

    /* Get source content */
    public static JSONObject getSource() throws ParseException, IOException {
        IExperimental iExperimental = new IExperimental();
        StringBuilder stringBuilder = new StringBuilder();

        InputStream inputStream = new IExperimental().classLoader.getResourceAsStream("resources/iExperimental.json");

        if (inputStream == null) return null;

        BufferedReader bufferedReader_2 = new BufferedReader(new InputStreamReader(inputStream));
        Object[] resourceData = bufferedReader_2.lines().toArray();

        if (resourceData.length == 0) return null;

        for (Object data : resourceData) {
            stringBuilder.append(data);
        }

        bufferedReader_2.close();
        inputStream.close();

        return (JSONObject) iExperimental.jsonParser.parse(stringBuilder.toString());
    }

    /* Get client content */
    public static JSONObject getClient() throws IOException, ParseException {
        FileReader fileReader = new FileReader(getFile());
        return (JSONObject) new IExperimental().jsonParser.parse(fileReader);
    }

    /* Check if the file is existed, if not then it will be created. */
    public static void createNewFile() throws IOException {
        File file = getFile();

        FolderHandler.init();

        if (!file.exists()) {
            boolean createFile = file.createNewFile();
        }
    }

    /* Get the iExperimental.json file. */
    public static File getFile() {
        return new File(FolderList.FOLDER_A.getFolder(), "iExperimental.json");
    }

    /* Open iExperimental panel. */
    public static void open(Player player) throws IOException, ParseException {
        new Handler(GUIUtilityHandler.getGUIUtility(player)).open();
    }
}
