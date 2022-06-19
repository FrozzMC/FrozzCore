package me.thejokerdev.frozzcore.listeners;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.type.FUser;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldListeners implements Listener {
    private SpigotMain plugin;

    public WorldListeners(SpigotMain plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e){
        Player p = e.getPlayer();
        FUser user = plugin.getClassManager().getPlayerManager().getUser(p);
        World w = p.getWorld();

        if (plugin.getConfig().getBoolean("settings.perWorld")){
            if (plugin.getSpawn() != null){
                if (plugin.getSpawn().getWorld().equals(w)){
                    spawnRandomLoc(p, plugin.getSpawn());
                    user.getItemsManager().setItems();
                }
            }
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
        Location location = loc.clone().add(randomInt.get(new Random().nextInt(randomInt.size())), 0, randomInt.get(new Random().nextInt(randomInt.size())));
        p.teleport(location);
        if (p.hasPermission("core.fly")){
            p.setAllowFlight(true);
        }
    }
}
