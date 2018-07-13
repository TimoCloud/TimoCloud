package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;

public class StopCommandHandler implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        sender.sendMessage("Stopping TimoCloudCore...");
        System.exit(0);
    }

}
