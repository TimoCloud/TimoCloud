package cloud.timo.TimoCloud.bungeecord.commands;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class FindCommand extends Command {

    public FindCommand() {
        super("find", "bungeecord.command.find", "rfind");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "Please specify the name of the player you want to find");
            return;
        }
        PlayerObject playerObject = TimoCloudAPI.getUniversalAPI().getPlayer(args[0]);
        if (playerObject == null) {
            sendMessage(sender, "&cThe player '&e" + args[0] + "&c' is not online.");
            return;
        }
        sendMessage(sender, "&e" + playerObject.getName() + " &ais online at &6" + playerObject.getServer().getName());
    }

    private static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
    }
}
