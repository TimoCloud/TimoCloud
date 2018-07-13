package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.objects.ServerGroup;

public class RestartCommandHandler implements CommandHandler {
    
    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        String instance = args[0];
        ServerGroup serverGroup = TimoCloudCore.getInstance().getInstanceManager().getServerGroupByName(instance);
        ProxyGroup proxyGroup = TimoCloudCore.getInstance().getInstanceManager().getProxyGroupByName(instance);

        Server server = TimoCloudCore.getInstance().getInstanceManager().getServerByName(instance);
        Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().getProxyByName(instance);

        if (serverGroup == null && proxyGroup == null && server == null && proxy == null) {
            sender.sendError("Could not find any group, server or proxy with the name '" + instance + "'");
            return;
        }

        if (serverGroup != null) serverGroup.stopAllServers();
        else if (proxyGroup != null) proxyGroup.stopAllProxies();
        else if (server != null) server.stop();
        else if (proxy != null) proxy.stop();

        sender.sendMessage("&2The group/server/proxy has successfully been stopped/restarted.");
    }
    
}
