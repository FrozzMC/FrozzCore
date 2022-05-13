package me.thejokerdev.frozzcore.commands.admin;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.utils.LocationUtil;
import me.thejokerdev.frozzcore.enums.SenderType;
import me.thejokerdev.frozzcore.type.CMD;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetLobbyCMD extends CMD {

    public SetLobbyCMD(SpigotMain plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "setlobby";
    }

    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER;
    }

    @Override
    public String getPermission() {
        return "frozzcore.command.setlobby";
    }

    @Override
    public String getHelp() {
        return "commands.setlobby.help";
    }

    @Override
    public boolean onCMD(CommandSender sender, String alias, String[] args) {
        Player p = (Player)sender;
        if (args.length == 0){
            Location loc = LocationUtil.center(p.getLocation());
            getPlugin().setSpawn(loc);
            getPlugin().getConfig().set("lobby.spawn", LocationUtil.getString(loc, false));
            getPlugin().saveConfig();
            getPlugin().reloadConfig();
            getPlugin().getClassManager().getUtils().sendMessage(sender, "commands.setlobby.success");
        }
        return true;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        return new ArrayList<>();
    }
}
