package me.thejokerdev.frozzcore.commands.admin;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.SenderType;
import me.thejokerdev.frozzcore.type.CMD;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JumppadsCMD extends CMD {
    public JumppadsCMD(SpigotMain plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "jumppads";
    }

    @Override
    public SenderType getSenderType() {
        return SenderType.PLAYER;
    }

    @Override
    public String getPermission() {
        return "core.admin.jumppads";
    }

    @Override
    public String getHelp() {
        return "commands.jumppads.help";
    }

    @Override
    public boolean onCMD(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        if (args.length < 2){
            getPlugin().getUtils().sendMessage(sender,getHelp()
            );
            return true;
        }
        if (args.length == 2){
            String var1 = args[0].toLowerCase();
            String var2 = args[1];

            if (var1.equals("material")){
                XMaterial mat;
                try {
                    mat = XMaterial.valueOf(var2.toUpperCase());
                } catch (IllegalArgumentException e) {
                    getPlugin().getUtils().sendMessage(sender, "{prefix}&cEse material no existe.");
                    return true;
                }
                getPlugin().getConfig().set("jumppads.material", mat.name().toLowerCase());
                getPlugin().saveConfig();
                getPlugin().reloadConfig();
                getPlugin().getUtils().sendMessage(sender, "{prefix}&aEstableciste el material de jumppad a &e"+mat+"&a.");
                return true;
            }
            if (var1.equals("height")){
                double dob;
                try {
                    dob = Double.parseDouble(var2);
                } catch (NumberFormatException e) {
                    getPlugin().getUtils().sendMessage(sender, "{prefix}&c¡Ese no es un número válido para los ajustes!");
                    return true;
                }
                getPlugin().getConfig().set("jumppads.height", dob);
                getPlugin().saveConfig();
                getPlugin().reloadConfig();
                getPlugin().getUtils().sendMessage(sender, "{prefix}&aEstableciste la altura de jumppad a &e"+dob+"&a.");
                return true;
            }
            if (var1.equals("length")){
                double dob;
                try {
                    dob = Double.parseDouble(var2);
                } catch (NumberFormatException e) {
                    getPlugin().getUtils().sendMessage(sender, "{prefix}&c¡Ese no es un número válido para los ajustes!");
                    return true;
                }
                getPlugin().getConfig().set("jumppads.length", dob);
                getPlugin().saveConfig();
                getPlugin().reloadConfig();
                getPlugin().getUtils().sendMessage(sender, "{prefix}&aEstableciste la potencia del jumppad a &e"+dob+"&a.");
                return true;
            }
            if (var1.equals("sound")){
                XSound sound;
                try {
                    sound = XSound.valueOf(var2.toUpperCase());
                } catch (IllegalArgumentException e) {
                    getPlugin().getUtils().sendMessage(sender, "{prefix}&c¡Ese no es un sonido válido para los ajustes!");
                    return true;
                }
                getPlugin().getConfig().set("jumppads.sound", sound.name().toLowerCase());
                getPlugin().saveConfig();
                getPlugin().reloadConfig();
                getPlugin().getUtils().sendMessage(sender, "{prefix}&aEstableciste el sonido del jumppad a &e"+var2+"&a.");
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1){
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("material", "height", "length", "sound"), list);
        }
        if (args.length == 2){
            String arg1 = args[0].toLowerCase();
            if (args[0].equals("material")){
                return StringUtil.copyPartialMatches(args[1], Arrays.stream(XMaterial.values()).map(XMaterial::name).map(String::toLowerCase).collect(Collectors.toList()), list);
            }
            if (args[0].equals("sound")){
                return StringUtil.copyPartialMatches(args[1], Arrays.stream(XSound.values()).map(XSound::name).map(String::toLowerCase).collect(Collectors.toList()), list);
            }
        }
        return list;
    }
}
