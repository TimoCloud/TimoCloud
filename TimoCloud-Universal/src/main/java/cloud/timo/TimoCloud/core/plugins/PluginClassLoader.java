package cloud.timo.TimoCloud.core.plugins;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PluginClassLoader extends URLClassLoader {

    private static final Set<PluginClassLoader> allLoaders = new CopyOnWriteArraySet<>();

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public PluginClassLoader(URL[] urls) {
        super(urls);
        allLoaders.add(this);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass0(name, resolve, true);
    }

    private Class<?> loadClass0(String name, boolean resolve, boolean checkOther) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ignored) {
        }

        if (checkOther) {
            for (PluginClassLoader loader : allLoaders) {
                if (loader == this) continue;
                try {
                    return loader.loadClass0(name, resolve, false);
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
        throw new ClassNotFoundException(name);
    }

}
