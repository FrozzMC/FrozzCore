package me.thejokerdev.frozzcore.managers;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.commands.admin.ReloadCMD;
import me.thejokerdev.frozzcore.commands.admin.SetLobbyCMD;
import me.thejokerdev.frozzcore.enums.SenderType;
import me.thejokerdev.frozzcore.type.CMD;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class CMDManager implements CommandExecutor, TabCompleter {
    private SpigotMain plugin;
    private List<CMD> commands = new ArrayList<>();

    public CMDManager(SpigotMain plugin) {
        this.plugin = plugin;

        plugin.getCommand("frozzcore").setExecutor(this);
        plugin.getCommand("frozzcore").setTabCompleter(this);

        initCMDs();
    }

    public void initCMDs(){
        commands = new ArrayList<>();

        commands.add(new SetLobbyCMD(plugin));
        commands.add(new ReloadCMD(plugin));
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
}
