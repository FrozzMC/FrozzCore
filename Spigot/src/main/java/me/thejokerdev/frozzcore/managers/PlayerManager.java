package me.thejokerdev.frozzcore.managers;

import lombok.Getter;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.type.FUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class PlayerManager implements Listener {
    private HashMap<UUID, FUser> users = new HashMap<>();

    public PlayerManager(SpigotMain plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public FUser getUser(Player p){
        if (!users.containsKey(p.getUniqueId())){
            registerUser(p);
        }

        return users.get(p.getUniqueId());
    }

    public void registerUser(Player p){
        if (!users.containsKey(p.getUniqueId())){
            FUser user = new FUser(p);
            users.put(p.getUniqueId(), user);
            user.initItems();
        }
    }

    public void removeUser(Player p){
        if (users.containsKey(p.getUniqueId())){
            users.get(p.getUniqueId()).saveData(true);
        }
        users.remove(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        registerUser(p);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        removeUser(p);
    }
}
