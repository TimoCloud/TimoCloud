package at.TimoCraft.TimoCloud.bungeecord.managers;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import net.md_5.bungee.api.CommandSender;

/**
 * Created by Timo on 27.12.16.
 */
public class MessageManager {
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(TimoCloud.getInstance().getPrefix() + message.replace("&", "§"));
    }

    public static void noPermission(CommandSender sender) {
        sendMessage(sender, "§cDazu hast du keine Rechte!");
    }
}
