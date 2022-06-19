package me.thejokerdev.frozzcore.api.visual;

import java.util.ArrayList;
import java.util.List;

public class LoreScroller {
    public LoreScroller() {
    }

    public static List<String> scroller(List<String> var0, int var1, long var2) {
        if (var0.size() <= var1) {
            return var0;
        } else {
            List<String> var4 = new ArrayList<>();
            var4.addAll(var0);
            var4.addAll(var0);
            var4.addAll(var0);
            long var5 = System.currentTimeMillis() - var2;
            int var7 = Math.round((float)(var5 / 150L)) % var0.size();
            return var4.subList(var7, var7 + var1);
        }
    }
}
