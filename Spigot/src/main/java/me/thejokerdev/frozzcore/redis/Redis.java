package me.thejokerdev.frozzcore.redis;

import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.redis.payload.Payload;
import org.bukkit.configuration.ConfigurationSection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.logging.Level;

public class Redis {
    private JedisPool pool;
    private boolean active = false;
    private final SpigotMain plugin;

    public Redis(SpigotMain plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("redis");

        try {
            plugin.console("{prefix}&eConectando a redis...");
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                stablishConnection(section, pool -> {
                    this.pool = pool;
                    this.active = true;
                    plugin.console("{prefix}&aConectado a redis.");
                });
            });
        } catch (Exception var5) {
            plugin.console("{prefix}&cError al conectar a redis.");
            this.active = false;
        }

    }

    public void stablishConnection(ConfigurationSection section, final Consumer<JedisPool> consumer) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(section.getInt("config.maxConnections", 8));
        JedisPool pool = new JedisPool(config, section.getString("host", "localhost"), section.getInt("port", 6379), 0, section.getString("auth.password", ""), section.getBoolean("config.useSSL", false));
        consumer.accept(pool);
    }

    public void disconnect() {
        this.pool.destroy();
    }

    public void write(String json) {
        this.write("bcore_data", json);
    }

    public void addServer(String name, String ip, String port){
        String msg = (new RedisMessage(plugin, Payload.SERVER_ADD)).setParam("name", name).setParam("ip", ip).setParam("port", port).toJSON();
        write(msg);
    }

    public void removeServer(String name){
        String msg = (new RedisMessage(plugin, Payload.SERVER_REMOVE)).setParam("name", name).toJSON();
        write(msg);
    }

    public void write(String channel, String json) {
        try {
            Jedis jedis = this.pool.getResource();
            Throwable var4 = null;

            try {
                jedis.publish(channel, json);
            } catch (Throwable var14) {
                var4 = var14;
                throw var14;
            } finally {
                if (jedis != null) {
                    if (var4 != null) {
                        try {
                            jedis.close();
                        } catch (Throwable var13) {
                            var4.addSuppressed(var13);
                        }
                    } else {
                        jedis.close();
                    }
                }

            }

        } catch (JedisConnectionException var16) {
            plugin.getLogger().log(Level.SEVERE, "Unable to get connection from pool - did your Redis server go away?", var16);
            throw new RuntimeException("Unable to publish channel message", var16);
        }
    }

    public JedisPool getPool() {
        return this.pool;
    }

    public boolean isActive() {
        return this.active;
    }
}
