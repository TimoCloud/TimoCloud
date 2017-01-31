package at.TimoCraft.TimoCloud.base.managers;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Timo on 31.01.17.
 */
public class FileManager {
    private String configsDirectory = "configs/";
    private String templatesDirectory = "templates/";
    private String temporaryDirectory = "temporary/";
    private File configFile;
    private Map config;

    public FileManager() {
        load();
    }

    public void load() {
        try {
            new File(getConfigsDirectory()).mkdirs();
            new File(getTemplatesDirectory()).mkdirs();
            new File(getTemporaryDirectory()).mkdirs();
            configFile = new File(getConfigsDirectory(), "config.yml");
            configFile.createNewFile();
            Yaml configYaml = new Yaml();
            configYaml.load(new FileInputStream(configFile));
            config = (Map) configYaml;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getConfigsDirectory() {
        return configsDirectory;
    }

    public String getTemplatesDirectory() {
        return templatesDirectory;
    }

    public String getTemporaryDirectory() {
        return temporaryDirectory;
    }

    public File getConfigFile() {
        return configFile;
    }

    public Map getConfig() {
        return config;
    }
}
