package me.thejokerdev.frozzcore.commands.admin;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.hooks.PAPI;
import me.thejokerdev.frozzcore.enums.SenderType;
import me.thejokerdev.frozzcore.type.CMD;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReloadCMD extends CMD {

    public ReloadCMD(SpigotMain plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public SenderType getSenderType() {
        return SenderType.BOTH;
    }

    @Override
    public String getPermission() {
        return "core.command.reload";
    }

    @Override
    public String getHelp() {
        return "commands.reload.help";
    }

    @Override
    public boolean onCMD(CommandSender sender, String alias, String[] args) {
        if (args.length == 0){
            getPlugin().reloadConfig();
            getPlugin().getClassManager().getLangManager().reload();
            new PAPI(getPlugin()).register();
            getPlugin().getItemsCache().getItems().clear();
            getPlugin().getClassManager().getUtils().sendMessage(sender, "commands.reload.success");
            return true;
        }
        if (args.length == 1){
            String var1 = args[0].toLowerCase();
            if (var1.equals("local")){
                getPlugin().reloadConfig();
                getPlugin().getClassManager().getLangManager().loadFiles();
                getPlugin().getItemsCache().getItems().clear();
                getPlugin().getClassManager().getUtils().sendMessage(sender, "commands.reload.local");
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1){
            List<String> list1 = new ArrayList<>(Collections.singletonList("local"));
            StringUtil.copyPartialMatches(args[0], list1, list);
        }
        return list;
    }
}
