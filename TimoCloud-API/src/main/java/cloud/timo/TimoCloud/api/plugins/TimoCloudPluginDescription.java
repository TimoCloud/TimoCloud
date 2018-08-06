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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimoCloudPluginDescription that = (TimoCloudPluginDescription) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (author != null ? !author.equals(that.author) : that.author != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (mainClass != null ? !mainClass.equals(that.mainClass) : that.mainClass != null) return false;
        if (depends != null ? !depends.equals(that.depends) : that.depends != null) return false;
        if (softDepends != null ? !softDepends.equals(that.softDepends) : that.softDepends != null) return false;
        if (file != null ? !file.equals(that.file) : that.file != null) return false;
        return plugin != null ? plugin.equals(that.plugin) : that.plugin == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (mainClass != null ? mainClass.hashCode() : 0);
        result = 31 * result + (depends != null ? depends.hashCode() : 0);
        result = 31 * result + (softDepends != null ? softDepends.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        result = 31 * result + (plugin != null ? plugin.hashCode() : 0);
        return result;
    }
}
