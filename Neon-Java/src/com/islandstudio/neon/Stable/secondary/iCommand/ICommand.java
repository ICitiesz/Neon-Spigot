package com.islandstudio.neon.stable.secondary.iCommand;

import com.islandstudio.neon.stable.primary.iConstructor.IConstructor;
import com.islandstudio.neon.stable.primary.iExperimental.IExperimental;
import com.islandstudio.neon.stable.primary.iServerConfig.IServerConfig;
import com.islandstudio.neon.stable.secondary.iEffect.IEffect;
import com.islandstudio.neon.stable.secondary.iGameMode.IGameMode;
import com.islandstudio.neon.stable.secondary.iModerator.IModerator;
import com.islandstudio.neon.stable.secondary.iRank.IRank;
import com.islandstudio.neon.stable.secondary.iRegen.IRegen;
import com.islandstudio.neon.stable.secondary.iWaypoints.IWaypoints;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ICommand implements Listener, TabExecutor {
    protected static final String COLORED_PLUGIN_NAME = ChatColor.WHITE + "[" + ChatColor.AQUA + "Neon" + ChatColor.WHITE + "] ";

    private static final String COMMAND_PREFIX = "neon";

    public static class Handler {
        private static final JSONParser jsonParser = new JSONParser();

        private static final Map<CommandAccessibility, Map<String, JSONArray>> LOADED_COMMAND_LIST = new HashMap<>();

        /**
         * Initialization for the iCommand.
         */
        public static void init() {
            PluginCommand pluginCommand = IConstructor.getPlugin().getServer().getPluginCommand(COMMAND_PREFIX);

            if (pluginCommand == null) {
                throw new NullPointerException("An error while trying to initialize iCommand: PluginCommand not initialize yet!");
            }

            pluginCommand.setExecutor(new ICommand());

            LOADED_COMMAND_LIST.put(CommandAccessibility.PUBLIC, getPublicCommandList());
            LOADED_COMMAND_LIST.put(CommandAccessibility.PRIVATE, getPrivateCommandList());
        }

        private static Map<String, JSONArray> getLoadedPublicCommandList() {
            return LOADED_COMMAND_LIST.get(CommandAccessibility.PUBLIC);
        }

        private static Map<String, JSONArray> getLoadedPrivateCommandList() {
            return LOADED_COMMAND_LIST.get(CommandAccessibility.PRIVATE);
        }

        @SuppressWarnings("unchecked")
        private static Map<String, JSONArray> getPublicCommandList() {
            final String ERR_MSG = "An error occurred while trying to get public command list: ";

            StringBuilder stringBuilder = new StringBuilder();
            Map<String, JSONArray> publicCommandList = new HashMap<>();

            try {
                InputStream internalInputStream = ICommand.Handler.class.getClassLoader().getResourceAsStream("resources/Command_List-Public.json");
                if (internalInputStream == null) throw new NullPointerException(ERR_MSG + "Is internal resource 'Command_List-Public.json' missing or corrupted?");

                BufferedReader internalBufferedReader = new BufferedReader(new InputStreamReader(internalInputStream));
                internalBufferedReader.lines().forEach(stringBuilder::append);

                internalInputStream.close();
                internalBufferedReader.close();

                JSONObject commandList = (JSONObject) jsonParser.parse(stringBuilder.toString());

                publicCommandList.putAll(commandList);
            } catch (IOException err) {
                System.out.println(ERR_MSG + "Is internal resource 'Command_List-Public.json' missing?");
            } catch (ParseException err) {
                System.out.println(ERR_MSG + "Is internal resource 'Command_List-Public.json' corrupted?");
            }

            return publicCommandList;
        }

        @SuppressWarnings("unchecked")
        private static Map<String, JSONArray> getPrivateCommandList() {
            final String ERR_MSG = "An error occurred while trying to get private command list: ";

            StringBuilder stringBuilder = new StringBuilder();
            Map<String, JSONArray> privateCommandList = new HashMap<>();

            try {
                InputStream internalInputStream = ICommand.Handler.class.getClassLoader().getResourceAsStream("resources/Command_List-Private.json");
                if (internalInputStream == null) throw new NullPointerException(ERR_MSG + "Is internal resource 'Command_List-Private.json' missing or corrupted?");

                BufferedReader internalBufferedReader = new BufferedReader(new InputStreamReader(internalInputStream));
                internalBufferedReader.lines().forEach(stringBuilder::append);

                internalInputStream.close();
                internalBufferedReader.close();

                JSONObject commandList = (JSONObject) jsonParser.parse(stringBuilder.toString());

                privateCommandList.putAll(commandList);
            } catch (IOException err) {
                System.out.println(ERR_MSG + "Is internal resource 'Command_List-Private.json' missing?");
            } catch (ParseException err) {
                System.out.println(ERR_MSG + "Is internal resource 'Command_List-Private.json' corrupted?");
            }

            return privateCommandList;
        }

        /**
         * Shows the command list to the player.
         *
         * @param player The player to show the command list to.
         */
        private static void showCommandList(Player player) {
            final String ERR_MSG = "An error occurred while trying to generate command list: ";

            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookMeta = (BookMeta) book.getItemMeta();

            if (bookMeta == null) throw new NullPointerException(ERR_MSG + "Book meta is null!");
            bookMeta.setAuthor("ICities");
            bookMeta.setTitle("Neon Command List");

            Map<String, JSONArray> commandList = new HashMap<>(getLoadedPublicCommandList());

            if (player.isOp()) commandList.putAll(getLoadedPrivateCommandList());

            AtomicInteger page = new AtomicInteger(1);
            AtomicInteger totalLines = new AtomicInteger();
            AtomicInteger commandIndex = new AtomicInteger(1);

            AtomicReference<ArrayList<BaseComponent>> pageComponents = new AtomicReference<>(new ArrayList<>());

            /* Adding main title */
            if (page.get() == 1) {
                BaseComponent titleComponent = new TextComponent(" " + ChatColor.BLUE + ChatColor.BOLD
                        + ChatColor.UNDERLINE + "Neon Command List\n");

                titleComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(ChatColor.GRAY + "" + ChatColor.BOLD + "Command Prefix: \n"
                                + ChatColor.GREEN + "/neon <command>").create()));

                totalLines.getAndAdd(1);
                pageComponents.get().add(titleComponent);
            }

            /* Adding command and its usage */
            commandList.forEach((command, sytaxs) -> {
                final int LINES_PER_COMMAND = 1 + (sytaxs.size() + 1);
                int syntaxIndex = 1;

                totalLines.getAndAdd(LINES_PER_COMMAND);

                if (totalLines.get() > 14) {
                    page.getAndIncrement();
                    totalLines.getAndSet(0);

                    bookMeta.spigot().addPage(pageComponents.get().toArray(new BaseComponent[0]));
                    pageComponents.set(new ArrayList<>());
                }

                BaseComponent commandTitleComponent = new TextComponent( ChatColor.DARK_GRAY
                        + commandIndex.toString() + ". " + ChatColor.GOLD + "" + ChatColor.BOLD + command.substring(6) + "\n");

                commandTitleComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(ChatColor.GOLD + command).create()));

                pageComponents.get().add(commandTitleComponent);

                for (Object commandSyntax: sytaxs) {
                    JSONObject jsonObject = (JSONObject) commandSyntax;

                    final String syntax = ChatColor.GREEN + (String) jsonObject.get("syntax");
                    final String description = ChatColor.YELLOW + (String) jsonObject.get("description");

                    BaseComponent usageComponent = new TextComponent(ChatColor.DARK_GRAY + "  [" +
                            ChatColor.DARK_GREEN + "Syntax " + syntaxIndex + ChatColor.DARK_GRAY + "]\n");

                    if (syntaxIndex == sytaxs.size()) {
                        usageComponent = new TextComponent(ChatColor.DARK_GRAY + "  [" +
                                ChatColor.DARK_GREEN + "Syntax " + syntaxIndex + ChatColor.DARK_GRAY +"]\n\n");
                    }

                    usageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(ChatColor.GRAY + "" + ChatColor.BOLD + "Syntax: \n" + syntax + "\n\n"
                                    + ChatColor.GRAY + "" + ChatColor.BOLD +"Description: \n" + description).create()));

                    pageComponents.get().add(usageComponent);

                    syntaxIndex++;
                }

                if (commandIndex.get() == commandList.size()) {
                    bookMeta.spigot().addPage(pageComponents.get().toArray(new BaseComponent[0]));
                }

                commandIndex.getAndIncrement();
            });

            book.setItemMeta(bookMeta);
            player.openBook(book);
        }
    }
    /**
     * Command execution management.
     *
     * @param sender The player who perform the command.
     * @param cmd The command.
     * @param label Command label.
     * @param args A list of command arguments.
     * @return True for hide command call back after command executed successfully, else show
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            IConstructor.getPlugin().getServer().getConsoleSender().sendMessage(COLORED_PLUGIN_NAME + ChatColor.RED + "The command doesn't support console execution!");
            return true;
        }

        Player commander = (Player) sender;

        if (!cmd.getName().equalsIgnoreCase(COMMAND_PREFIX)) return true;

        if (!(args.length > 0)) {
            Handler.showCommandList(commander);
            return true;
        }

        Arrays.stream(Commands.values()).forEach(cmdAlias -> {
            if (!args[0].equalsIgnoreCase(cmdAlias.getCommandAlias())) return;

            switch (cmdAlias) {
                case RANK: {
                    new IRank.Handler().setCommandHandler(commander, args);
                    break;
                }

                case WAYPOINTS: {
                    new IWaypoints.Handler().setCommandHandler(commander, args);
                    break;
                }

                case EXPERIMENTAL: {
                    new IExperimental.Handler().setCommandHandler(commander, args);
                    break;
                }

                case GM: {
                    new IGameMode.Handler().setCommandHandler(commander, args);
                    break;
                }

                case REGEN: {
                    new IRegen.Handler().setCommandHandler(commander, args);
                    break;
                }

                case SERVERCONFIG: {
                    new IServerConfig.Handler().setCommandHandler(commander, args);
                    break;
                }

                case DEBUG: {
                    if (!commander.isOp()) {
                        commander.sendMessage(CommandSyntax.INVALID_PERMISSION.getSyntaxMessage());
                        break;
                    }

                    if (args.length != 2) {
                        commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                        break;
                    }

                    if (!args[1].equalsIgnoreCase("showIExperimentalDemo")){
                        commander.sendMessage(CommandSyntax.INVALID_ARGUMENT.getSyntaxMessage());
                        break;
                    }

                    IExperimental.Handler.setShowIExperimentDemo(true);
                    commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "Debug function 'showIExperimentalDemo' has been executed!"));
                    break;
                }

                case MOD: {
                    new IModerator.Handler().setCommandHandler(commander, args);
                    break;
                }

                case EFFECT: {
                    new IEffect.Handler().setCommandHandler(commander, args);
                    break;
                }
            }
        });

        if (Arrays.stream(Commands.values()).noneMatch(cmdAlias -> cmdAlias.getCommandAlias().equalsIgnoreCase(args[0]))) {
            commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "Sorry, there are no such command as " + ChatColor.WHITE
                    + "'" + ChatColor.GRAY + args[0] + ChatColor.WHITE + "'" + ChatColor.YELLOW + "!"));
            commander.sendMessage(CommandSyntax.Handler.createSyntaxMessage(ChatColor.YELLOW + "Please type '/neon' to show available command!"));
        }

        return true;
    }

    /**
     * Show the autocompletion of the command for tab completion.
     *
     * @param sender The player who perform the command.
     * @param cmd The command.
     * @param label The command label.
     * @param args The command arguments.
     * @return A list of valid command arguments.
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        final ArrayList<String> BLANK_LIST = new ArrayList<>();

        if (!(sender instanceof Player)) {
            IConstructor.getPlugin().getServer().getConsoleSender().sendMessage(COLORED_PLUGIN_NAME + ChatColor.RED + "The command doesn't support console execution!");
            return BLANK_LIST;
        }

        if (!cmd.getName().equalsIgnoreCase(COMMAND_PREFIX)) return BLANK_LIST;

        Player commander = (Player) sender;

        if (args.length == 1) return Arrays.stream(Commands.values()).sorted().map(Commands::getCommandAlias)
                .filter(command -> command.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());

        if (!(args.length > 1)) return BLANK_LIST;

        for (Commands cmds : Commands.values()) {
            if (!args[0].equalsIgnoreCase(cmds.getCommandAlias())) continue;

            switch (cmds) {
                case WAYPOINTS: {
                    return new IWaypoints.Handler().tabCompletion(commander, args);
                }

                case RANK: {
                    return new IRank.Handler().tabCompletion(commander, args);
                }

                case GM: {
                    return new IGameMode.Handler().tabCompletion(commander, args);
                }

                case REGEN: {
                    return new IRegen.Handler().tabCompletion(commander, args);
                }

                case MOD: {
                    return new IModerator.Handler().tabCompletion(commander, args);
                }

                case SERVERCONFIG: {
                    return new IServerConfig.Handler().tabCompletion(commander, args);
                }

                case DEBUG: {
                    if (args.length != 2) {
                        return new ArrayList<>();
                    }

                    return Arrays.stream(new String[]{"showIExperimentalDemo"}).filter(value -> value.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                }
            }
        }

        return BLANK_LIST;
    }
}
