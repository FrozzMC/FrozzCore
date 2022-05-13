package me.thejokerdev.frozzcore.type;

import me.thejokerdev.frozzcore.api.utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Lang {
    private final String id;
    private final String section;

    private final FileUtils file;

    private final Date lastUpdate;

    public Lang(File var1){
        id = var1.getName().replace(".yml", "");
        section = var1.getPath();
        file = new FileUtils(var1);
        lastUpdate = new Date();
    }

    public String getId() {
        return id;
    }

    public String getSection() {
        return section;
    }

    public FileUtils getFile() {
        return file;
    }

    public String getLastUpdate(){
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(lastUpdate);
    }
}
