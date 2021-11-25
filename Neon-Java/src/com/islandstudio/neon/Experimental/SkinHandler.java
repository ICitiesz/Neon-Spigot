package com.islandstudio.neon.Experimental;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.islandstudio.neon.MainCore;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SkinHandler {
    private static final HashMap<String, PlayerInfo> INFO = new HashMap<>();
    private static final HashMap<Integer, ItemStack> item = new HashMap<Integer, ItemStack>();

    public static void test() {
    }

    public static void setSkin(String playerName) {
        Player player = Bukkit.getServer().getPlayer(playerName);
        String[] skin = getSkin(playerName);
        CraftPlayer craftPlayer = (CraftPlayer) player ;
        //craftPlayer.getProfile().getProperties().removeAll("textures");
        assert craftPlayer != null;
        craftPlayer.getProfile().getProperties().put("textures", new Property(Objects.requireNonNull(skin)[0], Objects.requireNonNull(skin)[1]));

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null) {
                item.put(player.getInventory().first(itemStack), itemStack);
            }
        }
        INFO.put(player.getName(),new PlayerInfo(player.getHealth(), player.getFoodLevel(), player.getLocation().clone().add(0,1, 0), item));

        //sendPackets(new PacketPlayOutEntityDestroy(craftPlayer.getEntityId()));
        //sendPackets(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, craftPlayer.getHandle()));

        player.getInventory().clear();
        player.setHealth(0D);

        new BukkitRunnable() {

            @Override
            public void run() {
                player.spigot().respawn();
                PlayerInfo info = INFO.get(player.getName());

                player.setHealth(info.getHealth());
                player.setFoodLevel(info.getFood());
                player.teleport(info.getLocation());

                for (ItemStack itemStack : info.getItems().values()) {
                    player.getInventory().setItem(getKeyByValue(info.getItems(), itemStack), itemStack);
                }

                INFO.remove(player.getName());

                sendPackets(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftPlayer.getHandle()));
                sendPacketsNotFor(player.getName(), new PacketPlayOutNamedEntitySpawn(craftPlayer.getHandle()));
            }
        }.runTaskLater(MainCore.getPlugin(MainCore.class), 2);
    }

    private static void sendPackets(Packet... packets) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            for (Packet packet : packets) {
                EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
                entityPlayer.playerConnection.sendPacket(packet);
            }
        }
    }

    private static void sendPacketsNotFor(String notFor, Packet... packets) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!player.getName().equals(notFor)) {
                for (Packet packet : packets) {
                    EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
                    entityPlayer.playerConnection.sendPacket(packet);
                }
            }
        }
    }

    private static <T,E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            return entry.getKey();
        }
        return null;
    }


    //https://api.mojang.com/users/profiles/minecraft/
    //https://sessionserver.mojang.com/session/minecraft/profile/ +  + ?unsigned=false

    public static String[] getSkin(String playerName) {
        try {
            URL profileURL = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            InputStreamReader profileReader = new InputStreamReader(profileURL.openStream());
            String uuID = new JsonParser().parse(profileReader).getAsJsonObject().get("id").getAsString();

            URL sessionURL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuID + "?unsigned=false");
            InputStreamReader sessionReader = new InputStreamReader(sessionURL.openStream());
            JsonObject property = new JsonParser().parse(sessionReader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

            String texture = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();

            System.out.println(texture);
            System.out.println(signature);

            return new String[]{texture, signature};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
