package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;

public class ReloadPluginsCommandHandler implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        try {
            TimoCloudCore.getInstance().getPluginManager().unloadPlugins();
            TimoCloudCore.getInstance().getPluginManager().loadPlugins();
            sender.sendMessage("Successfully reloaded plugins!");
        } catch (Exception e) {
            sender.sendError("An error occurred while reloading the plugins. See console for more details");
            TimoCloudCore.getInstance().severe(e);
        }
    }

}
