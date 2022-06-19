package me.thejokerdev.frozzcore.api.nametag;

import lombok.Getter;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.data.NameTag;
import me.thejokerdev.frozzcore.api.events.NametagFirstLoadedEvent;
import me.thejokerdev.frozzcore.api.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;

@Getter
public class NametagHandler implements Listener {

    private SpigotMain plugin;

    public NametagHandler(SpigotMain plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        applyTagToPlayer(p, true);
        plugin.getClassManager().getNametagManager().sendTeams(p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();

        clear(p);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e){
        Player p = e.getPlayer();
        if (plugin.getConfig().getBoolean("settings.perWorld")){
            applyTagToPlayer(p, false);
        }
    }

    public NametagManager getManager(){
        return plugin.getClassManager().getNametagManager();
    }

    public NameTag getNameTag(Player p){
        LinkedList<NameTag> tags = getManager().getNameTags();
        NameTag tag = getManager().getDefault();

        if (tag == null){
            plugin.debug("Tag Default is NULL");
        }

        if (plugin.haveLP()){
            return plugin.getClassManager().getNametagManager().getTag(plugin.getLp().getGroup(p));
        } else {
            for (NameTag var1 : tags) {
                if (p.hasPermission(var1.getPerm())){
                    plugin.debug("Tag for "+ p.getName()+ " apply for "+ var1.getName());
                    tag = var1;
                }
            }
        }

        if (tag == null){
            plugin.debug("Tag is NULL");
        }

        return tag;
    }

    public void applyTags() {
        if (!Bukkit.isPrimaryThread()) {
            (new BukkitRunnable() {
                public void run() {
                    NametagHandler.this.applyTags();
                }
            }).runTask(this.plugin);
        } else {
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                if (online != null) {
                    this.applyTagToPlayer(online, false);
                    getManager().sendTeams(online);
                }
            }

            this.plugin.debug("Applied tags to all online players.");
        }
    }

    public void applyTagToPlayer(final Player player, final boolean loggedIn) {
        if (Bukkit.isPrimaryThread()) {
            (new BukkitRunnable() {
                public void run() {
                    NametagHandler.this.applyTagToPlayer(player, loggedIn);
                }
            }).runTaskAsynchronously(this.plugin);
        } else {
            NameTag tempNametag = getNameTag(player);

            if (tempNametag != null) {
                this.plugin.debug("Applying tag to " + player.getName());
                (new BukkitRunnable() {
                    public void run() {
                        plugin.getClassManager().getNametagManager().setNametag(player.getName(), tempNametag);
                        if (!plugin.getConfig().getBoolean("modules.nametags")) {
                            player.setPlayerListName(Utils.ct("&f" + player.getPlayerListName()));
                        } else if (plugin.getConfig().getBoolean("settings.longTags")) {
                            player.setPlayerListName(plugin.getClassManager().getUtils().formatMSG(player, tempNametag.getPrefix() + player.getName() + tempNametag.getSuffix()));
                        } else {
                            player.setPlayerListName(null);
                        }

                        if (loggedIn) {
                            Bukkit.getPluginManager().callEvent(new NametagFirstLoadedEvent(player, tempNametag));
                        }

                    }
                }).runTask(this.plugin);
            }
        }
    }

    public void clear(Player p) {
        getManager().reset(p.getName());
    }
}
