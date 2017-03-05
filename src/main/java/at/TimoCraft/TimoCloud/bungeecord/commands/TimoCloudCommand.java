package at.TimoCraft.TimoCloud.bungeecord.commands;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.managers.MessageManager;
import at.TimoCraft.TimoCloud.bungeecord.objects.BaseObject;
import at.TimoCraft.TimoCloud.bungeecord.objects.ServerGroup;
import at.TimoCraft.TimoCloud.bungeecord.objects.TemporaryServer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.PluginDescription;

import java.util.List;

/**
 * Created by Timo on 27.12.16.
 */
public class TimoCloudCommand extends Command {

    public TimoCloudCommand() {
        super("TimoCloud");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            if (args.length < 1) {
                sendVersion(sender);
                return;
            }
            if (!sender.hasPermission("timocloud.admin")) {
                MessageManager.noPermission(sender);
                return;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                TimoCloud.getInstance().getFileManager().load();
                MessageManager.sendMessage(sender, "&aSuccessfully reloaded configs!");
                return;
            }
            if (args[0].equalsIgnoreCase("version")) {
                sendVersion(sender);
                return;
            }
            if (args[0].equalsIgnoreCase("listgroups")) {
                List<ServerGroup> groups = TimoCloud.getInstance().getServerManager().getGroups();
                if (groups.size() == 0) {
                    MessageManager.sendMessage(sender, "&cNo groups yet!");
                    return;
                }
                MessageManager.sendMessage(sender, "&6Groups (" + groups.size() + "):");
                for (ServerGroup group : groups) {
                    MessageManager.sendMessage(sender, "  &b" + group.getName() + " &e(&7RAM: &6" + group.getRam() + (group.getRam() < 128 ? "G" : "M") + "&e, &7Amount: &6" + group.getStartupAmount() + "&e)");
                    for (TemporaryServer server : group.getTemporaryServers()) {
                        MessageManager.sendMessage(sender, "    &b" + server.getName());
                    }
                }
                return;
            }
            if (args[0].equalsIgnoreCase("removegroup")) {
                String name = args[1];
                try {
                    TimoCloud.getInstance().getServerManager().removeGroup(name);
                } catch (Exception e) {
                    MessageManager.sendMessage(sender, "&cError while saving groups.yml. See console for mor information.");
                    e.printStackTrace();
                }
                return;
            }
            if (args[0].equalsIgnoreCase("restartgroup")) {
                ServerGroup group = TimoCloud.getInstance().getServerManager().getGroupByName(args[1]);
                if (group == null) {
                    MessageManager.sendMessage(sender, "&cGroup &e" + args[1] + " &cdoes not exist.");
                    return;
                }
                group.stopAllServers();
                return;
            }
            if (args[0].equalsIgnoreCase("stopserver") || args[0].equalsIgnoreCase("restartserver")) {
                TemporaryServer server = TimoCloud.getInstance().getServerManager().getServerByName(args[1]);
                if (server == null) {
                    MessageManager.sendMessage(sender, "&cServer &e" + args[1] + " &cdoes not exist.");
                    return;
                }
                server.stop();
                return;
            }
            if (args[0].equalsIgnoreCase("addgroup")) {
                String name = args[1];
                int amount = Integer.parseInt(args[2]);
                int ram = Integer.parseInt(args[3]);
                boolean isStatic = Boolean.parseBoolean(args[4]);
                String base = args[5];
                ServerGroup serverGroup = new ServerGroup(name, amount, ram, isStatic, base);

                if (TimoCloud.getInstance().getServerManager().groupExists(serverGroup)) {
                    MessageManager.sendMessage(sender, "&cThis group already exists.");
                    return;
                }
                try {
                    TimoCloud.getInstance().getServerManager().updateGroup(serverGroup);
                } catch (Exception e) {
                    MessageManager.sendMessage(sender, "&cError while saving groups.yml. See console for mor information.");
                    e.printStackTrace();
                }
                return;
            }
            if (args[0].equalsIgnoreCase("editgroup")) {
                String groupName = args[1];
                ServerGroup group = TimoCloud.getInstance().getServerManager().getGroupByName(groupName);
                if (group == null) {
                    MessageManager.sendMessage(sender, "&cGroup &e" + groupName + " &cnot found. Try /timocloud listgroups");
                    return;
                }
                String key = args[2];
                switch (key.toLowerCase()) {
                    case "onlineamount":
                        int onlineAmount = Integer.parseInt(args[3]);
                        group.setStartupAmount(onlineAmount);
                        TimoCloud.getInstance().getServerManager().updateGroup(group);
                        break;
                    case "base":
                        String baseName = args[3];
                        BaseObject base = TimoCloud.getInstance().getServerManager().getBase(baseName);
                        if (base == null) {
                            MessageManager.sendMessage(sender, "&cBase &e" + baseName + "&c is not connected or does not exist.");
                            return;
                        }
                        group.setBase(base);
                        TimoCloud.getInstance().getServerManager().updateGroup(group);
                        break;
                    case "ram":
                        int ram = Integer.parseInt(args[3]);
                        group.setRam(ram);
                        TimoCloud.getInstance().getServerManager().updateGroup(group);
                        break;
                    case "static":
                        boolean isStatic = Boolean.parseBoolean(args[3]);
                        group.setStatic(isStatic);
                        TimoCloud.getInstance().getServerManager().updateGroup(group);
                        break;
                    default:
                        MessageManager.sendMessage(sender, "&cNo valid argument found. Please use \n" +
                                "/TimoCloud editgroup <name> <onlineAmount (int), base (String), ram (int), static (boolean)> <value>");
                        break;
                }
                return;
            }
        } catch (Exception e) {
            sendHelp(sender);
        }
    }

    private void sendVersion(CommandSender sender) {
        PluginDescription description = TimoCloud.getInstance().getDescription();
        MessageManager.sendMessage(sender, "&bTimoCloud Version &e[&6" + description.getVersion() + "&e] &bby &6TimoCrafter");
    }

    public void sendHelp(CommandSender sender) {
        MessageManager.sendMessage(sender, "&6Available commands for &bTimoCloud&6:");
        MessageManager.sendMessage(sender, "  &a/TimoCloud help &7- shows this page");
        MessageManager.sendMessage(sender, "  &a/TimoCloud version &7- shows the plugin version");
        MessageManager.sendMessage(sender, "  &a/TimoCloud reload &7- reloads all configs");
        MessageManager.sendMessage(sender, "  &a/TimoCloud addgroup <groupName (String)> <startupAmount (int)> <ram (int)> <static (boolean), use false if you don't know what you want> <base (String)> &7- creates a group, please make sure you created a template with the group name in the specific base");
        MessageManager.sendMessage(sender, "  &a/TimoCloud removegroup <groupName> &7- deletes a group");
        MessageManager.sendMessage(sender, "  &a/TimoCloud listgroups &7- lists all groups and started servers");
        MessageManager.sendMessage(sender, "  &a/TimoCloud restartgroup <groupName> &7- restarts all servers in a given group");
        MessageManager.sendMessage(sender, "  &a/TimoCloud editgroup <name> <onlineAmount (int), base (String), ram (int), static (boolean)> <value>");

    }
}
