package me.thejokerdev.frozzcore.type;

import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.events.PlayerChangeLangEvent;
import me.thejokerdev.frozzcore.enums.VisibilityType;
import me.thejokerdev.frozzcore.managers.ItemsManager;
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
    private ItemsManager itemsManager;

    private VisibilityType visibilityType = VisibilityType.ALL;

    public FUser(Player p){
        this(p.getName(), p.getUniqueId());
    }

    public FUser(String var1, UUID var2){
        this.name = var1;
        this.uniqueID = var2;
        SpigotMain.getPlugin().getClassManager().getDataManager().getData().getData(this);
    }

    public void initItems(){
        itemsManager = new ItemsManager(this);
        itemsManager.check();
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

    public String getMSG(String configKey){
        configKey = configKey.replace("key:", "");
        String str = SpigotMain.getPlugin().getClassManager().getLangManager().getLanguageOfSection("general", lang).getFile().getString(configKey);
        if (str ==null){
            return configKey;
        }
        str = PlaceholderAPI.setPlaceholders(getPlayer(), str);
        return SpigotMain.getPlugin().getClassManager().getUtils().getMessage(str);
    }
}
