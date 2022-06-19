package me.thejokerdev.frozzcore.managers;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.commands.admin.ReloadCMD;
import me.thejokerdev.frozzcore.commands.admin.SetLobbyCMD;
import me.thejokerdev.frozzcore.commands.other.FlyCMD;
import me.thejokerdev.frozzcore.commands.user.LangCMD;
import me.thejokerdev.frozzcore.commands.user.OpenCMD;
import me.thejokerdev.frozzcore.enums.SenderType;
import me.thejokerdev.frozzcore.type.CMD;
import me.thejokerdev.frozzcore.type.CustomCMD;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.util.StringUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class CMDManager implements CommandExecutor, TabCompleter {
    private final SpigotMain plugin;
    private List<CMD> commands = new ArrayList<>();
    private final HashMap<String, CustomCMD> customCommands = new HashMap<>();

    public CMDManager(SpigotMain plugin) {
        this.plugin = plugin;

        plugin.getCommand("frozzcore").setExecutor(this);
        plugin.getCommand("frozzcore").setTabCompleter(this);
    }

    public void initCMDs(){
        commands = new ArrayList<>();

        commands.add(new SetLobbyCMD(plugin));
        commands.add(new ReloadCMD(plugin));
        commands.add(new LangCMD(plugin));
        commands.add(new OpenCMD(plugin));

        customCommands.put("fly", new FlyCMD(plugin));

        customCommands.values().forEach(CustomCMD::register);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            sendHelp(sender);
            return true;
        }
        String var1 = args[0];
        for (CMD cmd : commands){
            if (cmd.getName().equalsIgnoreCase(var1)){
                if (!sender.hasPermission(cmd.getPermission())){
                    plugin.getClassManager().getUtils().sendMessage(sender, "noPermission");
                    return false;
                }
                if (cmd.getSenderType() != SenderType.BOTH){
                    if (sender instanceof Player && cmd.getSenderType() == SenderType.CONSOLE){
                        plugin.getClassManager().getUtils().sendMessage(sender, "onlyConsole");
                        return false;
                    } else if (!(sender instanceof Player) && cmd.getSenderType() == SenderType.PLAYER){
                        plugin.getClassManager().getUtils().sendMessage(sender, "onlyPlayers");
                        return false;
                    }
                }
                Vector<String> vector = new Vector<>(Arrays.asList(args));
                vector.remove(0);
                args = vector.toArray(new String[0]);
                return cmd.onCMD(sender, label, args);
            }
        }
        plugin.getClassManager().getUtils().sendMessage(sender, "commandNotExist");
        return false;
    }

    public void sendHelp(CommandSender sender){
        String help = plugin.getClassManager().getUtils().getMSG("commands.help");
        List<String> list = new ArrayList<>();
        for (CMD command : commands){
            list.add(plugin.getClassManager().getUtils().getMSG(command.getHelp()));
        }
        help = help.replace("{commands}", String.join("\n", list));
        plugin.getClassManager().getUtils().sendMessage(sender, help);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1){
            List<String> cmds = commands.stream().map(CMD::getName).collect(Collectors.toList());
            StringUtil.copyPartialMatches(args[0], cmds, list);
        }
        String var1 = args[0];
        for (CMD cmd : commands){
            if (cmd.getName().equalsIgnoreCase(var1)){
                if (!sender.hasPermission(cmd.getPermission())){
                    return list;
                }
                if (cmd.getSenderType() != SenderType.BOTH){
                    if (sender instanceof Player && cmd.getSenderType() == SenderType.CONSOLE){
                        return list;
                    } else if (!(sender instanceof Player) && cmd.getSenderType() == SenderType.PLAYER){
                        return list;
                    }
                }
                Vector<String> vector = new Vector<>(Arrays.asList(args));
                vector.remove(0);
                args = vector.toArray(new String[0]);
                return cmd.onTab(sender, alias, args);
            }
        }
        return list;
    }

    public boolean registerCommand(CustomCMD cmd) {
        if (plugin.getCommand(cmd.getName()) == null) {
            PluginCommand command = getCommand(cmd.getName(), plugin);
            if (cmd.getPermission() != null || cmd.getPermission().equals("none")) {
                command.setPermission(cmd.getPermission());
            }
            if (cmd.getPermissionError()!=null) {
                command.setDescription(cmd.getDescription());
                command.setAliases(cmd.getAliases());
            }
            try {
                getCommandMap().register(plugin.getDescription().getName(), command);
            } catch (Exception e) {
                return false;
            }
            plugin.getCommand(cmd.getName()).setExecutor(cmd);
            if (cmd.isTabComplete()) {
                plugin.getCommand(cmd.getName()).setTabCompleter(cmd);
            }
            plugin.console("{prefix}&fLoaded command: &a"+cmd.getName());
            return true;
        }
        return false;
    }

    private PluginCommand getCommand(String name, SpigotMain plugin) {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);
            command = c.newInstance(name, plugin);
        } catch (SecurityException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return command;
    }

    private CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);
                commandMap = (CommandMap)f.get(Bukkit.getPluginManager());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return commandMap;
    }
}
