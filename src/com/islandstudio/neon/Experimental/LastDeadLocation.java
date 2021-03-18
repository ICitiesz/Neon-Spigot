package com.islandstudio.neon.Experimental;

import com.islandstudio.neon.Stable.New.Utilities.NMS_Class_Version;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class LastDeadLocation {
    private static Player deadPlayer;

    private static float localYaw;
    private static float localPitch;
    private static int localPosX;
    private static int localPosY;
    private static int localPosZ;
    private static World localWorld;

    public static void sendLocation(Player player) throws Exception {
        Location location = player.getLocation();

        float yaw = location.getYaw();
        float pitch = location.getPitch();
        int posX = location.getBlockX();
        int posY = location.getBlockY();
        int posZ = location.getBlockZ();
        World.Environment dimension = Objects.requireNonNull(location.getWorld()).getEnvironment();

        localYaw = yaw;
        localPitch = pitch;
        localPosX = posX;
        localPosY = posY;
        localPosZ = posZ;
        localWorld = location.getWorld();

        sendChat(player, ChatColor.YELLOW + "Your last dead location is " + ChatColor.GREEN + posX + ChatColor.WHITE
                + ", " + ChatColor.GREEN + posY + ChatColor.WHITE + ", " + ChatColor.GREEN + posZ + ChatColor.WHITE + " [" + getDimension(dimension) + ChatColor.WHITE + "] ");
    }

    public static HashMap<String, Object> playerLocation() {
        HashMap<String, Object> locations = new HashMap<>();

        locations.put("yaw", localYaw);
        locations.put("pitch", localPitch);
        locations.put("posX", localPosX);
        locations.put("posY", localPosY);
        locations.put("posZ", localPosZ);
        locations.put("dimension", localWorld);

        return locations;
    }


    public static void setDeadPlayer(Player player) {
        deadPlayer = player;
    }

    public static Player getDeadPlayer() {
        return deadPlayer;
    }

    private static String getDimension(World.Environment dimension) {
        switch (dimension) {
            case NORMAL: {
                return ChatColor.GREEN + "OverWorld";
            }

            case NETHER: {
                return ChatColor.DARK_RED + "Nether";
            }

            case THE_END: {
                return ChatColor.DARK_PURPLE + "The End";
            }
        }

        return null;
    }

    private static void sendChat(Player player, String locationText) throws Exception {
        for (Class<?> declaredClass : NMS_Class_Version.getNMSClass("IChatBaseComponent").getDeclaredClasses()) {
            if (declaredClass.getSimpleName().equalsIgnoreCase("ChatSerializer")) {
                Object chatMsg = declaredClass.getMethod("a", String.class).invoke(null, "[{\"text\":\"" + locationText + "\"},{\"text\":\"" + ChatColor.GOLD + "Teleport?\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/ldl\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + ChatColor.DARK_PURPLE + "Click here to teleport!\"}}}]");
                Object chatType = NMS_Class_Version.getNMSClass("ChatMessageType").getField("CHAT").get(null);

                Constructor<?> constructor = NMS_Class_Version.getNMSClass("PacketPlayOutChat").getConstructor(NMS_Class_Version.getNMSClass("IChatBaseComponent"), NMS_Class_Version.getNMSClass("ChatMessageType"), UUID.class);
                Object packet = constructor.newInstance(chatMsg, chatType, player.getUniqueId());

                sendPacket(player, packet);
            }
        }
    }

    private static void sendPacket(Player player, Object packet) throws Exception {
        Object handler = player.getClass().getMethod("getHandle").invoke(player);
        Object playerConnection = handler.getClass().getField("playerConnection").get(handler);
        playerConnection.getClass().getMethod("sendPacket", NMS_Class_Version.getNMSClass("Packet")).invoke(playerConnection, packet);
    }
}