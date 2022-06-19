package me.thejokerdev.frozzcore.api.visual;

import me.thejokerdev.frozzcore.api.utils.MinecraftVersion;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Color {
    private static final String HEX_PATTERN = "(#(([0-9A-Fa-f]){6}|([0-9A-Fa-f]){3}))|(([0-9A-Fa-f]){6}|([0-9A-Fa-f]){3})";
    private String colorCode;
    private final int r;
    private final int g;
    private final int b;
    private final boolean valid;

    public static Color from(String var0) {
        return new Color(var0);
    }

    public static Color from(int var0, int var1, int var2) {
        java.awt.Color var3 = new java.awt.Color(var0, var1, var2);
        return from(Integer.toHexString(var3.getRGB()).substring(2));
    }

    private Color(String var1) {
        if (!var1.matches("(#(([0-9A-Fa-f]){6}|([0-9A-Fa-f]){3}))|(([0-9A-Fa-f]){6}|([0-9A-Fa-f]){3})")) {
            this.valid = false;
            this.r = this.g = this.b = 0;
        } else {
            this.valid = true;
            this.colorCode = var1.replace("#", "");
            if (this.colorCode.length() == 3) {
                String[] var2 = this.colorCode.split("");
                this.colorCode = var2[0] + var2[0] + var2[1] + var2[1] + var2[2] + var2[2];
            }

            java.awt.Color var3 = new java.awt.Color(Integer.parseInt(this.colorCode, 16));
            this.r = var3.getRed();
            this.g = var3.getGreen();
            this.b = var3.getBlue();
        }

    }

    public String getTag() {
        return "{#" + this.colorCode + "}";
    }

    public String getColorCode() {
        return this.colorCode;
    }

    public int getRed() {
        return this.r;
    }

    public int getGreen() {
        return this.g;
    }

    public int getBlue() {
        return this.b;
    }

    public boolean isValid() {
        return this.valid;
    }

    public String getAppliedTag() {
        return this.isValid() ? (hasHexSupport() ? "§x" + (String)Arrays.stream(this.colorCode.split("")).map((var0) -> {
            return "§" + var0;
        }).collect(Collectors.joining()) : MinecraftColor.getClosest(this).getAppliedTag()) : "";
    }

    public String getColorTag() {
        return "{#" + this.colorCode + "}";
    }

    public static int difference(Color var0, Color var1) {
        return Math.abs(var0.r - var1.r) + Math.abs(var0.g - var1.g) + Math.abs(var0.b - var1.b);
    }

    public static boolean hasHexSupport() {
        return MinecraftVersion.getServersVersion().isAboveOrEqual(MinecraftVersion.V1_16_R1);
    }

    public String toString() {
        return this.getAppliedTag();
    }
}
