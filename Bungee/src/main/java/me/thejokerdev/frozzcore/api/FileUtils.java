package me.thejokerdev.frozzcore.api;

import me.thejokerdev.frozzcore.BungeeMain;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileUtils {
    private File file;
    private Configuration configuration;

    public FileUtils(String path, String fileName){
        file = new File(BungeeMain.getPlugin().getDataFolder()+path, fileName);
        init();
    }

    public FileUtils(String fileName){
        file = new File(BungeeMain.getPlugin().getDataFolder(), fileName);
        init();
    }

    public FileUtils(File file){
        this.file = file;
        init();
    }

    void init(){
        if (file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(){
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public Object get(String key){
        return get(key, null);
    }
    public <T> T get(String key, T def){
        return configuration.get(key, def);
    }

    public String getString(String key){
        return getString(key, null);
    }
    public String getString(String key, String def){
        return configuration.getString(key, def);
    }

    public List<String> getStringList(String key){
        return configuration.getStringList(key);
    }

    public List<?> getList(String key){
        return getList(key);
    }

    public List<?> getList(String key, List<?> def){
        return configuration.getList(key, def);
    }

    public boolean getBoolean(String key){
        return getBoolean(key, false);
    }
    public boolean getBoolean(String key, boolean def){
        return configuration.getBoolean(key, def);
    }

    public Integer getInt(String key){
        return getInt(key, 0);
    }
    public Integer getInt(String key, int def){
        return configuration.getInt(key, def);
    }

    public double getDouble(String key){
        return getDouble(key, 0.0D);
    }
    public double getDouble(String key, double def){
        return configuration.getDouble(key, def);
    }

    public float getFloat(String key){
        return getFloat(key, 0F);
    }
    public float getFloat(String key, float def){
        return configuration.getFloat(key, def);
    }

    public void reload(){
        save();
        init();
    }
}
