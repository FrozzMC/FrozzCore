package me.thejokerdev.frozzcore.commands.other;

import lombok.Getter;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.type.CustomCMD;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FlyCMD extends CustomCMD {
    private SpigotMain plugin;

    public FlyCMD(SpigotMain plugin) {
        super(plugin);
        setName("fly");
        addAliases("volar", "flight");
        setPermission("core.fly");
        setDescription("Usa este comando para volar.");
        setTabComplete(true);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            getPlugin().getClassManager().getUtils().sendMessage(sender, "onlyPlayers");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission(getPermission())){
            getPlugin().getClassManager().getUtils().sendMessage(sender, "noPermission");
            return true;
        }
        if (args.length == 0){
            if (p.getAllowFlight()){
                getPlugin().getClassManager().getUtils().sendMessage(sender, "commands.fly.deactivated");
                p.setAllowFlight(false);
            } else {
                getPlugin().getClassManager().getUtils().sendMessage(sender, "commands.fly.activated");
                p.setAllowFlight(true);
                p.setFlying(true);
            }
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
