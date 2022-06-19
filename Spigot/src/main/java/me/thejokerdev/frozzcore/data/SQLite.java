package me.thejokerdev.frozzcore.data;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.DataType;
import me.thejokerdev.frozzcore.type.Data;
import me.thejokerdev.frozzcore.type.FUser;

import java.sql.*;

public class SQLite extends Data {
    private Connection con;
    private boolean running = false;
    public SQLite(SpigotMain plugin) {
        super(plugin);
    }

    @Override
    public DataType getType() {
        return DataType.SQLITE;
    }

    @Override
    public synchronized Connection getConnection() {
        return con;
    }

    @Override
    public void syncData(FUser user) {
        syncData(con, user);
    }

    @Override
    public synchronized void getData(FUser user) {
        getData(con, user);
    }

    @Override
    public void reload() {
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void close() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException var2) {
            var2.printStackTrace();
        }
    }

    private void addColumn(String var1, String var2, String var3) {
        ResultSet var4 = null;
        Statement var5 = null;

        try {
            var5 = con.createStatement();
            DatabaseMetaData var6 = con.getMetaData();
            var4 = var6.getColumns(null, null, var1, var2);
            if (!var4.next()) {
                var5.executeUpdate(String.format("ALTER TABLE %s ADD COLUMN %s %s;", var1, var2, var3));
            }
        } catch (SQLException var10) {
            var10.printStackTrace();
        } finally {
            this.close(var4);
            this.close(var5);
        }

    }

    @Override
    public void setup() {
        connect();

        Statement var1 = null;

        try {
            var1 = con.createStatement();
            var1.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS '%s' ('id' INTEGER PRIMARY KEY, 'uuid' TEXT(40), 'username' TEXT(32), 'lang' TEXT, 'firstJoin' BOOLEAN DEFAULT TRUE, 'hype' INT(12) DEFAULT '0', 'visibility' TEXT); CREATE INDEX IF NOT EXISTS fudata_username_idx ON %s(username); CREATE INDEX IF NOT EXISTS fudata_uuid ON %s(uuid);", this.TABLE_DATA, this.TABLE_DATA, this.TABLE_DATA));
            this.addColumn(this.TABLE_DATA, "uuid", "VARCHAR(255) NOT NULL UNIQUE");
            this.addColumn(this.TABLE_DATA, "username", "VARCHAR(255) DEFAULT NULL");
            this.addColumn(this.TABLE_DATA, "lang", "TEXT");
            this.addColumn(this.TABLE_DATA, "firstJoin", "BOOLEAN DEFAULT TRUE");
            this.addColumn(this.TABLE_DATA, "hype", "INT(12) DEFAULT 0");
            this.addColumn(this.TABLE_DATA, "visibility", "TEXT");
            var1.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            this.close(var1);
        }

        plugin.debug("SQLite Setup finished");
        running = true;

    }

    private synchronized void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            plugin.debug("SQLite driver loaded");
            con = DriverManager.getConnection("jdbc:sqlite:"+plugin.getDataFolder()+"/database.db");
            plugin.debug("SQLite.connect: isClosed = " + con.isClosed());
        } catch (SQLException | ClassNotFoundException var2) {
            var2.printStackTrace();
        }

    }
}
