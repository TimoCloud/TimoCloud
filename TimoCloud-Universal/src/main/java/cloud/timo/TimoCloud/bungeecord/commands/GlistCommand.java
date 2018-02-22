package cloud.timo.TimoCloud.bungeecord.commands;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.stream.Collectors;

public class GlistCommand extends Command {

    public GlistCommand() {
        super("glist", "bungeecord.command.list", "redisbungee", "rglist");
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        for (ServerGroupObject serverGroupObject : TimoCloudAPI.getBungeeInstance().getThisProxy().getGroup().getServerGroups()) {
            for (ServerObject serverObject : serverGroupObject.getServers()) {
                sender.sendMessage(
                        new TextComponent(ChatColor.translateAlternateColorCodes('&', "&a[" + serverObject.getName() + "] &e(" + serverObject.getOnlinePlayerCount() + "): &r" +
                        serverObject.getOnlinePlayers().stream().map(PlayerObject::getName).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.joining(", "))))
                );
            }
        }
        sender.sendMessage(new TextComponent(ChatColor.RESET + "Total players online: " + TimoCloudAPI.getBungeeInstance().getThisProxy().getGroup().getOnlinePlayerCount()));
    }
}
