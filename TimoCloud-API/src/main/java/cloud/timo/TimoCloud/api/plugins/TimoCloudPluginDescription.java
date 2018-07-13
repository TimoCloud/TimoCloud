package cloud.timo.TimoCloud.api.plugins;

import java.io.File;
import java.util.List;

public class TimoCloudPluginDescription {
    private String name;
    private String author;
    private String version;
    private String mainClass;
    private List<String> depends;
    private List<String> softDepends;
    private File file;

    // Store main class of the plugin
    private TimoCloudPlugin plugin;

    public TimoCloudPluginDescription(String name, String author, String version, String mainClass, List<String> depends, List<String> softDepends, File file) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.mainClass = mainClass;
        this.depends = depends;
        this.softDepends = softDepends;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getVersion() {
        return version;
    }

    public String getMainClass() {
        return mainClass;
    }

    public List<String> getDepends() {
        return depends;
    }

    public List<String> getSoftDepends() {
        return softDepends;
    }

    public File getFile() {
        return file;
    }

    public TimoCloudPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(TimoCloudPlugin plugin) {
        this.plugin = plugin;
    }
}
