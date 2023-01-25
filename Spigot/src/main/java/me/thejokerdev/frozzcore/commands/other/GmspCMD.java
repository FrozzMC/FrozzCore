package me.thejokerdev.frozzcore.commands.other;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.type.CustomCMD;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class GmspCMD extends CustomCMD {
    public GmspCMD(SpigotMain plugin) {
        super(plugin);
        setName("gmsp");
        addAliases("gm3", "gmspec", "gmspectator");
        setPermission("minecraft.command.gamemode");
        setDescription("Cambia tu modo de juego a espectador.");
        setTabComplete(true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            getPlugin().getClassManager().getUtils().sendMessage(sender, "noPermission");
            return true;
        }
        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            player.setGameMode(GameMode.SPECTATOR);
            player.setAllowFlight(true);
            player.setFlying(true);
            String msg = getPlugin().getClassManager().getUtils().getLangMSG(player, "commands.gamemode.success.self");
            String msg_mode = getPlugin().getUtils().getLangMSG(player, "commands.gamemode.modes.SPECTATOR");
            msg = msg.replace("{mode}", msg_mode);
            getPlugin().getUtils().sendMessage(player, msg);
            return true;
        }
        if (args.length == 1){
            if (!sender.hasPermission(getPermission() + ".others")){
                getPlugin().getClassManager().getUtils().sendMessage(sender, "noPermission");
                return true;
            }
            args[0] = args[0].toLowerCase().replace("@a", "all").replace("*", "all");
            Player target = getPlugin().getServer().getPlayer(args[0]);
            if (target == null && !args[0].equalsIgnoreCase("all")){
                getPlugin().getUtils().sendMessage(sender, "commands.playerNotFound");
                return true;
            }
            String msg;
            String msg_mode;
            if (target != null) {
                target.setGameMode(GameMode.SPECTATOR);
                target.setAllowFlight(true);
                target.setFlying(true);
                msg = getPlugin().getClassManager().getUtils().getLangMSG(target, "commands.gamemode.success.self");
                msg_mode = getPlugin().getUtils().getLangMSG(target, "commands.gamemode.modes.SPECTATOR");
                msg = msg.replace("{mode}", msg_mode);
                getPlugin().getUtils().sendMessage(target, msg);

                msg = getPlugin().getClassManager().getUtils().getLangMSG(sender, "commands.gamemode.success.other");
                msg_mode = getPlugin().getUtils().getLangMSG(sender, "commands.gamemode.modes.SPECTATOR");
                msg = msg.replace("{mode}", msg_mode);
                msg = msg.replace("{player}", target.getName());
                getPlugin().getUtils().sendMessage(sender, msg);
            }

            if (args[0].equalsIgnoreCase("all")){
                for (Player player : getPlugin().getServer().getOnlinePlayers()){
                    player.setGameMode(GameMode.SPECTATOR);
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    msg = getPlugin().getClassManager().getUtils().getLangMSG(player, "commands.gamemode.success.self");
                    msg_mode = getPlugin().getUtils().getLangMSG(player, "commands.gamemode.modes.SPECTATOR");
                    msg = msg.replace("{mode}", msg_mode);
                    getPlugin().getUtils().sendMessage(player, msg);
                }
                msg = getPlugin().getClassManager().getUtils().getLangMSG(sender, "commands.gamemode.success.all");
                msg_mode = getPlugin().getUtils().getLangMSG(sender, "commands.gamemode.modes.SPECTATOR");
                msg = msg.replace("{mode}", msg_mode);
                getPlugin().getUtils().sendMessage(sender, msg);
            }
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission(getPermission() + ".others")) return new ArrayList<>();
        List<String> list = new ArrayList<>();
        if (args.length == 1){
            for (Player player : getPlugin().getServer().getOnlinePlayers()){
                list.add(player.getName());
            }
            list.add("all");
            return StringUtil.copyPartialMatches(args[0], list, new ArrayList<>());
        }
        return new ArrayList<>();
    }
}

