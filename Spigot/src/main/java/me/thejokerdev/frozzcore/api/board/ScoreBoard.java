package me.thejokerdev.frozzcore.api.board;

import me.clip.placeholderapi.PlaceholderAPI;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.utils.Utils;
import me.thejokerdev.frozzcore.type.FUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.LinkedList;

public class ScoreBoard implements Listener {
    private SpigotMain plugin;
    private BukkitTask task;

    public ScoreBoard(SpigotMain plugin) {
        this.plugin = plugin;
        loadTask();
    }

    public void loadTask(){
        if (task!=null){
            task.cancel();
        }
        task = (new BukkitRunnable() {
            public void run() {
                if (plugin.getClassManager().getPlayerManager().getUsers().size() == 0){
                    return;
                }
                for (FUser var3 : plugin.getClassManager().getPlayerManager().getUsers().values()) {
                    if (var3 != null) {
                        contentBoard(var3);
                    }
                }
            }
        }).runTaskTimerAsynchronously(plugin, 0L, plugin.getConfig().getLong("scoreboard.update"));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        create(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerQuit(PlayerQuitEvent event) {
        ScoreBoardBuilder.remove(event.getPlayer());
    }

    private void create(Player player) {
        ScoreBoardBuilder.create(player);
    }

    public void createAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            create(player);
        }
    }

    public void contentBoard(FUser var0) {
        if (var0!=null) {
            Player var1 = var0.getPlayer();
            if (ScoreBoardBuilder.get(var1) == null) {
                return;
            }
            String title;
            LinkedList<String> list;
            title = plugin.getConfig().getString("scoreboard.boards.default.title");
            list = new LinkedList<>(plugin.getConfig().getStringList("scoreboard.boards.default.lines"));
            title = Utils.ct(title);
            title = PlaceholderAPI.setPlaceholders(var0.getPlayer(), title);
            LinkedList<String> finalList = new LinkedList<>();
            for (String value : list) {
                value = Utils.ct(value);
                value = PlaceholderAPI.setPlaceholders(var0.getPlayer(), value);
                if (value.contains("\\n")) {
                    Collections.addAll(finalList, value.split("\\n"));
                    continue;
                }
                if (value.contains("\n")) {
                    Collections.addAll(finalList, value.split("\n"));
                    continue;
                }
                finalList.add(value);
            }
            /*ScoreBoardBuilder.get(var1).setTitle(title);
            ScoreBoardBuilder.get(var1).setSlotsFromList(finalList);*/
            BoardAPI.scoredSidebar(var0.getPlayer(), title, BoardAPI.getLinkedHashMap(finalList));
        }
    }
}
