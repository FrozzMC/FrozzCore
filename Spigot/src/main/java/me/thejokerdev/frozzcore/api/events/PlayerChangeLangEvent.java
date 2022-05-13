package me.thejokerdev.frozzcore.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerChangeLangEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final String lastLang;
    private final String newLang;

    public PlayerChangeLangEvent(Player var1, String var2, String var3){
        player = var1;
        lastLang = var2;
        newLang = var3;
    }

    public Player getPlayer() {
        return player;
    }

    public String getLastLang() {
        return lastLang;
    }

    public String getNewLang() {
        return newLang;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
