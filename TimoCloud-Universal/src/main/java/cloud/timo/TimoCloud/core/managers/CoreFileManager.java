package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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

    private File configFile;
    private Map<String, Object> config;
    private File serverGroupsFile;
    private File proxyGroupsFile;

    public CoreFileManager() {}

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

            this.configFile = new File(configsDirectory, "config.yml");
            configFile.createNewFile();
            Yaml yaml = new Yaml();
            this.config = (Map<String, Object>) yaml.load(new FileReader(configFile));
            if (this.config == null) this.config = new HashMap<>();
            Map<String, Object> defaults = (Map<String, Object>) yaml.load(this.getClass().getResourceAsStream("/core/config.yml"));
            for (String key : defaults.keySet()) {
                if (!config.containsKey(key)) config.put(key, defaults.get(key));
            }
            saveConfig();

            this.serverGroupsFile = new File(configsDirectory, "serverGroups.json");
            serverGroupsFile.createNewFile();
            this.proxyGroupsFile = new File(configsDirectory, "proxyGroups.json");
            proxyGroupsFile.createNewFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveConfig() {
        try {
            FileWriter writer = new FileWriter(configFile);
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            new Yaml(dumperOptions).dump(config, writer);
        } catch (Exception e) {
            TimoCloudBase.severe("Error while saving config: ");
            e.printStackTrace();
        }
    }

    public JSONArray loadJson(File file) throws IOException, ParseException {
        String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
        return (JSONArray) new JSONParser().parse(fileContent == null || fileContent.trim().isEmpty() ? "[]" : fileContent);
    }

    public void saveJson(JSONArray jsonArray, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file, false);
        fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(jsonArray.toJSONString()))); //Prettify JSON
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

    public File getConfigFile() {
        return configFile;
    }

    public Map getConfig() {
        return config;
    }

    public File getServerGroupsFile() {
        return serverGroupsFile;
    }

    public File getProxyGroupsFile() {
        return proxyGroupsFile;
    }
}
