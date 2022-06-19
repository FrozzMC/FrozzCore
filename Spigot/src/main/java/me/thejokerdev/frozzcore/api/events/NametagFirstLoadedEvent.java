package me.thejokerdev.frozzcore.api.events;

import me.thejokerdev.frozzcore.api.data.NameTag;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.beans.ConstructorProperties;

public class NametagFirstLoadedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private Player player;
    private NameTag nametag;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public NameTag getNametag() {
        return this.nametag;
    }

    @ConstructorProperties({"player", "nametag"})
    public NametagFirstLoadedEvent(Player player, NameTag nametag) {
        this.player = player;
        this.nametag = nametag;
    }
}
