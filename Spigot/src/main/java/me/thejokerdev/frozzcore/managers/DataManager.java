package me.thejokerdev.frozzcore.managers;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.data.MySQL;
import me.thejokerdev.frozzcore.data.SQLite;
import me.thejokerdev.frozzcore.type.Data;

public class DataManager {
    private SpigotMain plugin;
    private Data data;

    public DataManager(SpigotMain plugin) {
        this.plugin = plugin;

        initData();
    }

    public void initData(){
        String str = plugin.getConfig().getString("data.type", "yml");
        switch (str.toLowerCase()){
            case "mysql":{
                data = new MySQL(plugin);
                break;
            }
            case "sqlite":
            case "sql":
            default: {
                data = new SQLite(plugin);
                break;
            }
        }
        data.setup();
    }

    public Data getData() {
        return data;
    }
}
