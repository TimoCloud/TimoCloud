package cloud.timo.TimoCloud.lib.modules;

import cloud.timo.TimoCloud.lib.global.logging.TimoCloudLogger;
import cloud.timo.TimoCloud.lib.utils.options.OptionSet;

public interface TimoCloudModule extends TimoCloudLogger {

    void load(OptionSet optionSet) throws Exception;

    void unload() throws Exception;

    ModuleType getModuleType();

}
