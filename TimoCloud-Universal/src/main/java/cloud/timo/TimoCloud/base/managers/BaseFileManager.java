package cloud.timo.TimoCloud.base.managers;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import org.apache.commons.io.FileDeleteStrategy;
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
        private File serverTemplatesDirectory;
            private File serverGlobalDirectory;
        private File proxyTemplatesDirectory;
            private File proxyGlobalDirectory;
    private File temporaryDirectory;
    private File serverTemporaryDirectory;
    private File proxyTemporaryDirectory;
    private File staticDirectory;
        private File serverStaticDirectory;
        private File proxyStaticDirectory;
    private File cacheDirectory;
    private File logsDirectory;
        private File serverLogsDirectory;
        private File proxyLogsDirectory;
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
            serverTemplatesDirectory = new File(templatesDirectory, "server/");
            serverTemplatesDirectory.mkdirs();
            serverGlobalDirectory = new File(serverTemplatesDirectory, "Global/");
            serverGlobalDirectory.mkdirs();
            proxyTemplatesDirectory = new File(templatesDirectory, "proxy/");
            proxyTemplatesDirectory.mkdirs();
            proxyGlobalDirectory = new File(proxyTemplatesDirectory, "Global/");
            proxyGlobalDirectory.mkdirs();
            temporaryDirectory = new File(baseDirectory, "temporary/");
            temporaryDirectory.mkdirs();
            serverTemporaryDirectory = new File(temporaryDirectory, "server/");
            serverTemporaryDirectory.mkdirs();
            proxyTemporaryDirectory = new File(temporaryDirectory, "proxy/");
            proxyTemporaryDirectory.mkdirs();
            staticDirectory = new File(baseDirectory, "static/");
            staticDirectory.mkdirs();
            serverStaticDirectory = new File(staticDirectory, "server/");
            serverStaticDirectory.mkdirs();
            proxyStaticDirectory = new File(staticDirectory, "proxy/");
            proxyStaticDirectory.mkdirs();

            cacheDirectory = new File(temporaryDirectory, "cache/");
            cacheDirectory.mkdirs();

            new File(serverGlobalDirectory, "plugins/").mkdirs();
            logsDirectory = new File(baseDirectory, "logs/");
            serverLogsDirectory = new File(logsDirectory, "server/");
            deleteDirectory(serverLogsDirectory);
            serverLogsDirectory.mkdirs();
            proxyLogsDirectory = new File(logsDirectory, "proxy/");
            deleteDirectory(proxyLogsDirectory);
            proxyLogsDirectory.mkdirs();

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
            TimoCloudBase.getInstance().severe(e);
        }
    }

    private void saveConfig() {
        try {
            FileWriter writer = new FileWriter(configFile);
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            new Yaml(dumperOptions).dump(config, writer);
        } catch (Exception e) {
            TimoCloudBase.getInstance().severe("Error while saving config: ");
            TimoCloudBase.getInstance().severe(e);
        }
    }

    public static void deleteDirectory(File directory) {
        if (directory.exists()) FileDeleteStrategy.FORCE.deleteQuietly(directory);
    }

    public File getBaseDirectory() {
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

    public File getServerTemporaryDirectory() {
        return serverTemporaryDirectory;
    }

    public File getProxyTemporaryDirectory() {
        return proxyTemporaryDirectory;
    }

    public File getStaticDirectory() {
        return staticDirectory;
    }

    public File getServerStaticDirectory() {
        return serverStaticDirectory;
    }

    public File getProxyStaticDirectory() {
        return proxyStaticDirectory;
    }

    public File getCacheDirectory() {
        return cacheDirectory;
    }

    public File getLogsDirectory() {
        return logsDirectory;
    }

    public File getServerLogsDirectory() {
        return serverLogsDirectory;
    }

    public File getProxyLogsDirectory() {
        return proxyLogsDirectory;
    }

    public File getConfigFile() {
        return configFile;
    }

    public Map getConfig() {
        return config;
    }

}
