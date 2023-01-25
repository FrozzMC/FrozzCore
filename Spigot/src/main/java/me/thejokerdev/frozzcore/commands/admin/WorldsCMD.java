package me.thejokerdev.frozzcore.commands.admin;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.Modules;
import me.thejokerdev.frozzcore.enums.SenderType;
import me.thejokerdev.frozzcore.type.CMD;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WorldsCMD extends CMD {

    public WorldsCMD(SpigotMain plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "worlds";
    }

    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER;
    }

    @Override
    public String getPermission() {
        return "core.admin.worlds";
    }

    @Override
    public String getHelp() {
        return "commands.worlds.help";
    }

    @Override
    public boolean onCMD(CommandSender sender, String alias, String[] args) {
        if (args.length < 2){
            getPlugin().getUtils().sendMessage(sender, getHelp()
            );
            return true;
        }
        String var1 = args[0].toLowerCase();
        String var2 = args[1].toLowerCase();

        if (getPlugin().getServer().getWorld(var2)==null){
            getPlugin().getUtils().sendMessage(sender, "{prefix}&c¡Ese mundo no existe!");
            return true;
        }

        if (var1.equalsIgnoreCase("add")){
            if (getPlugin().getUtils().getWorlds().get("worlds."+var2.toLowerCase())!=null){
                getPlugin().getUtils().sendMessage(sender, "{prefix}&cEse mundo ya está protegido.");
                return true;
            }
            for (Modules mod : Modules.values()){
                getPlugin().getUtils().getWorlds().add("worlds."+var2.toLowerCase()+"."+mod.name().toLowerCase(), true);
            }
            getPlugin().getUtils().getWorlds().save();
            getPlugin().getUtils().getWorlds().reload();
            getPlugin().getUtils().sendMessage(sender, "{prefix}&aHas añadido al mundo &e"+getPlugin().getServer().getWorld(var2).getName()+"&a a los mundos "+(getPlugin().getUtils().getWorlds().getString("settings.mode").equalsIgnoreCase("whitelist") ? "protegidos." : "bloqueados."));
            return true;
        }
        if (var1.equalsIgnoreCase("remove")){
            if (getPlugin().getUtils().getWorlds().get("worlds."+var2.toLowerCase())==null){
                getPlugin().getUtils().sendMessage(sender, "{prefix}&cEse mundo no está protegido.");
                return true;
            }
            getPlugin().getUtils().getWorlds().set("worlds."+var2.toLowerCase(), null);
            getPlugin().getUtils().getWorlds().save();
            getPlugin().getUtils().getWorlds().reload();
            getPlugin().getUtils().sendMessage(sender, "{prefix}&aEliminaste el mundo &e"+var2+"&a de los mundos "+(getPlugin().getUtils().getWorlds().getString("settings.mode").equalsIgnoreCase("whitelist") ? "protegidos." : "bloqueados."));
            return true;
        }
        if (var1.equalsIgnoreCase("toggle")){
            if (args.length != 3){
                getPlugin().getUtils().sendMessage(sender, "{prefix}&cDebes especificar el módulo que quieres alternar. Haz Tab para conocer los módulos disponibles.");
                return true;
            }
            String var3 = args[2].toLowerCase();
            Modules module;
            try {
                module = Modules.valueOf(var3.toUpperCase());
            } catch (IllegalArgumentException e) {
                getPlugin().getUtils().sendMessage(sender, "{prefix}&c¡Ese módulo no existe!");
                return true;
            }
            boolean bol = getPlugin().getUtils().getWorlds().getBoolean("worlds."+var2.toLowerCase()+"."+var3);
            getPlugin().getUtils().getWorlds().set("worlds."+var2.toLowerCase()+"."+var3, !bol);
            getPlugin().getUtils().sendMessage(sender, "{prefix}&aHas &e"+(!bol ? "activado" : "desactivado")+"&a el módulo de &b"+var3+"&a.");
            getPlugin().getUtils().getWorlds().save();
            getPlugin().getUtils().getWorlds().reload();
            return true;
        }
        if (var1.equalsIgnoreCase("reset")){
            if (getPlugin().getUtils().getWorlds().get("worlds."+var2.toLowerCase())==null){
                getPlugin().getUtils().sendMessage(sender, "{prefix}&cEse mundo no está protegido.");
                return true;
            }
            for (Modules mod : Modules.values()){
                getPlugin().getUtils().getWorlds().set("worlds."+var2.toLowerCase()+"."+mod.name().toLowerCase(), true);
            }
            getPlugin().getUtils().getWorlds().save();
            getPlugin().getUtils().getWorlds().reload();
            getPlugin().getUtils().sendMessage(sender, "{prefix}&aHas reestablecido las opciones del mundo &e"+getPlugin().getServer().getWorld(var2).getName()+"&a a los mundos "+(getPlugin().getUtils().getWorlds().getString("settings.mode").equalsIgnoreCase("whitelist") ? "protegidos." : "bloqueados."));
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 0){
            return list;
        }
        if (args.length == 1){
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("add", "remove", "toggle", "reset"), list);
        }
        String var1 = args[0].toLowerCase();
        if (args.length == 2){
            if (var1.equals("add")){
                List<String> worlds = getPlugin().getServer().getWorlds().stream().map(World::getName).filter(f -> getPlugin().getUtils().getWorlds().get("worlds."+f.toLowerCase())==null).collect(Collectors.toList());
                return StringUtil.copyPartialMatches(args[1], worlds, list);
            }
            if (var1.equals("remove") || var1.equals("toggle") || var1.equals("reset")){
                List<String> worlds = getPlugin().getServer().getWorlds().stream().map(World::getName).filter(f -> getPlugin().getUtils().getWorlds().get("worlds."+f.toLowerCase())!=null).collect(Collectors.toList());
                return StringUtil.copyPartialMatches(args[1], worlds, list);
            }
        }
        if (args.length == 3){
            if (var1.equals("toggle")){
                return StringUtil.copyPartialMatches(args[2], Arrays.stream(Modules.values()).map(Modules::name).map(String::toLowerCase).collect(Collectors.toList()), list);
            }
        }
        return list;
    }
}
