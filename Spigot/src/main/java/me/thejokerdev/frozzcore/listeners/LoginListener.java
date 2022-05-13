package me.thejokerdev.frozzcore.listeners;

import me.thejokerdev.frozzcore.SpigotMain;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoginListener implements Listener {
    private SpigotMain plugin;

    public LoginListener(SpigotMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if (plugin.getSpawn() != null){
            spawnRandomLoc(p, plugin.getSpawn());
        }

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
        if (p.hasPermission("core.fly")){
            p.setAllowFlight(true);
            p.setFlying(true);
        }
    }
}
