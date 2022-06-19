package me.thejokerdev.frozzcore.type;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.DataType;
import me.thejokerdev.frozzcore.enums.VisibilityType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public abstract class Data {

    public SpigotMain plugin;
    public String TABLE_DATA = "FROZZ_USER_DATA";

    public Data(SpigotMain plugin) {
        this.plugin = plugin;
    }

    public abstract DataType getType();

    public abstract Connection getConnection();

    public abstract void syncData(FUser var);
    public abstract void getData(FUser var);

    protected void getData(Connection var1, FUser var2) {
        PreparedStatement var3 = null;
        ResultSet var4 = null;

        try {
            var3 = var1.prepareStatement(String.format("SELECT * FROM %s WHERE uuid=? OR (uuid IS NULL AND username=?) OR (username=?)", this.TABLE_DATA));
            var3.setString(1, var2.getUniqueID().toString());
            var3.setString(2, var2.getName());
            var3.setString(3, var2.getName());
            var4 = var3.executeQuery();
            if (var4.next()) {
                String var5;
                if (var4.getString("lang") != null) {
                    var5 = var4.getString("lang");
                    var2.setLang(var5, true, false);
                } else {
                    var2.setLang(plugin.getClassManager().getLangManager().getDefault());
                }
                if (var4.getString("visibility") != null) {
                    var5 = var4.getString("visibility");
                    var2.setVisibilityType(VisibilityType.valueOf(var5.toUpperCase()));
                }
                if (var4.getBoolean("firstJoin")) {
                    var2.setFirstJoin(false);
                }
                var2.setHype(var4.getInt("hype"));
            } else {
                var4.close();
                var3.close();
                var3 = var1.prepareStatement(String.format("INSERT INTO %s (uuid,username) VALUES (?,?)", this.TABLE_DATA));
                var3.setString(1, var2.getUniqueID().toString());
                var3.setString(2, var2.getName());
                var3.executeUpdate();
            }
        } catch (SQLException var13) {
            var13.printStackTrace();
        } finally {
            this.close(var4);
            this.close(var3);
        }

    }

    protected void syncData(Connection var1, FUser var2) {
        PreparedStatement var3 = null;

        try {
            var3 = var1.prepareStatement(String.format("UPDATE %s SET lang=?, firstJoin=?, hype=?, visibility=? WHERE uuid=? OR (uuid IS NULL AND username=?)", this.TABLE_DATA));
            var3.setString(1, var2.getLang() == null ? plugin.getClassManager().getLangManager().getDefault() : var2.getLang());
            var3.setBoolean(2, var2.isFirstJoin());
            var3.setInt(3, var2.getHype());
            var3.setString(4, var2.getVisibilityType().name().toLowerCase());
            var3.setString(5, var2.getUniqueID().toString());
            var3.setString(6, var2.getName());
            var3.executeUpdate();
        } catch (SQLException var8) {
            var8.printStackTrace();
        } finally {
            this.close(var3);
        }

    }
    public abstract void reload();

    public abstract boolean isRunning();

    public abstract void close();

    public void close(AutoCloseable var1) {
        if (var1 != null) {
            try {
                var1.close();
            } catch (Exception ignored) {
            }
        }

    }

    public abstract void setup();
}
