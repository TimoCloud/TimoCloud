package at.TimoCraft.TimoCloud.bukkit.managers;

import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by Timo on 27.12.16.
 */
public class BukkitFileManager {

    private File configFile;
    private File signTemplatesFile;
    private File signInstancesFile;

    private FileConfiguration config;
    private FileConfiguration signTemplates;

    public BukkitFileManager() {
        init();
    }

    public void init() {
        try {
            File path  = new File(TimoCloudBukkit.getInstance().getTemplateDirectory(), "/plugins/TimoCloud/");
            File signsPath = new File(path, "/signs/");
            signsPath.mkdirs(); // Will create #path as well

            configFile = new File(path, "config.yml");
            configFile.createNewFile();
            config = YamlConfiguration.loadConfiguration(configFile);


            signTemplatesFile = new File(signsPath, "signTemplates.yml");
            signTemplatesFile.createNewFile();
            signTemplates = YamlConfiguration.loadConfiguration(signTemplatesFile);

            signInstancesFile = new File(signsPath, "signInstances.json");
            signInstancesFile.createNewFile();

            if (new File(path, "signLayouts.yml").exists() || new File(path, "signs.yml").exists() || new File(path, "dynamicSigns.yml").exists()) {
                TimoCloudBukkit.log("&eOld signLayouts.yml, signs.yml and dynamicSigns.yml are no longer supported and will be ignored. Please update your configuration to the new layout in the 'signs' folder and delete the old files to hide this warning.");
            }

            addConfigDefaults();
            addSignTemplatesDefaults();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addConfigDefaults() {
        config.options().copyDefaults(true);
        config.addDefault("prefix", "&6[&bTimo&fCloud&6]");
        if (config.get("updateSignsInServerTicks") != null) TimoCloudBukkit.log("&eThe 'updateSignsInServerTicks' setting is no longer supported and will be ignored. Remove it from config to hide this warning.");
        config.addDefault("defaultMapName", "Village");
        config.addDefault("MotdToState.§aOnline", "ONLINE");
        config.addDefault("PlayersToState.enabled", false);
        config.addDefault("PlayersToState.full", "FULL");
        TimoCloudBukkit.getInstance().setPrefix(config.getString("prefix"));
        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSignTemplatesDefaults() {
        signTemplates.options().copyDefaults(true);

        signTemplates.addDefault("Default.layouts.Default.updateSpeed", 0L);
        signTemplates.addDefault("Default.layouts.Default.lines.1", "[&3%name%&0]");
        signTemplates.addDefault("Default.layouts.Default.lines.2", "&6%state%");
        signTemplates.addDefault("Default.layouts.Default.lines.3", "&5%map%");
        signTemplates.addDefault("Default.layouts.Default.lines.4", "%current_players%/%max_players%");

        signTemplates.addDefault("Default.layouts.ONLINE.updateSpeed", 0L);
        signTemplates.addDefault("Default.layouts.ONLINE.lines.1", "[&3%name%&0]");
        signTemplates.addDefault("Default.layouts.ONLINE.lines.2", "&aOnline");
        signTemplates.addDefault("Default.layouts.ONLINE.lines.3", "&5%map%");
        signTemplates.addDefault("Default.layouts.ONLINE.lines.4", "%current_players%/%max_players%");

        signTemplates.addDefault("Default.layouts.STARTING.updateSpeed", 5L);
        signTemplates.addDefault("Default.layouts.STARTING.lines.1", "[&3%name%&0]");
        signTemplates.addDefault("Default.layouts.STARTING.lines.2", "&eServer is");
        signTemplates.addDefault("Default.layouts.STARTING.lines.3", "&estarting...");
        signTemplates.addDefault("Default.layouts.STARTING.lines.4", "&2▲▲▲;&2▶▲▲;&2▶▶▲;&2▶▶▶;&2▲▶▶;&2▲▲▶;&2▲▲▲;&2▲▲◀;&2▲◀◀;&2◀◀◀;&2◀◀▲;&2◀▲▲");

        signTemplates.addDefault("NoFreeServerFound.layouts.Default.updateSpeed", 5L);
        signTemplates.addDefault("NoFreeServerFound.layouts.Default.lines.1", "&cWaiting");
        signTemplates.addDefault("NoFreeServerFound.layouts.Default.lines.2", "&cfor");
        signTemplates.addDefault("NoFreeServerFound.layouts.Default.lines.3", "&cserver");
        signTemplates.addDefault("NoFreeServerFound.layouts.Default.lines.4", "&2▲▲▲;&2▶▲▲;&2▶▶▲;&2▶▶▶;&2▲▶▶;&2▲▲▶;&2▲▲▲;&2▲▲◀;&2▲◀◀;&2◀◀◀;&2◀◀▲;&2◀▲▲");

        try {
            signTemplates.save(signTemplatesFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getConfigFile() {
        return configFile;
    }

    public File getSignTemplatesFile() {
        return signTemplatesFile;
    }

    public File getSignInstancesFile() {
        return signInstancesFile;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getSignTemplates() {
        return signTemplates;
    }

    public JSONArray getSignInstances() {
        try {
            String fileContent = FileUtils.readFileToString(signInstancesFile, StandardCharsets.UTF_8.name());
            return (JSONArray) new JSONParser().parse(fileContent == null || fileContent.trim().isEmpty() ? "[]" : fileContent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveSignInstances(JSONArray jsonArray) {
        try {
            FileWriter fileWriter = new FileWriter(signInstancesFile, false);
            fileWriter.write(jsonArray.toJSONString());
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
