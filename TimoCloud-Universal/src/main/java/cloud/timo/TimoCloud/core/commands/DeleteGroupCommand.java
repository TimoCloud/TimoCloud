package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.commands.utils.CommandFormatUtil;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import cloud.timo.TimoCloud.core.objects.ServerGroup;

public class DeleteGroupCommand extends CommandFormatUtil implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        if(args.length == 0){
            notEnoughArgs(sender, "removegroup <groupName>");
            return;
        }
        String name = args[0];
        try {
            ServerGroup serverGroup = TimoCloudCore.getInstance().getInstanceManager().getServerGroupByName(name);
            ProxyGroup proxyGroup = TimoCloudCore.getInstance().getInstanceManager().getProxyGroupByName(name);
            if (serverGroup == null && proxyGroup == null) {
                sender.sendError("The group " + name + " does not exist. Type 'listgroups' for a list of all groups.");
                return;
            }
            if (serverGroup != null)
                TimoCloudCore.getInstance().getInstanceManager().deleteGroup(serverGroup);
            if (proxyGroup != null) TimoCloudCore.getInstance().getInstanceManager().deleteGroup(proxyGroup);

            sender.sendMessage("Successfully deleted group &e" + name);
        } catch (Exception e) {
            sender.sendError("Error while saving groups.yml. See console for mor information.");
            e.printStackTrace();
        }
    }

}
