package com.islandstudio.neon.Stable.New.Utilities;

import io.netty.channel.*;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class PacketReceiver {
    public static void playerInjection(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                if (packet instanceof PacketPlayOutRecipeUpdate) {
                    System.out.println("Recipe update packet received");
                }


                super.write(channelHandlerContext, packet, channelPromise);
            }
        };

        try {
            ChannelPipeline channelPipeline = (ChannelPipeline) Objects.requireNonNull(getChannel(player)).getClass().getMethod("pipeline").invoke(getChannel(player));
            channelPipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // To be continued:
    // - Try to get all the recipes, put them into RecipeUpdatePacket and send it to the client.

    public static void playerRemoval(Player player) {
        try {
            Channel channel = (Channel) getChannel(player);
            //Channel channel = (Channel) getChannel_2(player);
            assert channel != null;
            channel.eventLoop().submit(() -> {
                channel.pipeline().remove(player.getName());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getChannel(Player player) {
        try {
            Object getHandle = player.getClass().getMethod("getHandle").invoke(player);
            Object getPlayerConnection = getHandle.getClass().getField("playerConnection").get(getHandle);
            Object getNetworkManager = getPlayerConnection.getClass().getField("networkManager").get(getPlayerConnection);
            return getNetworkManager.getClass().getField("channel").get(getNetworkManager);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getChannel_2(Player player) throws Exception {
        Object handle = player.getClass().getMethod("getHandle").invoke(player);
        Object playerConnection = handle.getClass().getMethod("PlayerConnection").invoke(handle);
        System.out.println(playerConnection.toString());
        return null;
        //return ((CraftPlayer) player).getHandle().b.a.k;
    }

}
