package at.TimoCraft.TimoCloud.bungeecord.commands;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.managers.MessageManager;
import at.TimoCraft.TimoCloud.bungeecord.objects.ServerGroup;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

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

        if (args.length < 1) {
            MessageManager.sendMessage(sender, "&bBy TimoCrafter");
            return;
        }

        if (!sender.hasPermission("timocloud.admin")) {
            MessageManager.noPermission(sender);
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
                MessageManager.sendMessage(sender, "  &b" + group.getName() + " &e(&7RAM: &6" + group.getRam() + "G&e, &7Amount: &6" + group.getStartupAmount() + "&e)");
            }
            return;
        }

        if (args.length < 2) {
            MessageManager.sendMessage(sender, "&cWrong usage.");
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
        }

        if (args.length < 4) {
            MessageManager.sendMessage(sender, "&cWrong usage.");
            return;
        }
        if (args[0].equalsIgnoreCase("addgroup")) {
            String name = args[1];
            File directory = new File(TimoCloud.getInstance().getFileManager().getTemplatesDirectory() + name);
            if (! directory.exists()) {
                MessageManager.sendMessage(sender, "&cPlease create the directory &b" + directory.toString() + " &cfirst.");
                return;
            }
            int amount = Integer.parseInt(args[2]);
            int ram = Integer.parseInt(args[3]);
            ServerGroup serverGroup = new ServerGroup(name, amount, ram);

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
    }
}
