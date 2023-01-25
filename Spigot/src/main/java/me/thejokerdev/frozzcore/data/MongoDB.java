package me.thejokerdev.frozzcore.data;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.org.apache.xpath.internal.operations.Mod;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.DataType;
import me.thejokerdev.frozzcore.enums.ModifierStatus;
import me.thejokerdev.frozzcore.enums.VisibilityType;
import me.thejokerdev.frozzcore.type.Data;
import me.thejokerdev.frozzcore.type.FUser;
import org.bson.Document;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;

public class MongoDB extends Data {

    private MongoCollection<Document> collection;
    private MongoDatabase db;
    private MongoClient client;

    private boolean running = false;

    public MongoDB(SpigotMain plugin) {
        super(plugin);
    }

    @Override
    public DataType getType() {
        return DataType.MONGODB;
    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public void syncData(FUser var) {
        Document query = new Document("uuid", var.getUniqueID().toString());
        Document found = collection.find(query).first();

        if (found == null){
            Document document = new Document("uuid", var.getUniqueID().toString());
            document.put("name", var.getName());
            document.put("lang", plugin.getClassManager().getLangManager().getDefault());
            document.put("visibility", VisibilityType.ALL.name());
            document.put("firstJoin", true);
            document.put("hype", 0);
            document.put("jump", ModifierStatus.OFF.name());
            document.put("doubleJump", ModifierStatus.OFF.name());
            document.put("fly", ModifierStatus.OFF.name());
            document.put("speed", ModifierStatus.OFF.name());
            collection.insertOne(document);
            return;
        }

        Document document = new Document("uuid", var.getUniqueID().toString());
        document.put("name", var.getName());
        document.put("lang", var.getLang());
        document.put("visibility", var.getVisibilityType().name());
        document.put("firstJoin", false);
        document.put("hype", var.getHype());
        document.put("jump", var.getJump().name());
        document.put("doubleJump", var.getDoubleJump().name());
        document.put("fly", var.getAllowFlight().name());
        document.put("speed", var.getSpeed().name());
        collection.replaceOne(found, document);
    }

    @Override
    public void getData(FUser var) {
        Document query = new Document("uuid", var.getUniqueID().toString());
        Document found = collection.find(query).first();

        if (found == null){
            Document document = new Document("uuid", var.getUniqueID().toString());
            document.put("name", var.getName());
            document.put("lang", plugin.getClassManager().getLangManager().getDefault());
            document.put("visibility", VisibilityType.ALL.name());
            document.put("firstJoin", true);
            document.put("hype", 0);
            document.put("jump", ModifierStatus.OFF.name());
            document.put("doubleJump", ModifierStatus.OFF.name());
            document.put("fly", ModifierStatus.OFF.name());
            document.put("speed", ModifierStatus.OFF.name());
            collection.insertOne(document);
            return;
        }

        var.setLang(found.getString("lang"));
        var.setVisibilityType(VisibilityType.valueOf(found.getString("visibility").toUpperCase()));
        var.setFirstJoin(found.getBoolean("firstJoin"));
        var.setHype(found.getInteger("hype"));
        try {
            var.setJump(ModifierStatus.valueOf(found.getString("jump").toUpperCase()));
            var.setDoubleJump(ModifierStatus.valueOf(found.getString("doubleJump").toUpperCase()));
            var.setAllowFlight(ModifierStatus.valueOf(found.getString("fly").toUpperCase()));
            var.setSpeed(ModifierStatus.valueOf(found.getString("speed").toUpperCase()));
        } catch (ClassCastException e){
            var.setJump(ModifierStatus.OFF);
            var.setDoubleJump(ModifierStatus.OFF);
            var.setAllowFlight(ModifierStatus.OFF);
            var.setSpeed(ModifierStatus.OFF);
        }
    }

    @Override
    public void reload() {
        close();
        setup();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void close() {
        client.close();
    }

    @Override
    public void setup() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("data.mongodb");
        boolean useURI = !section.getString("uri", "mongodb://localhost:27017").equals("mongodb://localhost:27017");
        if (useURI) {
            MongoClientURI uri = new MongoClientURI(section.getString("uri"));
            client = new MongoClient(uri);
        } else {
            String host = section.getString("host");
            int port = 27017;
            String[] split = host.split(":");
            if (split.length == 2) {
                host = split[0];
                port = Integer.parseInt(split[1]);
            }
            ServerAddress address = new ServerAddress(host, port);

            String password = section.getString("password");
            if (password != null && !password.equalsIgnoreCase("")){
                MongoCredential credential = MongoCredential.createCredential(section.getString("user"), section.getString("database"), password.toCharArray());
                client = new MongoClient(address, credential, MongoClientOptions.builder().build());
            } else {
                client = new MongoClient(address);
            }
        }
        db = client.getDatabase(section.getString("database"));
        collection = db.getCollection("core");

        plugin.debug("{prefix}&7Connected to database.");
    }
}
