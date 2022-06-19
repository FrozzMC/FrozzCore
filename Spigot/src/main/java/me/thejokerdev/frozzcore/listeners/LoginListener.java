package me.thejokerdev.frozzcore.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.VisibilityType;
import me.thejokerdev.frozzcore.type.FUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class LoginListener implements Listener {
    private SpigotMain plugin;

    public LoginListener(SpigotMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        FUser user = plugin.getClassManager().getPlayerManager().getUser(p);
        if (plugin.getSpawn() != null){
            spawnRandomLoc(p, plugin.getSpawn());
        }
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        e.setJoinMessage(null);

        if (getJoinMessage(p)!=null && plugin.getConfig().getBoolean("lobby.vipMessages")){
            if (plugin.getConfig().getBoolean("settings.perWorld") && plugin.getSpawn()!=null){
                for (Player t : plugin.getSpawn().getWorld().getPlayers()){
                    plugin.getClassManager().getUtils().sendMessage(t, getJoinMessage(p));
                }
            } else {
                e.setJoinMessage(getJoinMessage(p));
            }
        }
        if (plugin.getConfig().getBoolean("items.onJoin")){
            new BukkitRunnable() {
                @Override
                public void run() {
                    user.getItemsManager().setItems();
                }
            }.runTaskLaterAsynchronously(plugin, 20L);
        }


        checkVisibility();

        plugin.getClassManager().getMenusManager().loadMenus(p);

    }

    public void checkVisibility(){
        for (Player p : Bukkit.getOnlinePlayers()){
            if (plugin.getSpawn() !=null){
                if (!plugin.getSpawn().getWorld().equals(p.getWorld())){
                    continue;
                }
            }
            FUser user = plugin.getClassManager().getPlayerManager().getUser(p);
            if (user.getVisibilityType() != VisibilityType.ALL){
                for (Player t : p.getWorld().getPlayers()) {
                    if (user.getVisibilityType() == VisibilityType.RANKS) {
                        if (t.hasPermission("core.rank")) {
                            continue;
                        }
                    }
                    t.hidePlayer(p);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        e.setQuitMessage(null);
    }

    public void spawnRandomLoc(Player p, Location loc){
        List<Double> randomInt = new ArrayList<>();
        randomInt.add(0.5);
        randomInt.add(-0.5);
        randomInt.add(0.25);
        randomInt.add(-0.25);
        randomInt.add(0.125);
        randomInt.add(-0.125);
        Location location = loc.clone().add(randomInt.get(new Random().nextInt(randomInt.size())), p.hasPermission("core.fly") ? 2.5 : 0, randomInt.get(new Random().nextInt(randomInt.size())));
        p.teleport(location);
        p.getInventory().setHeldItemSlot(4);
        if (p.hasPermission("core.fly")){
            p.setAllowFlight(true);
            p.setFlying(true);
        }
    }

    public String getJoinMessage(Player p){
        LinkedList<String> list = new LinkedList<>(plugin.getConfig().getConfigurationSection("vip.messages").getKeys(false));
        String out = null;
        for (String perm : list){
            if (p.hasPermission("core.vipjoin."+perm)){
                out = plugin.getConfig().getString("vip.messages."+perm);
            }
        }
        if (out == null){
            return null;
        }
        return plugin.getClassManager().getUtils().getMessage(PlaceholderAPI.setPlaceholders(p, out));
    }
}
