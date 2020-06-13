package cloud.timo.TimoCloud.bungeecord.managers;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class BungeeMessageManager {

    public static void sendMessage(CommandSender sender, String message) {
        if (message.equals("")) return;
        sender.sendMessage(TextComponent.fromLegacyText(TimoCloudBungee.getInstance().getPrefix() + ChatColor.translateAlternateColorCodes('&', message)));
    }

    public static void noPermission(CommandSender sender) {
        sendMessage(sender, "&cYou don't have any permission to do that!");
    }

    public static void onlyForPlayers(CommandSender sender) {
        sendMessage(sender, "&cThis command is only for players!");
    }

}
