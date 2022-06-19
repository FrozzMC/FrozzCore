package me.thejokerdev.frozzcore.api.hooks;

import me.thejokerdev.frozzcore.SpigotMain;
import net.skinsrestorer.api.SkinsRestorerAPI;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SR {
    private SpigotMain plugin;

    public SR(SpigotMain plugin) {
        this.plugin = plugin;
    }

    public String getSkin(UUID uuid){
        return SkinsRestorerAPI.getApi().getProfile(uuid.toString()).getValue();
    }

}
