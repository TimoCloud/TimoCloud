package at.TimoCraft.TimoCloud.bukkit.commands;

import at.TimoCraft.TimoCloud.api.TimoCloudAPI;
import at.TimoCraft.TimoCloud.bukkit.managers.BukkitMessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TimoCloudBukkitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("timocloud.command.bukkit")) {
            BukkitMessageManager.noPermission(sender);
            return false;
        }
        if (args.length < 2) {
            sendHelp(sender);
            return false;
        }
        if (args[0].equalsIgnoreCase("setstate")) {
            TimoCloudAPI.getBukkitInstance().setState(args[1]);
            BukkitMessageManager.sendMessage(sender, "&aState has successfully been set to &e" + args[1]);
        }
        return false;
    }

    private void sendHelp(CommandSender sender) {
        BukkitMessageManager.sendMessage(sender, "&6Available commands: ");
        BukkitMessageManager.sendMessage(sender, "&b/timocloudbukkit setState <state>");
    }
}
