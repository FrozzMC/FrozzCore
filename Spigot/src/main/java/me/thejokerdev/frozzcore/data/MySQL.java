package me.thejokerdev.frozzcore.data;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.DataType;
import me.thejokerdev.frozzcore.type.Data;
import me.thejokerdev.frozzcore.type.FUser;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;

public class MySQL extends Data {
    private HikariDataSource ds;
    boolean running = false;
    public MySQL(SpigotMain plugin) {
        super(plugin);
    }

    @Override
    public DataType getType() {
        return DataType.MYSQL;
    }

    @Override
    public Connection getConnection() {
        try {
            return this.ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void syncData(FUser user) {
        try {
            Connection var2 = this.getConnection();
            Throwable var3 = null;

            try {
                this.syncData(var2, user);
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if (var2 != null) {
                    if (var3 != null) {
                        try {
                            var2.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        var2.close();
                    }
                }

            }
        } catch (SQLException var15) {
            var15.printStackTrace();
        }

    }

    @Override
    public synchronized void getData(FUser user) {
        try {
            Connection var2 = this.getConnection();
            Throwable var3 = null;

            try {
                this.getData(var2, user);
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if (var2 != null) {
                    if (var3 != null) {
                        try {
                            var2.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        var2.close();
                    }
                }

            }
        } catch (SQLException var15) {
            var15.printStackTrace();
        }

    }

    @Override
    public void reload() {
        close();
        getConnection();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void close() {
        if (this.ds != null && !this.ds.isClosed()) {
            this.ds.close();
        }

    }

    @Override
    public void setup() {
        try {
            this.setConnectionArguments();
        } catch (RuntimeException var3) {
            if (var3 instanceof IllegalArgumentException) {
                plugin.console("{prefix}&4&lERROR: &cInvalid database arguments! Please check your configuration!",
                        "If this error persists, please report it to the developer!");
                throw new IllegalArgumentException(var3);
            }

            if (var3 instanceof HikariPool.PoolInitializationException) {
                plugin.console("{prefix}&4&lERROR: &cCan't initialize database connection! Please check your configuration!",
                        "If this error persists, please report it to the developer!");
                throw new HikariPool.PoolInitializationException(var3);
            }

            plugin.console("{prefix}&4&lERROR: Can't use the Hikari Connection Pool! Please, report this error to the developer!");
            throw var3;
        }

        try {
            this.setupConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void setConnectionArguments() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.mysql");
        String host;
        int port;
        if (section.getString("host").contains(":")){
            String [] hostA = section.getString("host").split(":");
            host = hostA[0];
            port = Integer.parseInt(hostA[1]);
        } else {
            host = section.getString("host");
            port = 3306;
        }
        String database = section.getString("database");
        String username = section.getString("user");
        String password = section.getString("password");
        this.ds = new HikariDataSource();
        this.ds.setPoolName("FrozzCore MySQL");
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);

        this.ds.addDataSourceProperty("cachePrepStmts", "true");
        this.ds.addDataSourceProperty("prepStmtCacheSize", "250");
        this.ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.ds.addDataSourceProperty("characterEncoding", "utf8");
        this.ds.addDataSourceProperty("encoding", "UTF-8");
        this.ds.addDataSourceProperty("useUnicode", "true");
        this.ds.addDataSourceProperty("useSSL", "false");
        this.ds.setUsername(username);
        this.ds.setPassword(password);
        this.ds.setMaxLifetime(180000L);
        this.ds.setIdleTimeout(60000L);
        this.ds.setMinimumIdle(1);
        this.ds.setMaximumPoolSize(64);
        running = true;
        plugin.debug("Connection arguments loaded, Hikari ConnectionPool ready!");
    }

    private void setupConnection() throws SQLException {
        Connection var1 = this.getConnection();
        Throwable var2 = null;

        try {
            Statement var3 = var1.createStatement();
            var3.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS `%s` (`id` INT NOT NULL AUTO_INCREMENT, `username` VARCHAR(32) NOT NULL UNIQUE, `uuid` varchar(40) UNIQUE, `lang` TEXT, `firstJoin` BOOLEAN, `hype` INT(12) DEFAULT '0', `visibility` TEXT, PRIMARY KEY (id), KEY `fudata_username_idx` (`username`(32))) ENGINE=InnoDB;", this.TABLE_DATA));
            this.addColumn(this.TABLE_DATA, "uuid", "VARCHAR(255) NOT NULL UNIQUE", "id");
            this.addColumn(this.TABLE_DATA, "username", "VARCHAR(255) NOT NULL UNIQUE", "uuid");
            this.addColumn(this.TABLE_DATA, "lang", "TEXT", "username");
            this.addColumn(this.TABLE_DATA, "firstJoin", "BOOLEAN", "lang");
            this.addColumn(this.TABLE_DATA, "hype", "INT(12) DEFAULT 0", "firstJoin");
            this.addColumn(this.TABLE_DATA, "visibility", "TEXT", "hype");
            var3.close();
            DatabaseMetaData var5 = var1.getMetaData();
            ResultSet var4 = var5.getIndexInfo(null, null, this.TABLE_DATA, true, false);
            boolean var6 = false;

            while(var4.next()) {
                String var7 = var4.getString("COLUMN_NAME");
                String var8 = var4.getString("INDEX_NAME");
                if (var8 != null && var8.startsWith("username_")) {
                    var3 = var1.createStatement();
                    var3.executeUpdate(String.format("DROP INDEX %s ON %s", var8, this.TABLE_DATA));
                    var3.close();
                }

                if (var7 != null && var8 != null && var7.equalsIgnoreCase("username") && var8.equalsIgnoreCase("username")) {
                    var6 = true;
                }
            }

            var4.close();
            if (!var6) {
                var3 = var1.createStatement();
                var3.executeUpdate(String.format("ALTER TABLE %s ADD UNIQUE (username);", this.TABLE_DATA));
                var3.close();
            }
        } catch (Throwable var16) {
            var2 = var16;
            throw var16;
        } finally {
            if (var1 != null) {
                if (var2 != null) {
                    try {
                        var1.close();
                    } catch (Throwable var15) {
                        var2.addSuppressed(var15);
                    }
                } else {
                    var1.close();
                }
            }

        }

        plugin.debug("MySQL setup finished");
    }

    private void addColumn(String var1, String var2, String var3, String var4) {
        ResultSet var5 = null;
        Statement var6 = null;

        try {
            Connection var7 = this.getConnection();
            Throwable var8 = null;

            try {
                var6 = var7.createStatement();
                DatabaseMetaData var9 = var7.getMetaData();
                var5 = var9.getColumns(null, null, var1, var2);
                if (!var5.next()) {
                    var6.executeUpdate(String.format("ALTER TABLE %s ADD COLUMN %s %s AFTER %s;", var1, var2, var3, var4));
                }
            } catch (Throwable var26) {
                var8 = var26;
                throw var26;
            } finally {
                if (var7 != null) {
                    if (var8 != null) {
                        try {
                            var7.close();
                        } catch (Throwable var25) {
                            var8.addSuppressed(var25);
                        }
                    } else {
                        var7.close();
                    }
                }

            }
        } catch (SQLException var28) {
            var28.printStackTrace();
        } finally {
            this.close(var5);
            this.close(var6);
        }

    }
}
