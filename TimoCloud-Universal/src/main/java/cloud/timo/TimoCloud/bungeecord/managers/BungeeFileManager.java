package cloud.timo.TimoCloud.bungeecord.managers;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class BungeeFileManager {

    private String baseDirectory = "plugins/TimoCloud/";
    private String configsDirectory = baseDirectory + "configs/";
    private File configFile;
    private Configuration config;
    private File messagesFile;
    private Configuration messages;

    public BungeeFileManager() {
        load();
    }

    public void load() {
        try {
            File configs = new File(configsDirectory);
            configs.mkdirs();

            //Load configFile
            configFile = new File(configsDirectory, "config.yml");
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            configFile.delete();
            Files.copy(this.getClass().getResourceAsStream("/bungeecord/config.yml"), configFile.toPath());
            Configuration configNew = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            for (String key : config.getKeys()) {
                configNew.set(key, config.get(key));
            }
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configNew, configFile);
            config = configNew;

            //Load messagesFile
            messagesFile = new File(configsDirectory, "protocol.yml");
            if (!messagesFile.exists()) {
                messagesFile.createNewFile();
            }
            messages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(messagesFile);
            messagesFile.delete();
            Files.copy(this.getClass().getResourceAsStream("/bungeecord/messages.yml"), messagesFile.toPath());
            Configuration messagesNew = ConfigurationProvider.getProvider(YamlConfiguration.class).load(messagesFile);
            for (String key : messages.getKeys()) {
                messagesNew.set(key, messages.get(key));
            }
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(messagesNew, messagesFile);
            messages = messagesNew;

            TimoCloudBungee.getInstance().setPrefix(ChatColor.translateAlternateColorCodes('&', config.getString("prefix") + " "));
        } catch (Exception e) {
            TimoCloudBungee.getInstance().severe("Exception while initializing files:");
            TimoCloudBungee.getInstance().severe(e);
        }
    }

    public JsonArray loadJson(File file) {
        try {
            String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            if (fileContent == null || fileContent.trim().isEmpty()) fileContent = "[]";
            return new JsonParser().parse(fileContent).getAsJsonArray();
        } catch (Exception e) {
            TimoCloudBungee.getInstance().severe(e);
            return null;
        }
    }

    public void saveJson(JsonArray jsonArray, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonArray)); //Prettify JSON
            fileWriter.close();
        } catch (Exception e) {
            TimoCloudBungee.getInstance().severe(e);
        }
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public File getConfigFile() {
        return configFile;
    }

    public Configuration getConfig() {
        return config;
    }

    public File getMessagesFile() {
        return messagesFile;
    }

    public Configuration getMessages() {
        return messages;
    }
}