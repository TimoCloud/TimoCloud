package at.TimoCraft.TimoCloud.bukkit.commands;

import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Timo on 29.12.16.
 */
public class SignsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("timocloud.command.signs")) {
            sender.sendMessage(TimoCloudBukkit.getInstance().getPrefix() + "§cYou don´t have any permission to do that!");
            return false;
        }
        if (args.length < 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            TimoCloudBukkit.getInstance().getFileManager().init();
            TimoCloudBukkit.getInstance().getSignManager().load();
            sender.sendMessage("§aSuccessfully reloaded signs.");
        }
        return false;
    }
}
