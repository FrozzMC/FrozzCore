package me.thejokerdev.frozzcore.api.packets;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PacketWrapper {
    public String error;
    private final Object packet = PacketAccessor.createPacket();
    private static Constructor<?> ChatComponentText;
    private static Class<? extends Enum> typeEnumChatFormat;

    public PacketWrapper(String name, int param, List<String> members) {
        if (param != 3 && param != 4) {
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        } else {
            this.setupDefaults(name, param);
            this.setupMembers(members);
        }
    }

    public PacketWrapper(String name, String prefix, String suffix, int param, Collection<?> players) {
        this.setupDefaults(name, param);
        if (param == 0 || param == 2) {
            try {
                if (PacketAccessor.isLegacyVersion()) {
                    PacketAccessor.DISPLAY_NAME.set(this.packet, name);
                    PacketAccessor.PREFIX.set(this.packet, prefix);
                    PacketAccessor.SUFFIX.set(this.packet, suffix);
                } else {
                    String color = ChatColor.getLastColors(prefix);
                    String colorCode = null;
                    if (!color.isEmpty()) {
                        colorCode = color.substring(color.length() - 1);
                        String chatColor = ChatColor.getByChar(colorCode).name();
                        if (chatColor.equalsIgnoreCase("MAGIC")) {
                            chatColor = "OBFUSCATED";
                        }

                        Enum<?> colorEnum = Enum.valueOf(typeEnumChatFormat, chatColor);
                        PacketAccessor.TEAM_COLOR.set(this.packet, colorEnum);
                    }

                    PacketAccessor.DISPLAY_NAME.set(this.packet, ChatComponentText.newInstance(name));
                    PacketAccessor.PREFIX.set(this.packet, ChatComponentText.newInstance(prefix));
                    if (colorCode != null) {
                        suffix = ChatColor.getByChar(colorCode) + suffix;
                    }

                    PacketAccessor.SUFFIX.set(this.packet, ChatComponentText.newInstance(suffix));
                }

                PacketAccessor.PACK_OPTION.set(this.packet, 1);
                if (PacketAccessor.VISIBILITY != null) {
                    PacketAccessor.VISIBILITY.set(this.packet, "always");
                }

                if (param == 0) {
                    ((Collection)PacketAccessor.MEMBERS.get(this.packet)).addAll(players);
                }
            } catch (Exception var10) {
                this.error = var10.getMessage();
            }
        }

    }

    private void setupMembers(Collection<?> players) {
        try {
            players = players != null && !players.isEmpty() ? players : new ArrayList<>();
            ((Collection)PacketAccessor.MEMBERS.get(this.packet)).addAll(players);
        } catch (Exception var3) {
            this.error = var3.getMessage();
        }

    }

    private void setupDefaults(String name, int param) {
        try {
            PacketAccessor.TEAM_NAME.set(this.packet, name);
            PacketAccessor.PARAM_INT.set(this.packet, param);
        } catch (Exception var4) {
            this.error = var4.getMessage();
        }

    }

    public void send() {
        PacketAccessor.sendPacket(Bukkit.getOnlinePlayers(), this.packet);
    }

    public void send(Player player) {
        PacketAccessor.sendPacket(player, this.packet);
    }

    static {
        try {
            if (!PacketAccessor.isLegacyVersion()) {
                String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                Class<?> typeChatComponentText = Class.forName("net.minecraft.server." + version + ".ChatComponentText");
                ChatComponentText = typeChatComponentText.getConstructor(String.class);
                typeEnumChatFormat = (Class<? extends Enum>) Class.forName("net.minecraft.server." + version + ".EnumChatFormat");
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }
}
