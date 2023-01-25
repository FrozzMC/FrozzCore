package me.thejokerdev.frozzcore.listeners;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.Modules;
import me.thejokerdev.frozzcore.type.Button;
import me.thejokerdev.frozzcore.type.FUser;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class ItemEvents implements Listener {

    private SpigotMain plugin;

    private final HashMap<UUID, HashMap<Button, Long>> time = new HashMap();

    public ItemEvents(SpigotMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        FUser user = plugin.getClassManager().getPlayerManager().getUser(p);
        World w = p.getWorld();

        if (!plugin.getConfig().getBoolean("modules.items")){
            return;
        }

        if (p.getGameMode() != GameMode.CREATIVE) {
            if (plugin.getUtils().isWorldProtected(w, Modules.LOBBY)){
                e.setCancelled(true);
            }
        } else {
            if (!p.hasPermission("core.admin.build")){
                if (plugin.getUtils().isWorldProtected(w, Modules.LOBBY)){
                    e.setCancelled(true);
                }
            }
        }
        if (!plugin.getUtils().isWorldProtected(w, Modules.ITEMS)){
            return;
        }

        ItemStack item = e.getItem();
        if (item == null) {
            return;
        }
        if (p.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        for (Button b : user.getItemsManager().getItems().values()) {
            if (!b.canView()){
                continue;
            }
            if (b.getItem().build(p).isSimilar(item)) {
                if (item.getType() == Material.ENDER_PEARL){
                    e.setCancelled(true);
                }
                if (b.getCooldown() > 0) {
                    if (this.canUseItem(b, p.getUniqueId(), b.getCooldown() * 1000)) {
                        HashMap<Button, Long> preMap;
                        if (this.time.containsKey(p.getUniqueId())) {
                            preMap = this.time.get(p.getUniqueId());
                            preMap.put(b, System.currentTimeMillis());
                            this.time.put(p.getUniqueId(), preMap);
                        } else {
                            preMap = new HashMap<>();
                            preMap.put(b, System.currentTimeMillis());
                            this.time.put(p.getUniqueId(), preMap);
                        }

                        b.executePhysicallyItemsActions(e);
                        if (b.canInteract()){
                            e.setCancelled(false);
                        }
                        return;
                    } else {
                        String msg = plugin.getClassManager().getUtils().getLangMSG(p, "items.cooldown");
                        double timeleft = Math.round((float) (((Long) ((HashMap<?, ?>) this.time.get(p.getUniqueId())).get(b) + (b.getCooldown() * 1000L) - System.currentTimeMillis()) / 1000L * 100L));
                        plugin.getClassManager().getUtils().sendMessage(p, msg.replaceAll("<time>", String.valueOf((int)(timeleft / 100.0D))));
                    }
                } else {
                    b.executePhysicallyItemsActions(e);
                    if (b.canInteract()){
                        e.setCancelled(false);
                    }
                }
            }
        }
    }

    private boolean canUseItem(Button b, UUID uuid, int cooldown) {
        if (this.time.containsKey(uuid)) {
            long current = System.currentTimeMillis();
            if (this.time.get(uuid).containsKey(b)) {
                return (Long)((HashMap<?, ?>)this.time.get(uuid)).get(b) + (long)cooldown <= current;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @EventHandler
    public void onInteractEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        FUser user = plugin.getClassManager().getPlayerManager().getUser(p);

        if (!plugin.getConfig().getBoolean("modules.items")){
            return;
        }

        if (!plugin.getUtils().isWorldProtected(p.getWorld(), Modules.ITEMS)){
            return;
        }

        if (plugin.getConfig().getBoolean("settings.perWorld") && plugin.getSpawn() != null){
            if (!plugin.getSpawn().getWorld().equals(p.getWorld())){
                return;
            }
        }

        ItemStack item = e.getCurrentItem();
        if (item == null) {
            return;
        }
        if (p.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        for (Button b : user.getItemsManager().getItems().values()) {
            if (b.getItem().build(p).isSimilar(item)) {
                if (b.canInteract()){
                    e.setCancelled(false);
                    continue;
                }
                e.setCancelled(true);
            }
        }
        if (p.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e){
        Entity proj = e.getEntity();
        if (proj instanceof Arrow){
            if (((Arrow) proj).getShooter() instanceof Player){
                Player p = ((Player) ((Arrow) proj).getShooter()).getPlayer();
                if (p == null){
                    return;
                }
                ItemStack bow = p.getItemInHand();
                if (bow == null){
                    return;
                }
                for (Button b : plugin.getClassManager().getPlayerManager().getUser(p).getItemsManager().getItems().values()){
                    if (b.canInteract()){
                        if (b.hasMetaData() && b.getMetaData().equalsIgnoreCase("tpbow")){
                            proj.setCustomName("tpbow");
                            task((Arrow) proj);
                        }
                    }
                }
            }
        }
    }

    public void task(Arrow arrow){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isDead()){
                    cancel();
                    return;
                }
                if (arrow.isOnGround()){
                    cancel();
                    return;
                }
                if (arrow.getCustomName() == null){
                    return;
                }
                if (!arrow.getCustomName().equalsIgnoreCase("tpbow")){
                    return;
                }
                Location loc = arrow.getLocation();
                loc.getWorld().playEffect(loc, Effect.HAPPY_VILLAGER, 1);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e){
        Entity proj = e.getEntity();
        if (proj instanceof Arrow){
            Arrow arrow = (Arrow) proj;
            if (arrow.getShooter() instanceof Player){
                Player p = ((Player) ((Arrow) proj).getShooter()).getPlayer();
                if (p == null){
                    return;
                }
                if (proj.getCustomName()!=null && proj.getCustomName().equals("tpbow")){
                    Location loc = proj.getLocation();
                    loc.setYaw(p.getLocation().getYaw());
                    loc.setPitch(p.getLocation().getPitch());
                    p.teleport(loc);
                    proj.remove();
                }
            }
        }
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent e){

        if (!plugin.getConfig().getBoolean("modules.items")){
            return;
        }

        if (!plugin.getUtils().isWorldProtected(e.getPlayer().getWorld(), Modules.ITEMS)){
            return;
        }
        if (plugin.getConfig().getBoolean("settings.perWorld") && plugin.getSpawn() != null){
            if (!plugin.getSpawn().getWorld().equals(e.getPlayer().getWorld())){
                return;
            }
        }
        if (e.getNewGameMode() != GameMode.CREATIVE) {
            FUser player = plugin.getClassManager().getPlayerManager().getUser(e.getPlayer());
            player.getItemsManager().reloadItems();
        }
    }

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent e) {


        if (!plugin.getUtils().isWorldProtected(e.getPlayer().getWorld(), Modules.LOBBY)){
            return;
        }

        if (!plugin.getConfig().getBoolean("modules.items")){
            return;
        }
        if (plugin.getConfig().getBoolean("settings.perWorld") && plugin.getSpawn() != null){
            if (!plugin.getSpawn().getWorld().equals(e.getPlayer().getWorld())){
                return;
            }
        }

        FUser user = plugin.getClassManager().getPlayerManager().getUser(e.getPlayer());

        ItemStack item = e.getItem().getItemStack();
        if (item == null) {
            return;
        }
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        for (Button b : user.getItemsManager().getItems().values()) {
            if (b.getItem().build(e.getPlayer()).isSimilar(item)) {
                e.setCancelled(true);
            }
        }
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if (!plugin.getConfig().getBoolean("modules.items")){
            return;
        }
        if (!plugin.getUtils().isWorldProtected(e.getPlayer().getWorld(), Modules.LOBBY)){
            return;
        }
        if (plugin.getConfig().getBoolean("settings.perWorld") && plugin.getSpawn() != null){
            if (!plugin.getSpawn().getWorld().equals(e.getPlayer().getWorld())){
                return;
            }
        }
        FUser user = plugin.getClassManager().getPlayerManager().getUser(e.getPlayer());

        ItemStack item = e.getItemDrop().getItemStack();
        if (item == null) {
            return;
        }
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        for (Button b : user.getItemsManager().getItems().values()) {
            if (b.getItem().build(e.getPlayer()).isSimilar(item)) {
                e.setCancelled(true);
            }
        }
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        e.setCancelled(true);
    }
}
