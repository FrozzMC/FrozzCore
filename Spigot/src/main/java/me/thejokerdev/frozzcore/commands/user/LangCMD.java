package me.thejokerdev.frozzcore.commands.user;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.SenderType;
import me.thejokerdev.frozzcore.type.CMD;
import me.thejokerdev.frozzcore.type.FUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LangCMD extends CMD {

    public LangCMD(SpigotMain plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "lang";
    }

    @Override
    public SenderType getSenderType() {
        return SenderType.BOTH;
    }

    @Override
    public String getPermission() {
        return "core.user";
    }

    @Override
    public String getHelp() {
        return "commands.lang.help";
    }

    @Override
    public boolean onCMD(CommandSender sender, String alias, String[] args) {
        if (args.length == 0){
            getPlugin().getClassManager().getUtils().sendMessage(sender, getHelp());
            return true;
        } else {
            String var1 = args[0];
            switch (var1.toLowerCase()){
                case "reset":{
                    if (args.length == 1 && sender instanceof Player){
                        FUser user = getPlugin().getClassManager().getPlayerManager().getUser((Player)sender);
                        user.setLang(getPlugin().getClassManager().getLangManager().getDefault(), false, true);
                        String msg = user.getMSG("commands.lang.reset");
                        msg = msg.replace("{lang}", getPlugin().getClassManager().getLangManager().getDefault());
                        getPlugin().getClassManager().getUtils().sendMessage(sender, msg);
                        return true;
                    }
                    if (args.length == 2 && sender.hasPermission("core.admin")){
                        String var2 = args[1];
                        Player p = Bukkit.getPlayer(var2);
                        if (p == null){
                            getPlugin().getClassManager().getUtils().sendMessage(sender, "playerNotExist");
                            return true;
                        }
                        FUser user = getPlugin().getClassManager().getPlayerManager().getUser(p);
                        user.setLang(getPlugin().getClassManager().getLangManager().getDefault(), false, true);
                        String msg = user.getMSG("commands.lang.reset_others");
                        msg = msg.replace("{player}", p.getName());
                        msg = msg.replace("{lang}", getPlugin().getClassManager().getLangManager().getDefault());
                        getPlugin().getClassManager().getUtils().sendMessage(sender, msg);
                        return true;
                    }
                }
                case "get":{
                    if (args.length == 1 && sender instanceof Player){
                        FUser user = getPlugin().getClassManager().getPlayerManager().getUser((Player)sender);
                        String msg = getPlugin().getClassManager().getUtils().getLangMSG(sender, "commands.lang.get");
                        msg = msg.replace("{player}", user.getName());
                        msg = msg.replace("{lang}", user.getLang());
                        System.out.println(getPlugin().getClassManager().getUtils().getMSG("commands.lang.get"));
                        System.out.println(msg);
                        getPlugin().getClassManager().getUtils().sendMessage(sender, msg);
                        return true;
                    }
                    if (args.length == 2 && sender.hasPermission("core.admin")){
                        String var2 = args[1];
                        Player p = Bukkit.getPlayer(var2);
                        if (p == null){
                            getPlugin().getClassManager().getUtils().sendMessage(sender, "playerNotExist");
                            return true;
                        }
                        FUser user = getPlugin().getClassManager().getPlayerManager().getUser(p);
                        String msg = getPlugin().getClassManager().getUtils().getLangMSG(sender, "commands.lang.get_other");
                        msg = msg.replace("{player}", p.getName());
                        msg = msg.replace("{lang}", user.getLang());
                        getPlugin().getClassManager().getUtils().sendMessage(sender, msg);
                        return true;
                    }
                }
                case "set":{
                    if (args.length == 2 && sender instanceof Player){
                        String lang = args[1];
                        FUser user = getPlugin().getClassManager().getPlayerManager().getUser((Player)sender);
                        user.setLang(lang, false, true);
                        String msg = user.getMSG("commands.lang.set");
                        msg = msg.replace("{player}", user.getName());
                        msg = msg.replace("{lang}", lang);
                        getPlugin().getClassManager().getUtils().sendMessage(sender, msg);
                        return true;
                    }
                    if (args.length == 3 && sender.hasPermission("core.admin")){
                        String lang = args[1];
                        String var2 = args[2];
                        Player p = Bukkit.getPlayer(var2);
                        if (p == null){
                            getPlugin().getClassManager().getUtils().sendMessage(sender, "playerNotExist");
                            return true;
                        }
                        if (!getPlugin().getClassManager().getLangManager().getLanguageList().contains(lang)){
                            getPlugin().getClassManager().getUtils().sendMessage(sender, "languageNotFound");
                            return true;
                        }
                        FUser user = getPlugin().getClassManager().getPlayerManager().getUser(p);
                        user.setLang(lang, false, true);
                        String msg = user.getMSG("commands.lang.set_other");
                        msg = msg.replace("{player}", p.getName());
                        msg = msg.replace("{lang}", lang);
                        getPlugin().getClassManager().getUtils().sendMessage(sender, msg);
                        return true;
                    }
                }
                default:{
                    getPlugin().getClassManager().getUtils().sendMessage(sender, getHelp());
                    return true;
                }
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 0){
            return list;
        }
        String var1 = args[0].toLowerCase();
        if (args.length == 1){
            List<String> cmds = new ArrayList<>();
            cmds.add("get");
            cmds.add("set");
            cmds.add("reset");
            StringUtil.copyPartialMatches(args[0], cmds, list);
            Collections.sort(list);
            return list;
        }
        switch (var1){
            case "get":
            case "reset":{
                StringUtil.copyPartialMatches(args[1], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), list);
                Collections.sort(list);
                return list;
            }
            case "set":{
                if (args.length == 2){
                    StringUtil.copyPartialMatches(args[1], getPlugin().getClassManager().getLangManager().getLanguageList(), list);
                    Collections.sort(list);
                    return list;
                }
                if (args.length == 3){
                    if (!sender.hasPermission("core.admin")){
                        return list;
                    }
                    StringUtil.copyPartialMatches(args[2], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), list);
                    Collections.sort(list);
                    return list;
                }
            }
            default:{
                return list;
            }
        }
    }
}
