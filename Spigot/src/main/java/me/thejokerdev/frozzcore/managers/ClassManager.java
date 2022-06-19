package me.thejokerdev.frozzcore.managers;

import lombok.Getter;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.LangDownloader;
import me.thejokerdev.frozzcore.api.board.ScoreBoard;
import me.thejokerdev.frozzcore.api.nametag.NametagHandler;
import me.thejokerdev.frozzcore.api.nametag.NametagManager;
import me.thejokerdev.frozzcore.api.utils.Utils;
import me.thejokerdev.frozzcore.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Arrays;

@Getter
public class ClassManager {
    private SpigotMain plugin;

    /* Managers */
    private CMDManager cmdManager;
    private MenusManager menusManager;
    private LangManager langManager;
    private LangDownloader langDownloader;
    private PlayerManager playerManager;
    private DataManager dataManager;

    private NametagManager nametagManager;
    private NametagHandler nametagHandler;

    /* Utils */
    private Utils utils;

    public ClassManager(SpigotMain plugin) {
        this.plugin = plugin;

        initManagers();
    }

    public void initManagers() {
        cmdManager = new CMDManager(plugin);
        playerManager = new PlayerManager(plugin);
        dataManager = new DataManager(plugin);
        menusManager = new MenusManager(plugin);

        /* Languages */
        if (plugin.getConfig().getBoolean("modules.languages")) {
            langDownloader = new LangDownloader(plugin);
            langManager = new LangManager(plugin);
        }
        /* Lobby */
        if (plugin.getConfig().getBoolean("modules.lobby")) {
            listener(new LobbyListener(plugin));
        }
        if (plugin.getConfig().getBoolean("modules.lobby")) {
            listener(new LobbyListener(plugin));
        }
        if (plugin.getConfig().getBoolean("modules.chat")) {
            listener(new ChatListener(plugin));
        }

        if (plugin.getConfig().getBoolean("modules.nametags")) {
            nametagManager = new NametagManager(plugin);
            nametagHandler = new NametagHandler(plugin);
        }

        utils = new Utils(plugin);

        new ScoreBoard(plugin);

        listener(new LoginListener(plugin), new WorldListeners(plugin), new ItemEvents(plugin));
    }

    public void init() {
        if (langManager != null){
            langManager.init();
        }
        if (nametagManager != null){
            nametagManager.init();
        }
    }

    public void listener(Listener... listener) {
        Arrays.stream(listener).forEach(listener1 -> Bukkit.getServer().getPluginManager().registerEvents(listener1, plugin));
    }
}
