package me.thejokerdev.frozzcore.api.utils;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.thejokerdev.frozzcore.SpigotMain;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkullUtils {
    public static Map<String, String> cached = new HashMap<>();

    public static String getHeadValue(String name){
        return getValue(name);
    }

    public static String getValue(String name){
        try {
            String result = getURLContent("https://api.mojang.com/users/profiles/minecraft/" + name);
            Gson g = new Gson();
            JsonObject obj = g.fromJson(result, JsonObject.class);
            String uid = obj.get("id").toString().replace("\"","");
            String signature = getURLContent("https://sessionserver.mojang.com/session/minecraft/profile/" + uid);
            obj = g.fromJson(signature, JsonObject.class);
            String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
            String decoded = new String(Base64.getDecoder().decode(value));
            obj = g.fromJson(decoded,JsonObject.class);
            String skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
            byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();
            return new String(Base64.getEncoder().encode(skinByte));
        } catch (Exception ignored){ }
        return null;
    }
    private static String getURLContent(String urlStr) {
        URL url;
        BufferedReader in = null;
        StringBuilder sb = new StringBuilder();
        try{
            url = new URL(urlStr);
            in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8) );
            String str;
            while((str = in.readLine()) != null) {
                sb.append( str );
            }
        } catch (Exception ignored) { }
        finally{
            try{
                if(in!=null) {
                    in.close();
                }
            }catch(IOException ignored) { }
        }
        return sb.toString();
    }

    @SuppressWarnings("deprecation")
    public static ItemStack getHead(OfflinePlayer player) {
        String skinURL = null;
        loadSkin: if (cached.containsKey(player.getName())) {
            skinURL = cached.get(player.getName());
        }else {
            try {
                skinURL = getHeadValue(player.getName());
            } catch (Exception ignored) {
            }
        }
        ItemStack head = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial(), 1, (short)3);
        if (skinURL == null && SpigotMain.getPlugin().haveSR()){
            skinURL = SpigotMain.getPlugin().getSr().getSkin(player.getUniqueId());
        }
        if (skinURL == null){
            return head;
        }
        cached.put(player.getName(), skinURL);
        return getHead(skinURL);
    }
    @SuppressWarnings("deprecation")
    public static ItemStack getHead(String skinURL) {
        ItemStack head = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial(), 1, (short)3);
        ItemMeta headMeta = head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", skinURL));
        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
    @SuppressWarnings("deprecation")
    public static ItemStack getHead(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        String skinURL = null;
        loadSkin: if (cached.containsKey(player.getName())) {
            skinURL = cached.get(player.getName());
        }else {
            try {
                skinURL = getHeadValue(player.getName());
            } catch (Exception ignored) {
            }
        }
        ItemStack head = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial(), 1, (short)3);
        if (skinURL == null){
            return head;
        }
        cached.put(player.getName(), skinURL);
        return getHead(skinURL);
    }
}
