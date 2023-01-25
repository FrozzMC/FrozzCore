package me.thejokerdev.frozzcore.api.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.visual.Animation;
import me.thejokerdev.frozzcore.api.visual.Color;
import me.thejokerdev.frozzcore.type.FUser;
import me.thejokerdev.frozzcore.type.Lang;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PAPI extends PlaceholderExpansion {
    private SpigotMain plugin;

    public PAPI(SpigotMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "core";
    }

    @Override
    public @NotNull String getAuthor() {
        return "TheJokerDev";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean register() {
        return super.register();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (plugin.getConfig().get("placeholders")!=null){
            for (String key : plugin.getConfig().getConfigurationSection("placeholders").getKeys(false)){
                if (key.equalsIgnoreCase(params)){
                    return plugin.getClassManager().getUtils().formatMSG(player, plugin.getConfig().getString("placeholders."+key));
                }
            }
        }
        if (params.equals("chatcolor")){
            LinkedList<String> list = new LinkedList<>(plugin.getConfig().getConfigurationSection("chat.colors").getKeys(false));
            String out = plugin.getConfig().getString("chat.colors.default");
            for (String perm : list){
                if (perm.equals("default") && !player.hasPermission("core.chatcolor.status")){
                    continue;
                }
                if (player.hasPermission("core.chatcolor."+perm)){
                    out = plugin.getConfig().getString("chat.colors."+perm);
                }
            }
            return out;
        }
        if (params.equals("fly")){
            FUser user = plugin.getClassManager().getPlayerManager().getUser(player);
            if (user != null){
                return user.getAllowFlight().name();
            }
        }
        if (params.equals("speed")){
            FUser user = plugin.getClassManager().getPlayerManager().getUser(player);
            if (user != null){
                return user.getSpeed().name();
            }
        }
        if (params.equals("server_id")){
            return plugin.getId();
        }
        if (params.equals("jump")){
            FUser user = plugin.getClassManager().getPlayerManager().getUser(player);
            if (user != null){
                return user.getJump().name();
            }
        }
        if (params.equals("doublejump")){
            FUser user = plugin.getClassManager().getPlayerManager().getUser(player);
            if (user != null){
                return user.getDoubleJump().name();
            }
        }
        if (params.equals("hype")){
            if (player != null){
                FUser user = plugin.getClassManager().getPlayerManager().getUser(player);
                if (user != null){
                    return user.getHype()+"";
                }
            }
        }
        if (params.equals("visibility")){
            if (player != null){
                FUser user = plugin.getClassManager().getPlayerManager().getUser(player);
                if (user != null){
                    return plugin.getClassManager().getUtils().getLangMSG(player, "visibility."+user.getVisibilityType().name());
                }
            }
        }
        String[] split = params.split("_");
        if (split.length == 1 || split.length >= 4) {
            String arg1 = split[0];
            if (arg1.equalsIgnoreCase("wave")) {
                if (split.length > 3){
                    String arg2 = split[1];
                    String arg3 = split[2];
                    String arg4 = split[3];
                    if (split.length == 5){
                        String arg5 = split[4].toUpperCase();
                        return Animation.wave(arg2, Boolean.parseBoolean(arg5), 5, 10, Color.from(arg3), Color.from(arg4));
                    }
                    return Animation.wave(arg2, Color.from(arg3), Color.from(arg4));
                }
                return Animation.wave("Test", Color.from("986532"), Color.from("722626"));
            }
            if (arg1.equalsIgnoreCase("fading")) {
                if (split.length >= 4) {
                    String arg2 = split[1];
                    String arg3 = split[2];
                    String arg4 = split[3];
                    if (split.length == 5){
                        String arg5 = split[4].toUpperCase();
                        return Animation.fading(arg2, Boolean.parseBoolean(arg5), 5, 10, Color.from(arg3), Color.from(arg4));
                    }
                    return Animation.fading(arg2, Color.from(arg3), Color.from(arg4));
                }
                return Animation.fading("Test", Color.from("986532"), Color.from("722626t"));
            }
        }
        if (!params.contains("_")){
            return PlaceholderAPI.setPlaceholders(player, plugin.getClassManager().getUtils().getMSG("error"));
        }
        if (split.length != 2){
            return PlaceholderAPI.setPlaceholders(player, plugin.getClassManager().getUtils().getMSG("error"));
        }
        String lang = plugin.getClassManager().getLangManager().getDefault();
        if (player != null) {
            FUser fUser = plugin.getClassManager().getPlayerManager().getUser(player);
            if (fUser != null) {
                lang = fUser.getLang();
            }
        }
        String section = split[0];

        if (!plugin.getClassManager().getLangManager().getLanguages().containsKey(section)){
            return PlaceholderAPI.setPlaceholders(player, plugin.getClassManager().getUtils().getMSG("sectionNotFound").replace("{section}", section));
        }

        if (plugin.getClassManager().getLangManager().getLanguageOfSection(section, lang) == null){
            lang = plugin.getClassManager().getLangManager().getDefault();
        }

        Lang language = plugin.getClassManager().getLangManager().getLanguageOfSection(section, lang);
        String key = split[1];

        if (language.getFile().get(key)==null){
            language = plugin.getClassManager().getLangManager().getLanguageOfSection(section, plugin.getClassManager().getLangManager().getDefault());
        }

        if (language.getFile().get(key)==null) {
            return PlaceholderAPI.setPlaceholders(player, plugin.getClassManager().getUtils().getMSG("keyNotFound").replace("{key}", key));
        }

        return PlaceholderAPI.setPlaceholders(player, language.getFile().getString(key));
    }
}
