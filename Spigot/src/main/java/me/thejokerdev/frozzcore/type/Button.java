package me.thejokerdev.frozzcore.type;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.utils.FileUtils;
import me.thejokerdev.frozzcore.enums.ItemRequirements;
import me.thejokerdev.frozzcore.enums.ItemType;
import me.thejokerdev.frozzcore.managers.ItemsManager;
import me.thejokerdev.frozzcore.managers.MenusManager;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import sun.applet.Main;

import javax.swing.plaf.SplitPaneUI;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Button {
    private final SpigotMain plugin = SpigotMain.getPlugin();
    private SimpleItem item;
    private final List<Integer> slot;
    private final FUser player;
    private final ConfigurationSection section;
    private final FileUtils file;
    private int cooldown = 0;

    private boolean update = false;
    private List<String> updateItems = new ArrayList<>();
    private ItemType type;

    public Button(FUser player, FileUtils file, String section, ItemType type, String menuId){
        if (menuId == null){
            menuId = "";
        }
        this.file = file;
        this.player = player;
        this.type = type;
        item = plugin.getItemsCache().getItem(type, menuId+section);

        if (item == null){
            item = player.getItemsManager().createItem(player.getPlayer(), file.getSection(section), null);
            plugin.getItemsCache().addItem(type,menuId+section, item);
        }
        this.slot = getSlotFromString(file.getSection(section).getString("slot"));
        this.section = file.getSection(section);
        if (this.section.get("cooldown")!=null){
            this.cooldown = this.section.getInt("cooldown");
        }
        if (this.section.get("update")!=null){
            this.update = this.section.getBoolean("update");
            this.updateItems = new ArrayList<>(this.section.getStringList("update-items"));
        }
    }

    public boolean hasMetaData(){
        return getItem().getMetaData()!=null;
    }

    public String getMetaData(){
        return getItem().getMetaData();
    }

    public boolean hasRequirements(){
        return section.getConfigurationSection("requirements")!=null;
    }

    public boolean canInteract(){
        return section.getBoolean("interact", false);
    }

    public boolean canView(){
        if (!hasRequirements()){
            return true;
        }
        List<String> keys = new ArrayList<>(section.getConfigurationSection("requirements").getKeys(false));
        List<ConfigurationSection> sections = new ArrayList<>();
        if (!keys.contains("type")){
            for (String key : keys){
                sections.add(section.getConfigurationSection("requirements."+key));
            }
        } else {
            sections.add(section.getConfigurationSection("requirements"));
        }
        Player p = player.getPlayer();
        boolean bol = false;

        for (ConfigurationSection sec : sections){
            ItemRequirements type = ItemRequirements.valueOf(sec.getString("type").toUpperCase());
            String syntax = PlaceholderAPI.setPlaceholders(player.getPlayer(), sec.getString("syntax"));
            String value = sec.getString("value");

            switch (type){
                case INT_GREATER_THAN:{
                    bol = Integer.parseInt(syntax)>=Integer.parseInt(value);
                    break;
                }
                case INT_LESS_THAN:{
                    bol = Integer.parseInt(syntax)<=Integer.parseInt(value);
                    break;
                }
                case INT_EQUALS_TO:{
                    bol = Integer.parseInt(syntax)==Integer.parseInt(value);
                    break;
                }
                case INT_NOT_EQUALS_TO:{
                    bol = Integer.parseInt(syntax)!=Integer.parseInt(value);
                    break;
                }
                case STRING:{
                    bol = syntax.equalsIgnoreCase(value);
                    break;
                }
                case STRING_NOT:{
                    bol = !syntax.equalsIgnoreCase(value);
                    break;
                }
                case BOOLEAN:{
                    bol = Boolean.parseBoolean(syntax);
                    break;
                }
                case BOOLEAN_NOT:{
                    bol = !Boolean.parseBoolean(syntax);
                    break;
                }
                case PERMISSION:{
                    bol = p.hasPermission(syntax);
                    break;
                }
                case PERMISSION_NOT:{
                    bol = !p.hasPermission(syntax);
                    break;
                }
            }
            if (!bol){
                break;
            }
        }
        return bol;
    }

    private List<Integer> getSlotFromString(String var1){
        if (var1 == null){
            return new ArrayList<>(Collections.singletonList(0));
        }
        boolean isOne = !var1.contains(",") && (var1.startsWith("-1") || !var1.contains("-"));
        List<Integer> slots = new ArrayList<>();
        if (isOne){
            try {
                int i = Integer.parseInt(var1);
                slots.add(i);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            String[] var2 = new String[0];
            if (var1.contains(",")){
                var2 = var1.split(",");
            } else if (var1.contains("-")){
                var2 = var1.split("-");
                if (var2.length == 2){
                    int start = Integer.parseInt(var2[0]);
                    int end = Integer.parseInt(var2[1]);
                    for (int i = start; i <= end; i++){
                        slots.add(i);
                    }
                }
                return slots;
            }
            for (String s : var2){
                slots.addAll(getSlotFromString(s));
            }
        }
        return slots;
    }

    public Button(FUser player, FileUtils file, String section, HashMap<String, String> pl){
        this.player = player;
        this.file = file;
        this.item = player.getItemsManager().createItem(player.getPlayer(), file.getSection(section), pl);
        this.slot = getSlotFromString(file.getSection(section).getString("slot"));
        this.section = file.getSection(section);
        if (this.section.get("cooldown")!=null){
            this.cooldown = this.section.getInt("cooldown");
        }
    }

    public SimpleItem getItem() {
        return ItemsManager.setPlaceHolders(item, player.getPlayer());
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean executePhysicallyItemsActions(PlayerInteractEvent e){
        e.setCancelled(true);
        boolean bol = true;
        if (section.get("actions")==null){
            return bol;
        }
        List<String> leftClick = section.getStringList("actions.leftclick");
        List<String> rightClick = section.getStringList("actions.rightclick");
        List<String> shiftClick = section.getStringList("actions.shiftclick");
        List<String> all = section.getStringList("actions.multiclick");

        if (!leftClick.isEmpty() && e.getAction().name().contains("LEFT")){
            bol = SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), leftClick);
        }
        if (!rightClick.isEmpty() && e.getAction().name().contains("RIGHT")){
            bol = SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), rightClick);
        }
        if (!shiftClick.isEmpty() && e.getPlayer().isSneaking()){
            bol = SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), shiftClick);
        }
        if (!all.isEmpty()){
            bol = SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), all);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (update){
                    int slot = e.getPlayer().getInventory().getHeldItemSlot();
                    for (Button b : player.getItemsManager().getItems().values()){
                        if (b.getSlot().contains(slot)){
                            for (int i : b.getSlot()){
                                if (!b.canView()){
                                    continue;
                                }
                                player.getPlayer().getInventory().setItem(i, b.getItem().build(player.getPlayer()));
                            }
                        }
                    }
                    for (String updateItem : updateItems){
                        Button b = player.getItemsManager().getItems().get(updateItem);
                        if (b==null){
                            continue;
                        }
                        for (int i : b.getSlot()){
                            if (!b.canView()){
                                continue;
                            }
                            player.getPlayer().getInventory().setItem(i, b.getItem().build(player.getPlayer()));
                        }
                    }
                    player.getPlayer().updateInventory();
                }
            }
        }.runTaskLater(SpigotMain.getPlugin(), 1L
        );
        return bol;
    }

    public boolean executeItemInMenuActions(InventoryClickEvent e){
        boolean bol = true;
        if (section.get("actions")==null){
            return bol;
        }
        List<String> leftClick = section.getStringList("actions.leftclick");
        List<String> rightClick = section.getStringList("actions.rightclick");
        List<String> middleClick = section.getStringList("actions.middleclick");
        List<String> shiftClick = section.getStringList("actions.shiftclick");
        List<String> all = section.getStringList("actions.multiclick");

        if (!leftClick.isEmpty() && e.getClick() == ClickType.LEFT){
            bol = SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), leftClick);
        }
        if (!rightClick.isEmpty() && e.getClick() == ClickType.RIGHT){
            bol = SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), rightClick);
        }
        if (!middleClick.isEmpty() && e.getClick() == ClickType.MIDDLE){
            bol = SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), middleClick);
        }
        if (!shiftClick.isEmpty() && e.getClick().name().contains("SHIFT")){
            bol = SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), shiftClick);
        }
        if (!all.isEmpty()){
            bol = SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), all);
        }
        if (update){
            for (Menu var3 : SpigotMain.getPlugin().getClassManager().getMenusManager().getPlayerMenus((Player) e.getWhoClicked()).values()) {
                if (e.getView().getTitle().equals(var3.getTitle()) && e.getCurrentItem() != null) {
                    e.setCancelled(true);
                    var3.update();
                }
            }
            for (String updateItem : updateItems){
                Button b = player.getItemsManager().getItems().get(updateItem);
                if (b==null){
                    continue;
                }
                for (int i : b.getSlot()){
                    if (!b.canView()){
                        continue;
                    }
                    player.getPlayer().getInventory().setItem(i, b.getItem().build(player.getPlayer()));
                }
            }
        }
        return bol;
    }
}
