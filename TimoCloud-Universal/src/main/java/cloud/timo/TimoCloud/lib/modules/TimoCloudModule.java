package cloud.timo.TimoCloud.lib.modules;

import cloud.timo.TimoCloud.lib.utils.options.OptionSet;

public interface TimoCloudModule {
    void load(OptionSet optionSet);
    void unload();
    ModuleType getModuleType();
}
