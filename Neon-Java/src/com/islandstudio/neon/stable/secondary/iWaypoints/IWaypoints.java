package com.islandstudio.neon.stable.secondary.iWaypoints;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.islandstudio.neon.stable.secondary.iCommand.CommandHandler;
import com.islandstudio.neon.stable.secondary.iCommand.CommandSyntax;
import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.primary.iFolder.FolderList;
import com.islandstudio.neon.stable.primary.iFolder.IFolder;
import com.islandstudio.neon.stable.utils.iGUI.IGUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.util.Consumer;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class IWaypoints {
    private final Location waypointLocation;
    private final String waypointName;
    private String waypointDimension;
    private final int waypointBlockX;
    private final int waypointBlockY;
    private final int waypointBlockZ;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final JSONParser jsonParser = new JSONParser();
    private static final ClassLoader classLoader = IWaypoints.class.getClassLoader();

    public IWaypoints(Map.Entry<String, JSONObject> waypointData) {
        this.waypointLocation = Handler.locationDeserializer((String) waypointData.getValue().get("Location"));
        this.waypointName = waypointData.getKey();

        Consumer<String> dimensionNameWithColor = (worldDimension) -> {
            switch (worldDimension) {
                case "NORMAL": {
                    this.waypointDimension = ChatColor.GREEN + "Over World";
                    break;
                }

                case "NETHER": {
                    this.waypointDimension = ChatColor.RED + "Nether";
                    break;
                }

                case "THE_END": {
                    this.waypointDimension = ChatColor.DARK_PURPLE + "The End";
                    break;
                }

                default: {
                    this.waypointDimension = ChatColor.GRAY + "Unknown";
                    break;
                }

            }
        };

        dimensionNameWithColor.accept(Objects.requireNonNull(waypointLocation.getWorld()).getEnvironment().toString());

        this.waypointBlockX = waypointLocation.getBlockX();
        this.waypointBlockY = waypointLocation.getBlockY();
        this.waypointBlockZ = waypointLocation.getBlockZ();
    }

    public Location getWaypointLocation() {
        return waypointLocation;
    }

    public String getWaypointName() {
        return waypointName;
    }

    public String getWaypointDimension() {
        return waypointDimension;
    }

    public int getWaypointBlockX() {
        return waypointBlockX;
    }

    public int getWaypointBlockY() {
        return waypointBlockY;
    }

    public int getWaypointBlockZ() {
        return waypointBlockZ;
    }

    public boolean canTeleportOverDimension(Player player) {
        World.Environment playerEnvironment = Objects.requireNonNull(player.getLocation().getWorld()).getEnvironment();
        World.Environment locationEnvironment = Objects.requireNonNull(waypointLocation.getWorld()).getEnvironment();

        return playerEnvironment.equals(locationEnvironment);
    }

    /**
     * Waypoint teleportation.
     *
     * @param player The player who perform the teleport.
     * @param waypointName The chosen waypoint name.
     * @param location The chosen waypoint location.
     */
    public static void teleportToWaypoint(Player player, String waypointName, Location location) {
        player.spawnParticle(Particle.DRAGON_BREATH, player.getLocation(), 700);
        player.teleport(location);

        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1);
        player.spawnParticle(Particle.PORTAL, player.getLocation(), 700);
        player.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GREEN + "You have been teleported to "
                + ChatColor.GOLD + waypointName + ChatColor.WHITE + " [" + ChatColor.AQUA + location.getBlockX()
                + ChatColor.GRAY + ", " + ChatColor.AQUA + location.getBlockY() + ChatColor.GRAY + ", "
                + ChatColor.AQUA + location.getBlockZ() + ChatColor.WHITE + "]" + ChatColor.GREEN + "!"));
    }

    public static class Handler implements CommandHandler {
        private static final Map<UUID, Map<String, JSONObject>> iWaypointGUISessions = new TreeMap<>();

        /**
         * Initialization for iWaypoints.
         */
        public static void init() {
            File waypointFile = Handler.getWaypointFile();

            IFolder.createNewFile(waypointFile, FolderList.IWAYPOINTS.getFolder());

            IConstructor.enableEvent(new EventController());

            final String ERR_MSG = "An error occurred while trying to initialize iWaypoint '" + waypointFile.getName() + "': ";

            try {
                BufferedReader externalBufferedReader = new BufferedReader(new FileReader(waypointFile));
                final long externalWaypointFileSize = externalBufferedReader.lines().count();

                externalBufferedReader.close();

                if (externalWaypointFileSize != 0L) return;

                BufferedWriter externalBufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(waypointFile.toPath())));

                InputStream internalInputStream = classLoader.getResourceAsStream("resources/iWaypoints.json");

                if (internalInputStream == null) throw new NullPointerException(ERR_MSG + "Is internal resource 'iWaypoints.json' corrupted or missing?");

                BufferedReader internalBufferedReader = new BufferedReader(new InputStreamReader(internalInputStream));
                Object[] internalWaypointData = internalBufferedReader.lines().toArray();

                StringBuilder stringBuilder = new StringBuilder();

                Arrays.stream(internalWaypointData).sequential().forEach(stringBuilder::append);

                externalBufferedWriter.write(gson.toJson(jsonParser.parse(stringBuilder.toString())));
                externalBufferedWriter.close();
            } catch (IOException err) {
                if (err instanceof FileNotFoundException) {
                    System.out.println(ERR_MSG + "Is " + waypointFile.getName() + " missing?");
                    return;
                }

                System.out.println(ERR_MSG + "Failed to close I/O stream!");
            } catch (ParseException err) {
                System.out.println(ERR_MSG +"Is internal resource '" + waypointFile.getName() + "' corrupted?");
            }
        }

        @Override
        public void setCommandHandler(Player commander, String[] args) {
            if (commander.isSleeping()) {
                commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "Unable to use iWaypoint while player is sleeping!"));
                return;
            }

            Map<String, JSONObject> waypointData = getWaypointData();
            final String NO_WAYPOINT_WARN = CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "No waypoint can be showed at the moment!");

            switch (args.length) {
                case 1: {
                    if (waypointData.isEmpty()) {
                        commander.sendMessage(NO_WAYPOINT_WARN);
                        return;
                    }

                    /* Create a session with the latest waypoint data when player launching the GUI */
                    Handler.setGUISession(commander);

                    new GUIHandlerMain(IGUI.Handler.getIGUI(commander)).openGUI();
                    break;
                }

                case 2: {
                    if (!args[1].equalsIgnoreCase("remove")) {
                        commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                        return;
                    }

                    if (waypointData.isEmpty()) {
                        commander.sendMessage(NO_WAYPOINT_WARN);
                        return;
                    }

                    /* Create a session with the latest waypoint data when player launching the GUI */
                    Handler.setGUISession(commander);

                    new GUIHandlerRemoval(IGUI.Handler.getIGUI(commander)).openGUI();
                    break;
                }

                case 3: {
                    final String WAYPOINT_NAME = args[2];

                    if (args[1].equalsIgnoreCase("add")) {
                        /* Special character validation */
                        if (WAYPOINT_NAME.contains("\\") || WAYPOINT_NAME.contains("\"")) {
                            commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.RED
                            + "The waypoint name must not contain " + ChatColor.GOLD + "\\ " + ChatColor.RED
                            + "or " + ChatColor.GOLD + "\"" + ChatColor.RED + "!"));
                            return;
                        }

                        /* Waypoint name length validation */
                        if (WAYPOINT_NAME.length() > 32) {
                            commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.RED
                            + "The waypoint name must not exceed " + ChatColor.GOLD + "32 " + ChatColor.RED + "characters!"));
                            return;
                        }

                        /* Existence validation */
                        if (waypointData.containsKey(WAYPOINT_NAME)) {
                            commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW
                            + "The waypoint name" + ChatColor.GRAY + " '" + ChatColor.GOLD + WAYPOINT_NAME
                            + ChatColor.GRAY + "' " + ChatColor.YELLOW + "already exist! Please try another one!"));
                            return;
                        }

                        addWaypoint(WAYPOINT_NAME, commander);

                        commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GREEN
                                + "The waypoint has been saved as " + ChatColor.GRAY + "'" + ChatColor.GOLD
                                + WAYPOINT_NAME + ChatColor.GRAY + "'" + ChatColor.GREEN + "!"));
                        return;
                    }

                    if (args[1].equalsIgnoreCase("remove")) {
                        /* Existence validation */
                        if (!waypointData.containsKey(WAYPOINT_NAME)) {
                            commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW
                                    + "No such waypoint as" + ChatColor.GRAY + " '" + ChatColor.GOLD + WAYPOINT_NAME
                                    + ChatColor.GRAY + "' " + ChatColor.YELLOW + "in iWaypoints!"));
                            return;
                        }

                        /* Remove the waypoint from the other player that also using Waypoint Removal
                        * Which update the current waypoint data to the latest */
                        GUIHandlerRemoval.pendingRemovalContainer.forEach((playerUUID, selectedWaypoint) -> {
                            if (playerUUID.equals(commander.getUniqueId())) return;

                            GUIHandlerRemoval.pendingRemovalContainer.get(playerUUID).remove(WAYPOINT_NAME);
                        });

                        /* Remove the waypoint from the other player that also using Waypoint Main GUI
                         * Which update the current waypoint data to the latest */
                        Handler.getGUISession().forEach((playerUUID, currentWaypointData) -> {
                            if (playerUUID.equals(commander.getUniqueId())) return;

                            Handler.getGUISession().get(playerUUID).remove(WAYPOINT_NAME);
                        });

                        removeWaypoint(WAYPOINT_NAME);

                        IConstructor.getPlugin().getServer().broadcastMessage(CommandSyntax.Handler.
                                createSyntaxMessage(ChatColor.WHITE + commander.getName() + ChatColor.RED + " removed the waypoint," + ChatColor.GRAY
                                + " '" + ChatColor.GOLD + WAYPOINT_NAME + ChatColor.GRAY + "' "
                                + ChatColor.RED + "from iWaypoints!"));
                    }

                    break;
                }

                default: {
                    commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                }
            }
        }

        @Override
        public List<String> tabCompletion(Player commander, String[] args) {
            final ArrayList<String> BLANK_LIST = new ArrayList<>();

            switch (args.length) {
                case 2: {
                    return Arrays.stream(new String[]{"add", "remove"}).filter(value -> value.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                }

                case 3: {
                    if (!args[1].equalsIgnoreCase("remove")) return BLANK_LIST;

                    IWaypoints.Handler.setGUISession(commander);

                    final ArrayList<String> waypointNames = new ArrayList<>(IWaypoints.Handler.getWaypointDataFromSession(commander).keySet());

                    IWaypoints.Handler.removeGUISession(commander);

                    return waypointNames.stream().filter(waypointName -> waypointName.startsWith(args[2])).collect(Collectors.toList());
                }

                default: {
                    return BLANK_LIST;
                }
            }
        }

        /**
         * Add waypoint operation.
         *
         * @param waypointName The given waypoint name.
         * @param player The player who wants to set the waypoint.
         */
        @SuppressWarnings("unchecked")
        public static void addWaypoint(String waypointName, Player player) {
            File waypointFile = getWaypointFile();

            final String ERR_MSG = "An error occurred while trying to add waypoint: ";

            if (!waypointFile.exists()) init();

            final long externalWaypointFileLength = waypointFile.length();

            try {
                BufferedReader externalBufferedReader = new BufferedReader(new FileReader(waypointFile));
                JSONObject mainStructure = (JSONObject) jsonParser.parse(externalBufferedReader);
                externalBufferedReader.close();

                if (externalWaypointFileLength == 0L) init();

                JSONArray waypointsContainer = (JSONArray) mainStructure.get("Waypoints");
                JSONObject waypoint = new JSONObject();
                JSONArray waypointDataContainer = new JSONArray();
                JSONObject waypointData = new JSONObject();

                waypointData.put("Location", locationSerializer(player));
                waypointDataContainer.add(waypointData);
                waypoint.put(waypointName, waypointDataContainer);
                waypointsContainer.add(waypoint);

                BufferedWriter externalBufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(waypointFile.toPath())));

                externalBufferedWriter.write(gson.toJson(mainStructure));
                externalBufferedWriter.close();
            } catch (IOException err) {
                if (err instanceof FileNotFoundException) {
                    System.out.println(ERR_MSG + "Is '" + waypointFile.getName() + "' missing?");
                    return;
                }

                System.out.println(ERR_MSG + "Failed to create/close I/O stream!");
            } catch (ParseException err) {
                System.out.println(ERR_MSG + "Is '" + waypointFile.getName() + "' corrupted?");
            }
        }

        /**
         * Remove waypoint operation.
         *
         * @param waypointName The given waypoint name to be removed.
         */
        @SuppressWarnings("unchecked")
        public static void removeWaypoint(String waypointName) {
            File waypointFile = getWaypointFile();

            final String ERR_MSG = "An error occurred while trying to remove waypoint: ";

            try {
                FileReader externalFileReader = new FileReader(waypointFile);
                JSONObject mainStructure = (JSONObject) jsonParser.parse(externalFileReader);
                JSONArray waypointsContainer = (JSONArray) mainStructure.get("Waypoints");
                JSONArray modifiedWaypointsContainer = (JSONArray) waypointsContainer.clone();

                waypointsContainer.forEach(element -> {
                    JSONObject waypoint = (JSONObject) element;

                    if (!waypoint.containsKey(waypointName)) return;

                    modifiedWaypointsContainer.remove(waypoint);
                });

                mainStructure.replace("Waypoints", modifiedWaypointsContainer);
                externalFileReader.close();

                BufferedWriter externalBufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(waypointFile.toPath())));

                externalBufferedWriter.write(gson.toJson(mainStructure));
                externalBufferedWriter.close();
            } catch (IOException err) {
                if (err instanceof FileNotFoundException) {
                    System.out.println(ERR_MSG + "Is '" + waypointFile.getName() + "' missing?");
                    return;
                }

                System.out.println(ERR_MSG + "Failed to create/close I/O streams!");
            } catch (ParseException err) {
                System.out.println(ERR_MSG + "Is '" + waypointFile.getName() + "' corrupted?");
            }
        }

        /**
         * Get waypoint data from external resource, 'iWaypoints-Global.json'
         *
         * @return A treemap of waypoint data with key of waypoint name and value of waypoint details.
         */
        @SuppressWarnings("unchecked")
        public static Map<String, JSONObject> getWaypointData() {
            File waypointFile = Handler.getWaypointFile();

            Map<String, JSONObject> waypointData = new TreeMap<>();

            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(waypointFile));

                JSONArray waypointsContainer = (JSONArray) ((JSONObject) jsonParser.parse(bufferedReader)).get("Waypoints");

                bufferedReader.close();

                waypointsContainer.forEach(element -> {
                    JSONObject waypoint = (JSONObject) element;

                    waypoint.keySet().forEach(waypointName -> {
                        JSONArray waypointDataContainer = (JSONArray) waypoint.get(waypointName);

                        waypointDataContainer.stream().findFirst().ifPresent(waypointDetails -> waypointData.put((String) waypointName, (JSONObject) waypointDetails));
                    });
                });
            } catch (IOException | ParseException err) {
                final String ERR_MSG = "An error occurred while trying to get waypoint data: ";

                if (err instanceof FileNotFoundException) {
                    System.out.println(ERR_MSG + "Is '" + waypointFile.getName() + "' missing?");
                }

                if (err instanceof IOException) {
                    System.out.println(ERR_MSG + "Failed to create/close I/O streams!");
                }

                if (err instanceof ParseException) {
                    System.out.println(ERR_MSG + "Is '" + waypointFile.getName() + "' corrupted?");
                }
            }

            return waypointData;
        }

        /**
         * Get waypoint data from player session where it has been created when player using the GUI.
         *
         * @param player The player who using the GUI.
         * @return A treemap of waypoint data with key of waypoint name and value of waypoint details.
         */
        protected static Map<String, JSONObject> getWaypointDataFromSession(Player player) {
            return iWaypointGUISessions.get(player.getUniqueId());
        }

        /**
         * Create a iWaypoints session per player that includes the latest waypoint data.
         *
         * @param player The player who using the iWaypoints.
         */
        private static void setGUISession(Player player) {
            iWaypointGUISessions.put(player.getUniqueId(), getWaypointData());
        }

        /**
         * Get iWaypoints session container.
         *
         * @return The iWaypoints session container.
         */
        public static Map<UUID, Map<String, JSONObject>> getGUISession() {
            return iWaypointGUISessions;
        }

        /**
         * Remove iWaypoints session if the player not using the iWaypoints.
         *
         * @param player The player.
         */
        private static void removeGUISession(Player player) {
            iWaypointGUISessions.remove(player.getUniqueId());
        }

        /**
         * Get waypoint file from the external resource.
         *
         * @return The waypoint file that used to store created waypoints.
         */
        private static File getWaypointFile() {
            return new File(FolderList.IWAYPOINTS.getFolder(), "iWaypoints-Global.json");
        }

        /**
         * Deserialize waypoint location data from the iWaypoint-Global.json
         *
         * @param waypointLocationData Waypoint location data.
         * @return A location object used by the Bukkit.
         */
        public static Location locationDeserializer(String waypointLocationData) {
            Location deserializedLocation = null;

            try {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(waypointLocationData));
                BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

                deserializedLocation = (Location) bukkitObjectInputStream.readObject();
            } catch (IOException | ClassNotFoundException err) {
                final String ERR_MSG = "An error occurred while trying to deserialize location: ";

                if (err instanceof ClassNotFoundException) {
                    throw new ClassCastException(ERR_MSG + "Internal class not found error!");
                }

                System.out.println(ERR_MSG + "Failed to create input stream!");
            }

            return deserializedLocation;
        }

        /**
         * Serialize waypoint location data from player location to encoded format.
         *
         * @param player The player where the waypoint location come from.
         * @return An encoded waypoint location data.
         */
        private static String locationSerializer(Player player) {
            Location location = player.getLocation();
            String serializedLocation = "";

            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);

                bukkitObjectOutputStream.writeObject(location);
                bukkitObjectOutputStream.flush();

                serializedLocation = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            } catch (IOException err) {
                System.out.println("An error occurred while trying to serialize location: Failed to create output stream!");
            }

            return serializedLocation;
        }
    }

    private static class EventController implements Listener {
        @EventHandler
        private void onInventoryClick(InventoryClickEvent e) {
            GUIHandlerMain.setEventHandler(e);
            GUIHandlerRemoval.setEventHandler(e);
        }

        @EventHandler
        private void onInventoryClose(InventoryCloseEvent e) {
            final String GUI_NAME = e.getView().getTitle();
            final Player player = (Player) e.getPlayer();

            if (GUI_NAME.equals(new GUIHandlerMain(IGUI.Handler.getIGUI(player)).getGUIName())) {
                if (GUIHandlerMain.isNavigating) {
                    GUIHandlerMain.isNavigating = false;
                    return;
                }

                /* Remove player from the guiSession */
                IGUI.Handler.iGUIContainer.remove(player);
                Handler.removeGUISession(player);
            }

            if (GUI_NAME.equals(new GUIHandlerRemoval(IGUI.Handler.getIGUI(player)).getGUIName())) {
                if (GUIHandlerRemoval.isNavigating) {
                    GUIHandlerRemoval.isNavigating = false;
                    return;
                }

                /* Remove player from the gui session as well as clearing out player selected pending remove waypoint */
                GUIHandlerRemoval.pendingRemovalContainer.remove(player.getUniqueId());
                IGUI.Handler.iGUIContainer.remove(player);
                Handler.removeGUISession(player);
            }
        }
    }
}
