package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.objects.ServerGroup;

public class SendCommandCommandHandler implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        String target = args[0];
        ServerGroup serverGroup = TimoCloudCore.getInstance().getInstanceManager().getServerGroupByName(target);
        ProxyGroup proxyGroup = TimoCloudCore.getInstance().getInstanceManager().getProxyGroupByName(target);

        Server server = TimoCloudCore.getInstance().getInstanceManager().getServerById(target);
        Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().getProxyById(target);

        if (serverGroup == null && proxyGroup == null && server == null && proxy == null) {
            sender.sendError("Could not find any group, server or proxy with the name '" + target + "'");
            return;
        }
        String cmd = "";
        for (int i = 1; i < args.length; i++) cmd += args[i] + " ";
        cmd = cmd.trim();
        if (cmd.length() == 0) {
            sender.sendError("Please provide a command.");
            TimoCloudCore.getInstance().getCommandManager().sendHelp(sender);
            return;
        }

        if (serverGroup != null) for (Server server1 : serverGroup.getServers()) server1.executeCommand(cmd);
        else if (proxyGroup != null) for (Proxy proxy1 : proxyGroup.getProxies()) proxy1.executeCommand(cmd);
        else if (server != null) server.executeCommand(cmd);
        else if (proxy != null) proxy.executeCommand(cmd);

        sender.sendMessage("&2The command has successfully been executed on the target.");
    }

}
