package me.thejokerdev.frozzcore.managers;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.utils.FileUtils;
import me.thejokerdev.frozzcore.api.utils.Utils;
import me.thejokerdev.frozzcore.enums.ItemType;
import me.thejokerdev.frozzcore.type.Button;
import me.thejokerdev.frozzcore.type.FUser;
import me.thejokerdev.frozzcore.type.Menu;
import me.thejokerdev.frozzcore.type.SimpleItem;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


@Getter
public class ItemsManager {

    private final SpigotMain plugin = SpigotMain.getPlugin();
    private HashMap<String, Button> items;

    private FileUtils file;

    private FUser player;

    public ItemsManager(FUser user) {
        this.player = user;
    }

    public void check(){
        if (plugin.getConfig().getBoolean("modules.items")){
            loadItems();
        }
    }

    public void reloadItems(){
        loadItems();
        for (Menu menu : plugin.getClassManager().getMenusManager().getPlayerMenus(player.getPlayer()).values()){
            try {
                menu.updateLang();
            } catch (ConcurrentModificationException e) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        menu.updateLang();
                    }
                }.runTaskLaterAsynchronously(plugin, 15L);
            }
        }
        setItems();
    }

    public void loadItems(){

        if (!plugin.getConfig().getBoolean("modules.items")){
            return;
        }
        items = new HashMap<>();

        file = plugin.getClassManager().getUtils().getFile("items.yml");

        if (file.getKeys(false).size() == 0){
            return;
        }

        for (String id : file.getKeys(false)){
            items.put(id, new Button(player, file, id, ItemType.ITEMS, null));
        }
    }

    public void setItems(){
        if (!plugin.getConfig().getBoolean("modules.items")){
            return;
        }
        Player p = getPlayer().getPlayer();
        p.getInventory().clear();
        for (Button button : items.values()){
            for (int i : button.getSlot()){
                if (!button.canView()){
                    continue;
                }
                p.getInventory().setItem(i, button.getItem().build(player.getPlayer()));
            }
        }
        p.updateInventory();
    }

    public SimpleItem createItem(Player player, ConfigurationSection section, HashMap<String, String> placeholders){
        int int1 = 0;
        int int2 = 0;
        int int3 = 0;
        boolean hasMeta = section.get("meta")!=null;
        boolean hasName = section.get("meta.name")!=null;
        boolean hasAmount = section.get("amount")!=null;
        boolean hasLore = section.get("meta.lore")!=null;
        boolean hasSkullData = section.get("skull")!= null;
        boolean isGlow = section.get("glow")!= null;
        boolean hideFlags = section.get("hideFlags")!= null;
        boolean hasFireWork = section.get("firework")!= null;
        boolean hasPotion = section.get("potion")!= null;
        boolean hasColor = section.get("color")!= null;
        boolean hasMaterial = section.get("material")!= null;
        boolean hasData = section.get("data")!= null;
        boolean hasPlaceholder = section.get("placeholders")!= null;
        boolean hasMetaData = section.get("metadata")!=null;
        boolean isUnbreakable = section.get("unbreakable")!=null;

        SimpleItem item = new SimpleItem(XMaterial.BARRIER);

        if (hasMaterial){
            XMaterial material = XMaterial.valueOf(section.getString("material").toUpperCase());
            item.setMaterial(material);
            item.setDurability(material.getData());
        }
        if (hasData){
            int data = section.getInt("data");
            item.setDurability(data);
        }
        if (placeholders != null && !placeholders.isEmpty()){
            placeholders.forEach(item::addPlaceholder);
        }
        if (hasPlaceholder){
            List<String> pl = section.getStringList("placeholders");
            for (String s : pl){
                if (s.contains(",")){
                    String[] s2 = s.split(",");
                    item.addPlaceholder(s2[0], s2[1]);
                }
            }
        }
        if (isUnbreakable){
            item.setUnbreakable(section.getBoolean("unbreakable"));
        }
        if (hasMeta){
            if (hasName){
                String name = section.getString("meta.name");
                item.setDisplayName(Utils.ct(name));
            }
            if (hasLore){
                List<String> lore = section.getStringList("meta.lore");
                if (section.getString("meta.lore").contains("\n")){
                    lore.addAll(Arrays.asList(section.getString("meta.lore").split("\n")));
                }
                item.setLore(Utils.ct(lore));
            }
        }
        if (hasMetaData){
            item.setMetaData(section.getString("metadata"));
            if (item.getMetaData().equals("tpbow")){
                item.addEnchantment(XEnchantment.ARROW_INFINITE, 1);
            }
        }
        if (hasAmount){
            item.setAmount(section.getInt("amount"));
        }
        if (hasSkullData){
            if (section.get("skull").getClass().getName().equalsIgnoreCase("java.util.ArrayList")){
                List<String> skull = section.getStringList("skull");
                item.setSkinsTexture(skull);
                item.setSkin(skull.get(0));
            } else {
                String skull = section.getString("skull");
                item.setSkin(skull);
            }

        }
        if (isGlow){
            item.setGlowing(section.getBoolean("glow"));
        }
        if (hideFlags){
            item.setShowAttributes(!section.getBoolean("hideFlags"));
        }
        if (hasFireWork){
            String color;
            String[] var1;
            ItemMeta meta = item.build(player).getItemMeta();
            FireworkEffectMeta metaFw = (FireworkEffectMeta) meta;
            color = section.getString("firework");
            Color color1;
            var1 = color.split("-");
            if (var1.length == 3) {
                int1 = plugin.getClassManager().getUtils().isNumeric(var1[0]) ? Integer.parseInt(var1[0]) : 0;
                int2 = plugin.getClassManager().getUtils().isNumeric(var1[1]) ? Integer.parseInt(var1[1]) : 0;
                int3 = plugin.getClassManager().getUtils().isNumeric(var1[2]) ? Integer.parseInt(var1[2]) : 0;
            }
            color1 = Color.fromRGB(int1, int2, int3);
            FireworkEffect effect = FireworkEffect.builder().withColor(color1).build();
            metaFw.setEffect(effect);
            item.setFireworkEffectMeta(metaFw);
        }
        if (hasColor){
            String color = section.getString("color");
            String[] var1 =color.split("-");
            item.setColor(getColor(var1));
        }
        if (hasPotion){
            String id = section.getString("potion.id").toUpperCase();
            PotionEffectType potEffect = XPotion.valueOf(id).getPotionEffectType();
            int multiplier = section.getInt("potion.multiplier")-1;
            int time = section.getInt("potion.time")*20;
            boolean showParticles = section.getBoolean("potion.showParticles");
            boolean ambient = section.getBoolean("potion.ambient");
            PotionMeta potionMeta = (PotionMeta)item.getMeta();
            potionMeta.addCustomEffect(new PotionEffect(potEffect, time, multiplier, ambient, showParticles), true);
            item.setMeta(potionMeta);
        }
        return item;
    }

    public Color getColor(String[] var1a) {
        int int1;
        int int2;
        int int3;
        Color color1;
        if (var1a.length == 3) {
            int1 = plugin.getClassManager().getUtils().isNumeric(var1a[0]) ? Integer.parseInt(var1a[0]) : 0;
            int2 = plugin.getClassManager().getUtils().isNumeric(var1a[1]) ? Integer.parseInt(var1a[1]) : 0;
            int3 = plugin.getClassManager().getUtils().isNumeric(var1a[2]) ? Integer.parseInt(var1a[2]) : 0;
        } else {
            int1 = 0;
            int2 = 0;
            int3 = 0;
        }
        color1 = Color.fromRGB(int1, int2, int3);
        return color1;
    }

    public static ItemStack setPlaceHolders(ItemStack item, Player p) {
        if (item.getType() == Material.AIR){
            return item;
        }
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(PlaceholderAPI.setPlaceholders(p, meta.getDisplayName()));
        List<String> lore;
        if (meta.hasLore()) {
            lore = new ArrayList<>();
            for (String s : meta.getLore()) {
                lore.add(PlaceholderAPI.setPlaceholders(p, s));
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    public static SimpleItem setPlaceHolders(SimpleItem item, Player p) {
        SimpleItem simpleItem = item.clone();
        if (simpleItem.hasDisplayName()){
            simpleItem.setDisplayName(PlaceholderAPI.setPlaceholders(p, simpleItem.getDisplayName()));
        }
        if (simpleItem.hasLore()) {
            List<String> lore;
            if (simpleItem.hasLore()) {
                lore = new ArrayList<>();
                for (String s : simpleItem.getLore()) {
                    lore.add(PlaceholderAPI.setPlaceholders(p, s));
                }
                simpleItem.setLore(lore);
            }
        }
        return simpleItem;
    }
}
