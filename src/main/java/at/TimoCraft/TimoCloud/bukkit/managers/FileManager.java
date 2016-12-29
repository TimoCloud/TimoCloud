package at.TimoCraft.TimoCloud.bukkit.managers;

import at.TimoCraft.TimoCloud.bukkit.Main;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Created by Timo on 27.12.16.
 */
public class FileManager {
    private String path;
    private File configFile;
    private FileConfiguration config;
    private File signsFile;
    private FileConfiguration signs;
    private File signLayoutsFile;
    private FileConfiguration signLayouts;

    public FileManager() {
        init();
    }

    public void init() {
        try {
            path = "../../templates/" + Main.getInstance().getGroupByServer(Main.getInstance().getServerName()) + "/plugins/TimoCloud/";
            File directory = new File(path);
            directory.mkdirs();

            configFile = new File(path, "config.yml");
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            config = YamlConfiguration.loadConfiguration(configFile);

            signsFile = new File(path, "signs.yml");
            if (!signsFile.exists()) {
                signsFile.createNewFile();
            }
            signs = YamlConfiguration.loadConfiguration(signsFile);

            signLayoutsFile = new File(path, "signLayouts.yml");
            if (!signLayoutsFile.exists()) {
                signLayoutsFile.createNewFile();
            }
            signLayouts = YamlConfiguration.loadConfiguration(signLayoutsFile);

            config.options().copyDefaults(true);
            config.addDefault("prefix", "&6[&bTimo&fCloud&6]");
            config.addDefault("socket-port", 5000);
            Main.getInstance().setPrefix(config.getString("prefix"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getSigns() {
        return signs;
    }

    public File getSignsFile() {
        return signsFile;
    }

    public File getSignLayoutsFile() {
        return signLayoutsFile;
    }

    public FileConfiguration getSignLayouts() {
        return signLayouts;
    }
}
