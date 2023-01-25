package me.thejokerdev.frozzcore.api.cache;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.ItemType;
import me.thejokerdev.frozzcore.type.SimpleItem;

import java.util.concurrent.ConcurrentHashMap;

public class ItemsCache {
    private final SpigotMain plugin;
    private ConcurrentHashMap<String, SimpleItem> items;

    public ItemsCache(SpigotMain plugin){
        this.plugin = plugin;

        items = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, SimpleItem> getItems() {
        return items;
    }

    public void addItem(ItemType type, String name, SimpleItem item){
        items.put(type.name()+"-"+name, item);
    }

    public SimpleItem getItem(ItemType type, String name){
        return items.get(type.name()+"-"+name);
    }


}
