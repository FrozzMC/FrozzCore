package me.thejokerdev.frozzcore.managers;

import lombok.Getter;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.LangDownloader;
import me.thejokerdev.frozzcore.api.board.ScoreBoard;
import me.thejokerdev.frozzcore.api.utils.Utils;
import me.thejokerdev.frozzcore.listeners.LobbyListener;
import me.thejokerdev.frozzcore.listeners.LoginListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Arrays;

@Getter
public class ClassManager {
    private SpigotMain plugin;

    /* Managers */
    private CMDManager cmdManager;
    private LangManager langManager;
    private LangDownloader langDownloader;
    private PlayerManager playerManager;
    private DataManager dataManager;

    /* Utils */
    private Utils utils;

    public ClassManager(SpigotMain plugin) {
        this.plugin = plugin;

        initManagers();
    }

    public void initManagers(){
        cmdManager = new CMDManager(plugin);
        playerManager = new PlayerManager(plugin);
        dataManager = new DataManager(plugin);

        /* Languages */
        if (plugin.getConfig().getBoolean("modules.languages")){
            langDownloader = new LangDownloader(plugin);
            langManager = new LangManager(plugin);
        }
        /* Lobby */
        if (plugin.getConfig().getBoolean("modules.lobby")){
            listener(new LobbyListener(plugin));
        }

        utils = new Utils(plugin);

        listener(new ScoreBoard(plugin), new LoginListener(plugin));
    }

    public void init(){
        langManager.init();
    }

    public void listener(Listener... listener){
        Arrays.stream(listener).forEach(listener1 -> Bukkit.getServer().getPluginManager().registerEvents(listener1, plugin));
    }
}
