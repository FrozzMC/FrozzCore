package me.thejokerdev.frozzcore.type;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.utils.FileUtils;
import me.thejokerdev.frozzcore.managers.ItemsManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Getter
public class Button {
    private final SimpleItem item;
    private final List<Integer> slot;
    private final FUser player;
    private final ConfigurationSection section;
    private final FileUtils file;
    private int cooldown = 0;

    private boolean update = false;

    public Button(FUser player, FileUtils file, String section){
        this.file = file;
        this.player = player;
        this.item = player.getItemsManager().createItem(player.getPlayer(), file.getSection(section), null);
        this.slot = getSlotFromString(file.getSection(section).getString("slot"));
        this.section = file.getSection(section);
        if (this.section.get("cooldown")!=null){
            this.cooldown = this.section.getInt("cooldown");
        }
        if (this.section.get("update")!=null){
            this.update = this.section.getBoolean("update");
        }
    }

    private List<Integer> getSlotFromString(String var1){
        if (var1 == null){
            return new ArrayList<>(Collections.singletonList(0));
        }
        boolean isOne = !var1.contains(",") && !var1.contains("-");
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
                for (int i = Integer.parseInt(var2[0]); i <= Integer.parseInt(var2[1]); i++){
                    slots.add(i);
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

    public void executePhysicallyItemsActions(PlayerInteractEvent e){
        e.setCancelled(true);
        if (section.get("actions")==null){
            return;
        }
        List<String> leftClick = section.getStringList("actions.leftclick");
        List<String> rightClick = section.getStringList("actions.rightclick");
        List<String> shiftClick = section.getStringList("actions.shiftclick");
        List<String> all = section.getStringList("actions.multiclick");

        if (!leftClick.isEmpty() && e.getAction().name().contains("LEFT")){
            SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), leftClick);
        }
        if (!rightClick.isEmpty() && e.getAction().name().contains("RIGHT")){
            SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), rightClick);
        }
        if (!shiftClick.isEmpty() && e.getPlayer().isSneaking()){
            SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), shiftClick);
        }
        if (!all.isEmpty()){
            SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), all);
        }
        if (update){
            for (int i : getSlot()){
                player.getPlayer().getInventory().setItem(i, XMaterial.AIR.parseItem());
                player.getPlayer().getInventory().setItem(i, getItem().build(player.getPlayer()));
            }
            player.getPlayer().updateInventory();
        }
    }

    public void executeItemInMenuActions(InventoryClickEvent e){
        if (section.get("actions")==null){
            return;
        }
        List<String> leftClick = section.getStringList("actions.leftclick");
        List<String> rightClick = section.getStringList("actions.rightclick");
        List<String> middleClick = section.getStringList("actions.middleclick");
        List<String> shiftClick = section.getStringList("actions.shiftclick");
        List<String> all = section.getStringList("actions.multiclick");

        if (!leftClick.isEmpty() && e.getClick() == ClickType.LEFT){
            SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), leftClick);
        }
        if (!rightClick.isEmpty() && e.getClick() == ClickType.RIGHT){
            SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), rightClick);
        }
        if (!middleClick.isEmpty() && e.getClick() == ClickType.MIDDLE){
            SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), middleClick);
        }
        if (!shiftClick.isEmpty() && e.getClick().name().contains("SHIFT")){
            SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), shiftClick);
        }
        if (!all.isEmpty()){
            SpigotMain.getPlugin().getClassManager().getUtils().actions(getPlayer().getPlayer(), all);
        }
    }
}
