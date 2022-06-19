package me.thejokerdev.frozzcore.api.hooks;

import me.thejokerdev.frozzcore.SpigotMain;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.group.GroupCreateEvent;
import net.luckperms.api.event.group.GroupDeleteEvent;
import net.luckperms.api.event.group.GroupLoadEvent;
import net.luckperms.api.model.group.Group;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class LP {
    private SpigotMain plugin;
    private LuckPerms luckperms;

    public LP(SpigotMain plugin) {
        this.plugin = plugin;
        luckperms = LuckPermsProvider.get();
        EventBus bus = luckperms.getEventBus();
        bus.subscribe(plugin, GroupLoadEvent.class, this::onGroupsLoad);
        bus.subscribe(plugin, GroupDeleteEvent.class, this::onGroupDelete);
        bus.subscribe(plugin, GroupCreateEvent.class, this::onGroupCreate);
    }

    public List<String> getGroups(){
        return luckperms.getGroupManager().getLoadedGroups().stream().map(Group::getName).collect(Collectors.toList());
    }

    public String getPrefix(String group, Player p){
        String prefix = luckperms.getUserManager().getUser(p.getUniqueId()).getCachedData().getMetaData().getPrefix();
        return prefix == null ? " " : prefix;
    }

    public int getWeight(String group){
        return luckperms.getGroupManager().getGroup(group).getWeight().getAsInt();
    }

    private void onGroupsLoad(GroupLoadEvent event){
        if (!plugin.haveLP()){
            return;
        }
        plugin.getClassManager().getNametagManager().addNameTag(event.getGroup().getName(), event.getGroup().getWeight().getAsInt());
    }

    private void onGroupCreate(GroupCreateEvent event){
        if (!plugin.haveLP()){
            return;
        }
        plugin.getClassManager().getNametagManager().addNameTag(event.getGroup().getName(), event.getGroup().getWeight().getAsInt());
    }

    private void onGroupDelete(GroupDeleteEvent event){
        if (!plugin.haveLP()){
            return;
        }
        plugin.getClassManager().getNametagManager().removeTag(event.getGroupName());
    }

    public String getGroup(Player p){
        return luckperms.getUserManager().getUser(p.getUniqueId()).getPrimaryGroup();
    }


}
