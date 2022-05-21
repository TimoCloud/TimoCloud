package cloud.timo.TimoCloud.core.plugins;

import cloud.timo.TimoCloud.TimoCloudTest;
import cloud.timo.TimoCloud.api.plugins.PluginLoadException;
import cloud.timo.TimoCloud.api.plugins.TimoCloudPlugin;
import cloud.timo.TimoCloud.api.plugins.TimoCloudPluginDescription;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        PluginManager.class
})
public class PluginManagerTest extends TimoCloudTest {

    private PluginManager pluginManager;

    @Mock
    private PluginClassLoader pluginClassLoader;

    @Before
    public void setUp() throws Exception {
        whenNew(PluginClassLoader.class).withAnyArguments().thenReturn(pluginClassLoader);
        when(pluginClassLoader.loadClass(anyString())).thenReturn((Class) TimoCloudPlugin.class);
        when(pluginClassLoader.loadClass(anyString(), anyBoolean())).thenReturn((Class) TimoCloudPlugin.class);

        pluginManager = new PluginManager();
    }

    @Test
    public void testDependency1() throws Exception {
        TimoCloudPluginDescription plugin1 = generatePluginDescriptionWithDepends("Plugin1", "Plugin2");
        TimoCloudPluginDescription plugin2 = generatePluginDescriptionWithDepends("Plugin2");

        InOrder inOrder = Mockito.inOrder(pluginClassLoader);

        pluginManager.loadPlugins(Arrays.asList(plugin1, plugin2));

        inOrder.verify(pluginClassLoader).loadClass(eq(plugin2.getMainClass()));
        inOrder.verify(pluginClassLoader).loadClass(eq(plugin1.getMainClass()));
    }

    @Test
    public void testDependency2() throws Exception {
        TimoCloudPluginDescription plugin1 = generatePluginDescriptionWithDepends("Plugin1", "Plugin2", "Plugin3");
        TimoCloudPluginDescription plugin2 = generatePluginDescriptionWithDepends("Plugin2", "Plugin3");
        TimoCloudPluginDescription plugin3 = generatePluginDescriptionWithDepends("Plugin3");

        InOrder inOrder = Mockito.inOrder(pluginClassLoader);

        pluginManager.loadPlugins(Arrays.asList(plugin1, plugin2, plugin3));

        inOrder.verify(pluginClassLoader).loadClass(eq(plugin3.getMainClass()));
        inOrder.verify(pluginClassLoader).loadClass(eq(plugin2.getMainClass()));
        inOrder.verify(pluginClassLoader).loadClass(eq(plugin1.getMainClass()));
    }

    @Test(expected = PluginLoadException.class)
    public void testDependencyFail1() throws Exception {
        TimoCloudPluginDescription plugin1 = generatePluginDescriptionWithDepends("Plugin1", "Plugin2");
        TimoCloudPluginDescription plugin2 = generatePluginDescriptionWithDepends("Plugin2", "Plugin1");

        pluginManager.loadPlugins(Arrays.asList(plugin1, plugin2));
    }

    @Test(expected = PluginLoadException.class)
    public void testDependencyFail2() throws Exception {
        TimoCloudPluginDescription plugin1 = generatePluginDescriptionWithDepends("Plugin1", "Plugin2");
        TimoCloudPluginDescription plugin2 = generatePluginDescriptionWithDepends("Plugin2", "Plugin3");
        TimoCloudPluginDescription plugin3 = generatePluginDescriptionWithDepends("Plugin3", "Plugin1");

        pluginManager.loadPlugins(Arrays.asList(plugin1, plugin2, plugin3));
    }

    @Test
    public void testSoftDependency1() throws Exception {
        TimoCloudPluginDescription plugin1 = generatePluginDescriptionWithSoftDepends("Plugin1", "Plugin2", "Plugin3");
        TimoCloudPluginDescription plugin2 = generatePluginDescriptionWithSoftDepends("Plugin2", "Plugin3");
        // Plugin3 does not exist, this should not matter
        pluginManager.loadPlugins(Arrays.asList(plugin1, plugin2));

        expectNoException();
    }

    private TimoCloudPluginDescription generatePluginDescriptionWithSoftDepends(String name, String... softDepends) {
        return generatePluginDescription(name, new String[0], softDepends);
    }

    private TimoCloudPluginDescription generatePluginDescriptionWithDepends(String name, String... depends) {
        return generatePluginDescription(name, depends, new String[0]);
    }

    private TimoCloudPluginDescription generatePluginDescription(String name, String[] depends, String[] softDepends) {
        TimoCloudPluginDescription plugin = mock(TimoCloudPluginDescription.class);
        when(plugin.getName()).thenReturn(name);
        when(plugin.getAuthor()).thenReturn("TestAuthor");
        when(plugin.getVersion()).thenReturn("1.0.0");
        when(plugin.getDepends()).thenReturn(Arrays.asList(depends));
        when(plugin.getSoftDepends()).thenReturn(Arrays.asList(softDepends));
        when(plugin.getMainClass()).thenReturn("test." + name + ".Main");
        File file = new File(name + ".jar");
        when(plugin.getFile()).thenReturn(file);

        return plugin;
    }

}