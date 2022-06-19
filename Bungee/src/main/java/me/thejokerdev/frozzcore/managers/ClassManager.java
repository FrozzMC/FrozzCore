package me.thejokerdev.frozzcore.managers;

import lombok.Getter;
import me.thejokerdev.frozzcore.BungeeMain;
import me.thejokerdev.frozzcore.api.utils.Utils;
import me.thejokerdev.frozzcore.discord.Bot;

@Getter
public class ClassManager {
    private BungeeMain plugin;

    private Utils utils;

    //Classes
    private Bot bot;

    public ClassManager(BungeeMain plugin) {
        this.plugin = plugin;
        init();
    }

    private void init(){
        utils = new Utils(plugin);
    }

    public void setup(){
        int i = 0;
        long ms = System.currentTimeMillis();
        sendMSG("&b&m ================&b| &fFrozzCore &7- &eBungee &b|&m================ ",
                " ",
                "&7Cargando módulos..."
        );
        if (plugin.getConfig().getBoolean("modules.discord")){
            bot = new Bot(plugin);
            bot.connect();
            if (bot.isEnabled()){
                i++;
            }
        }

        ms = System.currentTimeMillis() - ms;
        sendMSG("&a"+i+" módulos cargados.",
                " ",
                "&7Plugin iniciado en "+ms+"ms.",
                "&b&m ======================================================== "
        );
    }

    void sendMSG(String... in){
        utils.sendMSG(in);
    }
}
