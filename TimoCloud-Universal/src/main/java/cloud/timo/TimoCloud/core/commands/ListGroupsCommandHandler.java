package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.commands.utils.CommandFormatUtil;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import cloud.timo.TimoCloud.core.objects.ServerGroup;

import java.util.Collection;

public class ListGroupsCommandHandler extends CommandFormatUtil implements CommandHandler {
    
    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        Collection<ServerGroup> serverGroups = TimoCloudCore.getInstance().getInstanceManager().getServerGroups();
        Collection<ProxyGroup> proxyGroups = TimoCloudCore.getInstance().getInstanceManager().getProxyGroups();

        sender.sendMessage("&6ServerGroups (&3" + serverGroups.size() + "&6):");
        for (ServerGroup group : serverGroups) {
            displayGroup(group, sender);
        }
        sender.sendMessage("&6ProxyGroups (&3" + proxyGroups.size() + "&6):");
        for (ProxyGroup group : proxyGroups) {
            displayGroup(group, sender);
        }
    }
    
}
