package cloud.timo.TimoCloud.bungeecord.commands;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.managers.BungeeMessageManager;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TimoCloudCommand extends Command implements TabExecutor {

    private final Map<String, CommandSender> senders;

    public TimoCloudCommand() {
        super("TimoCloud", "timocloud.admin");
        senders = new HashMap<>();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            if (args.length < 1) {
                sendVersion(sender);
                return;
            }

            if (args[0].equalsIgnoreCase("check")) {
                String user = "%%__USER__%%", nonce = "%%__NONCE__%%";
                if (user.startsWith("%%")) {
                    BungeeMessageManager.sendMessage(sender, "&cNot downloaded from spigotmc.org.");
                    return;
                }
                BungeeMessageManager.sendMessage(sender, "&6Downloaded by &ehttps://www.spigotmc.org/members/" + user + "/");
                BungeeMessageManager.sendMessage(sender, "&b" + nonce);
                return;
            }

            if (!sender.hasPermission("timocloud.admin")) {
                BungeeMessageManager.noPermission(sender);
                return;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                TimoCloudBungee.getInstance().getFileManager().load();
                BungeeMessageManager.sendMessage(sender, "&aSuccessfully reloaded from configuration!");
                // Do not return because we want to reload the Core configuration as well
            }
            if (args[0].equalsIgnoreCase("version")) {
                sendVersion(sender);
                return;
            }
            String command = Arrays.stream(args).collect(Collectors.joining(" "));
            senders.put(sender.getName(), sender);

            TimoCloudBungee.getInstance().getSocketClientHandler().sendMessage(Message.create()
                    .setType(MessageType.CORE_PARSE_COMMAND)
                    .setData(command)
                    .set("sender", sender.getName())
                    .toString());
        } catch (Exception e) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cAn error occured while exeuting command. Please see console for more details.")));
        }
    }

    private void sendVersion(CommandSender sender) {
        PluginDescription description = TimoCloudBungee.getInstance().getDescription();
        BungeeMessageManager.sendMessage(sender, "&bTimoCloud Version &e[&6" + description.getVersion() + "&e] &bby &6TimoCrafter");
    }

    public void sendMessage(String senderName, String message) {
        if (getSender(senderName) == null) return;
        getSender(senderName).sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
    }

    private CommandSender getSender(String name) {
        if (!senders.containsKey(name)) return null;
        return senders.get(name);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        if (commandSender.hasPermission("timocloud.admin")) {
            List<String> tabCompletions = new ArrayList<>();

            if (strings.length == 1) {
                addCompletionToList(tabCompletions, "help", strings[0]);
                addCompletionToList(tabCompletions, "version", strings[0]);
                addCompletionToList(tabCompletions, "reload", strings[0]);
                addCompletionToList(tabCompletions, "addbase", strings[0]);
                addCompletionToList(tabCompletions, "addgroup", strings[0]);
                addCompletionToList(tabCompletions, "removegroup", strings[0]);
                addCompletionToList(tabCompletions, "editgroup", strings[0]);
                addCompletionToList(tabCompletions, "restart", strings[0]);
                addCompletionToList(tabCompletions, "groupinfo", strings[0]);
                addCompletionToList(tabCompletions, "listgroups", strings[0]);
                addCompletionToList(tabCompletions, "baseinfo", strings[0]);
                addCompletionToList(tabCompletions, "listbases", strings[0]);
                addCompletionToList(tabCompletions, "sendcommand", strings[0]);
            }
            if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("addgroup")) {
                    addCompletionToList(tabCompletions, "server", strings[1]);
                    addCompletionToList(tabCompletions, "proxy", strings[1]);
                }
                if (strings[0].equalsIgnoreCase("removegroup") ||
                        strings[0].equalsIgnoreCase("editgroup") ||
                        strings[0].equalsIgnoreCase("groupinfo")) {
                    addServerGroupCompletions(strings[1], tabCompletions);
                    addProxyGroupCompletions(strings[1], tabCompletions);
                }
                if (strings[0].equalsIgnoreCase("restart")) {
                    addServerGroupCompletions(strings[1], tabCompletions);
                    addServerCompletions(strings[1], tabCompletions);
                    addProxyGroupCompletions(strings[1], tabCompletions);
                    addProxyCompletions(strings[1], tabCompletions);
                }
                if (strings[0].equalsIgnoreCase("baseinfo")) {
                    for (BaseObject bases : TimoCloudAPI.getUniversalAPI().getBases()) {
                        addCompletionToList(tabCompletions, bases.getName(), strings[1]);
                    }
                }
                if (strings[0].equalsIgnoreCase("sendcommand")) {
                    addServerCompletions(strings[1], tabCompletions);
                    addProxyCompletions(strings[1], tabCompletions);
                }
            }
            return tabCompletions;
        }
        return null;
    }

    private void addServerGroupCompletions(String s, List<String> list) {
        for (ServerGroupObject serverGroupObjects : TimoCloudAPI.getUniversalAPI().getServerGroups())
            addCompletionToList(list, serverGroupObjects.getName(), s);
    }

    private void addServerCompletions(String s, List<String> list) {
        for (ServerObject serverObjects : TimoCloudAPI.getUniversalAPI().getServers())
            addCompletionToList(list, serverObjects.getName(), s);
    }

    private void addProxyGroupCompletions(String s, List<String> list) {
        for (ProxyGroupObject proxyGroupObjects : TimoCloudAPI.getUniversalAPI().getProxyGroups())
            addCompletionToList(list, proxyGroupObjects.getName(), s);
    }

    private void addProxyCompletions(String s, List<String> list) {
        for (ProxyObject proxyObjects : TimoCloudAPI.getUniversalAPI().getProxies())
            addCompletionToList(list, proxyObjects.getName(), s);
    }

    private void addCompletionToList(List<String> list, String completion, String s) {
        if (!list.contains(completion) && completion.startsWith(s))
            list.add(completion);
    }
}
