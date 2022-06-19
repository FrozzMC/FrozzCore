package me.thejokerdev.frozzcore.api.visual;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text {
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("\\{#(([\\dA-Fa-f]){6}|([\\dA-Fa-f]){3})}");
    private static final Pattern HEX_GRADIENT_COLOR_PATTERN = Pattern.compile("(\\{(#[^{]*?)>})(.*?)(\\{(#.*?)<(>?)})");
    private static final Pattern STRIP_CHAT_COLOR_PATTERN = Pattern.compile("(§[\\dA-Fa-fK-ORk-or]|&[\\dA-Fa-fK-ORk-or])");

    public Text() {
    }

    public static String color(String var0) {
        return chatColor(gradientColor(var0));
    }

    public static String stripColor(String var0) {
        return stripHexColor(stripChatColor(var0));
    }

    public static String stripChatColor(String var0) {
        return var0.replaceAll(STRIP_CHAT_COLOR_PATTERN.pattern(), "");
    }

    public static String stripHexColor(String var0) {
        return var0.replaceAll(HEX_COLOR_PATTERN.pattern(), "");
    }

    public static String chatColor(String var0) {
        Preconditions.checkArgument(var0 != null, "Text must be defined.");
        char[] var1 = var0.toCharArray();

        for(int var2 = 0; var2 < var1.length - 1; ++var2) {
            if (var1[var2] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(var1[var2 + 1]) > -1) {
                var1[var2] = 167;
                var1[var2 + 1] = Character.toLowerCase(var1[var2 + 1]);
            }
        }

        return new String(var1);
    }

    public static String gradientColor(String var0) {
        Matcher var1 = HEX_GRADIENT_COLOR_PATTERN.matcher(var0);

        while(true) {
            String var2;
            Color var3;
            Color var4;
            do {
                do {
                    if (!var1.find()) {
                        return hexColor(chatColor(var0));
                    }

                    var2 = var1.group();
                    var3 = Color.from(var1.group(2).replace("#", "").replace("<", "").replace(">", ""));
                    var4 = Color.from(var1.group(5).replace("#", "").replace("<", "").replace(">", ""));
                } while(!var3.isValid());
            } while(!var4.isValid());

            String var5 = var1.group(3);
            boolean var6 = !var1.group(6).isEmpty();
            StringBuilder var7 = new StringBuilder();
            Set<String> var8 = ColorCalculations.getFormats(var5);
            var5 = stripColor(var5);

            for(int var9 = 0; var9 < var5.length(); ++var9) {
                char var10 = var5.charAt(var9);
                int var11 = var5.length();
                var11 = Math.max(var11, 2);
                double var12 = (double)var9 * 100.0 / (double)(var11 - 1);
                Color var14 = ColorCalculations.getColorInBetweenPercent(var3, var4, var12);
                var7.append("{#").append(var14.getColorCode()).append("}");
                if (!var8.isEmpty()) {

                    for (String var16 : var8) {
                        var7.append("§").append(var16);
                    }
                }

                var7.append(var10);
            }

            if (var6) {
                var7.append("{#").append(var1.group(5).replace("#", "")).append(">").append("}");
            }

            var0 = var0.replace(var2, var7.toString());
            if (var6) {
                var0 = gradientColor(var0);
            }
        }
    }

    public static String hexColor(String var0) {
        Preconditions.checkArgument(var0 != null, "Text must be defined.");

        String var2;
        String var3;
        for(Matcher var1 = HEX_COLOR_PATTERN.matcher(var0); var1.find(); var0 = var0.replace(var2, Color.from(var3).getAppliedTag())) {
            var2 = var1.group();
            var3 = var2.replace("{#", "").replace("}", "");
        }

        return var0;
    }

    public static String gradient(String var0, boolean var1, Color... var2) {
        Preconditions.checkArgument(var2.length > 1, "Define 2 or more colors.");
        int var3 = Math.max(1, var0.length() / var2.length);
        List<Color> var4 = new ArrayList<>();
        int var5 = 0;
        Color[] var6 = var2;
        int var7 = var2.length;

        int var8;
        for(var8 = 0; var8 < var7; ++var8) {
            Color var9 = var6[var8];
            Color var10 = var2.length == var5 + 1 ? null : var2[var5 + 1];
            if (var10 != null) {
                var4.addAll(ColorCalculations.getColorsInBetween(var9, var10, var3));
            }

            ++var5;
        }

        StringBuilder var12 = new StringBuilder();
        var5 = 0;
        char[] var13 = var0.toCharArray();
        var8 = var13.length;

        for(int var14 = 0; var14 < var8; ++var14) {
            char var15 = var13[var14];
            Color var11 = var4.size() <= var5 ? var4.get(var4.size() - 1) : var4.get(var5);
            var12.append(var11.getAppliedTag()).append((var1 ? "§l" : "") + var15);
            ++var5;
        }

        return var12.toString();
    }

    public static String firstUpperCase(String var0) {
        return var0.substring(0, 1).toUpperCase() + var0.substring(1).toLowerCase();
    }
}
