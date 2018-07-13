package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.commands.utils.CommandFormatUtil;
import cloud.timo.TimoCloud.core.objects.Group;

public class GroupInfoCommandHandler extends CommandFormatUtil implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        Group group = TimoCloudCore.getInstance().getInstanceManager().getGroupByName(args[0]);
        if (group == null) {
            sender.sendError("Could not find group '" + args[0] + "'.");
            return;
        }
        displayGroup(group, sender);
    }

}
