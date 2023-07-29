package cloud.timo.TimoCloud.core.commands;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;

import java.util.Set;

public class HelpCommandHandler implements CommandHandler {

    @Override
    public void onCommand(String command, CommandSender sender, String... args) {
        sender.sendMessage("&6Available commands for &bTimo&fCloud&7:");
        sender.sendMessage("  &6help &7- &7shows this page");
        sender.sendMessage("  &6version &7- &7shows the plugin version");
        sender.sendMessage("  &6reload &7- &7reloads all configs");
        sender.sendMessage("  &6addbase <publicKey> &7- registers a new base");
        sender.sendMessage("  &6editbase &7<&2name&7> <&2name &7(&9String&7) | &2maxRam &7(&9int&7) | &2keepFreeRam &7(&9int&7) | &2maxCpuLoad &7(&9double&7)> <&2value&7> &7- edits the give setting of a base");
        sender.sendMessage("  &6addgroup server &7<&2groupName &7(&9String&7)> <&2onlineAmount &7(&9int&7)> <&2ram &7(&9int&7)> <&2static &7(&9boolean&7)> <&2base &7(&9String&7), &6only needed if static=true&7> - &7creates a server group");
        sender.sendMessage("  &6addgroup proxy &7<&2groupName &7(&9String&7)> <&2ram &7(&9int&7)> <&2static &7(&9boolean&7)> <&2base &7(&9String&7), &6only needed if static=true&7> - &7creates a proxy group");
        sender.sendMessage("  &6removegroup &7<&2groupName&7> - &7deletes a group");
        sender.sendMessage("  &6editgroup &7<&2name&7> <&2onlineAmount &7(&9int&7) | &2maxAmount &7(&9int&7) | &2ram &7(&9int&7) | &2static &7(&9boolean&7) | &2priority &7(&9int&7) | &2base &7(&9String&7) | &2jrePath &7(&9String&7)> <&2value&7> - &7edits the give setting of a server group");
        sender.sendMessage("  &6editgroup &7<&2name&7> <&2playersPerProxy &7(&9int&7) | &2maxPlayers &7(&9int&7) | &2keepFreeSlots &7(&9int&7) | &2minAmount &7(&9int&7) | &2maxAmount &7(&9int&7) | &2ram &7(&9int&7) | &2static &7(&9boolean&7) | &2priority &7(&9int&7) | &2base &7(&9String&7) | &2jrePath &7(&9String&7)> <&2value&7> - &7edits the give setting of a proxy group");
        sender.sendMessage("  &6restart &7<&2groupName&7 | &2baseName&7 | &2serverName&7 | &2proxyName&7> - &7restarts the given group, base, server, or proxy (If a base, stops/restarts every server and proxy on the base)");
        sender.sendMessage("  &6groupinfo &7<&2groupName&7> - displays group info");
        sender.sendMessage("  &6listgroups &7- &7lists all groups and started servers");
        sender.sendMessage("  &6baseinfo &7<&2baseName&7> - displays base info");
        sender.sendMessage("  &6listbases &7- &7lists all bases");
        sender.sendMessage("  &6sendcommand &7<&2groupName&7 | &2serverName&7 | &2proxyName&7> <&2command&7> - &7sends the given command to all server of a given group or the given server");

        Set<String> pluginCommands = TimoCloudCore.getInstance().getCommandManager().getPluginCommandHandlers().keySet();

        if (pluginCommands.size() != 0) {
            sender.sendMessage(" ");
            sender.sendMessage("&6Available Plugin commands&7:");

            for (String pluginCommand : pluginCommands) {
                sender.sendMessage("  &6" + pluginCommand);
            }
        }
    }

}
