package me.thejokerdev.frozzcore.api.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.misc.DefaultFontInfo;
import me.thejokerdev.frozzcore.type.FUser;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.swing.plaf.SplitPaneUI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    private SpigotMain plugin;

    public Utils(SpigotMain plugin) {
        this.plugin = plugin;
    }

    /* String utils*/
    public static String ct(String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    public String getCenteredMSG(String message){
        message = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
            }else if(previousCode){
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = 154 - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message;
    }

    public void sendMessage(String... msg){
        sendMessage(Bukkit.getConsoleSender(), msg);
    }
    public void sendMessage(List<String> msg){
        sendMessage(Bukkit.getConsoleSender(), msg);
    }
    public String getMessage(String msg){
        msg = ct(msg);
        boolean hasPrefix = msg.contains("{prefix}");
        boolean hasCenter = msg.contains("{center}");

        if (msg.equals("")){
            msg = " ";
        }

        if (hasPrefix){
            msg = msg.replace("{prefix}", plugin.getPrefix());
        }
        if (hasCenter){
            msg = msg.replace("{center}", "");
            msg = getCenteredMSG(msg);
        }
        return msg;
    }
    public List<String> getList(List<String> list){
        return list.stream().map(this::getMessage).collect(Collectors.toList());
    }

    public String getLangMSG(CommandSender sender, String key){
        String section = "general";
        String lang = plugin.getClassManager().getLangManager().getDefault();
        if (key.contains("@")){
            section = key.split("@")[0];
            key = key.split("@")[1];
        }
        if (sender instanceof Player){
            FUser user = plugin.getClassManager().getPlayerManager().getUser((Player)sender);
            lang = user.getLang();
        }
        return plugin.getClassManager().getLangManager().getLanguageOfSection(section, lang).getFile().getString(key);
    }
    public void sendMessage(CommandSender sender, String msg){
        boolean isBroadcast = msg.contains("{broadcast}");

        if (msg.contains(".") || !msg.contains(" ")){
            msg = getLangMSG(sender, msg);
        }

        msg = getMessage(msg);

        msg = sender instanceof Player ? PlaceholderAPI.setPlaceholders((Player)sender, msg) : PlaceholderAPI.setPlaceholders(null, msg);

        if (isBroadcast){
            String finalMsg = msg.replace("{broadcast}", "");
            Bukkit.getConsoleSender().sendMessage(finalMsg);
            Bukkit.getOnlinePlayers().forEach(p->p.sendMessage(finalMsg));
            return;
        }
        if (sender instanceof Player){

            sender.sendMessage(msg);
        } else {
            Bukkit.getConsoleSender().sendMessage(msg);
        }
    }
    public void sendMessage(CommandSender sender, String... msg){
        Arrays.stream(msg).forEach(m->sendMessage(sender, m));
    }
    public void sendMessage(CommandSender sender, List<String> msg){
        msg.forEach(m->sendMessage(sender, m));
    }

    public String applyPlaceholders(String str, HashMap<String, String> hashMap){
        for (Map.Entry<String, String> entry : hashMap.entrySet()){
            str = str.replace(entry.getKey(), entry.getValue());
        }
        return str;
    }
    public List<String> applyPlaceholders(List<String> str, HashMap<String, String> hashMap){
        return str.stream().map(l -> applyPlaceholders(l, hashMap)).collect(Collectors.toList());
    }

    public String getMSG(String key){
        return getMessage(plugin.getClassManager().getLangManager().getLanguageOfSection("general", plugin.getClassManager().getLangManager().getDefault()).getFile().getString(key));
    }

    public List<String> getList(String key){
        return getList(plugin.getClassManager().getLangManager().getLanguageOfSection("general", plugin.getClassManager().getLangManager().getDefault()).getFile().getStringList(key));
    }
}
