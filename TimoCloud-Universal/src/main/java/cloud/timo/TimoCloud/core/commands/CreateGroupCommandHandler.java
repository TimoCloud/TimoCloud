package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.commands.utils.CommandFormatUtil;
import cloud.timo.TimoCloud.core.objects.Group;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import cloud.timo.TimoCloud.core.objects.ServerGroup;

import java.util.HashMap;
import java.util.Map;

public class CreateGroupCommandHandler extends CommandFormatUtil implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        if (args.length < 5) {
            notEnoughArgs(sender, "addgroup <groupType (String)> <groupName (String)> <onlineAmount (int)> <ram (int)> <static (boolean)> <base (String), only needed if static=true>");
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
            Map<String, Object> properties = new HashMap<>();
            properties.put("name", name);
            int onlineAmount = Integer.parseInt(args[2]);
            properties.put("online-amount", onlineAmount);
            properties.put("ram", Integer.parseInt(args[3]));
            boolean isStatic = Boolean.parseBoolean(args[4]);
            properties.put("static", isStatic);
            if (isStatic && args.length > 5) properties.put("base", args[5]);

            if (isStatic && !properties.containsKey("base")) {
                sender.sendError("If you create a static group, you have to specify a base!");
                return;
            }

            if (isStatic && onlineAmount > 1) {
                sender.sendError("Static server groups cannot have an onlineAmount bigger than 1!");
                return;
            }

            ServerGroup serverGroup = new ServerGroup(properties);
            TimoCloudCore.getInstance().getInstanceManager().addGroup(serverGroup);
            TimoCloudCore.getInstance().getInstanceManager().saveServerGroups();
            group = serverGroup;
        } else if (type.equalsIgnoreCase("proxy")) {
            Map<String, Object> properties = new HashMap<>();
            properties.put("name", name);
            properties.put("ram", Integer.parseInt(args[2]));
            boolean isStatic = Boolean.parseBoolean(args[3]);
            properties.put("static", isStatic);
            if (isStatic && args.length > 4) properties.put("base", args[4]);

            if (isStatic && !properties.containsKey("base")) {
                sender.sendError("If you create a static group, you have to specify a base!");
                return;
            }

            ProxyGroup proxyGroup = new ProxyGroup(properties);
            TimoCloudCore.getInstance().getInstanceManager().addGroup(proxyGroup);
            TimoCloudCore.getInstance().getInstanceManager().saveProxyGroups();
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
