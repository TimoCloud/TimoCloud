package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;

public class VersionCommandHandler implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        sender.sendMessage("&bTimo&fCloud &6version &e" + TimoCloudCore.class.getPackage().getImplementationVersion() + " &6by &cTimoCrafter.");
    }

}
