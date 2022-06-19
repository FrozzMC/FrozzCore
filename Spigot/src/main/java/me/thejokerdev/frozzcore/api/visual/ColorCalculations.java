package me.thejokerdev.frozzcore.api.visual;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorCalculations {
    private static final Pattern FORMAT_PATTERN = Pattern.compile("(&[klmnorKLMNOR])");

    public ColorCalculations() {
    }

    public static List<Color> getColorsInBetween(Color var0, Color var1, int var2) {
        double var3 = (double)(var1.getRed() - var0.getRed()) / (double)var2;
        double var5 = (double)(var1.getGreen() - var0.getGreen()) / (double)var2;
        double var7 = (double)(var1.getBlue() - var0.getBlue()) / (double)var2;
        ArrayList var9 = new ArrayList();

        for(int var10 = 1; var10 <= var2; ++var10) {
            int var11 = (int)Math.round((double)var0.getRed() + var3 * (double)var10);
            int var12 = (int)Math.round((double)var0.getGreen() + var5 * (double)var10);
            int var13 = (int)Math.round((double)var0.getBlue() + var7 * (double)var10);
            Color var14 = Color.from(var11, var12, var13);
            var9.add(var14);
        }

        return var9;
    }

    public static Color getColorInBetweenPercent(Color var0, Color var1, double var2) {
        var2 /= 100.0;
        double var4 = 1.0 - var2;
        int var6 = (int)((double)var1.getRed() * var2 + (double)var0.getRed() * var4);
        int var7 = (int)((double)var1.getGreen() * var2 + (double)var0.getGreen() * var4);
        int var8 = (int)((double)var1.getBlue() * var2 + (double)var0.getBlue() * var4);
        return Color.from(var6, var7, var8);
    }

    public static Set<String> getFormats(String var0) {
        var0 = var0.replace("§", "&");
        HashSet var1 = new HashSet();
        Matcher var2 = FORMAT_PATTERN.matcher(var0);

        while(var2.find()) {
            var1.add(var2.group());
        }

        return var1;
    }
}
