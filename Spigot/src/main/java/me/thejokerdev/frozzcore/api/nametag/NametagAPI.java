package me.thejokerdev.frozzcore.api.nametag;

import me.thejokerdev.frozzcore.api.data.NameTag;
import me.thejokerdev.frozzcore.api.events.NametagEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;

public final class NametagAPI implements INametagApi {
    private NametagHandler handler;
    private NametagManager manager;

    public NameTag getNametag(Player player) {
        return this.manager.getNameTag(player.getName());
    }

    public void clearNametag(Player player) {
        if (this.shouldFireEvent(player, NametagEvent.ChangeType.CLEAR)) {
            this.manager.reset(player.getName());
        }

    }

    public void reloadNametag(Player player) {
        if (this.shouldFireEvent(player, NametagEvent.ChangeType.RELOAD)) {
            this.handler.applyTagToPlayer(player, false);
        }

    }

    public void clearNametag(String player) {
        this.manager.reset(player);
    }

    public void setPrefix(Player player, String prefix) {
        NameTag nameTag = this.manager.getNameTag(player.getName());
        this.setNametag(player, prefix, nameTag == null ? null : nameTag.getSuffix());
    }

    public void setSuffix(Player player, String suffix) {
        NameTag nameTag = this.manager.getNameTag(player.getName());
        this.setNametag(player, nameTag == null ? null : nameTag.getPrefix(), suffix);
    }

    public void setPrefix(String player, String prefix) {
        NameTag nameTag = this.manager.getNameTag(player);
        this.manager.setNametag(player, player, prefix, nameTag == null ? null : nameTag.getSuffix());
    }

    public void setSuffix(String player, String suffix) {
        NameTag nameTag = this.manager.getNameTag(player);
        this.manager.setNametag(player, player, nameTag == null ? null : nameTag.getPrefix(), suffix);
    }

    public void setNametag(Player player, String prefix, String suffix) {
        this.manager.setNametag(player.getName(), player.getName(), prefix, suffix);
    }

    @Override
    public void setNametag(String var1, String var2, String var3) {
        this.manager.setNametag(var1, var1, var2, var3);
    }

    public void applyTags() {
        this.handler.applyTags();
    }

    public void applyTagToPlayer(Player player, boolean loggedIn) {
        this.handler.applyTagToPlayer(player, loggedIn);
    }

    private boolean shouldFireEvent(Player player, NametagEvent.ChangeType type) {
        NametagEvent event = new NametagEvent(player.getName(), "", this.getNametag(player), type);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
    }

    @ConstructorProperties({"handler", "manager"})
    public NametagAPI(NametagHandler handler, NametagManager manager) {
        this.handler = handler;
        this.manager = manager;
    }
}
