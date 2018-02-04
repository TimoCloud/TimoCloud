package cloud.timo.TimoCloud;

import cloud.timo.TimoCloud.utils.options.OptionSet;

public interface TimoCloudModule {
    void load(OptionSet optionSet);
    void unload();
    ModuleType getModuleType();
}
