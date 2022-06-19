package me.thejokerdev.frozzcore.api.board;

import me.thejokerdev.frozzcore.SpigotMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class BoardAPI {

    public void scoredSidebar(Player var0, String var1, LinkedHashMap<String, Integer> var2) {
        if (var1 == null) {
            var1 = "Unamed board";
        }

        if (var1.length() > 32) {
            var1 = var1.substring(0, 32);
        }

        String var4;
        label39:
        for(; var2.size() > 16; var2.remove(var4)) {
            var4 = (String)var2.keySet().toArray()[0];
            int var5 = var2.get(var4);
            Iterator<String> var6 = var2.keySet().iterator();

            while(true) {
                String var7;
                do {
                    if (!var6.hasNext()) {
                        continue label39;
                    }

                    var7 = var6.next();
                } while(var2.get(var7) >= var5 && (var2.get(var7) != var5 || var7.compareTo(var4) >= 0));

                var4 = var7;
                var5 = var2.get(var7);
            }
        }

        String finalVar = var1;
        Bukkit.getScheduler().runTask(SpigotMain.getPlugin(), () -> {
            if (var0 != null && var0.isOnline()) {
                if (Bukkit.getScoreboardManager().getMainScoreboard() != null && Bukkit.getScoreboardManager().getMainScoreboard() == var0.getScoreboard()) {
                    var0.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }

                if (var0.getScoreboard() == null) {
                    var0.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }

                Bukkit.getScheduler().runTaskAsynchronously(SpigotMain.getPlugin(), () -> {
                    Objective var3 = var0.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
                    if (var3 == null) {
                        var3 = var0.getScoreboard().registerNewObjective(finalVar.length() > 16 ? finalVar.substring(0, 15) : finalVar, "dummy");
                    }

                    var3.setDisplayName(finalVar);
                    if (var3.getDisplaySlot() == null || var3.getDisplaySlot() != DisplaySlot.SIDEBAR) {
                        var3.setDisplaySlot(DisplaySlot.SIDEBAR);
                    }

                    Iterator<String> var14 = var2.keySet().iterator();

                    while(true) {
                        String var5;
                        do {
                            if (!var14.hasNext()) {
                                var14 = var0.getScoreboard().getEntries().iterator();

                                while(var14.hasNext()) {
                                    var5 = var14.next();
                                    if (var3.getScore(var5).isScoreSet() && !var2.containsKey(var5)) {
                                        var0.getScoreboard().resetScores(var5);
                                    }
                                }

                                return;
                            }

                            var5 = var14.next();
                        } while(var3.getScore(var5).isScoreSet() && var3.getScore(var5).getScore() == var2.get(var5));

                        var3.getScore(var5).setScore(var2.get(var5));
                    }
                });
            }
        });
    }

    public LinkedHashMap<String, Integer> getLinkedHashMap(LinkedList<String> list){
        int slot = list.size();
        LinkedHashMap<String, Integer> hashMap = new LinkedHashMap<>();
        for(Iterator<String> var4 = list.iterator(); var4.hasNext(); --slot) {
            String line = var4.next();
            hashMap.put(fixDuplicates(hashMap, line), slot);
        }
        return hashMap;
    }

    private String fixDuplicates(LinkedHashMap<String, Integer> var0, String var1) {
        while(var0.containsKey(var1)) {
            var1 = var1 + "§r";
        }

        if (var1.length() > 40) {
            var1 = var1.substring(0, 39);
        }

        return var1;
    }
}
