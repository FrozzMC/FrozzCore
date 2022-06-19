package me.thejokerdev.frozzcore.api.events;

import me.thejokerdev.frozzcore.api.data.NameTag;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NametagEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;
    /** @deprecated */
    @Deprecated
    private String value;
    private NameTag nametag;
    private String player;
    private ChangeType changeType;
    private ChangeReason changeReason;
    private StorageType storageType;

    public NametagEvent(String player, String value) {
        this(player, value, ChangeType.UNKNOWN);
    }

    public NametagEvent(String player, String value, NameTag nametag, ChangeType type) {
        this(player, value, type);
        this.nametag = nametag;
    }

    public NametagEvent(String player, String value, ChangeType changeType) {
        this(player, value, changeType, StorageType.MEMORY, ChangeReason.UNKNOWN);
    }

    public NametagEvent(String player, String value, ChangeType changeType, ChangeReason changeReason) {
        this(player, value, changeType, StorageType.MEMORY, changeReason);
    }

    public NametagEvent(String player, String value, ChangeType changeType, StorageType storageType, ChangeReason changeReason) {
        this.player = player;
        this.value = value;
        this.changeType = changeType;
        this.storageType = storageType;
        this.changeReason = changeReason;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /** @deprecated */
    @Deprecated
    public String getValue() {
        return this.value;
    }

    /** @deprecated */
    @Deprecated
    public void setValue(String value) {
        this.value = value;
    }

    public NameTag getNametag() {
        return this.nametag;
    }

    public void setNametag(NameTag nametag) {
        this.nametag = nametag;
    }

    public String getPlayer() {
        return this.player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public ChangeType getChangeType() {
        return this.changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public ChangeReason getChangeReason() {
        return this.changeReason;
    }

    public void setChangeReason(ChangeReason changeReason) {
        this.changeReason = changeReason;
    }

    public StorageType getStorageType() {
        return this.storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public static enum StorageType {
        MEMORY,
        PERSISTENT;

        private StorageType() {
        }
    }

    public static enum ChangeType {
        PREFIX,
        SUFFIX,
        GROUP,
        CLEAR,
        PREFIX_AND_SUFFIX,
        RELOAD,
        UNKNOWN;

        private ChangeType() {
        }
    }

    public static enum ChangeReason {
        API,
        PLUGIN,
        UNKNOWN;

        private ChangeReason() {
        }
    }
}
