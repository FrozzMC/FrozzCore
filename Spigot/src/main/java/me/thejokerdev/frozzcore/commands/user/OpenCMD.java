package me.thejokerdev.frozzcore.commands.user;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.SenderType;
import me.thejokerdev.frozzcore.type.CMD;
import me.thejokerdev.frozzcore.type.Menu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class OpenCMD extends CMD {
    public OpenCMD(SpigotMain plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "open";
    }

    @Override
    public SenderType getSenderType() {
        return SenderType.BOTH;
    }

    @Override
    public String getPermission() {
        return "core.open";
    }

    @Override
    public String getHelp() {
        return "commands.open.help";
    }

    @Override
    public boolean onCMD(CommandSender sender, String alias, String[] args) {
        if (!sender.hasPermission(getPermission())){
            return true;
        }
        Player p = null;
        if (args.length > 0){
            if (args.length == 1 && sender instanceof Player){
                p = (Player) sender;
            }
            if (args.length == 2){
                p = Bukkit.getPlayer(args[1]);
            }
            if (p == null){
                return true;
            }
            String arg = args[0];
            Menu menu = getPlugin().getClassManager().getMenusManager().getPlayerMenu(p, arg);
            if (menu == null){
                getPlugin().getClassManager().getUtils().sendMessage(sender, "&c¡Ese menú no existe!");
                return true;
            }
            p.openInventory(menu.getInventory());
        }
        return false;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        return null;
    }
}
