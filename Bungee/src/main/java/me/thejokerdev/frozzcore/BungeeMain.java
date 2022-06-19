package me.thejokerdev.frozzcore;

import lombok.Getter;
import me.thejokerdev.frozzcore.api.FileUtils;
import me.thejokerdev.frozzcore.managers.ClassManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Getter
public final class BungeeMain extends Plugin {

    private static BungeeMain plugin;
    private FileUtils config = null;
    private ClassManager cm;
    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        cm = new ClassManager(this);

        cm.setup();
    }

    void saveDefaultConfig(){
        File file = new File(getDataFolder(), "config.yml");
        if (!getDataFolder().exists()){
            getDataFolder().mkdir();
        }
        if (!file.exists()){
            saveResource("config.yml", false);
        }
        config = new FileUtils(file);
    }

    public void saveResource(String file, boolean replace){
        File f = new File(getDataFolder(), file);
        if (!f.exists() && !replace){
            try (InputStream in = getResourceAsStream(file)){
                if (in == null) return;
                Files.copy(in, f.toPath());
            } catch (IOException ignored) {
            }
        }
    }

    public void reloadConfig(){
        config.reload();
    }
    public void saveConfig(){
        reloadConfig();
    }

    public static BungeeMain getPlugin() {
        return plugin;
    }

    public String getPrefix(){
        String prefix = "&b&lFrozzMC &8&l» &7";
        return config!=null ? config.get("settings.prefix")!=null ? cm.getUtils().ct(config.getString("settings.prefix")) : prefix : prefix;
    }

    public ClassManager getClassManager() {
        return cm;
    }

    @Override
    public void onDisable() {
        cm.getBot().stop();
    }
}
