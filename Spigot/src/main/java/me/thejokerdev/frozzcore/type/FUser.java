package me.thejokerdev.frozzcore.type;

import lombok.Getter;
import lombok.Setter;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.events.PlayerChangeLangEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@Getter
@Setter
public class FUser {
    private String name;
    private UUID uniqueID;

    private String lang;
    private boolean firstJoin;
    private int hype;

    public FUser(Player p){
        this(p.getName(), p.getUniqueId());
    }

    public FUser(String var1, UUID var2){
        this.name = var1;
        this.uniqueID = var2;
        SpigotMain.getPlugin().getClassManager().getDataManager().getData().getData(this);
    }

    public String getLang() {
        return lang == null ? SpigotMain.getPlugin().getClassManager().getLangManager().getDefault() : lang;
    }

    public boolean isFirstJoin() {
        return firstJoin;
    }

    public void setLang(String lang, boolean isJoin, boolean bungee) {
        this.lang = lang;
        if (!isJoin) {
            updateLang(lang);
        }
        if (bungee) {
            //Utils.sendUpdateRequest(this);
        }
    }

    public void updateLang(String lang){
        PlayerChangeLangEvent event = new PlayerChangeLangEvent(getPlayer(), getLang(), lang);
        Bukkit.getPluginManager().callEvent(event);
        saveData(true);
    }

    public void setFirstJoin(boolean firstJoin) {
        this.firstJoin = firstJoin;
    }

    public void saveData(boolean async){
        if (async){
            new BukkitRunnable() {
                @Override
                public void run() {
                    SpigotMain.getPlugin().getClassManager().getDataManager().getData().syncData(FUser.this);
                }
            }.runTaskAsynchronously(SpigotMain.getPlugin());
            return;

        }
        SpigotMain.getPlugin().getClassManager().getDataManager().getData().syncData(this);
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(uniqueID)==null ? Bukkit.getPlayer(getName()) : Bukkit.getPlayer(uniqueID);
    }

    public void saveForceData(){
        saveData(false);
    }
}
