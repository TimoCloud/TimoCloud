package cloud.timo.TimoCloud.common.modules;

import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;
import cloud.timo.TimoCloud.common.utils.options.OptionSet;

public interface TimoCloudModule extends TimoCloudLogger {

    void load(OptionSet optionSet) throws Exception;

    void unload() throws Exception;

    ModuleType getModuleType();

}
