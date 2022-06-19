package me.thejokerdev.frozzcore.listeners;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.type.Button;
import me.thejokerdev.frozzcore.type.FUser;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

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

        if (!plugin.getConfig().getBoolean("modules.items")){
            return;
        }
        if (plugin.getConfig().getBoolean("settings.perWorld") && plugin.getSpawn() != null){
            if (!plugin.getSpawn().getWorld().equals(p.getWorld())){
                return;
            }
        }

        ItemStack item = e.getItem();
        if (item == null) {
            return;
        }
        if (p.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        for (Button b : user.getItemsManager().getItems().values()) {
            if (b.getItem().build(p).isSimilar(item)) {
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
                        return;
                    } else {
                        String msg = plugin.getClassManager().getUtils().getLangMSG(p, "items.cooldown");
                        double timeleft = Math.round((float) (((Long) ((HashMap<?, ?>) this.time.get(p.getUniqueId())).get(b) + (b.getCooldown() * 1000L) - System.currentTimeMillis()) / 1000L * 100L));
                        plugin.getClassManager().getUtils().sendMessage(p, msg.replaceAll("<time>", String.valueOf((int)(timeleft / 100.0D))));
                    }
                } else {
                    b.executePhysicallyItemsActions(e);
                }
            }
        }
        if (p.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        e.setCancelled(true);
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
                e.setCancelled(true);
            }
        }
        if (p.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent e){

        if (!plugin.getConfig().getBoolean("modules.items")){
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
