package me.thejokerdev.frozzcore.api.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

public class FileUtils {
    private FileConfiguration config;
    private final File file;

    public FileUtils(File file) {
        this.file = file;
        this.load();
    }

    public FileUtils(File folder, String fileName) {
        this(new File(folder(folder), fileName));
    }


    public static File folder(File folder) {
        if (!folder.exists()) {
            folder.mkdir();
        }

        return folder;
    }
    public static boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        } else {
            return Files.isSymbolicLink(file.toPath());
        }
    }
    public static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        } else {
            return obj;
        }
    }
    private void load() {
        try {
            if (!this.file.exists()) {
                this.file.createNewFile();
            }

            this.config = YamlConfiguration.loadConfiguration(this.file);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void clear() {
        try {
            destroyFile(this.file);
            if (!this.file.exists()) {
                this.file.createNewFile();
            }

            this.reload();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public static void cleanDirectory(File directory) throws IOException {
        File[] files = verifiedListFiles(directory);
        IOException exception = null;
        File[] var3 = files;
        int var4 = files.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            File file = var3[var5];

            try {
                forceDelete(file);
            } catch (IOException var8) {
                exception = var8;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    private static File[] verifiedListFiles(File directory) throws IOException {
        String message;
        if (!directory.exists()) {
            message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        } else if (!directory.isDirectory()) {
            message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        } else {
            File[] files = directory.listFiles();
            if (files == null) {
                throw new IOException("Failed to list contents of " + directory);
            } else {
                return files;
            }
        }
    }
    public static void deleteDirectory(File directory) throws IOException {
        if (directory.exists()) {
            if (!isSymlink(directory)) {
                cleanDirectory(directory);
            }

            if (!directory.delete()) {
                String message = "Unable to delete directory " + directory + ".";
                throw new IOException(message);
            }
        }
    }
    public static void destroyFile(File file) throws Exception {
        forceDelete(file);
    }
    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }

                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }

    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void set(String path, Object value) {
        if (value instanceof Float) {
            float val = (Float)value;
            value = Float.toString(val);
        }

        this.config.set(path, value);
        this.save();
    }

    public void add(String path, Object value) {
        if (!this.contains(path)) {
            this.set(path, value);
        }

    }

    public boolean contains(String path) {
        return this.config.contains(path);
    }

    public Object get(String path) {
        return this.config.get(path);
    }

    public Object get(String path, Object def) {
        this.add(path, def);
        return this.get(path);
    }

    public String getString(String path) {
        return this.config.getString(Utils.ct(path));
    }

    public String getString(String path, String def) {
        this.add(path, def);
        return this.getString(path);
    }

    public boolean getBoolean(String path) {
        return this.config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean def) {
        this.add(path, def);
        return this.getBoolean(path);
    }

    public int getInt(String path) {
        return this.config.getInt(path);
    }

    public int getInt(String path, int def) {
        this.add(path, def);
        return this.getInt(path);
    }

    public double getDouble(String path) {
        return this.config.getDouble(path);
    }

    public double getDouble(String path, double def) {
        this.add(path, def);
        return this.getDouble(path);
    }

    public long getLong(String path) {
        return this.config.getLong(path);
    }

    public long getLong(String path, long def) {
        this.add(path, def);
        return this.getLong(path);
    }

    public float getFloat(String path) {
        return (float)Long.parseLong(this.getString(path));
    }

    public float getFloat(String path, float def) {
        this.add(path, def);
        return this.getFloat(path);
    }

    public List<?> getList(String path) {
        return this.config.getList(path);
    }

    public List<?> getList(String path, List<?> def) {
        this.add(path, def);
        return this.getList(path);
    }

    public List<String> getStringList(String path) {
        return this.config.getStringList(path);
    }

    public List<String> getStringList(String path, List<String> def) {
        this.add(path, def);
        return this.getStringList(path);
    }

    public List<Boolean> getBooleanList(String path) {
        return this.config.getBooleanList(path);
    }

    public List<Boolean> getBooleanList(String path, List<Boolean> def) {
        this.add(path, def);
        return this.getBooleanList(path);
    }

    public List<Integer> getIntList(String path) {
        return this.config.getIntegerList(path);
    }

    public List<Integer> getIntList(String path, List<Integer> def) {
        this.add(path, def);
        return this.getIntList(path);
    }

    public List<Double> getDoubleList(String path) {
        return this.config.getDoubleList(path);
    }

    public List<Double> getDoubleList(String path, List<Double> def) {
        this.add(path, def);
        return this.getDoubleList(path);
    }

    public List<Long> getLongList(String path) {
        return this.config.getLongList(path);
    }

    public List<Long> getLongList(String path, List<Long> def) {
        this.add(path, def);
        return this.getLongList(path);
    }

    public List<Float> getFloatList(String path) {
        return this.config.getFloatList(path);
    }

    public List<Float> getFloatList(String path, List<Float> def) {
        this.add(path, def);
        return this.getFloatList(path);
    }

    public ConfigurationSection getSection(String path) {
        return this.config.getConfigurationSection(path);
    }

    public Set<String> getKeys(boolean deep) {
        return this.config.getKeys(deep);
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public File getFile() {
        return this.file;
    }
}
