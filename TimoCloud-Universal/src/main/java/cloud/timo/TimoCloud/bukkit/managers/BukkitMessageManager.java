package cloud.timo.TimoCloud.bukkit.managers;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@UtilityClass
public class BukkitMessageManager {

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TimoCloudBukkit.getInstance().getPrefix() + message));
    }

    public void noPermission(CommandSender sender) {
        sendMessage(sender, "&cYou don't have any permission to do that!");
    }
}
