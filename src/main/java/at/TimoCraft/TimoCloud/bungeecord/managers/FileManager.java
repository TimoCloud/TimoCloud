package at.TimoCraft.TimoCloud.bungeecord.managers;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.nio.file.Files;

/**
 * Created by Timo on 27.12.16.
 */
public class FileManager {
    private String pluginsDirectory = "plugins/TimoCloud/";
    private String templatesDirectory = pluginsDirectory + "templates/";
    private String temporaryDirectory = pluginsDirectory + "temporary/";
    private String scriptsDirectory = pluginsDirectory + "scripts/";
    private String configsDirectory = pluginsDirectory + "configs/";
    private String logsDirectory = pluginsDirectory + "logs/";
    private File configFile;
    private File groupsFile;
    private Configuration config;
    private Configuration groups;

    public FileManager() {
        init();
    }

    private void init() {
        try {
            File templates = new File(templatesDirectory);
            File temporary = new File(temporaryDirectory);
            File scripts = new File(scriptsDirectory);
            File configs = new File(configsDirectory);
            File logs = new File(logsDirectory);
            templates.mkdirs();
            temporary.mkdirs();
            scripts.mkdirs();
            configs.mkdirs();
            logs.mkdirs();

            configFile = new File(configsDirectory, "config.yml");
            if (! configFile.exists()) {
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
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            groupsFile = new File(configsDirectory, "groups.yml");
            groupsFile.createNewFile();
            groups = ConfigurationProvider.getProvider(YamlConfiguration.class).load(groupsFile);
            TimoCloud.getInstance().setPrefix(config.getString("prefix").replace("&", "§") + " ");
            File startserverSH = new File(getScriptsDirectory(), "startserver.sh");
            if (startserverSH.exists()) {
                startserverSH.delete();
            }
            Files.copy(this.getClass().getResourceAsStream("/bungeecord/startserver.sh"), startserverSH.toPath());
            startserverSH.setExecutable(true);
        } catch (Exception e) {
            TimoCloud.severe("Exception while initializing files:");
            e.printStackTrace();
        }
    }

    public String getTemplatesDirectory() {
        return templatesDirectory;
    }

    public String getTemporaryDirectory() {
        return temporaryDirectory;
    }

    public String getScriptsDirectory() {
        return scriptsDirectory;
    }

    public String getLogsDirectory() {
        return logsDirectory;
    }

    public File getConfigFile() {
        return configFile;
    }

    public File getGroupsFile() {
        return groupsFile;
    }

    public Configuration getConfig() {
        return config;
    }

    public Configuration getGroups() {
        return groups;
    }
}
