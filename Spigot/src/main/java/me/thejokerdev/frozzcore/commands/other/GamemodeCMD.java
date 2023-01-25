package me.thejokerdev.frozzcore.commands.other;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.ModifierStatus;
import me.thejokerdev.frozzcore.type.CustomCMD;
import me.thejokerdev.frozzcore.type.FUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class GamemodeCMD extends CustomCMD {

    public GamemodeCMD(SpigotMain plugin) {
        super(plugin);
        setName("gamemode");
        addAliases("gm");
        setPermission("minecraft.command.gamemode");
        setDescription("&7Cambia tu modo de juego o el de un jugador.");
        setTabComplete(true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            getPlugin().getClassManager().getUtils().sendMessage(sender, "noPermission");
            return true;
        }
        if (!(sender instanceof Player)) {
            getPlugin().getClassManager().getUtils().sendMessage(sender, "onlyPlayers");
            return true;
        }
        Player p = (Player) sender;
        FUser user = getPlugin().getClassManager().getPlayerManager().getUser(p);
        if (args.length == 0){
            getPlugin().getClassManager().getUtils().sendMessage(sender, "commands.gamemode.usage");
            return true;
        }
        if (args.length == 1){
            String mode = args[0];
            if (mode.equalsIgnoreCase("c")){
                mode = "creative";
            } else if (mode.equalsIgnoreCase("a")){
                mode = "adventure";
            } else if (mode.equalsIgnoreCase("s")){
                mode = "survival";
            } else if (mode.equalsIgnoreCase("sp")){
                mode = "spectator";
            }

            String msg = getPlugin().getClassManager().getUtils().getLangMSG(p, "commands.gamemode.success.self");
            String msg_mode;
            if (mode.equalsIgnoreCase("0") || mode.equalsIgnoreCase("survival")){
                p.setGameMode(org.bukkit.GameMode.SURVIVAL);
                if (user.getAllowFlight() == ModifierStatus.ON){
                    p.setAllowFlight(true);
                    p.setFlying(true);
                }
                msg_mode = getPlugin().getUtils().getLangMSG(p, "commands.gamemode.modes.SURVIVAL");
                msg = msg.replace("{mode}", msg_mode);
                getPlugin().getUtils().sendMessage(sender, msg);
                return true;
            }
            if (mode.equalsIgnoreCase("1") || mode.equalsIgnoreCase("creative")){
                p.setGameMode(org.bukkit.GameMode.CREATIVE);
                p.setAllowFlight(true);
                p.setFlying(true);
                msg_mode = getPlugin().getUtils().getLangMSG(p, "commands.gamemode.modes.CREATIVE");
                msg = msg.replace("{mode}", msg_mode);
                getPlugin().getUtils().sendMessage(sender, msg);
                return true;
            }
            if (mode.equalsIgnoreCase("2") || mode.equalsIgnoreCase("adventure")){
                p.setGameMode(org.bukkit.GameMode.ADVENTURE);
                if (user.getAllowFlight() == ModifierStatus.ON){
                    p.setAllowFlight(true);
                    p.setFlying(true);
                }
                msg_mode = getPlugin().getUtils().getLangMSG(p, "commands.gamemode.modes.ADVENTURE");
                msg = msg.replace("{mode}", msg_mode);
                getPlugin().getUtils().sendMessage(sender, msg);
                return true;
            }
            if (mode.equalsIgnoreCase("3") || mode.equalsIgnoreCase("spectator")){
                p.setGameMode(org.bukkit.GameMode.SPECTATOR);
                p.setAllowFlight(true);
                p.setFlying(true);
                msg_mode = getPlugin().getUtils().getLangMSG(p, "commands.gamemode.modes.SPECTATOR");
                msg = msg.replace("{mode}", msg_mode);
                getPlugin().getUtils().sendMessage(sender, msg);
                return true;
            }
            return true;
        }
        if (args.length == 2){
            if (!sender.hasPermission(getPermission()+".others")){
                getPlugin().getClassManager().getUtils().sendMessage(sender, "noPermission");
                return true;
            }
            args[1] = args[1].replace("@a", "all");
            args[1] = args[1].replace("*", "all");
            Player target = getPlugin().getServer().getPlayer(args[1]);
            if (target == null && !args[1].equalsIgnoreCase("all")){
                getPlugin().getClassManager().getUtils().sendMessage(sender, "playerNotExist");
                return true;
            }
            String mode = args[0];
            mode = mode.toLowerCase().replace("c", "1").replace("a", "2").replace("s", "0").replace("sp", "3");
            String msg = getPlugin().getClassManager().getUtils().getLangMSG(p, "commands.gamemode.success.other");
            String msg_mode;
            if (mode.equalsIgnoreCase("0") || mode.equalsIgnoreCase("survival")){
                if (args[1].equalsIgnoreCase("all")){
                    msg = getPlugin().getClassManager().getUtils().getLangMSG(p, "commands.gamemode.success.all");
                    for (Player online : getPlugin().getServer().getOnlinePlayers()){
                        String msg2 = getPlugin().getClassManager().getUtils().getLangMSG(online, "commands.gamemode.success.self");
                        msg2 = msg2.replace("{mode}", getPlugin().getClassManager().getUtils().getLangMSG(online, "commands.gamemode.modes.SURVIVAL"));
                        online.setGameMode(org.bukkit.GameMode.SURVIVAL);
                        if (getPlugin().getClassManager().getPlayerManager().getUser(online).getAllowFlight() == ModifierStatus.ON){
                            online.setAllowFlight(true);
                            online.setFlying(true);
                        }
                        getPlugin().getClassManager().getUtils().sendMessage(online, msg2);
                    }
                    msg_mode = getPlugin().getUtils().getLangMSG(p, "commands.gamemode.modes.SURVIVAL");
                    msg = msg.replace("{mode}", msg_mode);
                    getPlugin().getUtils().sendMessage(sender, msg);
                    return true;
                }
                target.setGameMode(org.bukkit.GameMode.SURVIVAL);
                if (user.getAllowFlight() == ModifierStatus.ON){
                    target.setAllowFlight(true);
                    target.setFlying(true);
                }
                msg_mode = getPlugin().getUtils().getLangMSG(p, "commands.gamemode.modes.SURVIVAL");
                msg = msg.replace("{mode}", msg_mode);
                msg = msg.replace("{player}", target.getName());
                getPlugin().getUtils().sendMessage(sender, msg);
                return true;
            }
            if (mode.equalsIgnoreCase("1") || mode.equalsIgnoreCase("creative")){
                if (args[1].equalsIgnoreCase("all")){
                    msg = getPlugin().getClassManager().getUtils().getLangMSG(p, "commands.gamemode.success.all");
                    for (Player online : getPlugin().getServer().getOnlinePlayers()){
                        String msg2 = getPlugin().getClassManager().getUtils().getLangMSG(online, "commands.gamemode.success.self");
                        msg2 = msg2.replace("{mode}", getPlugin().getClassManager().getUtils().getLangMSG(online, "commands.gamemode.modes.CREATIVE"));
                        online.setGameMode(org.bukkit.GameMode.CREATIVE);
                        online.setAllowFlight(true);
                        online.setFlying(true);
                        getPlugin().getClassManager().getUtils().sendMessage(online, msg2);
                    }
                    msg_mode = getPlugin().getUtils().getLangMSG(p, "commands.gamemode.modes.CREATIVE");
                    msg = msg.replace("{mode}", msg_mode);
                    getPlugin().getUtils().sendMessage(sender, msg);
                    return true;
                }
                target.setGameMode(org.bukkit.GameMode.CREATIVE);
                target.setAllowFlight(true);
                target.setFlying(true);
                msg_mode = getPlugin().getUtils().getLangMSG(p, "commands.gamemode.modes.CREATIVE");
                msg = msg.replace("{mode}", msg_mode);
                msg = msg.replace("{player}", target.getName());
                getPlugin().getUtils().sendMessage(sender, msg);
                return true;
            }
            if (mode.equalsIgnoreCase("2") || mode.equalsIgnoreCase("adventure")){
                if (args[1].equalsIgnoreCase("all")){
                    msg = getPlugin().getClassManager().getUtils().getLangMSG(p, "commands.gamemode.success.all");
                    for (Player online : getPlugin().getServer().getOnlinePlayers()){
                        String msg2 = getPlugin().getClassManager().getUtils().getLangMSG(online, "commands.gamemode.success.self");
                        msg2 = msg2.replace("{mode}", getPlugin().getClassManager().getUtils().getLangMSG(online, "commands.gamemode.modes.ADVENTURE"));
                        online.setGameMode(org.bukkit.GameMode.ADVENTURE);
                        if (getPlugin().getClassManager().getPlayerManager().getUser(online).getAllowFlight() == ModifierStatus.ON){
                            online.setAllowFlight(true);
                            online.setFlying(true);
                        }
                        getPlugin().getClassManager().getUtils().sendMessage(online, msg2);
                    }
                    msg_mode = getPlugin().getUtils().getLangMSG(p, "commands.gamemode.modes.ADVENTURE");
                    msg = msg.replace("{mode}", msg_mode);
                    getPlugin().getUtils().sendMessage(sender, msg);
                    return true;
                }
                target.setGameMode(org.bukkit.GameMode.ADVENTURE);
                if (user.getAllowFlight() == ModifierStatus.ON){
                    target.setAllowFlight(true);
                    target.setFlying(true);
                }
                msg_mode = getPlugin().getUtils().getLangMSG(p, "commands.gamemode.modes.ADVENTURE");
                msg = msg.replace("{mode}", msg_mode);
                msg = msg.replace("{player}", target.getName());
                getPlugin().getUtils().sendMessage(sender, msg);
                return true;
            }
            if (mode.equalsIgnoreCase("3") || mode.equalsIgnoreCase("spectator")){
                if (args[1].equalsIgnoreCase("all")){
                    msg = getPlugin().getClassManager().getUtils().getLangMSG(p, "commands.gamemode.success.all");
                    for (Player online : getPlugin().getServer().getOnlinePlayers()){
                        String msg2 = getPlugin().getClassManager().getUtils().getLangMSG(online, "commands.gamemode.success.self");
                        msg2 = msg2.replace("{mode}", getPlugin().getClassManager().getUtils().getLangMSG(online, "commands.gamemode.modes.SPECTATOR"));
                        online.setGameMode(org.bukkit.GameMode.SPECTATOR);
                        online.setAllowFlight(true);
                        online.setFlying(true);
                        getPlugin().getClassManager().getUtils().sendMessage(online, msg2);
                    }
                    msg_mode = getPlugin().getUtils().getLangMSG(p, "commands.gamemode.modes.SPECTATOR");
                    msg = msg.replace("{mode}", msg_mode);
                    getPlugin().getUtils().sendMessage(sender, msg);
                    return true;
                }
                target.setGameMode(org.bukkit.GameMode.SPECTATOR);
                target.setAllowFlight(true);
                target.setFlying(true);
                msg_mode = getPlugin().getUtils().getLangMSG(p, "commands.gamemode.modes.SPECTATOR");
                msg = msg.replace("{mode}", msg_mode);
                msg = msg.replace("{player}", target.getName());
                getPlugin().getUtils().sendMessage(sender, msg);
                return true;
            }
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender.hasPermission(getPermission()))){
            return new ArrayList<>();
        }
        if (args.length == 1){
            List<String> modes = new ArrayList<>();
            modes.add("0");
            modes.add("1");
            modes.add("2");
            modes.add("3");
            modes.add("c");
            modes.add("a");
            modes.add("s");
            modes.add("sp");
            modes.add("survival");
            modes.add("creative");
            modes.add("adventure");
            modes.add("spectator");
            return StringUtil.copyPartialMatches(args[0], modes, new ArrayList<>());
        }
        if (args.length == 2 && sender.hasPermission(getPermission() + ".others")){
            List<String> players = new ArrayList<>();
            for (Player p : getPlugin().getServer().getOnlinePlayers()){
                players.add(p.getName());
            }
            players.add("all");
            return StringUtil.copyPartialMatches(args[1], players, new ArrayList<>());
        }
        return new ArrayList<>();
    }
}
