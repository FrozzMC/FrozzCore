package me.thejokerdev.frozzcore.type;

import lombok.Getter;
import lombok.Setter;
import me.thejokerdev.frozzcore.SpigotMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class CustomCMD implements CommandExecutor, TabCompleter {
    private SpigotMain plugin;
    private String name;
    private List<String> aliases = new ArrayList<>();
    private List<String> actions = new ArrayList<>();
    private int cooldown = 0;
    private String permission = "none";

    private String permissionError = "";

    private String description;

    private boolean tabComplete;

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

    public void addAliases(String... alias){
        aliases.addAll(Arrays.asList(alias));
    }

    public CustomCMD(SpigotMain plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    public void register(){
        plugin.getClassManager().getCmdManager().registerCommand(this);
    }
}
