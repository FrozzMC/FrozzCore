package me.thejokerdev.frozzcore.api.board;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class ScoreBoardBuilder {
    private static final HashMap<UUID, ScoreBoardBuilder> players = new HashMap<>();
    private static final HashMap<String, Integer> edit = new HashMap<>();

    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective sidebar;

    public static void create(Player player) {
        new ScoreBoardBuilder(player);
    }

    public static ScoreBoardBuilder get(Player player) {
        return players.get(player.getUniqueId());
    }

    public static void remove(Player player) {
        players.remove(player.getUniqueId());
    }

    private ScoreBoardBuilder(Player player) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.sidebar = this.scoreboard.registerNewObjective("sidebar", "dummy");
        this.sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(this.scoreboard);

        for(int i = 1; i <= 15; ++i) {
            Team team = this.scoreboard.registerNewTeam("SLOT_" + i);
            team.addEntry(this.genEntry(i));
        }

        players.put(player.getUniqueId(), this);
    }

    public void setTitle(String title) {
        title = PlaceholderAPI.setPlaceholders(this.player, title);
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }

        if (!this.sidebar.getDisplayName().equals(title)) {
            this.sidebar.setDisplayName(title);
        }

    }

    public void setSlot(int slot, String text) {
        Team team = this.scoreboard.getTeam("SLOT_" + slot);
        String entry = this.genEntry(slot);

        try {
            if (!scoreboard.getEntries().contains(entry)) {
                edit.put(entry, slot);
            }
        } catch (IllegalStateException | IllegalArgumentException ignored) {
        }

        text = PlaceholderAPI.setPlaceholders(this.player, text);
        String pre = this.getFirstSplit(text);
        String suf = this.getFirstSplit(this.getSecondSplit(text));
        if (!team.getPrefix().equals(pre)) {
            team.setPrefix(pre);
        }

        if (!team.getSuffix().equals(suf)) {
            team.setSuffix(suf);
        }
    }

    public void removeSlot(int slot) {
        String entry = this.genEntry(slot);
        if (this.scoreboard.getEntries().contains(entry)) {
            this.scoreboard.resetScores(entry);
        }

    }

    public void setSlotsFromList(LinkedList<String> list) {
        int slot = list.size();
        if (slot < 15) {
            for(int i = slot + 1; i <= 15; ++i) {
                this.removeSlot(i);
            }
        }

        for(Iterator<String> var4 = list.iterator(); var4.hasNext(); --slot) {
            String line = var4.next();
            this.setSlot(slot, line);
        }
        if (edit.isEmpty()){
            return;
        }
        HashMap<String, Integer> edit2 = (HashMap<String, Integer>) edit.clone();
        for (Map.Entry<String, Integer> map : edit2.entrySet()){
            sidebar.getScore(map.getKey()).setScore(map.getValue());
        }
        edit.clear();
    }

    private String genEntry(int slot) {
        return ChatColor.values()[slot].toString();
    }

    private String getFirstSplit(String s) {
        return s.length() > 16 ? s.substring(0, 16) : s;
    }

    private String getSecondSplit(String s) {
        if (s.length() > 32) {
            s = s.substring(0, 32);
        }

        return s.length() > 16 ? s.substring(16) : "";
    }
}
