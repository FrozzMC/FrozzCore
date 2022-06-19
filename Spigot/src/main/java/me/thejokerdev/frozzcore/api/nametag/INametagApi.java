package me.thejokerdev.frozzcore.api.nametag;

import me.thejokerdev.frozzcore.api.data.NameTag;
import org.bukkit.entity.Player;

public interface INametagApi {

    NameTag getNametag(Player var1);

    void clearNametag(Player var1);

    void reloadNametag(Player var1);

    void clearNametag(String var1);

    void setPrefix(Player var1, String var2);

    void setSuffix(Player var1, String var2);

    void setPrefix(String var1, String var2);

    void setSuffix(String var1, String var2);

    void setNametag(Player var1, String var2, String var3);

    void setNametag(String var1, String var2, String var3);

    void applyTags();

    void applyTagToPlayer(Player var1, boolean var2);
}
