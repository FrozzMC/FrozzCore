package me.thejokerdev.frozzcore;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import me.thejokerdev.frozzcore.api.hooks.LP;
import me.thejokerdev.frozzcore.api.hooks.PAPI;
import me.thejokerdev.frozzcore.api.hooks.SR;
import me.thejokerdev.frozzcore.api.utils.LocationUtil;
import me.thejokerdev.frozzcore.api.utils.Utils;
import me.thejokerdev.frozzcore.managers.ClassManager;
import me.thejokerdev.frozzcore.type.FUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public final class SpigotMain extends JavaPlugin {
    private static SpigotMain plugin;
    private ClassManager classManager;

    private LP lp = null;
    private SR sr = null;
    private Location spawn = null;

    private boolean loaded = false;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();

        classManager = new ClassManager(this);
        classManager.init();
        classManager.getCmdManager().initCMDs();

        if (!checkDependencies()){
            getServer().getPluginManager().disablePlugin(this);
        }

        if (getConfig().get("lobby.spawn")!=null){
            spawn = LocationUtil.getLocation(getConfig().getString("lobby.spawn"));
        }

        plugin.getClassManager().getUtils().startTab(false);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        loaded = true;
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

        if (pm.isPluginEnabled("LuckPerms")){
            console("&aLuckPerms found!");
            lp = new LP(this);
        }

        if (pm.isPluginEnabled("SkinsRestorer")){
            console("&aSkinsRestorer found!");
            sr = new SR(this);
        }

        return true;
    }

    public boolean haveLP(){
        return lp != null && getConfig().getBoolean("hooks.luckperms");
    }

    public boolean haveSR(){
        return sr != null && getConfig().getBoolean("hooks.skinsrestorer");
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
        if (classManager == null || classManager.getUtils() == null){
            Bukkit.getConsoleSender().sendMessage(Utils.ct(getPrefix() + "&e&lDEBUG: &7" + in));
            return;
        }
        getClassManager().getUtils().sendMessage("{prefix}&e&lDEBUG: &7"+in);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (loaded) {
            plugin.getClassManager().getUtils().startTab(true);
            if (plugin.getConfig().getBoolean("modules.nametags")){
                plugin.getClassManager().getNametagManager().init();
            }
            for (FUser user : plugin.getClassManager().getPlayerManager().getUsers().values()){
                user.getItemsManager().reloadItems();
                plugin.getClassManager().getMenusManager().loadMenus(user.getPlayer());
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
