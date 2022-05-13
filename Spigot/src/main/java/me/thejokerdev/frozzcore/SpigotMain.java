package me.thejokerdev.frozzcore;

import lombok.Getter;
import lombok.Setter;
import me.thejokerdev.frozzcore.api.hooks.PAPI;
import me.thejokerdev.frozzcore.api.utils.LocationUtil;
import me.thejokerdev.frozzcore.api.utils.Utils;
import me.thejokerdev.frozzcore.managers.ClassManager;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public final class SpigotMain extends JavaPlugin {
    private static SpigotMain plugin;
    private ClassManager classManager;
    private Location spawn = null;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();

        classManager = new ClassManager(this);
        classManager.init();

        if (!checkDependencies()){
            getServer().getPluginManager().disablePlugin(this);
        }

        if (getConfig().get("lobby.spawn")!=null){
            spawn = LocationUtil.getLocation(getConfig().getString("lobby.spawn"));
        }

    }

    public boolean checkDependencies(){
        PluginManager pm = getServer().getPluginManager();
        if (!pm.isPluginEnabled("PlaceholderAPI")){
            console("&4&lERROR: &cPlaceholderAPI doesn't found!");
            return false;
        } else {
            console("&aPlaceholderAPI found!");
            new PAPI(this).register();
            console("&fPlaceholderAPI hooked!");
        }

        return true;
    }

    public static SpigotMain getPlugin() {
        return plugin;
    }

    public String getPrefix(){
        return Utils.ct(getConfig().getString("settings.prefix"));
    }

    public void console(String... in){
        getClassManager().getUtils().sendMessage(in);
    }

    public void debug(String in){
        if (!getConfig().getBoolean("settings.debug")){
            return;
        }
        getClassManager().getUtils().sendMessage("{prefix}&e&lDEBUG: &7"+in);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
