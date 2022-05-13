package me.thejokerdev.frozzcore.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.thejokerdev.frozzcore.SpigotMain;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class LangDownloader {
    private SpigotMain plugin;
    private File langFolder;
    public final LinkedHashMap<String, String> readWithInputStreamCache = new LinkedHashMap<>();
    private String mainFolder;

    public LangDownloader(SpigotMain plugin) {
        this.plugin = plugin;
        langFolder = new File(plugin.getDataFolder()+"/lang/");
    }

    public boolean downloadFromGitHub(String folder) {
        boolean si = true;
        JsonParser parser = new JsonParser();
        try {
            String url = "https://api.github.com/repos/{Username}/{Repository}/contents/{Folder}";
            url = url.replace("{Username}", plugin.getConfig().getString("settings.languages.downloader.user"));
            url = url.replace("{Repository}", plugin.getConfig().getString("settings.languages.downloader.repo"));
            url = url.replace("{Folder}", folder);
            url = url.replace("{PluginName}", plugin.getDescription().getName());
            if (this.mainFolder == null)
                this.mainFolder = folder;
            String content = readWithInputStream(url);
            if (content != null) {
                JsonArray availableFiles = parser.parse(content).getAsJsonArray();
                for (JsonElement jsonElement : availableFiles) {
                    JsonObject json = jsonElement.getAsJsonObject();
                    String name = json.get("name").getAsString();
                    String type = json.get("type").getAsString();
                    String getPath = json.get("path").getAsString().replace(this.mainFolder + "/", "").replace(name, "");
                    if (type.equals("dir")) {
                        File file = new File(langFolder, name);
                        if (!file.exists())
                            file.mkdir();
                        si = downloadFromGitHub(folder + "/" + name);
                        continue;
                    }
                    if (type.equals("file") &&
                            name.endsWith(".yml") && name.contains("_")) {
                        String download = json.get("download_url").getAsString();
                        File file = new File(langFolder.getPath() + "/" + getPath, name);
                        file.delete();
                        downloadUsingCommons(download, file);
                    }
                }
            }
            return si;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void downloadUsingCommons(String url, File file) throws IOException {
        org.apache.commons.io.FileUtils.copyURLToFile(new URL(url), file);
    }

    public String readWithInputStream(String url) {
        if(!readWithInputStreamCache.containsKey(url)){
            try{
                URL javaURL = new URL(url);
                URLConnection connection = javaURL.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
                readWithInputStreamCache.put(url, new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().collect(Collectors.joining()));
            }catch (IOException ex){
                ex.printStackTrace();
                return null;
            }
        }

        return readWithInputStreamCache.get(url);
    }
}
