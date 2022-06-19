//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.thejokerdev.frozzcore.api.utils;

import java.io.File;
import org.bukkit.Bukkit;

public enum MinecraftVersion {
    V1_8_R1,
    V1_8_R2,
    V1_8_R3,
    V1_9_R1,
    V1_9_R2,
    V1_10_R1,
    V1_11_R1,
    V1_12_R1,
    V1_13_R1,
    V1_13_R2,
    V1_14_R1,
    V1_15_R1,
    V1_16_R1,
    V1_16_R2,
    V1_16_R3,
    V1_17_R1,
    V1_18_R1,
    V1_18_R2,
    UNKNOWN;

    private static MinecraftVersion currentVersion;

    private MinecraftVersion() {
    }

    public boolean isAboveOrEqual(MinecraftVersion var1) {
        return this.ordinal() >= var1.ordinal();
    }

    public boolean isBelow(MinecraftVersion var1) {
        return this.ordinal() < var1.ordinal();
    }

    public static MinecraftVersion getServersVersion() {
        return currentVersion;
    }

    static {
        try {
            File var0 = new File("server.properties");
            File var1 = new File("bukkit.yml");
            if (var0.exists() && var1.exists()) {
                currentVersion = valueOf(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].toUpperCase());
            } else {
                currentVersion = UNKNOWN;
            }
        } catch (Exception var2) {
            currentVersion = UNKNOWN;
        }

    }
}
