package cloud.timo.TimoCloud.bukkit.commands;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.bukkit.managers.BukkitMessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SignsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("timocloud.command.signs")) {
            BukkitMessageManager.noPermission(sender);
            return false;
        }

        
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                TimoCloudBukkit.getInstance().getFileManager().loadSignConfigs();
                TimoCloudBukkit.getInstance().getSignManager().load();
                BukkitMessageManager.sendMessage(sender, "&aSuccessfully reloaded signs.");
                return true;
            }

        sendHelp(sender);
        return false;
    }

    private void sendHelp(CommandSender sender) {
        BukkitMessageManager.sendMessage(sender, "&6Available commands: ");
        BukkitMessageManager.sendMessage(sender, "&b/signs reload");
    }
}
