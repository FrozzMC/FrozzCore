package me.thejokerdev.frozzcore.listeners;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.Modules;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LobbyListener implements Listener {
    private final SpigotMain plugin;

    public LobbyListener(SpigotMain plugin) {
        this.plugin = plugin;
    }

    public void checkDay(){
        if (plugin.getSpawn() == null){
            return;
        }
        if (!plugin.getConfig().getBoolean("lobby.disableDayCycle")){
            return;
        }

        World w = plugin.getSpawn().getWorld();
        if (w.getGameRuleValue("doDaylightCycle").equalsIgnoreCase("true") && plugin.getUtils().isWorldProtected(w, Modules.LOBBY)){
            w.setTime(6000);
            w.setGameRuleValue("doDaylightCycle", "false");
            w.setGameRuleValue("randomTickSpeed", "0");
        }
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent e){
        World w = e.getEntity().getWorld();

        if (e.getDamager() instanceof Player){
            Player p = (Player)e.getDamager();
            if (p.getGameMode() == GameMode.CREATIVE && e.getEntityType() == EntityType.ARMOR_STAND){
                return;
            }
        }

        if (plugin.getUtils().isWorldProtected(w, Modules.LOBBY) && plugin.getConfig().getBoolean("lobby.disablePvP")){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMobSpawn(EntitySpawnEvent e){
        World w = e.getEntity().getWorld();
        if (e.getEntityType() == EntityType.ARMOR_STAND){
            return;
        }
        if (plugin.getUtils().isWorldProtected(w, Modules.LOBBY) && plugin.getConfig().getBoolean("lobby.disableSpawn")) {
            if (e.getEntityType() == EntityType.ENDER_PEARL) {
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        World w = e.getEntity().getWorld();
        if (!plugin.getConfig().getBoolean("lobby.disableDamage")){
            return;
        }
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID && plugin.getUtils().isWorldProtected(w, Modules.VOIDTP)) {
            if (w == plugin.getSpawn().getWorld()){
                e.getEntity().teleport(plugin.getSpawn());
            } else {
                e.getEntity().teleport(w.getSpawnLocation());
            }
            e.setCancelled(true);
        }
        if (plugin.getUtils().isWorldProtected(w, Modules.LOBBY)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByBlock(EntityDamageByBlockEvent e){
        World w = e.getEntity().getWorld();
        if (plugin.getUtils().isWorldProtected(w, Modules.LOBBY) && plugin.getConfig().getBoolean("lobby.disableDamage")){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        World w = p.getWorld();
        if (plugin.getUtils().isWorldProtected(w, Modules.LOBBY) && plugin.getConfig().getBoolean("lobby.disableInteract")){
            if (p.hasPermission("core.admin.build") && p.getGameMode() == GameMode.CREATIVE){
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e){
        World w = e.getWorld();
        if (plugin.getUtils().isWorldProtected(w, Modules.LOBBY) && plugin.getConfig().getBoolean("lobby.disableWeather")){
            checkDay();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFeedLevelChange(FoodLevelChangeEvent e){
        World w = e.getEntity().getWorld();
        if (plugin.getUtils().isWorldProtected(w, Modules.LOBBY) && plugin.getConfig().getBoolean("lobby.disableHunger")){
            e.setFoodLevel(20);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        World w = p.getWorld();
        if (plugin.getUtils().isWorldProtected(w, Modules.LOBBY) && plugin.getConfig().getBoolean("lobby.respawn")){
            p.spigot().respawn();
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        World w = p.getWorld();
        if (plugin.getUtils().isWorldProtected(w, Modules.LOBBY) && plugin.getConfig().getBoolean("lobby.respawn")){
            plugin.getClassManager().getLoginListener().spawnRandomLoc(p, plugin.getSpawn() !=null ? plugin.getSpawn() : w.getSpawnLocation());
            if (plugin.getUtils().isWorldProtected(w, Modules.ITEMS) && plugin.getConfig().getBoolean("items.onRespawn")){
                plugin.getClassManager().getPlayerManager().getUser(p).getItemsManager().setItems();
            }
        }
    }

}
