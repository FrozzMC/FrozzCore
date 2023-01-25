package me.thejokerdev.frozzcore.api.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import me.clip.placeholderapi.PlaceholderAPI;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.misc.DefaultFontInfo;
import me.thejokerdev.frozzcore.enums.ModifierStatus;
import me.thejokerdev.frozzcore.enums.Modules;
import me.thejokerdev.frozzcore.enums.VisibilityType;
import me.thejokerdev.frozzcore.type.FUser;
import me.thejokerdev.frozzcore.type.Menu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    private final SpigotMain plugin;

    public Utils(SpigotMain plugin) {
        this.plugin = plugin;
    }

    /* String utils*/
    public static String ct(String msg){
        if (MinecraftVersion.getServersVersion().isAboveOrEqual(MinecraftVersion.V1_16_R1)){
            return ChatColor.translateAlternateColorCodes('&',msg);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    public static List<String> ct(List<String> msg){
        return msg.stream().map(Utils::ct).collect(Collectors.toList());
    }
    public static String getCenteredMSG(String message){
        assert message != null;
        message = ct(message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
            }else if(previousCode){
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = 154 - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message;
    }

    public void sendMessage(String... msg){
        sendMessage(Bukkit.getConsoleSender(), msg);
    }
    public void sendMessage(List<String> msg){
        sendMessage(Bukkit.getConsoleSender(), msg);
    }
    public String getMessage(String msg){
        msg = ct(msg);
        boolean hasPrefix = msg.contains("{prefix}");
        boolean hasCenter = msg.contains("{center}");

        if (msg.equals("")){
            msg = " ";
        }

        if (hasPrefix){
            msg = msg.replace("{prefix}", PlaceholderAPI.setPlaceholders(null, plugin.getPrefix()));
        }
        if (hasCenter){
            msg = getCenteredMSG(msg.replace("{center}", ""));
        }
        return msg;
    }

    public String formatMSG(Player p, String in){
        return getMessage(PlaceholderAPI.setPlaceholders(p, in));
    }
    public List<String> getList(List<String> list){
        return list.stream().map(this::getMessage).collect(Collectors.toList());
    }

    public String getLangMSG(CommandSender sender, String key){
        String section = "general";
        String lang = plugin.getClassManager().getLangManager().getDefault();
        if (key.contains("@")){
            section = key.split("@")[0];
            key = key.split("@")[1];
        }
        if (sender instanceof Player){
            FUser user = plugin.getClassManager().getPlayerManager().getUser((Player)sender);
            lang = user.getLang();
        }
        return plugin.getClassManager().getLangManager().getLanguageOfSection(section, lang).getFile().getString(key);
    }

    public boolean isNumeric(String var0) {
        try {
            Integer.parseInt(var0);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public void sendMessage(CommandSender sender, String msg){
        sendMessage(sender, msg, true);
    }
    public void sendMessage(CommandSender sender, String msg, boolean haveKey){
        boolean isBroadcast = msg.contains("{broadcast}");

        if (!msg.contains(" ") && haveKey){
            msg = getLangMSG(sender, msg);
        }

        if (msg.contains("\\n")){
            String[] split = msg.split("\\n");
            sendMessage(sender, split);
            return;
        }

        if (msg.contains("\n")){
            String[] split = msg.split("\n");
            sendMessage(sender, split);
            return;
        }

        msg = sender instanceof Player ? PlaceholderAPI.setPlaceholders((Player)sender, msg) : PlaceholderAPI.setPlaceholders(null, msg);

        msg = getMessage(msg);

        if (isBroadcast){
            String finalMsg = msg.replace("{broadcast}", "");
            Bukkit.getConsoleSender().sendMessage(finalMsg);
            Bukkit.getOnlinePlayers().forEach(p->p.sendMessage(finalMsg));
            return;
        }
        if (sender instanceof Player){

            sender.sendMessage(msg);
        } else {
            Bukkit.getConsoleSender().sendMessage(msg);
        }
    }
    public void sendMessage(CommandSender sender, String... msg){
        Arrays.stream(msg).forEach(m->sendMessage(sender, m, false));
    }
    public void sendMessage(CommandSender sender, List<String> msg){
        msg.forEach(m->sendMessage(sender, m));
    }

    public String applyPlaceholders(String str, HashMap<String, String> hashMap){
        for (Map.Entry<String, String> entry : hashMap.entrySet()){
            str = str.replace(entry.getKey(), entry.getValue());
        }
        return str;
    }
    public List<String> applyPlaceholders(List<String> str, HashMap<String, String> hashMap){
        return str.stream().map(l -> applyPlaceholders(l, hashMap)).collect(Collectors.toList());
    }

    public String getMSG(String key){
        return getMessage(plugin.getClassManager().getLangManager().getLanguageOfSection("general", plugin.getClassManager().getLangManager().getDefault()).getFile().getString(key));
    }

    public List<String> getList(String key){
        return getList(plugin.getClassManager().getLangManager().getLanguageOfSection("general", plugin.getClassManager().getLangManager().getDefault()).getFile().getStringList(key));
    }

    private BukkitTask task = null;
    public void startTab(boolean reload){
        if (reload){
            task.cancel();
            task = null;
        }
        if (task == null){
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!plugin.getConfig().getBoolean("modules.tab")){
                        return;
                    }
                    String header = plugin.getConfig().getString("tab.header");
                    String footer = plugin.getConfig().getString("tab.footer");

                    for (Player p : Bukkit.getOnlinePlayers()){
                        World w = plugin.getSpawn().getWorld();
                        if (w == null){
                            w = p.getWorld();
                        }
                        if (!isWorldProtected(w, Modules.TAB)){
                            continue;
                        }
                        header = getMessage(header);
                        header = PlaceholderAPI.setPlaceholders(p, header);
                        footer = getMessage(footer);
                        footer = PlaceholderAPI.setPlaceholders(p, footer);
                        Titles.sendTabList(header, footer, p);
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 0L, plugin.getConfig().getLong("tab.update"));
        }
    }

    public void sendPlayer(Player player, String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
            b.close();
            out.close();
        }
        catch (Exception ignored) {
        }
    }

    public void playAudio(Player p, String id){
        XSound sound;
        String[] split = id.split(",");
        try {
            sound = XSound.valueOf(split[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            return;
        }
        float volume = Float.parseFloat(split[1]);
        float pitch = Float.parseFloat(split[2]);
        sound.play(p, volume, pitch);
    }

    public boolean actions(Player p, List<String> list){
        for (String s : list){
            s = PlaceholderAPI.setPlaceholders(p, s);
            if (s.startsWith("[close]")){
                p.closeInventory();
                continue;
            }
            if (s.startsWith("[sound]")){
                playAudio(p, s.replace("[sound]", ""));
                continue;
            }
            if (s.startsWith("[server]")){
                sendPlayer(p, s.replace("[server]", ""));
                continue;
            }
            if (s.startsWith("[cmd]")){
                s = s.replace("[cmd]", "");
                p.chat("/"+s);
            }
            if (s.startsWith("[cmd=OP]")){
                s = s.replace("[cmd=OP]", "");
                Bukkit.dispatchCommand(p, s);
            }
            if (s.startsWith("[cmd=Console]")){
                s = s.replace("[cmd=Console]", "");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
            }
            if (s.startsWith("[msg]")){
                s = s.replace("[msg]", "");
                s = formatMSG(p, s);
                p.sendMessage(s);
            }
            if (s.startsWith("[open]")){
                s = s.replace("[open]", "");
                for (Menu menu : plugin.getClassManager().getMenusManager().getPlayerMenus(p).values()){
                    plugin.debug(menu.getMenuId()+" is loaded for "+p.getName());
                }
                Menu menu = plugin.getClassManager().getMenusManager().getPlayerMenu(p, s);
                if (menu == null){
                    plugin.getClassManager().getMenusManager().loadMenus(p);
                    sendMessage(p, "menus.not-exist");
                    return true;
                }
                p.openInventory(menu.getInventory());
            }
            if (s.startsWith("[title]")){
                s = s.replace("[title]", "");
                String[] split = s.split("`");
                if (split.length <= 2){
                    Titles.sendTitle(p, formatMSG(p, split[0]), formatMSG(p, split[1]));
                }
                if (split.length == 5){
                    Titles.sendTitle(p, Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), formatMSG(p, split[0]), formatMSG(p, split[1]));
                }
            }
            FUser user = plugin.getClassManager().getPlayerManager().getUser(p);
            if (s.startsWith("[action]")){
                s = s.replace("[action]", "");
                if (s.equalsIgnoreCase("visibility")){
                    changeVisibility(p);
                    user.saveData(false);
                }
                if (s.equalsIgnoreCase("return")){
                    return false;
                }
                if (s.equalsIgnoreCase("jump")){
                    user.setJump(user.getJump() == ModifierStatus.OFF ? ModifierStatus.ON : ModifierStatus.OFF);
                    user.saveData(false);
                }
                if (s.equalsIgnoreCase("disableJump")){
                    user.setJump(ModifierStatus.DEACTIVATED);
                    user.saveData(false);
                }
                if (s.equalsIgnoreCase("doublejump")){
                    user.setDoubleJump(user.getDoubleJump() == ModifierStatus.OFF ? ModifierStatus.ON : ModifierStatus.OFF);
                    user.saveData(false);
                }
                if (s.equalsIgnoreCase("disableDoubleJump")){
                    user.setDoubleJump(ModifierStatus.DEACTIVATED);
                    user.saveData(false);
                }
                if (s.equalsIgnoreCase("fly")){
                    user.setAllowFlight(user.getAllowFlight() == ModifierStatus.OFF ? ModifierStatus.ON : ModifierStatus.OFF);
                    user.saveData(false);
                }
                if (s.equalsIgnoreCase("disableFly")){
                    user.setAllowFlight(ModifierStatus.DEACTIVATED);
                    user.saveData(false);
                }
                if (s.equalsIgnoreCase("disableSpeed")){
                    user.setSpeed(ModifierStatus.DEACTIVATED);
                    user.saveData(false);
                }
                if (s.equalsIgnoreCase("enderbutt")){
                    if (p.getVehicle() != null && p.getVehicle() instanceof EnderPearl){
                        EnderPearl pearl = (EnderPearl) p.getVehicle();
                        pearl.remove();
                    }
                    EnderPearl pearl = p.launchProjectile(EnderPearl.class);
                    pearl.setPassenger(p);
                    actions(p, Arrays.asList("[sound]ENTITY_ENDERMAN_TELEPORT,1.0,1.0"));
                    setupEnderpearlRunnable(pearl);
                }
            }
            if (s.startsWith("[speed]")){
                s = s.replace("[speed]",  "");
                user.setSpeed(user.getSpeed() == ModifierStatus.OFF ? ModifierStatus.ON : ModifierStatus.OFF);
                user.saveData(false);
            }
        }
        return true;
    }

    public void setupEnderpearlRunnable(final EnderPearl item) {
        (new BukkitRunnable() {
            public void run() {
                if (item.isDead()){
                    cancel();
                } else {
                    item.getLocation().getWorld().playEffect(item.getLocation(), Effect.HAPPY_VILLAGER, 0);
                }
                if (item.getVelocity().getX() == 0.0D || item.getVelocity().getY() == 0.0D || item.getVelocity().getZ() == 0.0D) {
                    Player player = (Player)item.getPassenger();
                    item.remove();
                    if (player != null)
                        player.teleport(player.getLocation().add(0.0D, 0.5D, 0.0D));
                    cancel();
                }
            }
        }).runTaskTimer(plugin, 2L, 1L);
    }

    public FileUtils getWorlds(){
        File file = new File(plugin.getDataFolder(), "worlds.yml");
        if (!file.exists()){
            plugin.saveResource("worlds.yml", false);
        }
        return new FileUtils(file);
    }

    public boolean isWorldProtected(World w, Modules module){
        if (getWorlds().getString("settings.global").contains(module.name().toLowerCase())){
            return true;
        }
        boolean mode = getWorlds().getString("settings.mode").equalsIgnoreCase("whitelist") || getWorlds().getString("settings.mode").equalsIgnoreCase("wl");
        if (w == null){
            return false;
        }
        String str = "worlds."+w.getName().toLowerCase();
        if (getWorlds().get(str)==null && mode){
            return false;
        } else if (!mode && getWorlds().get(str)!=null){
            return false;
        }

        if (getWorlds().get(str+"."+module.name().toLowerCase()) == null){
            return false;
        }
        return getWorlds().getBoolean(str+"."+module.name().toLowerCase());
    }

    public void changeVisibility(Player p) {
        FUser user = plugin.getClassManager().getPlayerManager().getUser(p);
        VisibilityType type = user.getVisibilityType();

        if (type == VisibilityType.ALL) {
            type = VisibilityType.RANKS;
        } else if (type == VisibilityType.RANKS) {
            type = VisibilityType.NOBODY;
        } else if (type == VisibilityType.NOBODY) {
            type = VisibilityType.ALL;
        }
        user.setVisibilityType(type);

        plugin.getClassManager().getLoginListener().checkVisibility(p);
    }

    public FileUtils getFile(String file){
        File file1 = new File(plugin.getDataFolder(), file);

        if (!file1.exists()){
            plugin.saveResource(file, false);
        }

        return new FileUtils(file1);
    }

    /* ---- Boolean Utils ---- */
    public boolean compareItems(ItemStack item1, ItemStack item2) {
        boolean bool = false;
        if (item1 != null &&item2 != null && item1.getType() != XMaterial.AIR.parseMaterial()) {
            if (item1.getType() == item2.getType() && item1.getAmount() == item2.getAmount()) {
                if (item1.hasItemMeta() && item1.getItemMeta().hasDisplayName()) {
                    if (item1.getItemMeta().getDisplayName().equalsIgnoreCase(item2.getItemMeta().getDisplayName())) {
                        bool = true;
                    }
                }
            }
        }
        return bool;
    }
}
