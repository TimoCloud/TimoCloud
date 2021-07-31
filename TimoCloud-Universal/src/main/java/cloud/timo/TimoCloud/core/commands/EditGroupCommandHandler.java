package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.commands.utils.CommandFormatUtil;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.objects.Group;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import cloud.timo.TimoCloud.core.objects.ServerGroup;

public class EditGroupCommandHandler extends CommandFormatUtil implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        if (args.length < 3) {
            notEnoughArgs(sender, "editgroup <name> <onlineAmount (int) | maxAmount (int) | ram (int) | static (boolean) | priority (int) | base (String) | jrePath (String)> <value>");
            notEnoughArgs(sender, "editgroup <name> <playersPerProxy (int) | maxPlayers (int) | keepFreeSlots (int) | minAmount (int) | maxAmount (int) | base (String) | ram (int) | static (boolean) | priority (int) > <value>");
            return;
        }
        String groupName = args[0];
        Group group = TimoCloudCore.getInstance().getInstanceManager().getGroupByName(groupName);
        if (group == null) {
            sender.sendError("Group " + groupName + " not found. Get a list of all groups with 'listgroups'");
            return;
        }
        String key = args[1];
        String value = args[2];
        if (group instanceof ServerGroup) {
            ServerGroup serverGroup = (ServerGroup) group;
            switch (key.toLowerCase()) {
                case "onlineamount":
                    int newOnlineAmount = Integer.parseInt(value);
                    if (serverGroup.isStatic() && newOnlineAmount > 1) {
                        sender.sendError("Static server groups cannot have an onlineAmount bigger than 1!");
                        return;
                    }
                    serverGroup.setOnlineAmount(Integer.parseInt(value));
                    break;
                case "maxamount":
                    serverGroup.setMaxAmount(Integer.parseInt(value));
                    break;
                case "base":
                    String baseIdentifier = value;
                    if (baseIdentifier.equalsIgnoreCase("none") || baseIdentifier.equalsIgnoreCase("dynamic"))
                        baseIdentifier = null;
                    Base base = TimoCloudCore.getInstance().getInstanceManager().getBaseByIdentifier(baseIdentifier);
                    if (base == null) {
                        sender.sendError("A base with the given name or id does not exist!");
                        return;
                    }
                    serverGroup.setBase(base);
                    break;
                case "ram":
                    int ram = Integer.parseInt(value);
                    serverGroup.setRam(ram);
                    break;
                case "static":
                    boolean isStatic = Boolean.parseBoolean(value);
                    serverGroup.setStatic(isStatic);
                    break;
                case "priority":
                    int priority = Integer.parseInt(value);
                    serverGroup.setPriority(priority);
                    break;
                case "jrepath":
                    serverGroup.setJrePath(value);
                    break;
                default:
                    invalidArgs(sender, "editgroup <name> <onlineAmount (int) | maxAmount (int) | base (String) | ram (int) | static (boolean) | priority (int) | jrePath (String)> <value>");
                    return;
            }
            TimoCloudCore.getInstance().getInstanceManager().saveServerGroups();
        } else if (group instanceof ProxyGroup) {
            ProxyGroup proxyGroup = (ProxyGroup) group;
            switch (key.toLowerCase()) {
                case "playersperproxy":
                    proxyGroup.setMaxPlayerCountPerProxy(Integer.parseInt(value));
                    break;
                case "maxplayers":
                    proxyGroup.setMaxPlayerCount(Integer.parseInt(value));
                    break;
                case "keepfreeslots":
                    proxyGroup.setKeepFreeSlots(Integer.parseInt(value));
                    break;
                case "minamount":
                    proxyGroup.setMinAmount(Integer.parseInt(value));
                    break;
                case "maxamount":
                    proxyGroup.setMaxAmount(Integer.parseInt(value));
                    break;
                case "base":
                    String baseIdentifier = value;
                    if (baseIdentifier.equalsIgnoreCase("none") || baseIdentifier.equalsIgnoreCase("dynamic"))
                        baseIdentifier = null;
                    Base base = TimoCloudCore.getInstance().getInstanceManager().getBaseByIdentifier(baseIdentifier);
                    if (base == null) {
                        sender.sendError("A base with the given name or id does not exist!");
                        return;
                    }
                    proxyGroup.setBase(base);
                    break;
                case "ram":
                    int ram = Integer.parseInt(value);
                    proxyGroup.setRam(ram);
                    break;
                case "static":
                    boolean isStatic = Boolean.parseBoolean(value);
                    proxyGroup.setStatic(isStatic);
                    break;
                case "priority":
                    int priority = Integer.parseInt(value);
                    proxyGroup.setPriority(priority);
                    break;
                case "jrepath":
                    proxyGroup.setJrePath(value);
                    break;
                default:
                    invalidArgs(sender, "editgroup <name> <playersPerProxy (int) | maxPlayers (int) | keepFreeSlots (int) | minAmount (int) | maxAmount (int) | base (String) | ram (int) | static (boolean) | priority (int) | jrePath (String)> <value>");
                    return;
            }
            TimoCloudCore.getInstance().getInstanceManager().saveProxyGroups();
        }
        sender.sendMessage("&2Group &e" + group.getName() + " &2has successfully been edited. New data: ");
        displayGroup(group, sender);
    }

}
