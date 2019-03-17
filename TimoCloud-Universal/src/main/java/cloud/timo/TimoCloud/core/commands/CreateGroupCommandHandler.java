package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.api.objects.properties.ProxyGroupProperties;
import cloud.timo.TimoCloud.api.objects.properties.ServerGroupProperties;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.commands.utils.CommandFormatUtil;
import cloud.timo.TimoCloud.core.objects.Group;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import cloud.timo.TimoCloud.core.objects.ServerGroup;

public class CreateGroupCommandHandler extends CommandFormatUtil implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        if (args.length < 4) {
            notEnoughArgs(sender, "addgroup <groupType (String)> <groupName (String)> <onlineAmount (int), only needed for server groups> <ram (int)> <static (boolean)> <base (String), only needed if static=true>");
            return;
        }
        String type = args[0];
        String name = args[1];
        if (TimoCloudCore.getInstance().getInstanceManager().getGroupByName(name) != null) {
            sender.sendError("This group already exists.");
            return;
        }
        Group group;
        if (type.equalsIgnoreCase("server")) {
            ServerGroupProperties properties = new ServerGroupProperties(name);
            int onlineAmount = Integer.parseInt(args[2]);
            properties.setOnlineAmount(onlineAmount);
            properties.setRam(Integer.parseInt(args[3]));
            boolean isStatic = Boolean.parseBoolean(args[4]);
            properties.setStatic(isStatic);
            if (isStatic && args.length > 5) properties.setBaseIdentifier(args[5]);

            if (isStatic && properties.getBaseIdentifier() == null) {
                sender.sendError("When creating a static group, you have to specify a base!");
                return;
            }

            if (isStatic && onlineAmount > 1) {
                sender.sendError("Static server groups cannot have an onlineAmount bigger than 1!");
                return;
            }

            ServerGroup serverGroup = new ServerGroup(properties);
            TimoCloudCore.getInstance().getInstanceManager().createGroup(serverGroup);
            group = serverGroup;
        } else if (type.equalsIgnoreCase("proxy")) {
            ProxyGroupProperties properties = new ProxyGroupProperties(name);
            properties.setRam(Integer.parseInt(args[2]));
            boolean isStatic = Boolean.parseBoolean(args[3]);
            properties.setStatic(isStatic);
            if (isStatic) properties.setBaseIdentifier(args[4]);

            if (isStatic && properties.getBaseIdentifier() == null) {
                sender.sendError("When creating a static group, you have to specify a base!");
                return;
            }

            ProxyGroup proxyGroup = new ProxyGroup(properties);
            TimoCloudCore.getInstance().getInstanceManager().createGroup(proxyGroup);
            group = proxyGroup;
        } else {
            sender.sendError("Unknown group type: '" + type + "'");
            return;
        }

        try {
            sender.sendMessage("&2Group &e" + name + " &2has successfully been created.");
            displayGroup(group, sender);
        } catch (Exception e) {
            sender.sendError("Error while saving group: ");
            e.printStackTrace();
        }
    }

}
