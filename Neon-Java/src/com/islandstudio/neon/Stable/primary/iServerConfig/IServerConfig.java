package com.islandstudio.neon.stable.primary.iServerConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islandstudio.neon.stable.primary.iFolder.FolderList;
import com.islandstudio.neon.stable.primary.iFolder.IFolder;
import com.islandstudio.neon.stable.secondary.iCommand.CommandHandler;
import com.islandstudio.neon.stable.secondary.iCommand.CommandSyntax;
import com.islandstudio.neon.stable.secondary.iPVP.IPVP;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class IServerConfig {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final JSONParser jsonParser = new JSONParser();

    /**
     * Set the config value other value.
     *
     * @param setting The config name.
     * @param value The config value.
     */
    @SuppressWarnings("unchecked")
    public static void setServerConfig(String setting, Object value) {
        String serverConfigFileName = Handler.serverConfigFile.getName();

        try {
            JSONObject jsonObject = Handler.MODIFIABLE_SERVER_CONFIG;

            switch (setting) {
                case "PVP": {
                    jsonObject.replace(setting, value);

                    IPVP.setPVP((Boolean) value);
                    break;
                }

                case "TNT_Protection":
                case "iWaypoints-Cross_Dimension":
                case "iCutter":
                case "iSmelter":
                case "iHarvest": {
                    jsonObject.replace(setting, value);
                    break;
                }
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Handler.serverConfigFile.toPath())));

            bufferedWriter.write(gson.toJson(jsonObject));
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while trying to writing '" + serverConfigFileName + "'!");
        }
    }

    /**
     * Get external value by the given server config.
     *
     * @param serverConfig The server config.
     * @return The external value that's store as Object.
     */
    public static Object getExternalServerConfigValue(String serverConfig) {
        JSONObject externalServerConfig = new JSONObject(Handler.LOADED_EXTERNAL_SERVER_CONFIG);

        if (!externalServerConfig.containsKey(serverConfig)) {
            throw new NullPointerException("An error occurred while trying to get value from external server config: No such server config!");
        }

        if (isValueValid(externalServerConfig.get(serverConfig)) == null) return getInternalServerConfigValue(serverConfig);

        return isValueValid(externalServerConfig.get(serverConfig));
    }

    /**
     * Get internal value by the given server config.
     *
     * @param serverConfig The server config.
     * @return The internal value that's store as Object.
     */
    private static Object getInternalServerConfigValue(String serverConfig) {
        JSONObject internalServerConfig = new JSONObject(Handler.LOADED_INTERNAL_SERVER_CONFIG);

        if (!internalServerConfig.containsKey(serverConfig)) {
            throw new NullPointerException("No such server config from internal server config: Server_Configuration.json");
        }

        Object value = isValueValid(internalServerConfig.get(serverConfig));

        if (value == null) {
            throw new NullPointerException("An error occurred while trying to get internal server config value: Invalid value!'");
        }

        return value;
    }

    /**
     * Check if player given value is valid.
     *
     * @param value The player given value.
     * @return If it is valid, return its value, else return null.
     */
    private static Object isValueValid(Object value) {
        if (value instanceof Boolean) {
            return value;
        }

        if (value instanceof String) {
            String strValue = (String) value;

            if (strValue.equalsIgnoreCase("true") || strValue.equalsIgnoreCase("false")) {
                return Boolean.parseBoolean(strValue);
            }

            if (strValue.equalsIgnoreCase("0") || strValue.equalsIgnoreCase("1")
                    || strValue.equalsIgnoreCase("2")) {
                return Long.parseLong(strValue);
            }
        }

        if (value instanceof Long) {
            if (value.equals(0L) || value.equals(1L) || value.equals(2L)) {
                return value;
            }
        }

        return null;
    }

    public static class Handler implements CommandHandler {
        private static final File serverConfigFile = new File(FolderList.SERVER_CONFIGURATION.getFolder(), "Server_Configuration.json");

        private static final Map<String, Object> LOADED_INTERNAL_SERVER_CONFIG = new HashMap<>();
        private static final Map<String, Object> LOADED_EXTERNAL_SERVER_CONFIG = new HashMap<>();

        private static final JSONObject MODIFIABLE_SERVER_CONFIG = new JSONObject();

        /**
         * Load the server config into memory.
         *
         */
        @SuppressWarnings("unchecked")
        private static void loadServerConfig() {
            JSONObject externalServerConfig = getExternalServerConfig();

            LOADED_INTERNAL_SERVER_CONFIG.putAll(getInternalServerConfig());
            LOADED_EXTERNAL_SERVER_CONFIG.putAll(externalServerConfig);
            MODIFIABLE_SERVER_CONFIG.putAll(externalServerConfig);
        }

        /**
         * Initialization for server config.
         */
        public static void init() {
            IFolder.createNewFile(serverConfigFile, FolderList.SERVER_CONFIGURATION.getFolder());

            final String ERR_MSG = "An error occurred while trying to initialize iServerConfig: ";

            try {
                BufferedReader externalBufferedReader = new BufferedReader(new FileReader(serverConfigFile));
                long externalFileLines = externalBufferedReader.lines().count();

                externalBufferedReader.close();

                if (externalFileLines == 0) {
                    BufferedWriter clientBufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(serverConfigFile.toPath())));

                    clientBufferedWriter.write(gson.toJson(getInternalServerConfig()));
                    clientBufferedWriter.close();

                    return;
                }
            } catch (IOException err) {
                if (err instanceof FileNotFoundException) {
                    System.out.println(ERR_MSG + "Is '" + serverConfigFile.getName() + "' missing?");
                    return;
                }

                System.out.println(ERR_MSG + "Failed to create/close I/O stream!");
            }

            updateConfigElement();
            loadServerConfig();
        }

        /**
         * Set command handler
         *
         * @param commander The player who perform the command.
         * @param args The command arguments.
         */
        @SuppressWarnings("unchecked")
        public void setCommandHandler(Player commander, String[] args) {
            if (!commander.isOp()) {
                commander.sendMessage(CommandSyntax.INVALID_PERMISSION.getSyntaxMessage());
                return;
            }

            JSONObject serverConfig = MODIFIABLE_SERVER_CONFIG;

            switch (args.length) {
                case 2: {
                    if (!serverConfig.containsKey(args[1])) {
                        commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "Sorry, there are no such server config as "
                                + ChatColor.WHITE + "'" + ChatColor.GRAY + args[1] + ChatColor.WHITE + "'"
                                + ChatColor.YELLOW + "!"));
                    }

                    String serverConfigStatus = "";

                    Object validatedValue = isValueValid(serverConfig.get(args[1]));

                    if (validatedValue instanceof Boolean) {
                        if ((Boolean) validatedValue) {
                            serverConfigStatus = ChatColor.GREEN + String.valueOf(validatedValue);
                        } else {
                            serverConfigStatus = ChatColor.RED + String.valueOf(validatedValue);
                        }
                    }

                    if (validatedValue instanceof Long) {
                        serverConfigStatus = ChatColor.GREEN + String.valueOf(validatedValue);
                    }

                    commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GOLD + args[1]
                            + ChatColor.YELLOW + " is currently set to" + ChatColor.GRAY + ": " + serverConfigStatus));
                    break;
                }

                case 3: {
                    final Set<String> serverConfigNames = serverConfig.keySet();

                    if (serverConfigNames.stream().noneMatch(key -> key.equalsIgnoreCase(args[1]))) {
                        commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW
                                + "Sorry, there are no such server config as " + ChatColor.WHITE + "'"
                                + ChatColor.GRAY + args[1] + ChatColor.WHITE + "'" + ChatColor.YELLOW + "!"));
                        return;
                    }

                    Object validatedValue;

                    if (args[2].equalsIgnoreCase("default")) {
                        validatedValue = getInternalServerConfigValue(args[1]);
                    } else {
                        validatedValue = isValueValid(args[2]);
                    }

                    String serverConfigNewValue;

                    if (validatedValue == null) {
                        commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                        return;
                    }

                    if (args[1].equalsIgnoreCase("TNT_Protection")) {
                        if (!(validatedValue instanceof Long)) {
                            commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                            return;
                        }

                        serverConfigNewValue = ChatColor.GREEN + String.valueOf(validatedValue);
                    } else {
                        if (!(validatedValue instanceof Boolean)) {
                            commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                            return;
                        }

                        if ((Boolean) validatedValue) {
                            serverConfigNewValue = ChatColor.GREEN + String.valueOf(validatedValue);
                        } else {
                            serverConfigNewValue = ChatColor.RED + String.valueOf(validatedValue);
                        }
                    }

                    String serverConfigName = serverConfigNames.stream().filter(x -> x.equalsIgnoreCase(args[1])).findFirst().orElse("");

                    setServerConfig(serverConfigName, validatedValue);

                    commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GOLD + serverConfigName + ChatColor.YELLOW + " has been set to"
                            + ChatColor.GRAY + ": " + serverConfigNewValue));

                    commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "Please reload the server to apply the effect!"));

                    break;
                }

                default: {
                    commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                }
            }
        }


        /**
         * Tab completion for the command.
         *
         * @return A list of command arguments for /neon serverconfig
         */
        public List<String> tabCompletion(Player commander, String[] args) {
            final ArrayList<String> BLANK_LIST = new ArrayList<>();

            if (!commander.isOp()) return new ArrayList<>();

            ArrayList<String> serverConfigNames = new ArrayList<>(LOADED_INTERNAL_SERVER_CONFIG.keySet());

            switch (args.length) {
                case 2: {
                    return serverConfigNames.stream().filter(serverConfigName -> serverConfigName.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                }

                case 3: {
                    String serverConfigName = serverConfigNames.stream().filter(name -> name.equalsIgnoreCase(args[1])).findFirst().orElse(null);

                    if (serverConfigName == null) return new ArrayList<>();

                    if (!serverConfigName.equalsIgnoreCase("TNT_Protection")) {
                        return Arrays.stream(new String[]{"default", "true", "false"}).filter(value -> value.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
                    }

                    return Arrays.stream(new String[]{"default", "0", "1", "2"}).filter(value -> value.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
                }

                default: {
                    return BLANK_LIST;
                }
            }
        }

        /**
         * Get external server config as JSON object.
         *
         * @return A JSON object that contains external server config.
         */
        private static JSONObject getExternalServerConfig() {
            String serverConfigFileName = serverConfigFile.getName();

            final String ERR_MSG = "An error occurred while trying to get external server config: ";

            try {
                return (JSONObject) jsonParser.parse(new FileReader(serverConfigFile));
            } catch (IOException | ParseException err) {
                if (err instanceof IOException) {
                    System.out.println(ERR_MSG + "Is '" + serverConfigFileName + "' missing?");
                }

                if (err instanceof ParseException) {
                    System.out.println(ERR_MSG + "Is '" + serverConfigFileName + "' corrupted?");
                }
            }

            return new JSONObject();
        }

        /**
         * Get internal server config as JSON object.
         *
         * @return A JSON object that contains internal server config.
         */
        private static JSONObject getInternalServerConfig() {
            StringBuilder stringBuilder = new StringBuilder();

            final String ERR_MSG = "An error occurred while trying to get internal server config: ";

            try {
                InputStream internalInputStream = IServerConfig.class.getClassLoader().getResourceAsStream("resources/Server_Configuration.json");

                if (internalInputStream == null) throw new NullPointerException(ERR_MSG + "Is internal resource 'Server_Configuration.json' missing or corrupted?");

                BufferedReader internalBufferedReader = new BufferedReader(new InputStreamReader(internalInputStream));

                internalBufferedReader.lines().forEach(stringBuilder::append);

                internalInputStream.close();
                internalBufferedReader.close();

                return (JSONObject) jsonParser.parse(stringBuilder.toString());
            } catch (IOException err) {
                System.out.println(ERR_MSG + "Is internal resource 'Server_Configuration.json' missing?");
            } catch (ParseException err) {
                System.out.println(ERR_MSG + "Is internal resource 'Server_Configuration.json' corrupted?");
            }

            return new JSONObject();
        }

        /**
         * Keep external server config element up to date with internal config element.
         */
        @SuppressWarnings("unchecked")
        private static void updateConfigElement() {
            try {
                JSONObject internalServerConfig = getInternalServerConfig();
                JSONObject externalServerConfig = getExternalServerConfig();

                internalServerConfig.keySet().forEach(key -> {
                    if (!externalServerConfig.containsKey(key)) externalServerConfig.putIfAbsent(key, internalServerConfig.get(key));
                });

                externalServerConfig.keySet().removeIf(key -> !internalServerConfig.containsKey(key));

                BufferedWriter externalBufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(serverConfigFile.toPath())));

                externalBufferedWriter.write(gson.toJson(externalServerConfig));
                externalBufferedWriter.close();
            } catch (IOException err) {
                System.out.println("An error occurred while trying to update server config element: Failed to create/close I/O stream!'");
            }
        }
    }


}
