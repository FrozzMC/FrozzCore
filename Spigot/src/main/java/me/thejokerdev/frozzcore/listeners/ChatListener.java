package me.thejokerdev.frozzcore.listeners;

import com.cryptomorin.xseries.XSound;
import me.clip.placeholderapi.PlaceholderAPI;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.utils.Utils;
import me.thejokerdev.frozzcore.enums.Modules;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.LinkedList;

public class ChatListener implements Listener {
    private SpigotMain plugin;

    public ChatListener(SpigotMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncChat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();

        if (e.isCancelled()){
            return;
        }

        if (!plugin.getUtils().isWorldProtected(p.getWorld(), Modules.CHAT)){
            return;
        }

        String prefix = plugin.getConfig().getString("chat.format.prefix");
        String name = plugin.getConfig().getString("chat.format.name");
        String suffix = plugin.getConfig().getString("chat.format.suffix");
        String message = plugin.getConfig().getString("chat.format.message");
        message = message.replace("{color}", getColor(p))+e.getMessage();

        String format = prefix+name+suffix+message;
        format = PlaceholderAPI.setPlaceholders(p, format);
        format = Utils.ct(format);

        e.setFormat(format);

        if (plugin.getConfig().getBoolean("settings.perWorld")){
            for (Player var5 : Bukkit.getServer().getOnlinePlayers()) {
                if (var5.getWorld() != p.getWorld()) {
                    e.getRecipients().remove(var5);
                }
            }
        }
    }

    public String getColor(Player p){
        LinkedList<String> list = new LinkedList<>(plugin.getConfig().getConfigurationSection("chat.colors").getKeys(false));
        String out = plugin.getConfig().getString("chat.colors.default");
        for (String perm : list){
            if (perm.equals("default") && !p.hasPermission("core.chatcolor.status")){
                continue;
            }
            if (p.hasPermission("core.chatcolor."+perm)){
                out = plugin.getConfig().getString("chat.colors."+perm);
            }
        }
        return out;
    }
}
