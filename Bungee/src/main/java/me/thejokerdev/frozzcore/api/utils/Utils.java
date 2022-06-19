package me.thejokerdev.frozzcore.api.utils;

import me.thejokerdev.frozzcore.BungeeMain;
import me.thejokerdev.frozzcore.api.FileUtils;
import me.thejokerdev.frozzcore.api.misc.DefaultFontInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    private BungeeMain plugin;

    public Utils(BungeeMain plugin) {
        this.plugin = plugin;
    }

    public String ct(String in){
        return ChatColor.translateAlternateColorCodes('&', in);
    }
    public String[] ct (String... in){
        return Arrays.stream(in).map(this::ct).toArray(String[]::new);
    }
    public List<String> ct (List<String> in){
        return in.stream().map(this::ct).collect(Collectors.toList());
    }
    public String getCenteredMSG(String message){
        assert message != null;
        message = ct(message);

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


    public void sendMSG(String... msg){
        Arrays.stream(msg).forEach(s -> sendMSG(plugin.getProxy().getConsole(), s));
    }
    public void sendMSG(CommandSender sender, String... in){
        Arrays.stream(in).forEach(s -> sendMSG(sender, s));
    }

    public void sendMSG(CommandSender sender, String msg){
        sendMSG(sender, msg, false);
    }
    public void sendMSG(CommandSender sender, String msg, boolean haveKey){
        if (!msg.contains(" ") && haveKey){
            msg = getLangMSG(sender, msg);
        }

        if (msg.contains("\\n")){
            sendMSG(sender, msg.split("\\n"));
            return;
        }
        if (msg.contains("\n")){
            sendMSG(sender, msg.split("\n"));
            return;
        }

        msg = getMSG(sender, msg);

        BaseComponent component = new TextComponent(msg);

        if (component.toPlainText().equals("") || component.toPlainText().isEmpty()){
            return;
        }

        if (!(sender instanceof ProxiedPlayer)){
            plugin.getProxy().getConsole().sendMessage(component);
        } else {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            p.sendMessage(component);
        }
    }

    public String getLangMSG(CommandSender sender, String key){
        File lang = new File(plugin.getDataFolder(), "messages.yml");
        if (!lang.exists()){
            plugin.saveResource("messages.yml", false);
        }
        FileUtils file = new FileUtils(lang);
        return file.getString(key);
    }

    public String getMSG(CommandSender sender, String in){

        in = ct(in);

        if (in.contains("{prefix}") || in.contains("%prefix%")){
            in = in.replace("{prefix}", plugin.getPrefix()).replace("%prefix%", plugin.getPrefix());
        }

        if (in.contains("{center}") || in.contains("%center%")){
            in = in.replace("{center}", "").replace("%center%", "");
            in = getCenteredMSG(in);
        }

        return in;
    }
}
