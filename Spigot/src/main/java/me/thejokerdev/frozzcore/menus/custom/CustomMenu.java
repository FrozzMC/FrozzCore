package me.thejokerdev.frozzcore.menus.custom;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.ItemType;
import me.thejokerdev.frozzcore.type.Button;
import me.thejokerdev.frozzcore.type.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CustomMenu extends Menu {

    public CustomMenu(SpigotMain plugin, Player player, String id){
        super(plugin, player, id, true);

        updateLang();
        update();
    }
    @Override
    public void onOpen(InventoryOpenEvent var1) {
        update();
        if (getConfig().get("settings.update")==null){
            return;
        }
        int delay = getConfig().getInt("settings.update");
        if (delay == -1){
            return;
        }
        if (task != null){
            task.cancel();
            task = null;
        }
        task = new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, delay);
    }

    @Override
    public void onClose(InventoryCloseEvent var1) {
        if (task != null){
            task.cancel();
            task = null;
        }
    }

    @Override
    public void onClick(InventoryClickEvent var1) {
        for (Button b : buttons){
            if ((b.getSlot().contains(-1) || b.getSlot().contains(var1.getSlot())) && plugin.getClassManager().getUtils().compareItems(var1.getCurrentItem(), b.getItem().build(getPlayer()))){
                if (!b.canView()){
                    continue;
                }
                if (!b.executeItemInMenuActions(var1)){
                    return;
                }
            }
        }
    }

    @Override
    public void update() {
        boolean clear = getConfig().getBoolean("settings.clear", false);
        if (clear){
            getInventory().clear();
        }
        for (Button b : buttons){
            if (!b.canView()){
                continue;
            }
            setItem(b);
        }
    }

    @Override
    public void updateLang() {
        setTitle(getConfig().getString("settings.title"));
        buttons.clear();
        if (getConfig().get("extra-items")!=null){
            for (String key : getConfig().getSection("extra-items").getKeys(false)){
                key = "extra-items."+key;
                buttons.add(new Button(plugin.getClassManager().getPlayerManager().getUser(getPlayer()), getConfig(), key, ItemType.MENU, getMenuId()));
            }
        }
    }
}
