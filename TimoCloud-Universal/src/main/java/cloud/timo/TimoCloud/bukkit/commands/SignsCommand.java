package cloud.timo.TimoCloud.bukkit.commands;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.bukkit.managers.BukkitMessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SignsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("timocloud.command.signs")) {
            BukkitMessageManager.noPermission(sender);
            return false;
        }

        if (args.length < 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            TimoCloudBukkit instance = TimoCloudBukkit.getInstance();
            instance.getFileManager().loadSignConfigs();
            instance.getSignManager().load();

            BukkitMessageManager.sendMessage(sender, "&aSuccessfully reloaded signs.");
        }

        return false;
    }
}
