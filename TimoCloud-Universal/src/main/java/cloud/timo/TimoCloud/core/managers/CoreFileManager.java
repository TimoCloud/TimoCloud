package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.lib.messages.Message;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class CoreFileManager {
    private File baseDirectory;
    private File configsDirectory;
    private File templatesDirectory;
    private File serverTemplatesDirectory;
    private File serverGlobalDirectory;
    private File proxyTemplatesDirectory;
    private File proxyGlobalDirectory;
    private File temporaryDirectory;
    private File logsDirectory;
    private File debugDirectory;
    private File pluginsDirectory;

    private File configFile;
    private File cloudFlareConfigFile;
    private Map<String, Object> config;
    private Map<String, Object> cloudFlareConfig;
    private File serverGroupsFile;
    private File proxyGroupsFile;

    public CoreFileManager() {
    }

    public void load() {
        try {
            baseDirectory = new File("core/");
            baseDirectory.mkdirs();
            configsDirectory = new File(baseDirectory, "configs/");
            configsDirectory.mkdirs();
            templatesDirectory = new File(baseDirectory, "templates/");
            templatesDirectory.mkdirs();

            serverTemplatesDirectory = new File(templatesDirectory, "server");
            serverTemplatesDirectory.mkdirs();
            serverGlobalDirectory = new File(serverTemplatesDirectory, "Global/");
            serverGlobalDirectory.mkdirs();
            new File(serverGlobalDirectory, "plugins/").mkdirs();

            proxyTemplatesDirectory = new File(templatesDirectory, "proxy/");
            proxyTemplatesDirectory.mkdirs();
            proxyGlobalDirectory = new File(proxyTemplatesDirectory, "Global/");
            proxyGlobalDirectory.mkdirs();
            new File(proxyGlobalDirectory, "plugins/").mkdirs();

            temporaryDirectory = new File(baseDirectory, "temporary/");
            temporaryDirectory.mkdirs();
            logsDirectory = new File(baseDirectory, "logs/");
            logsDirectory.mkdirs();
            debugDirectory = new File(baseDirectory, "debug/");
            debugDirectory.mkdirs();

            pluginsDirectory = new File(baseDirectory, "plugins/");
            pluginsDirectory.mkdirs();

            this.configFile = new File(configsDirectory, "config.yml");
            configFile.createNewFile();
            config = (Map<String, Object>) loadYaml(configFile);
            if (config == null) config = new LinkedHashMap<>();
            Map<String, Object> defaults = (Map<String, Object>) new Yaml().load(this.getClass().getResourceAsStream("/core/config.yml"));
            for (String key : defaults.keySet()) {
                if (!config.containsKey(key)) config.put(key, defaults.get(key));
            }

            this.cloudFlareConfigFile = new File(configsDirectory, "cloudFlare.yml");
            cloudFlareConfigFile.createNewFile();
            cloudFlareConfig = (Map<String, Object>) loadYaml(cloudFlareConfigFile);
            if (cloudFlareConfig == null) cloudFlareConfig = new LinkedHashMap<>();
            Map<String, Object> cloudFlareDefaults = (Map<String, Object>) new Yaml().load(this.getClass().getResourceAsStream("/core/cloudFlare.yml"));
            for (String key : cloudFlareDefaults.keySet()) {
                if (!cloudFlareConfig.containsKey(key)) cloudFlareConfig.put(key, cloudFlareDefaults.get(key));
            }
            saveConfigs();

            this.serverGroupsFile = new File(configsDirectory, "serverGroups.json");
            serverGroupsFile.createNewFile();
            this.proxyGroupsFile = new File(configsDirectory, "proxyGroups.json");
            proxyGroupsFile.createNewFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveConfigs() throws IOException {
        saveYaml(config, configFile);
        saveYaml(cloudFlareConfig, cloudFlareConfigFile);
    }

    public Object loadYaml(File file) throws IOException {
        FileReader reader = new FileReader(file);
        Object data = new Yaml().load(reader);
        reader.close();
        return data;
    }

    public void saveYaml(Object data, File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        new Yaml(dumperOptions).dump(data, writer);
        writer.close();
    }

    public JsonArray loadJsonArray(File file) throws IOException {
        String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
        if (fileContent == null || fileContent.trim().isEmpty()) fileContent = "[]";
        return new JsonParser().parse(fileContent).getAsJsonArray();
    }

    public void saveJson(Message message, File file) throws IOException {
        saveJson(message.toJsonObject(), file);
    }

    public void saveJson(JsonElement json, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file, false);
        fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(json));
        fileWriter.close();
    }

    public File getBaseDirectory() {
        if (baseDirectory == null) return new File("core/");
        return baseDirectory;
    }

    public File getConfigsDirectory() {
        return configsDirectory;
    }

    public File getTemplatesDirectory() {
        return templatesDirectory;
    }

    public File getServerTemplatesDirectory() {
        return serverTemplatesDirectory;
    }

    public File getServerGlobalDirectory() {
        return serverGlobalDirectory;
    }

    public File getProxyTemplatesDirectory() {
        return proxyTemplatesDirectory;
    }

    public File getProxyGlobalDirectory() {
        return proxyGlobalDirectory;
    }

    public File getTemporaryDirectory() {
        return temporaryDirectory;
    }

    public File getLogsDirectory() {
        if (logsDirectory == null) return new File(getBaseDirectory(), "logs/");
        return logsDirectory;
    }

    public File getDebugDirectory() {
        return debugDirectory;
    }

    public File getPluginsDirectory() {
        return pluginsDirectory;
    }

    public File getConfigFile() {
        return configFile;
    }

    public Map getConfig() {
        return config;
    }

    public File getCloudFlareConfigFile() {
        return cloudFlareConfigFile;
    }

    public Map<String, Object> getCloudFlareConfig() {
        return cloudFlareConfig;
    }

    public File getServerGroupsFile() {
        return serverGroupsFile;
    }

    public File getProxyGroupsFile() {
        return proxyGroupsFile;
    }
}
