package at.TimoCraft.TimoCloud.bungeecord.managers;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import net.md_5.bungee.api.CommandSender;

/**
 * Created by Timo on 27.12.16.
 */
public class BungeeMessageManager {
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(TimoCloud.getInstance().getPrefix() + message.replace("&", "§"));
    }

    public static void noPermission(CommandSender sender) {
        sendMessage(sender, "&cYou don't have any permission to do that!");
    }

    public static void onlyForPlayers(CommandSender sender) {
        sendMessage(sender, "&cThis command is only for players!");
    }
}
