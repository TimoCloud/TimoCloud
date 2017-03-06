package at.TimoCraft.TimoCloud.bungeecord.managers;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;

/**
 * Created by Timo on 27.12.16.
 */
public class FileManager {
    private String pluginsDirectory = "plugins/TimoCloud/";
    private String configsDirectory = pluginsDirectory + "configs/";
    private File configFile;
    private File groupsFile;
    private Configuration config;
    private Configuration groups;

    public FileManager() {
        load();
    }

    public void load() {
        try {
            File configs = new File(configsDirectory);
            configs.mkdirs();

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
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            groupsFile = new File(configsDirectory, "groups.yml");
            groupsFile.createNewFile();
            groups = ConfigurationProvider.getProvider(YamlConfiguration.class).load(groupsFile);
            TimoCloud.getInstance().setPrefix(config.getString("prefix").replace("&", "§") + " ");

            //Delete old scripts folder which is no longer needed
            File scripts = new File(pluginsDirectory, "scripts/");
            if (scripts.exists()) {
                FileUtils.deleteDirectory(scripts);
            }

        } catch (Exception e) {
            TimoCloud.severe("Exception while initializing files:");
            e.printStackTrace();
        }
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
