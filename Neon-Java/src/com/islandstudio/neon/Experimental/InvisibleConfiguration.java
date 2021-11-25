package com.islandstudio.neon.Experimental;

import com.islandstudio.neon.MainCore;
import org.bukkit.plugin.Plugin;

public class InvisibleConfiguration {
    private final Plugin plug = MainCore.getPlugin(MainCore.class);
    public static boolean isHidden;

    public void muteAttackSoundEffect() {
//        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plug, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
//            @Override
//            public void onPacketSending(PacketEvent e) {
//                if (e.getPacketType().equals(PacketType.Play.Server.NAMED_SOUND_EFFECT)) {
//                    if (e.getPacket().getSoundEffects().getValues().contains(Sound.ENTITY_PLAYER_ATTACK_NODAMAGE)) {
//                        if (!e.getPlayer().isOp()) {
//                            e.setCancelled(isHidden);
//
//                        }
//                    }
//                }
//            }
//        });
    }

    public void interactDetection() {
//        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plug, PacketType.Play.Client.USE_ENTITY) {
//            @Override
//            public void onPacketReceiving(PacketEvent e) {
//                if (e.getPacket().getType().equals(PacketType.Play.Client.USE_ENTITY)) {
//                    if (e.getPlayer().isOp()) {
//                        if (isHidden) {
//                            muteAttackSoundEffect();
//                        }
//                    }
//                }
//            }
//        });
    }

}
