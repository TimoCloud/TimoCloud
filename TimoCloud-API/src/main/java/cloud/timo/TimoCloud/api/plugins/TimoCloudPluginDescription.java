package cloud.timo.TimoCloud.api.plugins;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class TimoCloudPluginDescription {

    private final String name;
    private final String author;
    private final String version;
    private final String mainClass;
    private final List<String> depends;
    private final List<String> softDepends;
    private final File file;

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

        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(author, that.author)) return false;
        if (!Objects.equals(version, that.version)) return false;
        if (!Objects.equals(mainClass, that.mainClass)) return false;
        if (!Objects.equals(depends, that.depends)) return false;
        if (!Objects.equals(softDepends, that.softDepends)) return false;
        return Objects.equals(file, that.file);
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
        return result;
    }
}
