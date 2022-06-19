package me.thejokerdev.frozzcore.api.visual;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Animation {
    private static final HashMap<String, List<String>> animationCache = new HashMap();

    public Animation() {
    }

    public static String wave(String var0, Color... var1) {
        return wave(var0, true, 5, 10, var1);
    }

    public static String wave(String var0, boolean var1, int var2, int var3, Color... var4) {
        Preconditions.checkArgument(var4.length > 1, "Not enough colors provided");
        String var5 = "wave-" + var0 + "-" + var1 + "-" + var2 + "-" + var3 + "-" + (String)Arrays.stream(var4).map(Color::getColorCode).collect(Collectors.joining("-"));
        if (animationCache.containsKey(var5)) {
            return currentFrame((List)animationCache.get(var5));
        } else {
            ArrayList var6 = new ArrayList();
            int var7 = 0;
            Color[] var8 = var4;
            int var9 = var4.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                Color var11 = var8[var10];
                Color var12 = var4[var4.length == var7 + 1 ? 0 : var7 + 1];
                var6.addAll(Collections.nCopies(var2, var11.getAppliedTag() + (var1 ? "§l" : "") + var0));
                ArrayList var13 = new ArrayList();
                var13.addAll(Collections.nCopies(var0.length(), var11.getAppliedTag()));
                var13.addAll((Collection)ColorCalculations.getColorsInBetween(var11, var12, var3).stream().map(Color::getAppliedTag).collect(Collectors.toList()));
                var13.addAll(Collections.nCopies(var0.length(), var12.getAppliedTag()));

                for(int var14 = 0; var14 <= var13.size() - var0.length(); ++var14) {
                    StringBuilder var15 = new StringBuilder();
                    int var16 = 0;
                    char[] var17 = var0.toCharArray();
                    int var18 = var17.length;

                    for(int var19 = 0; var19 < var18; ++var19) {
                        char var20 = var17[var19];
                        String var21 = (String)var13.get(var16 + var14);
                        var15.append(var21).append(var1 ? "§l" : "").append(var20);
                        ++var16;
                    }

                    var6.add(var15.toString());
                }

                var6.addAll(Collections.nCopies(var2, var12.getAppliedTag() + (var1 ? "§l" : "") + var0));
                ++var7;
            }

            animationCache.put(var5, var6);
            return currentFrame(var6);
        }
    }

    public static String fading(String var0, Color... var1) {
        return fading(var0, true, 10, 20, var1);
    }

    public static String fading(String var0, boolean var1, int var2, int var3, Color... var4) {
        Preconditions.checkArgument(var4.length > 1, "Not enough colors provided");
        String var5 = "fading-" + var0 + "-" + var1 + "-" + var2 + "-" + var3 + "-" + (String)Arrays.stream(var4).map(Color::getColorCode).collect(Collectors.joining("-"));
        if (animationCache.containsKey(var5)) {
            return currentFrame((List)animationCache.get(var5));
        } else {
            ArrayList var6 = new ArrayList();
            int var7 = 0;
            Color[] var8 = var4;
            int var9 = var4.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                Color var11 = var8[var10];
                Color var12 = var4[var4.length == var7 + 1 ? 0 : var7 + 1];
                var6.addAll(Collections.nCopies(var2, var11.getAppliedTag() + (var1 ? "§l" : "") + var0));
                Iterator var13 = ColorCalculations.getColorsInBetween(var11, var12, var3).iterator();

                while(var13.hasNext()) {
                    Color var14 = (Color)var13.next();
                    var6.add(var14.getAppliedTag() + (var1 ? "§l" : "") + var0);
                }

                ++var7;
            }

            animationCache.put(var5, var6);
            return currentFrame(var6);
        }
    }

    private static String currentFrame(List<String> var0) {
        long var1 = System.currentTimeMillis() / 50L;
        int var3 = (int)(var1 % (long)var0.size());
        return (String)var0.get(var3);
    }
}
