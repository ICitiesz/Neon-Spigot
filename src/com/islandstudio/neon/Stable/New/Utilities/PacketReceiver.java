package com.islandstudio.neon.Stable.New.Utilities;

import io.netty.channel.*;
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
                super.write(channelHandlerContext, packet, channelPromise);
            }
        };

        try {
            ChannelPipeline channelPipeline = (ChannelPipeline) Objects.requireNonNull(getChannel(player)).getClass().getMethod("pipeline").invoke(getChannel(player));
            channelPipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void playerRemoval(Player player) {
        try {
            Channel channel = (Channel) getChannel(player);
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

}
