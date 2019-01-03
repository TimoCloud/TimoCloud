package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.commands.utils.CommandFormatUtil;
import cloud.timo.TimoCloud.core.objects.*;

public class RestartCommandHandler extends CommandFormatUtil implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        if (args.length == 0) {
            notEnoughArgs(sender, "restart <groupName | baseName | serverName | proxyName>");
            return;
        }
        String instance = args[0];
        ServerGroup serverGroup = TimoCloudCore.getInstance().getInstanceManager().getServerGroupByName(instance);
        ProxyGroup proxyGroup = TimoCloudCore.getInstance().getInstanceManager().getProxyGroupByName(instance);

        Server server = TimoCloudCore.getInstance().getInstanceManager().getServerByIdentifier(instance);
        Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().getProxyByIdentifier(instance);

        Base base = TimoCloudCore.getInstance().getInstanceManager().getBaseByIdentifier(instance);

        if (serverGroup == null && proxyGroup == null && server == null && proxy == null && base == null) {
            sender.sendError("Could not find any group, server, base or proxy with the name '" + instance + "'");
            return;
        }

        if (serverGroup != null) serverGroup.stopAllServers();
        else if (proxyGroup != null) proxyGroup.stopAllProxies();
        else if (server != null) server.stop();
        else if (proxy != null) proxy.stop();
        else if (base != null) {
            for (Server serverOnBase : base.getServers()) serverOnBase.stop();
            for (Proxy proxyOnBase : base.getProxies()) proxyOnBase.stop();
        }
        sender.sendMessage("&2The group/server/proxy/base has successfully been stopped/restarted.");
    }

}
