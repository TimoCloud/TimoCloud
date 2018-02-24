package cloud.timo.TimoCloud.bukkit.managers;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BukkitFileManager {

    private File path;
    private File signsPath;

    private File configFile;
    private File signTemplatesFile;
    private File signInstancesFile;

    private FileConfiguration config;
    private FileConfiguration signTemplates;

    public BukkitFileManager() {
        load();
    }

    public void load() {
        try {
            path  = new File(TimoCloudBukkit.getInstance().getTemporaryDirectory(), "/plugins/TimoCloud/");
            path.mkdirs();

            loadConfig();
            loadSignConfigs();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        try {
            configFile = new File(path, "config.yml");
            configFile.createNewFile();
            config = YamlConfiguration.loadConfiguration(configFile);

            addConfigDefaults();
        } catch (Exception e) {
            TimoCloudBukkit.getInstance().severe("Error while loading config.yml: ");
            e.printStackTrace();
        }
    }

    public void loadSignConfigs() {
        try {
            signsPath = new File(path, "/signs/");
            signsPath.mkdirs();

            signTemplatesFile = new File(signsPath, "signTemplates.yml");
            signTemplatesFile.createNewFile();
            signTemplates = YamlConfiguration.loadConfiguration(signTemplatesFile);

            signInstancesFile = new File(signsPath, "signInstances.json");
            signInstancesFile.createNewFile();

            addSignTemplatesDefaults();
        } catch (Exception e) {
            TimoCloudBukkit.getInstance().severe("Error while load sign configs: ");
            e.printStackTrace();
        }
    }

    private void addConfigDefaults() {
        config.options().copyDefaults(true);
        config.addDefault("prefix", "&6[&bTimo&fCloud&6]");
        config.addDefault("defaultMapName", "Village");
        config.addDefault("MotdToState.§aOnline", "ONLINE");
        config.addDefault("PlayersToState.enabledWhileStates", Arrays.asList("WAITING", "LOBBY"));
        config.addDefault("PlayersToState.percentages.100,0", "FULL");
        config.addDefault("PlayersToState.percentages.50,0", "HALF_FULL");
        TimoCloudBukkit.getInstance().setPrefix(config.getString("prefix"));
        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSignTemplatesDefaults() {
        signTemplates.options().copyDefaults(true);

        signTemplates.addDefault("Default.layouts.Default.lines.1", "[&3%name%&0]");
        signTemplates.addDefault("Default.layouts.Default.lines.2", "&6%state%");
        signTemplates.addDefault("Default.layouts.Default.lines.3", "&5%map%");
        signTemplates.addDefault("Default.layouts.Default.lines.4", "%current_players%/%max_players%");
        signTemplates.addDefault("Default.layouts.Default.updateSpeed", 0L);

        signTemplates.addDefault("Default.layouts.STARTING.lines.1", "[&3%name%&0]");
        signTemplates.addDefault("Default.layouts.STARTING.lines.2", "&eServer is");
        signTemplates.addDefault("Default.layouts.STARTING.lines.3", "&estarting...");
        signTemplates.addDefault("Default.layouts.STARTING.lines.4", "&2▲▲▲;&2▶▲▲;&2▶▶▲;&2▶▶▶;&2▲▶▶;&2▲▲▶;&2▲▲▲;&2▲▲◀;&2▲◀◀;&2◀◀◀;&2◀◀▲;&2◀▲▲");
        signTemplates.addDefault("Default.layouts.STARTING.updateSpeed", 5L);
        signTemplates.addDefault("Default.layouts.STARTING.signBlockMaterial", "STAINED_CLAY");
        signTemplates.addDefault("Default.layouts.STARTING.signBlockData", 4);

        signTemplates.addDefault("Default.layouts.ONLINE.lines.1", "[&3%name%&0]");
        signTemplates.addDefault("Default.layouts.ONLINE.lines.2", "&aOnline");
        signTemplates.addDefault("Default.layouts.ONLINE.lines.3", "&5%map%");
        signTemplates.addDefault("Default.layouts.ONLINE.lines.4", "%current_players%/%max_players%");
        signTemplates.addDefault("Default.layouts.ONLINE.updateSpeed", 0L);
        signTemplates.addDefault("Default.layouts.ONLINE.signBlockMaterial", "STAINED_CLAY");
        signTemplates.addDefault("Default.layouts.ONLINE.signBlockData", 5);

        signTemplates.addDefault("NoFreeServerFound.layouts.Default.lines.1", "&cWaiting");
        signTemplates.addDefault("NoFreeServerFound.layouts.Default.lines.2", "&cfor");
        signTemplates.addDefault("NoFreeServerFound.layouts.Default.lines.3", "&cserver");
        signTemplates.addDefault("NoFreeServerFound.layouts.Default.lines.4", "&2▲▲▲;&2▶▲▲;&2▶▶▲;&2▶▶▶;&2▲▶▶;&2▲▲▶;&2▲▲▲;&2▲▲◀;&2▲◀◀;&2◀◀◀;&2◀◀▲;&2◀▲▲");
        signTemplates.addDefault("NoFreeServerFound.layouts.Default.updateSpeed", 5L);
        signTemplates.addDefault("NoFreeServerFound.layouts.Default.signBlockMaterial", "STAINED_CLAY");
        signTemplates.addDefault("NoFreeServerFound.layouts.Default.signBlockData", 8);

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
            fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(jsonArray.toJSONString()))); //Prettify JSON
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
