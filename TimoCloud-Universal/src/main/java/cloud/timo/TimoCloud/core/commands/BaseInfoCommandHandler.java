package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.commands.utils.CommandFormatUtil;
import cloud.timo.TimoCloud.core.objects.Base;

public class BaseInfoCommandHandler extends CommandFormatUtil implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        Base base = TimoCloudCore.getInstance().getInstanceManager().getBase(args[0]);
        if (base == null) {
            sender.sendError("Could not find base '" + args[0] + "'.");
            return;
        }
        displayBase(base, sender);
    }

}
