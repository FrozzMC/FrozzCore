package me.thejokerdev.frozzcore.listeners;

import java.util.ArrayList;
import java.util.List;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.ModifierStatus;
import me.thejokerdev.frozzcore.enums.Modules;
import me.thejokerdev.frozzcore.type.FUser;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class DoubleJump implements Listener {
    private SpigotMain plugin;

    public DoubleJump(SpigotMain plugin){
        this.plugin = plugin;
    }
   
    private List<String> players = new ArrayList<String>();;
   
    @EventHandler
    public void setFly(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        FUser user = plugin.getClassManager().getPlayerManager().getUser(p);

        if (user.getDoubleJump() == ModifierStatus.ON && !p.getAllowFlight() && plugin.getUtils().isWorldProtected(p.getWorld(), Modules.DOUBLEJUMP)){
            e.getPlayer().setAllowFlight(true);
            e.getPlayer().setFlying(false);
        }
       
    }
   
    @EventHandler
    public void setVelocity(PlayerToggleFlightEvent e) {
       
        Player p = e.getPlayer();
        if (!plugin.getUtils().isWorldProtected(p.getWorld(), Modules.DOUBLEJUMP)){
            return;
        }

        FUser user = plugin.getClassManager().getPlayerManager().getUser(p);

        if (user.getDoubleJump() == ModifierStatus.OFF){
            return;
        }
       
        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR || p.isFlying() || players.contains(p.getName())) {

            return;

        } else {
               
            players.add(p.getName());
           
            e.setCancelled(true);
           
            p.setAllowFlight(false);
            p.setFlying(false);
           
            p.setVelocity(e.getPlayer().getLocation().getDirection().multiply(1.5).setY(1));
            p.playSound(p.getLocation(), Sound.BAT_TAKEOFF, 1.0f, -5.0f);
           
            p.setFallDistance(100);

        }

    }
   
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!plugin.getUtils().isWorldProtected(e.getEntity().getWorld(), Modules.DOUBLEJUMP)){
            return;
        }
       
        if (e.getEntity() instanceof Player && e.getCause() == DamageCause.FALL) {
           
            Player p = (Player) e.getEntity();
           
            if (players.contains(p.getName())) {
               
                e.setCancelled(true);
               
                players.remove(p.getName());

                FUser user = plugin.getClassManager().getPlayerManager().getUser(p);

                if (user.getDoubleJump() == ModifierStatus.ON && !p.getAllowFlight()){
                    p.setAllowFlight(true);
                }
               
            }
           
        }
       
    }
   
    @EventHandler
    public void removePlayer(PlayerQuitEvent e) {

        players.remove(e.getPlayer().getName());
       
    }
   
}