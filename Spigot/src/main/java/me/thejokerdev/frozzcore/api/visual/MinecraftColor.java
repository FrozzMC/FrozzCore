//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.thejokerdev.frozzcore.api.visual;

public enum MinecraftColor {
    DARK_RED("&4", "#AA0000"),
    RED("&c", "#FF5555"),
    GOLD("&6", "#FFAA00"),
    YELLOW("&e", "#FFFF55"),
    DARK_GREEN("&2", "#00AA00"),
    GREEN("&a", "#55FF55"),
    AQUA("&b", "#55FFFF"),
    DARK_AQUA("&3", "#00AAAA"),
    DARK_BLUE("&1", "#0000AA"),
    BLUE("&9", "#5555FF"),
    LIGHT_PURPLE("&d", "#FF55FF"),
    DARK_PURPLE("&5", "#AA00AA"),
    WHITE("&f", "#FFFFFF"),
    GRAY("&7", "#AAAAAA"),
    DARK_GRAY("&8", "#555555"),
    BLACK("&0", "#000000");

    private final String chatColor;
    private final Color color;

    public static MinecraftColor getClosest(Color var0) {
        MinecraftColor var1 = null;
        int var2 = 0;
        MinecraftColor[] var3 = values();

        for (MinecraftColor var6 : var3) {
            int var7 = Color.difference(var0, var6.getColor());
            if (var1 == null || var2 > var7) {
                var2 = var7;
                var1 = var6;
            }
        }

        return var1;
    }

    private MinecraftColor(String var3, String var4) {
        this.chatColor = var3;
        this.color = Color.from(var4);
    }

    public String getName() {
        return this.name().toLowerCase();
    }

    public Color getColor() {
        return this.color;
    }

    public String getTag() {
        return this.chatColor;
    }

    public String getAppliedTag() {
        return this.chatColor.replace("&", "§");
    }

    public String toString() {
        return this.getAppliedTag();
    }
}
