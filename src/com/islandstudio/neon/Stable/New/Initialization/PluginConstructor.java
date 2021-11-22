package com.islandstudio.neon.Stable.New.Initialization;

import com.islandstudio.neon.Experimental.iCommand.CommandHandler;
import com.islandstudio.neon.Experimental.PVPHandler;
import com.islandstudio.neon.Experimental.iCutter.ICutter;
import com.islandstudio.neon.Experimental.iExperimental.IExperimental;
import com.islandstudio.neon.Experimental.iHarvest.IHarvest;
import com.islandstudio.neon.Experimental.iSmelter.ISmelter;
import com.islandstudio.neon.MainCore;
import com.islandstudio.neon.Stable.New.Event.EventCore;
import com.islandstudio.neon.Stable.New.Utilities.NamespaceVersion;
import com.islandstudio.neon.Stable.New.features.GUI.Initialization.GlowingItemEffect;
import com.islandstudio.neon.Stable.New.features.iWaypoints.IWaypoints;
import com.islandstudio.neon.Stable.New.Initialization.FolderManager.FolderHandler;
import com.islandstudio.neon.Stable.New.features.iRank.IRank;
import com.islandstudio.neon.Stable.New.Utilities.ServerCFGHandler;
import com.islandstudio.neon.Stable.New.Utilities.INamespaceKeys;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class PluginConstructor {
    private static final Plugin plugin = MainCore.getPlugin(MainCore.class);
    private static final String RAW_VERSION = plugin.getServer().getBukkitVersion().split("-")[0];
    private static final String VERSION = RAW_VERSION.split("\\.")[0] + "." + RAW_VERSION.split("\\.")[1];

    public static void start() throws Exception {
        switch (VERSION) {
            case "1.14":

            case "1.15":

            case "1.16": {
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.YELLOW + "Detected Minecraft " + ChatColor.GREEN + RAW_VERSION + ChatColor.YELLOW + "!");
                Thread.sleep(1000);
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.YELLOW + "Initializing features for Minecraft " + ChatColor.GREEN + VERSION + ChatColor.YELLOW + "......");

                /* Each initialization will be done according to priority. */
                /* Essential Component */
                eventRegister();
                glowingItemRegister();
                FolderHandler.init();
                ServerCFGHandler.init();
                IExperimental.init();

                /* Feature Component */
                ICutter.init();
                ISmelter.init();
                IHarvest.init();

                IWaypoints.init();
                CommandHandler.init();
                PVPHandler.init();
                IRank.init();
                //ChatLogger.initialize();
                loadConfig();

                Thread.sleep(2500);
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.GREEN + "Initialization complete!");
                sendIntro();
                break;
            }

            default: {
                final String SUPPORTED_VERSION = "1.14 ~ 1.16";
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.RED + "Incompatible Minecraft version! Please check for the latest version!");
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[Neon] " + ChatColor.YELLOW + "Supported version: " + ChatColor.GREEN + SUPPORTED_VERSION);
                break;
            }
        }
    }

    public static void sendIntro() {
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|+++++++++++++++++=================+++++++++++++++++|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------=================-----------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|---------------== Neon v1.9-pre_3 ==---------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------===" + ChatColor.GREEN + " <Started> " + ChatColor.GOLD + "===-----------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------=================-----------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|+++++++++++++++++=================+++++++++++++++++|");
    }

    public static void sendOutro() {
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|+++++++++++++++++=================+++++++++++++++++|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------=================-----------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|---------------== Neon v1.9-pre_3 ==---------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------===" + ChatColor.RED + " <Stopped> " + ChatColor.GOLD + "===-----------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|-----------------=================-----------------|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "|+++++++++++++++++=================+++++++++++++++++|");
    }

    public static String getVersion() {
        return VERSION;
    }

    private static void loadConfig() {
        plugin.getConfig().options().copyDefaults(true);
        plugin.getConfig().options().copyHeader(true);
        plugin.saveConfig();
    }

    private static void eventRegister() {
        plugin.getServer().getPluginManager().registerEvents(new EventCore(), plugin);
    }

    private static void glowingItemRegister() throws IllegalAccessException, NoSuchFieldException {
        if (Enchantment.getByKey(INamespaceKeys.NEON_BUTTON_GLOW.getKey()) != null) return;

        GlowingItemEffect glowingItemEffect = new GlowingItemEffect(INamespaceKeys.NEON_BUTTON_GLOW.getKey());

        Field field = Enchantment.class.getDeclaredField("acceptingNew");
        field.setAccessible(true);
        field.set(null, true);

        Enchantment.registerEnchantment(glowingItemEffect);
    }

    @SuppressWarnings("unchecked")
    public static void updateRecipe(Player player) throws Exception {
        Object handler = player.getClass().getMethod("getHandle").invoke(player);

        Object minecraftServer = handler.getClass().getMethod("getWorldServer").invoke(handler);
        Object craftingManager = minecraftServer.getClass().getMethod("getCraftingManager").invoke(minecraftServer);

        Map<Object, Map<Object, Object>> recipes = (Map<Object, Map<Object, Object>>) craftingManager.getClass().getField("recipes").get(craftingManager);

        List<Object> stoneCuttingRecipes = recipes.values().parallelStream().flatMap(map -> map.values().stream()).collect(Collectors.toList());

        Constructor<?> constructor = NamespaceVersion.getNameSpaceClass("PacketPlayOutRecipeUpdate").getConstructor(Collection.class);
        Object packet = constructor.newInstance(stoneCuttingRecipes);

        Object playerConnection = handler.getClass().getField("playerConnection").get(handler);
        playerConnection.getClass().getMethod("sendPacket", NamespaceVersion.getNameSpaceClass("Packet")).invoke(playerConnection, packet);
    }

}
