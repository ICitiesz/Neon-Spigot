package com.islandstudio.neon.stable.primary.iExperimental;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.primary.iFolder.FolderList;
import com.islandstudio.neon.stable.primary.iFolder.IFolder;
import com.islandstudio.neon.stable.secondary.iCommand.CommandHandler;
import com.islandstudio.neon.stable.secondary.iCommand.CommandSyntax;
import com.islandstudio.neon.stable.utils.iGUI.IGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class IExperimental {
    private final String experimentalFeatureName;
    private final boolean isEnabled;
    private final String description;
    private final String conflict;

    private final static ClassLoader classLoader = IExperimental.class.getClassLoader();
    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final static JSONParser jsonParser = new JSONParser();
    public IExperimental(Map.Entry<String, JSONObject> experimentalFeature) {
        this.experimentalFeatureName = experimentalFeature.getKey();
        this.isEnabled = (boolean) experimentalFeature.getValue().get("is_enabled");
        this.description = (String) experimentalFeature.getValue().get("description");
        this.conflict = (String) experimentalFeature.getValue().get("conflict");
    }

    public String getExperimentalFeatureName() {
        return experimentalFeatureName;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getDescription() {
        return description;
    }

    public String getConflict() {
        return conflict;
    }

    public static class Handler implements CommandHandler {
        private static boolean showIExperimentDemo = false;

        /**
         * Initialization for iExperimental.
         *
         */
        public static void init() {
            StringBuilder stringBuilder = new StringBuilder();
            IFolder.createNewFile(getExperimentalConfigFile(), FolderList.IEXPERIMENTAL.getFolder());

            final String ERR_MSG = "An error occurred while trying to initialize iExperimental: ";

            try {
                BufferedReader externalBufferedReader = new BufferedReader(new FileReader(getExperimentalConfigFile()));
                ArrayList<String> content = externalBufferedReader.lines().collect(Collectors.toCollection(ArrayList::new));

                final long targetDataLength = content.size();

                externalBufferedReader.close();

                /* Check if the target file is empty. */
                if (targetDataLength == 0) {
                    InputStream inputStream = classLoader.getResourceAsStream("resources/iExperimental.json");

                    if (inputStream == null)  throw new NullPointerException(ERR_MSG + "Is internal resource, 'iExperimental.json' missing or corrupted?");;

                    BufferedReader internalBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    Object[] internalExperimentalData = internalBufferedReader.lines().toArray();

                    if (internalExperimentalData.length == 0) throw new NullPointerException(ERR_MSG + "Is internal resource, 'iExperimental.json' corrupted?");;

                    /* Fetch data from resources directory inside .jar file
                     * Then, parse it into JSON object and write to the target file.
                     */
                    for (Object data : internalExperimentalData) {
                        stringBuilder.append(data);
                    }

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(getExperimentalConfigFile().toPath())));

                    JSONObject jsonObject = (JSONObject) jsonParser.parse(stringBuilder.toString());

                    bufferedWriter.write(gson.toJson(jsonObject));
                    bufferedWriter.close();

                    internalBufferedReader.close();
                    inputStream.close();
                }
            } catch (IOException | ParseException err) {
                if (err instanceof FileNotFoundException) {
                    System.out.println(ERR_MSG + "Is '" + Handler.getExperimentalConfigFile().getName() + "' missing?");
                    return;
                }

                if (err instanceof ParseException) {
                    System.out.println(ERR_MSG + "Is '" + Handler.getExperimentalConfigFile().getName() + "' corrupted?");
                    return;
                }

                System.out.println(ERR_MSG + "Failed to create/close I/O stream!");
            }

            updateExperimentalConfig();
            loadExperimentalFeature();
        }

        /**
         * Initialization for iExperiment event controller.
         *
         */
        public static void initEvent() {
            IConstructor.enableEvent(new EventController());
        }

        @Override
        public void setCommandHandler(Player commander, String[] args) {
            if (args.length != 1) {
                commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                return;
            }

            if (!commander.isOp()) {
                commander.sendMessage(CommandSyntax.INVALID_PERMISSION.getSyntaxMessage());
                return;
            }

            if (commander.isSleeping()) {
                commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "Unable to use iExperimental while player is sleeping!"));
                return;
            }

            if (getLoadedInternalExperimental().isEmpty()) {
                commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "There are aren't experimental features available for testing."));
                return;
            }

            Handler.setGUISession(commander);
            new GUIHandler(IGUI.Handler.getIGUI(commander)).openGUI();
        }

        @Override
        public List<String> tabCompletion(Player commander, String[] args) {
            return CommandHandler.super.tabCompletion(commander, args);
        }

        private static final Map<LoadedExperimental, String> loadedExperimental = new HashMap<>();
        private static final Map<UUID, Map<String, JSONObject>> experimentalGUISessions = new TreeMap<>();

        /**
         * Load all source of experimental feature into memory.
         */
        private static void loadExperimentalFeature() {
            final Map<String, JSONObject> INTERNAL_EXPERIMENTAL = getInternalExperimental();

            if (INTERNAL_EXPERIMENTAL.isEmpty()) return;

            final String EXTERNAL_EXPERIMENTAL = experimentalDataSerializer(getExternalExperimental());

            loadedExperimental.put(LoadedExperimental.INTERNAL, experimentalDataSerializer(getInternalExperimental()));
            loadedExperimental.put(LoadedExperimental.EXTERNAL, EXTERNAL_EXPERIMENTAL);
            loadedExperimental.put(LoadedExperimental.MODIFIABLE, EXTERNAL_EXPERIMENTAL);
        }

        public static boolean getShowExperimentalDemo() {
            return showIExperimentDemo;
        }

        public static void setShowIExperimentDemo(boolean toggle) {
            showIExperimentDemo = toggle;

            loadedExperimental.put(LoadedExperimental.INTERNAL, experimentalDataSerializer(getInternalExperimental()));
            loadedExperimental.put(LoadedExperimental.MODIFIABLE, experimentalDataSerializer(getInternalExperimental()));
        }

        protected static Map<String, JSONObject> getLoadedInternalExperimental() {
            String serializedInternalExperimental = loadedExperimental.get(LoadedExperimental.INTERNAL);

            if (serializedInternalExperimental == null) return new TreeMap<>();

            return experimentalDataDeserializer(serializedInternalExperimental);
        }

        public static Map<String, JSONObject> getLoadedExternalExperimental() {
            return experimentalDataDeserializer(loadedExperimental.get(LoadedExperimental.EXTERNAL));
        }

        protected static Map<String, JSONObject> getModifiableExperimental() {
            return experimentalDataDeserializer(loadedExperimental.get(LoadedExperimental.MODIFIABLE));
        }

        /**
         * Update the modifiable experimental feature data.
         *
         * @param experimentalData The experimental feature data.
         */
        protected static void setModifiableExperimental(Map<String, JSONObject> experimentalData) {
            loadedExperimental.replace(LoadedExperimental.MODIFIABLE, experimentalDataSerializer(experimentalData));
        }

        /**
         * Get the modifiable experimental feature data from the iExperimental GUI session.
         *
         * @param player The player who using the iExperimental.
         * @return The modifiable experimental feature data.
         */
        protected static Map<String, JSONObject> getModifiableExperimentalFromSession(Player player) {
            return experimentalGUISessions.get(player.getUniqueId());
        }

        /**
         * Create a iExperimental session per player that includes the latest experimental feature data.
         *
         * @param player The player who using the iExperimental.
         */
        private static void setGUISession(Player player) {
            experimentalGUISessions.putIfAbsent(player.getUniqueId(), getModifiableExperimental());
        }

        /**
         * Remove iExperimental session if the player not using the iExperimental.
         *
         * @param player The player.
         */
        private static void removeGUISession(Player player) {
            experimentalGUISessions.remove(player.getUniqueId());
        }

        /**
         * Serialize experimental data from map to string.
         *
         * @param experimentalData The experimental data.
         * @return The serialized experimental data.
         */
        private static String experimentalDataSerializer(Map<String, JSONObject> experimentalData) {
            String serializedExperimentalData = "";

            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

                objectOutputStream.writeObject(experimentalData);

                serializedExperimentalData = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());

                objectOutputStream.flush();
            } catch (IOException err) {
                System.out.println("An error occurred while trying to serialize experimental data: Failed to create output stream!");
            }

            return serializedExperimentalData;
        }

        /**
         * Deserialize experimental data from string to map.
         *
         * @param serializedExperimentalData The serialized experimental data.
         * @return The deserialized experimental data.
         */
        @SuppressWarnings("unchecked")
        private static Map<String, JSONObject> experimentalDataDeserializer(String serializedExperimentalData) {
            Map<String, JSONObject> deserializedExperimentalData = new TreeMap<>();

            try {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(serializedExperimentalData));
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

                deserializedExperimentalData = (Map<String, JSONObject>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException err) {
                final String ERR_MSG = "An error occurred while trying to deserialize experimental data: ";

                if (err instanceof ClassNotFoundException) {
                    throw new ClassCastException(ERR_MSG + "Internal class not found error!");
                }

                System.out.println(ERR_MSG + "Failed to create input stream!");
            }

            return deserializedExperimentalData;
        }

        /**
         * Get experimental feature from internal resource.
         *
         * @return A json object with experimental feature name and its toggle status.
         */
        @SuppressWarnings("unchecked")
        private static Map<String, JSONObject> getInternalExperimental() {
            final String ERR_MSG = "An error occurred while trying to get internal experimental feature: ";

            String resourceName = "iExperimental.json";

            Map<String, JSONObject> internalExperimental = new TreeMap<>();
            StringBuilder stringBuilder = new StringBuilder();

            if (showIExperimentDemo) {
                resourceName = "iExperimental-test_data.json";
            }

            try {
                InputStream inputStream = classLoader.getResourceAsStream("resources/" + resourceName);

                if (inputStream == null) throw new NullPointerException(ERR_MSG + "Is internal resource, " + resourceName + " missing or corrupted?");

                BufferedReader internalBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                Object[] internalExperimentalData = internalBufferedReader.lines().toArray();

                if (internalExperimentalData.length == 0) throw new NullPointerException(ERR_MSG + "Is internal resource, " + resourceName + " missing or corrupted?");

                for (Object data : internalExperimentalData) {
                    stringBuilder.append(data);
                }

                internalBufferedReader.close();
                inputStream.close();

                JSONObject experimentalFeature = (JSONObject) jsonParser.parse(stringBuilder.toString());

                experimentalFeature.forEach((experimentalFeatureName, experimentalFeatureDetail) -> {
                    JSONArray experimentalDataContainer = (JSONArray) experimentalFeatureDetail;

                    experimentalDataContainer.stream().findFirst().ifPresent(experimentalFeatureDetails
                            -> internalExperimental.putIfAbsent((String) experimentalFeatureName, (JSONObject) experimentalFeatureDetails));
                });

            } catch (IOException | ParseException err) {
                if (err instanceof IOException) {
                    System.out.println(ERR_MSG + "Is internal resource, " + resourceName + " missing?");
                }

                if (err instanceof ParseException) {
                    System.out.println(ERR_MSG + "Is internal resource, " + resourceName + " corrupted? ");
                }
            }

            return internalExperimental;
        }

        /**
         * Get experimental feature from external resource.
         *
         * @return A json object with experimental feature name and its toggle status.
         */
        @SuppressWarnings("unchecked")
        private static Map<String, JSONObject> getExternalExperimental() {
            Map<String, JSONObject> externalExperimental = new TreeMap<>();

            try {
                JSONObject experimentalFeature = (JSONObject) jsonParser.parse(new FileReader(Handler.getExperimentalConfigFile()));

                experimentalFeature.forEach((key, value) -> {
                    JSONArray experimentalDataContainer = (JSONArray) value;

                    experimentalDataContainer.stream().findFirst().ifPresent(experimentalFeatureDetails
                            -> externalExperimental.putIfAbsent((String) key, (JSONObject) experimentalFeatureDetails));
                });
            } catch (IOException | ParseException err) {
                final String ERR_MSG = "An error occurred while trying to get external experimental feature: ";

                if (err instanceof IOException) {
                    System.out.println(ERR_MSG + "Is '" + Handler.getExperimentalConfigFile().getName() + "' missing?");
                }

                if (err instanceof ParseException) {
                    System.out.println(ERR_MSG + "Is '" + Handler.getExperimentalConfigFile().getName() + "' corrupted? ");
                }
            }

            return externalExperimental;
        }

        /**
         * Get the iExperimental.json file
         *
         * @return The iExperimental.json file.
         */
        private static File getExperimentalConfigFile() {
            return new File(FolderList.SERVER_CONFIGURATION.getFolder(), "iExperimental.json");
        }
    }

    /**
     * Update the iExperimental.json if any new experimental feature added or removed.
     *
     */
    @SuppressWarnings("unchecked")
    private static void updateExperimentalConfig() {
        Map<String, JSONObject> internalExperimental = Handler.getInternalExperimental();
        Map<String, JSONObject> externalExperimental = Handler.getExternalExperimental();

        internalExperimental.forEach((experimentalName, experimentalDetail) -> {
            if (!externalExperimental.containsKey(experimentalName)) {
                externalExperimental.putIfAbsent(experimentalName, internalExperimental.get(experimentalName));
            }

            JSONObject externalExperimentalDetail = externalExperimental.get(experimentalName);

            /* Update experimental property */
            experimentalDetail.forEach((experimentalProperty, propertyValue) -> {
                if (!externalExperimentalDetail.containsKey(experimentalProperty)) {
                    externalExperimentalDetail.putIfAbsent(experimentalProperty, propertyValue);
                }
            });

            externalExperimentalDetail.keySet().removeIf(experimentalProperty -> !experimentalDetail.containsKey(experimentalProperty));

            /* Update experimental property value */
            String internalDescription = (String) experimentalDetail.get("description");
            String internalConflict = (String) experimentalDetail.get("conflict");

            String externalDescription = (String) externalExperimentalDetail.get("description");
            String externalConflict = (String) externalExperimentalDetail.get("conflict");

            if (!externalDescription.equals(internalDescription)) {
                externalExperimentalDetail.replace("description", internalDescription);
            }

            if (!externalConflict.equals(internalConflict)) {
                externalExperimentalDetail.replace("conflict", internalConflict);
            }
        });

        externalExperimental.keySet().removeIf(experimentalName -> !internalExperimental.containsKey(experimentalName));

        saveToggleStatus(externalExperimental);
    }

    /**
     * Save toggle status of experimental feature.
     *
     * @param experimentalData The modified experimental.
     */
    @SuppressWarnings("unchecked")
    public static void saveToggleStatus(Map<String, JSONObject> experimentalData) {
        try {
            JSONObject experimentalFeature = new JSONObject();

            experimentalData.forEach((experimentalName, experimentalDetail) -> experimentalFeature
                    .put(experimentalName, new Object[]{experimentalDetail}));

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Handler.getExperimentalConfigFile().toPath())));

            bufferedWriter.write(gson.toJson(experimentalFeature));
            bufferedWriter.close();
        } catch (IOException err) {
            System.out.println("An error occurred while trying to save modified experimental feature: Failed to create/close I/O stream!");
        }
    }

    private static class EventController implements Listener {
        @EventHandler
        private void onInventoryClick(InventoryClickEvent e) {
            GUIHandler.setEventHandler(e);
        }

        @EventHandler
        private void onInventoryClose(InventoryCloseEvent e) {
            final String GUI_NAME = e.getView().getTitle();
            final Player player = (Player) e.getPlayer();

            if (!GUI_NAME.equals(new GUIHandler(IGUI.Handler.getIGUI(player)).getGUIName())) return;

            if (GUIHandler.isNavigating) {
                GUIHandler.isNavigating = false;
                return;
            }

            /* Remove player from the guiSession */
            IGUI.Handler.iGUIContainer.remove(player);
            Handler.removeGUISession(player);
        }
    }
}
