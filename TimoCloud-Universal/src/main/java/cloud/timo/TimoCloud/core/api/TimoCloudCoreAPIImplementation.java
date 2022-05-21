package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.TimoCloudCoreAPI;
import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.core.TimoCloudCore;

public class TimoCloudCoreAPIImplementation implements TimoCloudCoreAPI {

    @Override
    public void registerCommandHandler(CommandHandler commandHandler, String... commands) {
        for (String command : commands) {
            TimoCloudCore.getInstance().getCommandManager().registerCommandHandler(command, commandHandler);
        }
    }

    @Override
    public void unregisterCommandHandler(String... commands) {
        for (String command : commands) {
            TimoCloudCore.getInstance().getCommandManager().unregisterCommandHandler(command);
        }
    }

}
