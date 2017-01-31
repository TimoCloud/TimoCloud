package at.TimoCraft.TimoCloud.bungeecord.commands;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.managers.MessageManager;
import at.TimoCraft.TimoCloud.bungeecord.objects.ServerGroup;
import at.TimoCraft.TimoCloud.bungeecord.objects.TemporaryServer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

import java.io.File;
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
                File directory = new File(TimoCloud.getInstance().getFileManager().getTemplatesDirectory() + name);
                if (!directory.exists()) {
                    MessageManager.sendMessage(sender, "&cPlease create the directory &b" + directory.toString() + " &cfirst.");
                    return;
                }
                int amount = Integer.parseInt(args[2]);
                int ram = Integer.parseInt(args[3]);
                boolean isStatic = Boolean.parseBoolean(args[4]);
                ServerGroup serverGroup = new ServerGroup(name, amount, ram, isStatic);

                if (TimoCloud.getInstance().getServerManager().groupExists(serverGroup)) {
                    MessageManager.sendMessage(sender, "&cThis group already exists.");
                    return;
                }
                try {
                    TimoCloud.getInstance().getServerManager().addGroup(serverGroup);
                } catch (Exception e) {
                    MessageManager.sendMessage(sender, "&cError while saving groups.yml. See console for mor information.");
                    e.printStackTrace();
                }
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
        MessageManager.sendMessage(sender, "  &a/TimoCloud addgroup <groupName> <startupAmount> <ram> &7- creates a group, please make sure you created a template with the group name");
        MessageManager.sendMessage(sender, "  &a/TimoCloud removegroup <groupName> &7- deletes a group");
        MessageManager.sendMessage(sender, "  &a/TimoCloud listgroups &7- lists all groups and started servers");
        MessageManager.sendMessage(sender, "  &a/TimoCloud restartgroup <groupName> &7- restarts all servers in a given group");
        MessageManager.sendMessage(sender, "  &a/TimoCloud restartserver <serverName> &7- restarts a given server");
    }
}
