package me.thejokerdev.frozzcore.listeners;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.ModifierStatus;
import me.thejokerdev.frozzcore.enums.Modules;
import me.thejokerdev.frozzcore.type.FUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class WorldListeners implements Listener {
    private SpigotMain plugin;

    public WorldListeners(SpigotMain plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        FUser user = plugin.getClassManager().getPlayerManager().getUser(p);
        World w = p.getWorld();
        boolean spawnExists = plugin.getSpawn() != null;

        if (spawnExists && plugin.getClassManager().getUtils().isWorldProtected(w, Modules.JOINTP)) {
            if (plugin.getSpawn().getWorld().equals(w)) {
                plugin.getClassManager().getLoginListener().spawnRandomLoc(p, plugin.getSpawn());
            }
        } else if (plugin.getClassManager().getUtils().isWorldProtected(w, Modules.JOINTP)) {
            plugin.getClassManager().getLoginListener().spawnRandomLoc(p, w.getSpawnLocation());
        }

        if (plugin.getClassManager().getUtils().isWorldProtected(w, Modules.ITEMS)) {
            user.getItemsManager().setItems();
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getClassManager().getLoginListener().checkVisibility(p);
            if (plugin.getConfig().getBoolean("settings.perWorld")) {
                for (Player t : Bukkit.getOnlinePlayers()) {
                    if (t != p) {
                        if (!p.getWorld().equals(t.getWorld())) {
                            if (p.canSee(t)) {
                                p.hidePlayer(t);
                            }
                            if (t.canSee(p)) {
                                t.hidePlayer(p);
                            }
                        }
                    }
                }
            }
        }, 5L);
        if (plugin.getUtils().isWorldProtected(w, Modules.JOINMESSAGES)) {
            for (Player t : w.getPlayers()) {
                if (!plugin.getUtils().isWorldProtected(t.getWorld(), Modules.JOINMESSAGES)) {
                    continue;
                }
                String str = plugin.getClassManager().getLoginListener().getJoinMessage(p);
                if (str != null) {
                    plugin.getClassManager().getUtils().sendMessage(t, str);
                }
            }
        }

        if (user.getDoubleJump() == ModifierStatus.ON && !p.getAllowFlight() && plugin.getUtils().isWorldProtected(p.getWorld(), Modules.DOUBLEJUMP)) {
            e.getPlayer().setAllowFlight(true);
            e.getPlayer().setFlying(false);
        }

        if (plugin.getUtils().isWorldProtected(p.getWorld(), Modules.JUMP)) {
            if (user.getJump() == ModifierStatus.ON) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,Integer.MAX_VALUE, 1, true, false), true);
            }
        } else {
            p.removePotionEffect(PotionEffectType.JUMP);
        }

        if (plugin.getUtils().isWorldProtected(p.getWorld(), Modules.SPEED)) {
            p.setWalkSpeed((user.getSpeed() == ModifierStatus.ON ? 6 : 2) / 10.0f);
        } else {
            p.setWalkSpeed(2 / 10.0f);
        }

    }
}
