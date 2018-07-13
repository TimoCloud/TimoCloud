package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;

public class ReloadCommandHandler implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        TimoCloudCore.getInstance().getFileManager().load();
        TimoCloudCore.getInstance().getInstanceManager().loadGroups();
        sender.sendMessage("&2Successfully reloaded from configuration!");
    }

}
