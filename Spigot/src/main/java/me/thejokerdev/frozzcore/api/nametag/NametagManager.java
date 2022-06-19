package me.thejokerdev.frozzcore.api.nametag;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.api.data.NameTag;
import me.thejokerdev.frozzcore.api.packets.PacketWrapper;
import me.thejokerdev.frozzcore.api.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;
import java.util.*;

public class NametagManager {
    private final LinkedHashMap<String, NameTag> TEAMS = new LinkedHashMap<>();
    private final LinkedHashMap<String, NameTag> CACHED_FAKE_TEAMS = new LinkedHashMap<>();
    private final SpigotMain plugin;

    private FileUtils file;

    public void init(){
        if (TEAMS.size() != 0){
            reset();
        }
        if (plugin.haveLP()){
            return;
        }
        file = plugin.getClassManager().getUtils().getFile("nametags.yml");
        plugin.debug("NameTags load. Step #1");
        if (file.getKeys(false).size() == 0){
            TEAMS.put("default", new NameTag("default", "&7", "", 0));
            plugin.debug("NameTags load. Step NON keys");
            return;
        }
        for (String key : file.getKeys(false)){
            NameTag nameTag = new NameTag(file.getSection(key));
            TEAMS.put(key, nameTag);
            plugin.debug("NameTags load. Key: "+ key);
        }

        plugin.debug("NameTags loaded: "+TEAMS.size());


        plugin.getClassManager().getNametagHandler().applyTags();
    }

    public void addNameTag(String group, int priority){
        NameTag nameTag = new NameTag(group, "%luckperms_prefix%", "%luckperms_suffix%", priority);
        TEAMS.put(group, nameTag);
    }

    public void removeTag(String group){
        TEAMS.remove(group);
    }

    private NameTag getNameTag(String prefix, String suffix) {
        Iterator<NameTag> var3 = this.TEAMS.values().iterator();

        NameTag fakeTeam;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            fakeTeam = var3.next();
        } while(!fakeTeam.isSimilar(prefix, suffix));

        return fakeTeam;
    }

    private void addPlayerToTeam(String player, String name, String prefix, String suffix, int sortPriority) {
        NameTag previous = this.getNameTag(player);
        if (previous != null && previous.isSimilar(prefix, suffix)) {
            this.plugin.debug(player + " already belongs to a similar team (" + previous.getName() + ")");
        } else {
            this.reset(player);
            NameTag joining = this.getNameTag(prefix, suffix);
            if (joining != null) {
                joining.addMember(player);
                this.plugin.debug("Using existing team for " + player);
            } else {
                joining = new NameTag(name, prefix, suffix, sortPriority);
                joining.addMember(player);
                this.TEAMS.put(joining.getName(), joining);
                this.addTeamPackets(joining);
                this.plugin.debug("Created NameTag " + joining.getName() + ". Size: " + this.TEAMS.size());
            }

            Player adding = Bukkit.getPlayerExact(player);
            if (adding != null) {
                this.addPlayerToTeamPackets(joining, adding.getName());
                this.cache(adding.getName(), joining);
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
                this.addPlayerToTeamPackets(joining, offlinePlayer.getName());
                this.cache(offlinePlayer.getName(), joining);
            }

            this.plugin.debug(player + " has been added to team " + joining.getName());
        }
    }

    public NameTag reset(String player) {
        return this.reset(player, this.decache(player));
    }

    private NameTag reset(String player, NameTag nameTag) {
        if (nameTag != null && nameTag.getMembers().remove(player)) {
            Player removing = Bukkit.getPlayerExact(player);
            boolean delete;
            if (removing != null) {
                delete = this.removePlayerFromTeamPackets(nameTag, removing.getName());
            } else {
                OfflinePlayer toRemoveOffline = Bukkit.getOfflinePlayer(player);
                delete = this.removePlayerFromTeamPackets(nameTag, toRemoveOffline.getName());
            }

            this.plugin.debug(player + " was removed from " + nameTag.getName());
            /*if (delete) {
                this.removeTeamPackets(nameTag);
                this.TEAMS.remove(nameTag.getName());
                this.plugin.debug("NameTag " + nameTag.getName() + " has been deleted. Size: " + this.TEAMS.size());
            }*/
        }
        return nameTag;
    }

    private NameTag decache(String player) {
        return this.CACHED_FAKE_TEAMS.remove(player);
    }

    public NameTag getNameTag(String player) {
        return this.CACHED_FAKE_TEAMS.get(player);
    }

    public NameTag getTag(String group) {
        return TEAMS.get(group) == null ? getDefault() : TEAMS.get(group);
    }

    private void cache(String player, NameTag nameTag) {
        this.CACHED_FAKE_TEAMS.put(player, nameTag);
    }

    public void setNametag(String player, NameTag nameTag) {

        addPlayerToTeam(player, player, nameTag.getPrefix(), nameTag.getSuffix(), nameTag.getPriority());
    }
    void setNametag(String player, String name, String prefix, String suffix) {
        this.addPlayerToTeam(player, name, prefix != null ? prefix : "", suffix != null ? suffix : "", -1);
    }

    void sendTeams(Player player) {

        for (NameTag nameTag : this.TEAMS.values()) {
            (new PacketWrapper(nameTag.getName(), plugin.getClassManager().getUtils().formatMSG(player, (nameTag.getPrefix())), plugin.getClassManager().getUtils().formatMSG(player, (nameTag.getSuffix())), 0, nameTag.getMembers())).send(player);
        }

    }

    void reset() {
        for (NameTag nameTag : this.TEAMS.values()) {
            this.removePlayerFromTeamPackets(nameTag, nameTag.getMembers());
            this.removeTeamPackets(nameTag);
        }
        this.CACHED_FAKE_TEAMS.clear();
        this.TEAMS.clear();
    }

    private void removeTeamPackets(NameTag nameTag) {
        (new PacketWrapper(nameTag.getName(), nameTag.getPrefix(), nameTag.getSuffix(), 1, new ArrayList<>())).send();
    }

    private boolean removePlayerFromTeamPackets(NameTag nameTag, String... players) {
        return this.removePlayerFromTeamPackets(nameTag, Arrays.asList(players));
    }

    private boolean removePlayerFromTeamPackets(NameTag nameTag, List<String> players) {
        (new PacketWrapper(nameTag.getName(), 4, players)).send();
        nameTag.getMembers().removeAll(players);
        return nameTag.getMembers().isEmpty();
    }

    private void addTeamPackets(NameTag nameTag) {
        (new PacketWrapper(nameTag.getName(), nameTag.getPrefix(), nameTag.getSuffix(), 0, nameTag.getMembers())).send();
    }

    private void addPlayerToTeamPackets(NameTag nameTag, String player) {
        (new PacketWrapper(nameTag.getName(), 3, Collections.singletonList(player))).send();
    }

    @ConstructorProperties({"plugin"})
    public NametagManager(SpigotMain plugin) {
        this.plugin = plugin;
    }

    public LinkedList<NameTag> getNameTags() {
        return new LinkedList<>(TEAMS.values());
    }

    public NameTag getDefault() {
        return TEAMS.get("default");
    }
}
