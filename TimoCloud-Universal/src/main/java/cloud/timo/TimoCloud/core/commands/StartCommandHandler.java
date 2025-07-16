package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.*;


public class StartCommandHandler implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        if (args.length != 1) {
            sender.sendError("Usage: start <groupName>");
            return;
        }

        String groupName = args[0];
        Group group = TimoCloudCore.getInstance().getInstanceManager().getGroupByName(groupName);

        if (group == null) {
            sender.sendError("Group '" + groupName + "' not found.");
            return;
        }

        if (group instanceof ServerGroup) {
            startServer((ServerGroup) group, sender);
        } else if (group instanceof ProxyGroup) {
            startProxy((ProxyGroup) group, sender);
        }
    }

    private void startServer(ServerGroup serverGroup, CommandSender sender) {
        if (serverGroup.getMaxAmount() > 0 && serverGroup.getServers().size() >= serverGroup.getMaxAmount()) {
            sender.sendError("Server group '" + serverGroup.getName() + "' has already reached its maximum amount of " + serverGroup.getMaxAmount() + " servers.");
            return;
        }

        Base base = TimoCloudCore.getInstance().getInstanceManager().getFreeBase(serverGroup);
        if (base == null) {
            if (serverGroup.getBase() != null) {
                sender.sendError("Base '" + serverGroup.getBase().getName() + "' is not available or does not have enough resources.");
            } else {
                sender.sendError("No available base found for server group '" + serverGroup.getName() + "'.");
            }
            return;
        }

        Server server = TimoCloudCore.getInstance().getInstanceManager().startServerManually(serverGroup, base);
        if (server != null) {
            sender.sendMessage("&aStarting server '" + server.getName() + "' from group '" + serverGroup.getName() + "'...");
        } else {
            sender.sendError("Failed to start server from group '" + serverGroup.getName() + "'.");
        }
    }

    private void startProxy(ProxyGroup proxyGroup, CommandSender sender) {
        if (proxyGroup.getMaxAmount() > 0 && proxyGroup.getProxies().size() >= proxyGroup.getMaxAmount()) {
            sender.sendError("Proxy group '" + proxyGroup.getName() + "' has already reached its maximum amount of " + proxyGroup.getMaxAmount() + " proxies.");
            return;
        }

        Base base = TimoCloudCore.getInstance().getInstanceManager().getFreeBase(proxyGroup);
        if (base == null) {
            if (proxyGroup.getBase() != null) {
                sender.sendError("Base '" + proxyGroup.getBase().getName() + "' is not available or does not have enough resources.");
            } else {
                sender.sendError("No available base found for proxy group '" + proxyGroup.getName() + "'.");
            }
            return;
        }

        Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().startProxyManually(proxyGroup, base);
        if (proxy != null) {
            sender.sendMessage("&aStarting proxy '" + proxy.getName() + "' from group '" + proxyGroup.getName() + "'...");
        } else {
            sender.sendError("Failed to start proxy from group '" + proxyGroup.getName() + "'.");
        }
    }
}
