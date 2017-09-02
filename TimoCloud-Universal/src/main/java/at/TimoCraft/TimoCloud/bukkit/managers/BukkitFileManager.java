package at.TimoCraft.TimoCloud.bukkit.managers;

import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import at.TimoCraft.TimoCloud.utils.ServerToGroupUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;

/**
 * Created by Timo on 27.12.16.
 */
public class BukkitFileManager {

    private File configFile;
    private FileConfiguration config;
    private File signsFile;
    private FileConfiguration signs;
    private File dynamicSignsFile;
    private FileConfiguration dynamicSigns;
    private File signLayoutsFile;
    private FileConfiguration signLayouts;
    private File signBlocksFile;
    private FileConfiguration signBlocks;

    public BukkitFileManager() {
        init();
    }

    public void init() {
        try {
            File path  = new File(TimoCloudBukkit.getInstance().getTemplate(), "/plugins/TimoCloud/");
            path.mkdirs();

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

            dynamicSignsFile = new File(path, "dynamicSigns.yml");
            if (!dynamicSignsFile.exists()) {
                dynamicSignsFile.createNewFile();
            }
            dynamicSigns = YamlConfiguration.loadConfiguration(dynamicSignsFile);

            signLayoutsFile = new File(path, "signLayouts.yml");
            if (!signLayoutsFile.exists()) {
                signLayoutsFile.createNewFile();
            }
            signLayouts = YamlConfiguration.loadConfiguration(signLayoutsFile);

            signBlocksFile = new File(path, "signBlocks.yml");
            if (!signBlocksFile.exists()) {
                signBlocksFile.createNewFile();
            }
            signBlocks = YamlConfiguration.loadConfiguration(signBlocksFile);

            config.options().copyDefaults(true);
            config.addDefault("prefix", "&6[&bTimo&fCloud&6]");
            config.addDefault("updateSignsInServerTicks", 45L);
            config.addDefault("defaultMapName", "Village");
            config.addDefault("MotdToState.§aOnline", "ONLINE");
            config.addDefault("PlayersToState.enabled", false);
            config.addDefault("PlayersToState.full", "FULL");
            TimoCloudBukkit.getInstance().setPrefix(config.getString("prefix"));
            config.save(configFile);

            signBlocks.options().copyDefaults(true);
            signBlocks.addDefault("ONLINE.type", "STAINED_CLAY");
            signBlocks.addDefault("ONLINE.dataValue", 5);
            signBlocks.addDefault("OFFLINE.type", "STAINED_CLAY");
            signBlocks.addDefault("OFFLINE.dataValue", 14);
            signBlocks.save(signBlocksFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addLayoutDefaults(String group) {
        signLayouts.options().copyDefaults(true);
        signLayouts.addDefault(group + ".sortOut", Arrays.asList("OFFLINE"));
        try {
            signLayouts.save(signLayoutsFile);
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

    public File getDynamicSignsFile() {
        return dynamicSignsFile;
    }

    public FileConfiguration getDynamicSigns() {
        return dynamicSigns;
    }

    public File getSignBlocksFile() {
        return signBlocksFile;
    }

    public FileConfiguration getSignBlocks() {
        return signBlocks;
    }
}
