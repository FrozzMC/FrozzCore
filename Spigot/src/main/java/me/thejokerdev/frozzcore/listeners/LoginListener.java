package me.thejokerdev.frozzcore.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.Modules;
import me.thejokerdev.frozzcore.enums.VisibilityType;
import me.thejokerdev.frozzcore.type.FUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LoginListener implements Listener {
    private SpigotMain plugin;

    public LoginListener(SpigotMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event){
        if (!plugin.isLoaded()){
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cEl servidor está cargando aún, por favor, inténtelo de nuevo en unos segundos.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        e.setJoinMessage(null);
        boolean spawnExists = plugin.getSpawn() != null;
        World w = p.getWorld();

        FUser user = plugin.getClassManager().getPlayerManager().getUser(p);
        boolean tpEveryJoin = plugin.getConfig().getBoolean("settings.tpEveryJoin", true);
        if (!tpEveryJoin) {
            if (!p.hasPlayedBefore()){
                if (spawnExists && plugin.getClassManager().getUtils().isWorldProtected(w, Modules.JOINTP)) {
                    if (plugin.getSpawn().getWorld().equals(w)) {
                        spawnRandomLoc(p, plugin.getSpawn());
                    }
                } else if (plugin.getClassManager().getUtils().isWorldProtected(w, Modules.JOINTP)) {
                    spawnRandomLoc(p, w.getSpawnLocation());
                }
            }
        } else {
            if (spawnExists && plugin.getClassManager().getUtils().isWorldProtected(w, Modules.JOINTP)) {
                if (plugin.getSpawn().getWorld().equals(w)) {
                    spawnRandomLoc(p, plugin.getSpawn());
                }
            } else if (plugin.getClassManager().getUtils().isWorldProtected(w, Modules.JOINTP)) {
                spawnRandomLoc(p, w.getSpawnLocation());
            }
        }
        if (plugin.getUtils().isWorldProtected(w, Modules.LOBBY)){
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
        }

        if (getJoinMessage(p)!=null && plugin.getConfig().getBoolean("lobby.vipMessages") && plugin.getUtils().isWorldProtected(w, Modules.JOINMESSAGES)){
            if (plugin.getConfig().getBoolean("settings.perWorld") && plugin.getSpawn()!=null){
                for (Player t : plugin.getSpawn().getWorld().getPlayers()){
                    plugin.getClassManager().getUtils().sendMessage(t, getJoinMessage(p));
                }
                plugin.getUtils().sendMessage(p, getJoinMessage(p));
            } else {
                e.setJoinMessage(getJoinMessage(p));
            }
        }
        if (plugin.getConfig().getBoolean("items.onJoin") && plugin.getUtils().isWorldProtected(w, Modules.ITEMS)){
            user.getItemsManager().setItems();
        }

        checkVisibility(p);

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getClassManager().getMenusManager().loadMenus(p);
            }
        }.runTaskAsynchronously(plugin);

    }

    public void checkVisibility(Player p){
        boolean bol = plugin.getUtils().isWorldProtected(p.getWorld(), Modules.VISIBILITY);
        plugin.debug("World: "+p.getWorld()+" is " + (bol ? "protected" : "not protected")+" of visibility module.");
        if (bol) {
            plugin.debug("Visibility: Executing method to world "+p.getWorld().getName());
            for (Player t : Bukkit.getOnlinePlayers()) {
                if (p != t) {
                    checkVisibility(p, t);
                }
            }
        } else {
            plugin.debug("Visibility: Executing method for no registered world.");
            for (Player t : p.getWorld().getPlayers()){
                if (p != t && !p.canSee(t)){
                    p.showPlayer(t);
                    t.showPlayer(p);
                }
            }
        }
    }

    public void checkVisibility(Player p, Player t){
        FUser user = plugin.getClassManager().getPlayerManager().getUser(p);
        FUser user2 = plugin.getClassManager().getPlayerManager().getUser(t);
        if (p.hasMetadata("NPC") || t.hasMetadata("NPC")){
            return;
        }
        plugin.debug("Visibility - Player 1: "+p.getName()+ " with selected option: "+user.getVisibilityType().name().toLowerCase()+" ("+user.getVisibilityType().ordinal()+").");
        switch (user.getVisibilityType().ordinal()){
            case 0:{
                if (!p.canSee(t)){
                    p.showPlayer(t);
                    plugin.debug("Visibility - Player 2 showed to Player 1");
                }
                break;
            }
            case 1:{
                if (t.hasPermission("core.visibility.rank")){
                    if (!p.canSee(t)){
                        p.showPlayer(t);
                        plugin.debug("Visibility - Player 2 showed to Player 1 with rank permission");
                    }
                    break;
                }
                if (p.canSee(t)){
                    plugin.debug("Visibility - Player 2 hided to Player 1 without the rank permission");
                    p.hidePlayer(t);
                }
                break;
            }
            case 2:{
                if (p.canSee(t)){
                    plugin.debug("Visibility - Player 2 hided to Player 1");
                    p.hidePlayer(t);
                }
                break;
            }
        }
        plugin.debug("Visibility - Player 2: "+t.getName()+ " with selected option: "+user2.getVisibilityType().name().toLowerCase()+" ("+user2.getVisibilityType().ordinal()+").");
        switch (user2.getVisibilityType().ordinal()){
            case 0:{
                if (!t.canSee(p)){
                    t.showPlayer(p);
                    plugin.debug("Visibility - Player 1 showed to Player 2");
                }
                break;
            }
            case 1:{
                if (p.hasPermission("core.visibility.rank")){
                    if (!t.canSee(p)){
                        t.showPlayer(p);
                        plugin.debug("Visibility - Player 1 showed to Player 2 with rank permission");
                    }
                    break;
                }
                if (t.canSee(p)){
                    t.hidePlayer(p);
                    plugin.debug("Visibility - Player 1 hided to Player 2 without rank permission");
                }
                break;
            }
            case 2:{
                if (t.canSee(p)){
                    t.hidePlayer(p);
                    plugin.debug("Visibility - Player 1 hided to Player 2");
                }
                break;
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        e.setQuitMessage(null);
    }

    public void spawnRandomLoc(Player p, Location loc){
        FUser user = plugin.getClassManager().getPlayerManager().getUser(p);
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
        if (p.hasPermission("core.fly") && plugin.getUtils().isWorldProtected(p.getWorld(), Modules.FLY)){
            p.setAllowFlight(true);
            p.setFlying(true);
        }
    }

    public String getJoinMessage(Player p){
        if (!p.hasPermission("core.joinmsg.status")){
            return null;
        }

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