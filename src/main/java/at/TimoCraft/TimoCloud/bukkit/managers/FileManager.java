package at.TimoCraft.TimoCloud.bukkit.managers;

import at.TimoCraft.TimoCloud.bukkit.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Files;

/**
 * Created by Timo on 27.12.16.
 */
public class FileManager {
    private String path = "plugins/TimoCloud/";
    private File configFile;
    private FileConfiguration config;

    public FileManager() {
        init();
    }

    private void init() {
        try {
            File directory = new File(path);
            directory.mkdirs();
            configFile = new File(path, "config.yml");
            if (!configFile.exists()) {
                Files.copy(this.getClass().getResourceAsStream("bukkit/config.yml"), configFile.toPath());
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            Main.getInstance().setPrefix(config.getString("prefix"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
