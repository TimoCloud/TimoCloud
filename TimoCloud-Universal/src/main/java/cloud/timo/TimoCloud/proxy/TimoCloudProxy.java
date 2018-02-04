package cloud.timo.TimoCloud.proxy;

import cloud.timo.TimoCloud.ModuleType;
import cloud.timo.TimoCloud.TimoCloudModule;
import cloud.timo.TimoCloud.utils.options.OptionSet;

public class TimoCloudProxy implements TimoCloudModule {

    private OptionSet options;

    @Override
    public void load(OptionSet optionSet) {
        this.options = optionSet;
    }

    @Override
    public void unload() {

    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.PROXY;
    }
}
