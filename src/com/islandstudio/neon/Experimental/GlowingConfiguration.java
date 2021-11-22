package com.islandstudio.neon.Experimental;

import com.islandstudio.neon.MainCore;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GlowingConfiguration {
    private final Plugin plug = MainCore.getPlugin(MainCore.class);


    public void setGlowing() {
//        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plug, PacketType.Play.Server.ENTITY_METADATA) {
//            @Override
//            public void onPacketReceiving(PacketEvent e) {
//            }
//
//            @Override
//            public void onPacketSending(PacketEvent e) {
//                if (e.getPacket().getType().equals(PacketType.Play.Server.ENTITY_METADATA)) {
//
//                }
//            }
//        });
    }

    public void addGlow(Player glowPlayer, Player packetSender) {
//        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
//        PacketContainer packetContainer = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
//
//        packetContainer.getIntegers().write(0, glowPlayer.getEntityId());
//        WrappedDataWatcher watcher = new WrappedDataWatcher();
//        watcher.setEntity(glowPlayer);
//        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
//        if (Commands.isGlow) {
//            watcher.setObject(0, serializer, (byte) (0x40));
//        } else {
//            //watcher.setObject(0, serializer, );
//        }
//
//        packetContainer.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
//        try {
//            protocolManager.sendServerPacket(packetSender, packetContainer);
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//
//        protocolManager.addPacketListener(new PacketAdapter(plug, PacketType.Play.Server.ENTITY_METADATA) {
//            @Override
//            public void onPacketSending(PacketEvent e) {
//                if (e.getPacketType().equals(PacketType.Play.Server.ENTITY_METADATA)) {
//
//                    if (!e.getPlayer().isOp()) {
//                        //System.out.println(e.getPlayer().getName());
//                        if (e.getPacket().getWatchableCollectionModifier().getValues().get(0).get(0).getIndex() == 0) {
//                            System.out.println(e.getPacket().getWatchableCollectionModifier().getValues().get(0).get(0).getValue());
//                        }
//
//                    }
//
//                }
//            }
//        });
    }
}
