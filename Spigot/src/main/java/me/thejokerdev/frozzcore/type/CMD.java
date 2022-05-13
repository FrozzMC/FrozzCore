package me.thejokerdev.frozzcore.type;

import lombok.Getter;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.SenderType;
import org.bukkit.command.CommandSender;

import java.util.List;

@Getter
public abstract class CMD {

    private SpigotMain plugin;

    public CMD(SpigotMain plugin) {
        this.plugin = plugin;
    }

    public abstract String getName();
    public abstract SenderType getSenderType();
    public abstract String getPermission();
    public abstract String getHelp();

    public abstract boolean onCMD(CommandSender sender, String alias, String[] args);

    public abstract List<String> onTab(CommandSender sender, String alias, String[] args);



}
