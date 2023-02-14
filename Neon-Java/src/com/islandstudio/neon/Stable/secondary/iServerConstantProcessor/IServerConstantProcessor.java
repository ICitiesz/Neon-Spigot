package com.islandstudio.neon.stable.secondary.iServerConstantProcessor;

import com.islandstudio.neon.stable.utils.IReflector;
import com.islandstudio.neon.stable.secondary.iCommand.CommandSyntax;
import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IServerConstantProcessor {
    public static class Handler {
        /**
         * Initialization for iServerConstantProcessor.
         *
         */
        public static void init() {
            IConstructor.enableEvent(new EventController());
        }
    }

    private static class EventController implements Listener {
        @EventHandler
        private void onPlayerJoinServer(PlayerJoinEvent e) {
            broadcastPlayerJoin(e);
        }

        @EventHandler
        private void onPlayerQuitServer(PlayerQuitEvent e) {
            broadcastPlayerQuit(e);
        }

        @EventHandler
        private void onServerLoad(ServerLoadEvent e) {
            if (!e.getType().equals(ServerLoadEvent.LoadType.RELOAD)) return;

            IConstructor.getPlugin().getServer().getOnlinePlayers().forEach(IServerConstantProcessor::updateRecipe);
        }
    }

    /**
     * Broadcast player join if the player joins the server.
     *
     * @param e PlayerJoinEvent
     */
    private static void broadcastPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Server server = player.getServer();

        e.setJoinMessage("");

        server.broadcastMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GOLD + "Welcome, "
                + ChatColor.GREEN + player.getName() + ChatColor.GOLD + "!"));

        server.broadcastMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GREEN + ""
                + server.getOnlinePlayers().size() + ChatColor.GOLD + " of "
                + ChatColor.RED + server.getMaxPlayers() + ChatColor.GOLD + " player(s) Online!"));
    }

    /**
     * Broadcast player quit if the player quit the server.
     *
     * @param e PlayerQuitEvent
     */
    private static void broadcastPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Server server = player.getServer();

        int onlinePlayers = server.getOnlinePlayers().size();

        e.setQuitMessage("");

        server.broadcastMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.GREEN + player.getName()
                + ChatColor.GOLD + " left, " + ChatColor.GREEN + (onlinePlayers - 1) + ChatColor.GOLD + " other(s) here!"));
    }

    /**
     * Update player recipe.
     *
     * @param player The player.
     */
    @SuppressWarnings("unchecked")
    private static void updateRecipe(Player player) {
        try {
            Object handler = player.getClass().getMethod("getHandle").invoke(player);
            Object minecraftServer = handler.getClass().getMethod("getWorldServer").invoke(handler);
            Object craftingManager = minecraftServer.getClass().getMethod("getCraftingManager").invoke(minecraftServer);

            Map<Object, Map<Object, Object>> recipes = (Map<Object, Map<Object, Object>>) craftingManager.getClass().getField("recipes").get(craftingManager);
            List<Object> stoneCuttingRecipes = recipes.values().parallelStream().flatMap(map -> map.values().stream()).collect(Collectors.toList());

            Constructor<?> constructor = IReflector.getNameSpaceClass("PacketPlayOutRecipeUpdate").getConstructor(Collection.class);
            Object packet = constructor.newInstance(stoneCuttingRecipes);

            Object playerConnection = handler.getClass().getField("playerConnection").get(handler);
            playerConnection.getClass().getMethod("sendPacket", IReflector.getNameSpaceClass("Packet")).invoke(playerConnection, packet);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException err) {
            if (err instanceof NoSuchMethodException) {
                System.out.println("Error while trying to access such method: No such method!");
            }

            if (err instanceof IllegalAccessException) {
                System.out.println("Error while trying to access such method/field: Invalid access!");
            }

            if (err instanceof InvocationTargetException) {
                System.out.println("Error while trying to invoke such method!");
            }
        } catch (NoSuchFieldException | ClassNotFoundException | InstantiationException err) {
            if (err instanceof NoSuchFieldException) {
                System.out.println("Error while trying to access such field: No such field found!");
            }

            if (err instanceof ClassNotFoundException) {
                System.out.println("Error while trying to access such class: No such class found!");
            }

            if (err instanceof InstantiationException) {
                System.out.println("Error while trying to create new instance of the class!");
            }
        }
    }

    /**
     * Get the server mode, either Online Mode or Offline Mode.
     *
     * @return The server mode.
     */
    public static String getServerMode() {
        return IConstructor.getPlugin().getServer().getOnlineMode() ? "Online_Mode" : "Offline_Mode";
    }
}
