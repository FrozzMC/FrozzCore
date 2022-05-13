package me.thejokerdev.frozzcore.type;

import lombok.Getter;
import me.thejokerdev.frozzcore.SpigotMain;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CustomCMD {
    private SpigotMain plugin;
    private String name;
    private List<String> aliases = new ArrayList<>();
    private List<String> actions = new ArrayList<>();
    private int cooldown = 0;
    private String permission = "none";

    public CustomCMD(SpigotMain plugin, ConfigurationSection section){
        this.plugin = plugin;

        name = section.getName();
        if (section.get("aliases")!=null){
            aliases.addAll(section.getStringList("aliases"));
        }
        if (section.get("actions")!=null){
            actions.addAll(section.getStringList("actions"));
        }
        if (section.get("cool-down")!=null){
            cooldown = section.getInt("cool-down");
        }
        if (section.get("permission")!=null){
            permission = section.getString("permission");
        }
    }
}
