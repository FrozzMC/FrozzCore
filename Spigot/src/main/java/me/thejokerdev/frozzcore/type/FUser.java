package me.thejokerdev.frozzcore.type;

import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.events.PlayerChangeLangEvent;
import me.thejokerdev.frozzcore.enums.ModifierStatus;
import me.thejokerdev.frozzcore.enums.VisibilityType;
import me.thejokerdev.frozzcore.managers.ItemsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
    private ModifierStatus jump = ModifierStatus.OFF;
    private ModifierStatus doubleJump = ModifierStatus.OFF;
    private ModifierStatus allowFlight = ModifierStatus.OFF;
    private ModifierStatus speed = ModifierStatus.OFF;
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

    public void setHype(int hype) {
        if (hype != this.hype){
            saveData(false);
        }
        this.hype = hype;
    }

    public void setVisibilityType(VisibilityType visibilityType) {
        this.visibilityType = visibilityType;
    }

    public void setSpeed(ModifierStatus modifier) {
        int speed = 0;
        if (modifier == ModifierStatus.OFF || modifier == ModifierStatus.DEACTIVATED){
            speed = 2;
        } else {
            speed = 6;
        }
        getPlayer().setWalkSpeed(speed/10.0f);
        this.speed = modifier;
    }

    public void setDoubleJump(ModifierStatus modifier) {
        this.doubleJump = modifier;
        getPlayer().setAllowFlight(modifier == ModifierStatus.ON);
    }

    public String getLang() {
        return lang == null ? SpigotMain.getPlugin().getClassManager().getLangManager().getDefault() : lang;
    }

    public boolean isFirstJoin() {
        return firstJoin;
    }

    public ModifierStatus getAllowFlight() {
        return allowFlight;
    }

    public void setAllowFlight(ModifierStatus modifier) {
        this.allowFlight = modifier;
        if (getDoubleJump() == ModifierStatus.ON && modifier != ModifierStatus.OFF){
            setDoubleJump(ModifierStatus.OFF);
           return;
        }
        if (modifier == ModifierStatus.ON && !getPlayer().hasPermission("core.fly")){
            setAllowFlight(ModifierStatus.OFF);
            return;
        }
        boolean allow = modifier == ModifierStatus.ON;
        getPlayer().setAllowFlight(allow);
    }

    public void setLang(String lang, boolean isJoin, boolean bungee) {
        if (!lang.equals(this.lang) && lang!=null){
            saveData(false);
        }
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

    public void setJump(ModifierStatus modifier) {
        if (modifier != this.jump){
            saveData(false);
        }
        if (modifier == ModifierStatus.ON){
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP,Integer.MAX_VALUE, 1, true, false), true);
        } else {
            getPlayer().removePotionEffect(PotionEffectType.JUMP);
        }
        this.jump = modifier;
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

    @Override
    public String toString() {
        return "FUser{" +
                "name='" + name + '\'' +
                ", uniqueID=" + uniqueID +
                ", lang='" + lang + '\'' +
                ", firstJoin=" + firstJoin +
                ", hype=" + hype +
                ", jump=" + jump +
                ", doubleJump=" + doubleJump +
                ", allowFlight=" + allowFlight +
                ", speed=" + speed +
                ", itemsManager=" + itemsManager +
                ", visibilityType=" + visibilityType +
                '}';
    }
}
