package me.thejokerdev.frozzcore.listeners;

import me.thejokerdev.frozzcore.SpigotMain;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

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
        if (w.getGameRuleValue("doDaylightCycle").equalsIgnoreCase("true")){
            w.setTime(6000);
            w.setGameRuleValue("doDaylightCycle", "false");
            w.setGameRuleValue("randomTickSpeed", "0");
        }
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent e){
        if (plugin.getSpawn() == null){
            return;
        }
        if (!plugin.getConfig().getBoolean("lobby.disablePvP")){
            return;
        }
        Entity entity = e.getEntity();
        if (plugin.getSpawn().getWorld() == entity.getWorld()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if (plugin.getSpawn() == null){
            return;
        }
        if (!plugin.getConfig().getBoolean("lobby.disableDamage")){
            return;
        }
        Entity entity = e.getEntity();
        if (plugin.getSpawn().getWorld() == entity.getWorld()){
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID){
                if (plugin.getConfig().getBoolean("lobby.teleportOnVoid")){
                    e.getEntity().teleport(plugin.getSpawn());
                }
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByBlock(EntityDamageByBlockEvent e){
        if (plugin.getSpawn() == null){
            return;
        }
        if (!plugin.getConfig().getBoolean("lobby.disableDamage")){
            return;
        }
        Entity entity = e.getEntity();
        if (plugin.getSpawn().getWorld() == entity.getWorld()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if (plugin.getSpawn() == null){
            return;
        }
        if (!plugin.getConfig().getBoolean("lobby.disableInteract")){
            return;
        }
        if (plugin.getSpawn().getWorld() == p.getWorld()){
            if (p.hasPermission("core.admin.build") && p.getGameMode() == GameMode.CREATIVE){
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e){
        if (plugin.getSpawn() == null){
            return;
        }
        if (!plugin.getConfig().getBoolean("lobby.disableWeather")){
            return;
        }
        if (e.getWorld() == plugin.getSpawn().getWorld()){
            checkDay();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFeedLevelChange(FoodLevelChangeEvent e){
        if (plugin.getSpawn() == null){
            return;
        }
        if (!plugin.getConfig().getBoolean("lobby.disableHunger")){
            return;
        }
        if (e.getEntity().getWorld() == plugin.getSpawn().getWorld()){
            e.setFoodLevel(20);
            e.setCancelled(true);
        }
    }
}
