package cloud.timo.TimoCloud.core.plugins;

import cloud.timo.TimoCloud.api.plugins.PluginLoadException;
import cloud.timo.TimoCloud.api.plugins.PluginUnloadException;
import cloud.timo.TimoCloud.api.plugins.TimoCloudPlugin;
import cloud.timo.TimoCloud.api.plugins.TimoCloudPluginDescription;
import cloud.timo.TimoCloud.common.Assert;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class PluginManager {

    private Map<TimoCloudPluginDescription, TimoCloudPlugin> plugins;

    public PluginManager() {

    }

    public void loadPlugins() throws IOException, PluginLoadException {
        Collection<TimoCloudPluginDescription> pluginDescriptions = new LinkedHashSet<>();
        for (File file : TimoCloudCore.getInstance().getFileManager().getPluginsDirectory().listFiles()) {
            if (file.isDirectory()) continue;
            if (!file.getName().endsWith(".jar")) continue;
            TimoCloudPluginDescription plugin = loadPlugin(file);
            pluginDescriptions.add(plugin);
        }

        loadPlugins(pluginDescriptions);
    }

    public void loadPlugins(Collection<TimoCloudPluginDescription> pluginDescriptions) throws PluginLoadException {
        plugins = new HashMap<>();
        Map<String, TimoCloudPluginDescription> descriptionsByName = new HashMap<>();
        for (TimoCloudPluginDescription description : pluginDescriptions) {
            descriptionsByName.put(description.getName(), description);
        }

        Collection<TimoCloudPluginDescription> order = new LinkedHashSet<>();
        for (TimoCloudPluginDescription plugin : pluginDescriptions) {
            Stack<TimoCloudPluginDescription> dependStack = new Stack<>();
            load(plugin, order, dependStack, descriptionsByName);
        }
        for (TimoCloudPluginDescription plugin : order) {
            try {
                URLClassLoader classLoader = new PluginClassLoader(new URL[]{plugin.getFile().toURI().toURL()});
                Class<?> main = classLoader.loadClass(plugin.getMainClass());
                if (!TimoCloudPlugin.class.isAssignableFrom(main)) {
                    throw new PluginLoadException("Main class does not extend TimoCloudPlugin");
                }
                TimoCloudPlugin mainInstance = (TimoCloudPlugin) main.getDeclaredConstructor().newInstance();
                try {
                    mainInstance.onLoad();
                    TimoCloudCore.getInstance().info("Loaded plugin " + plugin.getName() + " version " + plugin.getVersion() + " by " + plugin.getAuthor() + ".");
                    plugins.put(plugin, mainInstance);
                    plugin.setPlugin(mainInstance);
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe(e);
                    throw new PluginLoadException("Error while enabling plugin " + plugin.getName() + " version " + plugin.getVersion() + " by " + plugin.getAuthor() + ": " + e.getMessage());
                }
            } catch (Exception e) {
                TimoCloudCore.getInstance().severe(e);
                throw new PluginLoadException("Error while class-loading plugin '" + plugin.getName() + "': ");
            }
        }
    }

    public void load(TimoCloudPluginDescription plugin, Collection<TimoCloudPluginDescription> order, Stack<TimoCloudPluginDescription> dependStack, Map<String, TimoCloudPluginDescription> descriptionsByName) throws PluginLoadException {
        for (String depend : plugin.getDepends()) {
            TimoCloudPluginDescription dependPlugin = descriptionsByName.get(depend);
            if (dependPlugin == null) {
                throw new PluginLoadException("Error while loading plugin '" + plugin.getName() + "': Dependency '" + depend + "' could not be found.");
            }
            if (order.contains(dependPlugin)) {
                continue;
            }
            if (dependStack.contains(dependPlugin)) {
                throw new PluginLoadException("Dependency cycle: " + dependStack.stream().map(TimoCloudPluginDescription::getName).collect(Collectors.joining(" <- ")));
            }
            try {
                dependStack.push(dependPlugin);
                load(dependPlugin, order, dependStack, descriptionsByName);
                dependStack.pop();
            } catch (Exception e) {
                TimoCloudCore.getInstance().severe(e);
                throw new PluginLoadException("Could not load plugin " + plugin.getName() + " because loading of its dependency " + dependPlugin.getName() + " failed.");
            }
        }
        for (String depend : plugin.getSoftDepends()) {
            TimoCloudPluginDescription dependPlugin = descriptionsByName.get(depend);
            if (dependPlugin == null) {
                continue;
            }
            if (order.contains(dependPlugin)) {
                continue;
            }
            if (dependStack.contains(dependPlugin)) {
                continue;
            }
            dependStack.push(plugin);
            load(dependPlugin, order, dependStack, descriptionsByName);
            dependStack.pop();
        }
        order.add(plugin);
    }

    private static JarEntry searchForTimoCloudYml(JarFile jarFile) {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            File file = new File(entry.getName());

            if (file.getName().equals("timocloud.yml")) return entry;
        }
        return null;
    }

    public TimoCloudPluginDescription loadPlugin(File file) throws IOException, PluginLoadException {
        JarFile jar = new JarFile(file);
        JarEntry entry = searchForTimoCloudYml(jar);
        if (entry == null) {
            throw new PluginLoadException("Jar does not contain timocloud.yml");
        }
        InputStream inputStream = jar.getInputStream(entry);
        Map<String, Object> properties = new Yaml().load(inputStream);
        return construct(properties, file);
    }

    private TimoCloudPluginDescription getPluginDescriptionByName(String name) {
        for (TimoCloudPluginDescription description : plugins.keySet()) {
            if (description.getName().equalsIgnoreCase(name)) {
                return description;
            }
        }
        return null;
    }

    public void unloadPlugins() throws PluginUnloadException {
        Collection<TimoCloudPluginDescription> descriptions = new ArrayList<>(this.plugins.keySet());
        for (TimoCloudPluginDescription description : descriptions) {
            unloadPlugin(description);
        }
    }

    public void unloadPlugin(TimoCloudPluginDescription description) throws PluginUnloadException {
        TimoCloudPlugin plugin = plugins.get(description);
        if (plugin == null) {
            throw new PluginUnloadException(String.format("Plugin with name '%s' not found", description.getName()));
        }
        plugin.onUnload();
        plugins.remove(description);

        TimoCloudCore.getInstance().info(String.format("Successfully unloaded plugin '%s'", description.getName()));
    }

    private TimoCloudPluginDescription construct(Map<String, Object> properties, File file) throws PluginLoadException {
        String name;
        String author;
        String version;
        String main;
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

    public Collection<TimoCloudPlugin> getPlugins() {
        return plugins.values();
    }
}
