package cloud.timo.TimoCloud.bukkit.commands;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bukkit.managers.BukkitMessageManager;
import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;
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
            TimoCloudAPI.getBukkitAPI().getThisServer().setState(args[1]).onCompletion(() -> {
                BukkitMessageManager.sendMessage(sender, "&aState has successfully been set to &e" + args[1]);
            }).onError((error) -> {
                BukkitMessageManager.sendMessage(sender, "&cError while setting state, see console for details.");
                TimoCloudLogger.getLogger().severe(error);
            });
        }
        return false;
    }

    private void sendHelp(CommandSender sender) {
        BukkitMessageManager.sendMessage(sender, "&6Available commands: ");
        BukkitMessageManager.sendMessage(sender, "&b/timocloudbukkit setState <state>");
    }
}
