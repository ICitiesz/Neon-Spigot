package com.islandstudio.neon.stable.secondary.iPVP;

import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.primary.iServerConfig.IServerConfig;

public class IPVP {
    public static class Handler {
        /**
         * Initialization for iPVP.
         */
        public static void init() {
            setPVP((Boolean) IServerConfig.getExternalServerConfigValue("PVP"));
        }
    }


    public static void setPVP(boolean value) {
        IConstructor.getPlugin().getServer().getWorlds().forEach(world -> world.setPVP(value));
    }
}
