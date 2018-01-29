package cloud.timo.TimoCloud.base.managers;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class BaseFileManager {
    private File baseDirectory;
    private File configsDirectory;
    private File templatesDirectory;
    private File temporaryDirectory;
    private File staticDirectory;
    private File globalDirectory;
    private File logsDirectory;
    private File configFile;
    private Map<String, Object> config;

    public BaseFileManager() {
        load();
    }

    public void load() {
        try {
            baseDirectory = new File("base/");
            baseDirectory.mkdirs();
            configsDirectory = new File(baseDirectory, "configs/");
            configsDirectory.mkdirs();
            templatesDirectory = new File(baseDirectory, "templates/");
            templatesDirectory.mkdirs();
            temporaryDirectory = new File(baseDirectory, "temporary/");
            temporaryDirectory.mkdirs();
            staticDirectory = new File(baseDirectory, "static/");
            staticDirectory.mkdirs();
            globalDirectory = new File(templatesDirectory, "Global/");
            globalDirectory.mkdirs();
            new File(globalDirectory, "plugins/").mkdirs();
            logsDirectory = new File(baseDirectory, "logs/");
            logsDirectory.mkdirs();
            if (new File("plugins/").exists()) {
                TimoCloudBase.severe("The global 'plugins' directory is outdated and will no longer be used. Please move global plugins to 'templates/Global/plugins/' and delete the 'plugins/' directory to hide this warning.");
            }
            this.configFile = new File(getConfigsDirectory(), "config.yml");
            configFile.createNewFile();
            Yaml yaml = new Yaml();
            this.config = (Map<String, Object>) yaml.load(new FileReader(configFile));
            if (this.config == null) this.config = new HashMap<>();
            Map<String, Object> defaults = (Map<String, Object>) yaml.load(this.getClass().getResourceAsStream("/base/config.yml"));
            for (String key : defaults.keySet()) {
                if (! config.containsKey(key)) config.put(key, defaults.get(key));
            }
            saveConfig();
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

    public File getConfigsDirectory() {
        return configsDirectory;
    }

    public File getTemplatesDirectory() {
        return templatesDirectory;
    }

    public File getTemporaryDirectory() {
        return temporaryDirectory;
    }

    public File getStaticDirectory() {
        return staticDirectory;
    }

    public File getGlobalDirectory() {
        return globalDirectory;
    }

    public File getLogsDirectory() {
        return logsDirectory;
    }

    public File getConfigFile() {
        return configFile;
    }

    public Map getConfig() {
        return config;
    }

}
