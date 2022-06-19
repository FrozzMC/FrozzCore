package me.thejokerdev.frozzcore.api.data;


import lombok.Getter;
import lombok.Setter;
import me.thejokerdev.frozzcore.api.utils.MinecraftVersion;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class NameTag {
    private final String UNIQUE_ID = UUID.randomUUID().toString();
    private int ID = 0;

    private int priority = 0;
    private final List<String> members = new ArrayList<>();
    private String name;
    private String id;
    private String prefix = "";
    private String suffix = "";

    public NameTag(String id, String prefix, String suffix, int sortPriority) {
        id = id;
        name = id;
        setPriority(sortPriority);
        setName(UNIQUE_ID + "_" + this.getNameFromInput(getPriority()) + ++ID);
        if (MinecraftVersion.getServersVersion().isBelow(MinecraftVersion.V1_13_R1)){
            setName(getName().length() > 16 ? getName().substring(0, 16) : getName());
        } else {
            setName(getName().length() > 128 ? getName().substring(0, 128) : getName());
        }

        setPrefix(prefix);
        setSuffix(suffix);
    }

    public String getPerm(){
        return id!= null ? id.equals("default") ? "none" : "core.nametag."+id : "none";
    }

    public NameTag(ConfigurationSection section){
        this.id = section.getName();
        this.name = section.getName();
        if (section.get("priority")!=null){
            setPriority(section.getInt("priority"));
        }
        if (section.get("prefix")!=null){
            setPrefix(section.getString("prefix"));
        }
        if (section.get("suffix")!=null){
            setSuffix(section.getString("suffix"));
        }
    }

    public void addMember(String player) {
        if (!this.members.contains(player)) {
            this.members.add(player);
        }

    }

    public boolean isSimilar(String prefix, String suffix) {
        return this.prefix.equals(prefix) && this.suffix.equals(suffix);
    }

    private String getNameFromInput(int input) {
        if (input < 0) {
            return "A";
        } else {
            char letter = (char)(input / 5 + 65);
            int repeat = input % 5 + 1;
            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < repeat; ++i) {
                builder.append(letter);
            }

            return builder.toString();
        }
    }

    public List<String> getMembers() {
        return this.members;
    }

    public String getName() {
        return this.name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof NameTag)) {
            return false;
        } else {
            NameTag other = (NameTag)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$members = this.getMembers();
                    Object other$members = other.getMembers();
                    if (this$members == null) {
                        if (other$members == null) {
                            break label59;
                        }
                    } else if (this$members.equals(other$members)) {
                        break label59;
                    }

                    return false;
                }

                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                Object this$prefix = this.getPrefix();
                Object other$prefix = other.getPrefix();
                if (this$prefix == null) {
                    if (other$prefix != null) {
                        return false;
                    }
                } else if (!this$prefix.equals(other$prefix)) {
                    return false;
                }

                Object this$suffix = this.getSuffix();
                Object other$suffix = other.getSuffix();
                if (this$suffix == null) {
                    if (other$suffix != null) {
                        return false;
                    }
                } else if (!this$suffix.equals(other$suffix)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof NameTag;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $members = this.getMembers();
        result = result * 59 + ($members == null ? 0 : $members.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 0 : $name.hashCode());
        Object $prefix = this.getPrefix();
        result = result * 59 + ($prefix == null ? 0 : $prefix.hashCode());
        Object $suffix = this.getSuffix();
        result = result * 59 + ($suffix == null ? 0 : $suffix.hashCode());
        return result;
    }

    public String toString() {
        return "NameTag(members=" + this.getMembers() + ", name=" + this.getName() + ", prefix=" + this.getPrefix() + ", suffix=" + this.getSuffix() + ")";
    }
}
