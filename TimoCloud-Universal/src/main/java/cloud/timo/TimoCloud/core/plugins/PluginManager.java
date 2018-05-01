package cloud.timo.TimoCloud.core.plugins;

import cloud.timo.TimoCloud.api.plugins.PluginLoadException;
import cloud.timo.TimoCloud.api.plugins.TimoCloudPlugin;
import cloud.timo.TimoCloud.api.plugins.TimoCloudPluginDescription;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.lib.Assert;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginManager {

    private Map<String, TimoCloudPluginDescription> pluginDescriptions;
    private Map<TimoCloudPluginDescription, TimoCloudPlugin> plugins;

    public PluginManager() {

    }

    public void loadPlugins() {
        pluginDescriptions = new LinkedHashMap<>();
        plugins = new LinkedHashMap<>();

        for (File file : TimoCloudCore.getInstance().getFileManager().getPluginsDirectory().listFiles()) {
            if (file.isDirectory()) continue;
            if (! file.getName().endsWith(".jar")) continue;
            try {
                TimoCloudPluginDescription plugin = loadPlugin(file);
                pluginDescriptions.put(plugin.getName(), plugin);
            } catch (Exception e) {
                TimoCloudCore.getInstance().severe("Error while loading plugin '" + file.getName() + "': ");
                TimoCloudCore.getInstance().severe(e);
            }
        }
        Set<TimoCloudPluginDescription> used = new HashSet<>();
        List<TimoCloudPluginDescription> order = new LinkedList<>();
        for (TimoCloudPluginDescription plugin : pluginDescriptions.values()) {
            try {
                load(plugin, order, used, order.size());
            } catch (PluginLoadException e) {
                TimoCloudCore.getInstance().severe(e);
            }
        }
        for (TimoCloudPluginDescription plugin : order) {
            try {
                URLClassLoader classLoader = new URLClassLoader(new URL[] {plugin.getFile().toURI().toURL()});
                Class<?> main = classLoader.loadClass(plugin.getMainClass());
                if (! TimoCloudPlugin.class.isAssignableFrom(main)) {
                    throw new PluginLoadException("Main class does not extend TimoCloudPlugin");
                }
                TimoCloudPlugin mainInstance = (TimoCloudPlugin) main.getDeclaredConstructor().newInstance();
                try {
                    mainInstance.onLoad();
                    TimoCloudCore.getInstance().info("Loaded plugin " + plugin.getName() + " version " + plugin.getVersion() + " by " + plugin.getAuthor() + ".");
                    plugins.put(plugin, mainInstance);
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe("Error while enabling plugin " + plugin.getName() + " version " + plugin.getVersion() + " by " + plugin.getAuthor() + ": ");
                    TimoCloudCore.getInstance().severe(e);
                }
            } catch (Exception e) {
                TimoCloudCore.getInstance().severe("Error while class-loading plugin '" + plugin.getName() + "': ");
                TimoCloudCore.getInstance().severe(e);
            }

        }
    }

    public void load(TimoCloudPluginDescription plugin, List<TimoCloudPluginDescription> order, Set<TimoCloudPluginDescription> used, int index) throws PluginLoadException {
        if (used.contains(plugin)) return;
        used.add(plugin);
        for (String depend : plugin.getDepends()) {
            TimoCloudPluginDescription dependPlugin = getPluginDescription(depend);
            if (dependPlugin == null) {
                throw new PluginLoadException("Error while loading plugin '" + plugin.getName() + "': Dependency '" + depend + "' could not be found.");
            }
            if (used.contains(dependPlugin)) {
                throw new PluginLoadException("Error while loading plugin '" + plugin.getName() + "': Dependency cycle between pluginDescriptions: '" + plugin.getName() + "' depends on '" + dependPlugin.getName() + "' which directly or indirectly depends on '" + plugin.getName() + "' again.");
            }
            load(dependPlugin, order, used, index);
        }
        for (String depend : plugin.getSoftDepends()) {
            TimoCloudPluginDescription dependPlugin = getPluginDescription(depend);
            if (dependPlugin == null) {
                continue;
            }
            if (used.contains(dependPlugin)) {
                TimoCloudCore.getInstance().info("Warning while loading plugin '" + plugin.getName() + "': Dependency cycle between pluginDescriptions: '" + plugin.getName() + "' soft-depends on '" + dependPlugin.getName() + "' which directly or indirectly depends on '" + plugin.getName() + "' again.");
                continue;
            }
            load(dependPlugin, order, used, index);
        }
        order.add(index, plugin);
    }

    public TimoCloudPluginDescription loadPlugin(File file) throws IOException, PluginLoadException {
        JarFile jar = new JarFile(file);
        JarEntry entry = jar.getJarEntry("timocloud.yml");
        if (entry == null) {
            throw new PluginLoadException("Jar does not contain timocloud.yml");
        }
        InputStream inputStream = jar.getInputStream(entry);
        Map<String, Object> properties = (Map<String, Object>) new Yaml().load(inputStream);
        return construct(properties, file);
    }

    private TimoCloudPluginDescription construct(Map<String, Object> properties, File file) throws PluginLoadException {
        String name = null;
        String author = null;
        String version = null;
        String main = null;
        List<String> depends = new ArrayList<>();
        List<String> softDepends = new ArrayList<>();
        try {
            name = (String) properties.get("name");
            Assert.notNull(name);
        } catch (Exception e) {
            throw new PluginLoadException("Could not parse plugin's name");
        }
        try {
            author = (String) properties.get("author");
            Assert.notNull(author);
        } catch (Exception e) {
            throw new PluginLoadException("Could not parse plugin's author");
        }
        try {
            version = "" + properties.get("version");
            Assert.notNull(version);
        } catch (Exception e) {
            throw new PluginLoadException("Could not parse plugin's version");
        }
        try {
            main = (String) properties.get("main");
            Assert.notNull(main);
        } catch (Exception e) {
            throw new PluginLoadException("Could not parse plugin's main class");
        }
        try {
            if (properties.containsKey("depends")) {
                Assert.notNull(properties.get("depends"));
                depends = (List<String>) properties.get("depends");
            }
        } catch (Exception e) {
            throw new PluginLoadException("Could not parse plugin's dependencies");
        }
        try {
            if (properties.containsKey("softDepends")) {
                Assert.notNull(properties.get("softDepends"));
                softDepends = (List<String>) properties.get("softDepends");
            }
        } catch (Exception e) {
            throw new PluginLoadException("Could not parse plugin's soft-dependencies");
        }
        return new TimoCloudPluginDescription(name, author, version, main, depends, softDepends, file);
    }

    public TimoCloudPluginDescription getPluginDescription(String name) {
        if (pluginDescriptions.containsKey(name)) return pluginDescriptions.get(name);
        for (TimoCloudPluginDescription plugin : pluginDescriptions.values()) {
            if (name.equalsIgnoreCase(plugin.getName())) return plugin;
        }
        return null;
    }

    public Collection<TimoCloudPlugin> getPlugins() {
        return plugins.values();
    }
}
