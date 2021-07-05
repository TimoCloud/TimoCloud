package cloud.timo.TimoCloud.velocity.managers;

import cloud.timo.TimoCloud.common.utils.ChatColorUtil;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.moandjiezana.toml.Toml;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class VelocityFileManager {

    private final String baseDirectory = "plugins/TimoCloud/";
    private final String configsDirectory = baseDirectory + "configs/";
    private File configFile;
    private Toml config;
    private File messagesFile;
    private Toml messages;

    public VelocityFileManager() {
        load();
    }

    public void load() {
        config = loadConfig(new File(configsDirectory).toPath());
        messages = loadMessages(new File(configsDirectory).toPath());
        TimoCloudVelocity.getInstance().setPrefix(ChatColorUtil.translateAlternateColorCodes('&', config.getString("prefix") + " "));
    }

    private Toml loadConfig(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "config.toml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try (InputStream input = getClass().getResourceAsStream("/velocity/config.toml")) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }
        configFile = file;
        return new Toml().read(file);
    }

    private Toml loadMessages(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "messages.toml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try (InputStream input = getClass().getResourceAsStream("/velocity/messages.toml")) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }
        messagesFile = file;
        return new Toml().read(file);
    }

    public JsonArray loadJson(File file) {
        try {
            String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            if (fileContent == null || fileContent.trim().isEmpty()) fileContent = "[]";
            return new JsonParser().parse(fileContent).getAsJsonArray();
        } catch (Exception e) {
            TimoCloudVelocity.getInstance().severe(e);
            return null;
        }
    }

    public void saveJson(JsonArray jsonArray, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonArray)); //Prettify JSON
            fileWriter.close();
        } catch (Exception e) {
            TimoCloudVelocity.getInstance().severe(e);
        }
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public File getConfigFile() {
        return configFile;
    }

    public Toml getConfig() {
        return config;
    }

    public File getMessagesFile() {
        return messagesFile;
    }

    public Toml getMessages() {
        return messages;
    }
}