package cloud.timo.TimoCloud.bungeecord.commands;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.*;
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

import java.util.*;
import java.util.stream.Collectors;

public class TimoCloudCommand extends Command implements TabExecutor {

    private final Map<String, CommandSender> senders;
    private final Set<String> subCommandNames = new HashSet<>();
    private Set<String> serverGroupNames;
    private Set<String> serverNames;
    private Set<String> proxyGroupNames;
    private Set<String> proxyNames;
    private Set<String> baseNames;

    public TimoCloudCommand() {
        super("TimoCloud", "timocloud.admin");
        senders = new HashMap<>();
    }

    public void loadNames() {
        serverGroupNames = TimoCloudAPI.getUniversalAPI().getServerGroups().stream().map(ServerGroupObject::getName).collect(Collectors.toSet());
        serverNames = TimoCloudAPI.getUniversalAPI().getServers().stream().map(ServerObject::getName).collect(Collectors.toSet());
        proxyGroupNames = TimoCloudAPI.getUniversalAPI().getProxyGroups().stream().map(ProxyGroupObject::getName).collect(Collectors.toSet());
        proxyNames = TimoCloudAPI.getUniversalAPI().getProxies().stream().map(ProxyObject::getName).collect(Collectors.toSet());
        baseNames = TimoCloudAPI.getUniversalAPI().getBases().stream().map(BaseObject::getName).collect(Collectors.toSet());
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
        Set<String> tabCompletions = new HashSet<>();

        if (commandSender.hasPermission("timocloud.admin")) {
            switch (strings.length) {
                case 1:
                    subCommandNames.forEach(subCommandName -> addCompletionToList(tabCompletions, subCommandName, strings[0]));
                    break;
                case 2:
                    switch (strings[0].toLowerCase()) {
                        case "addgroup":
                            addCompletionToList(tabCompletions, "server", strings[1]);
                            addCompletionToList(tabCompletions, "proxy", strings[1]);
                            break;
                        case "removegroup":
                        case "editgroup":
                        case "groupinfo":
                            addServerGroupCompletions(strings[1], tabCompletions);
                            addProxyGroupCompletions(strings[1], tabCompletions);
                            break;
                        case "restart":
                            addServerGroupCompletions(strings[1], tabCompletions);
                            addServerCompletions(strings[1], tabCompletions);
                            addProxyGroupCompletions(strings[1], tabCompletions);
                            addProxyCompletions(strings[1], tabCompletions);
                            addBaseCompletions(strings[1], tabCompletions);
                            break;
                        case "baseinfo":
                            addBaseCompletions(strings[1], tabCompletions);
                            break;
                        case "sendcommand":
                            addServerCompletions(strings[1], tabCompletions);
                            addProxyCompletions(strings[1], tabCompletions);
                            break;
                        default:
                            break;
                    }
                    break;
                case 3:
                    if (strings[0].equalsIgnoreCase("editgroup")) {
                        addCompletionToList(tabCompletions, "onlineamount", strings[2]);
                        addCompletionToList(tabCompletions, "maxamount", strings[2]);
                        addCompletionToList(tabCompletions, "ram", strings[2]);
                        addCompletionToList(tabCompletions, "static", strings[2]);
                        addCompletionToList(tabCompletions, "priority", strings[2]);
                        addCompletionToList(tabCompletions, "base", strings[2]);
                        addCompletionToList(tabCompletions, "playersperproxy", strings[2]);
                        addCompletionToList(tabCompletions, "maxplayers", strings[2]);
                        addCompletionToList(tabCompletions, "keepfreeslots", strings[2]);
                        addCompletionToList(tabCompletions, "minamount", strings[2]);
                    }
                    break;
                case 4:
                    switch (strings[2].toLowerCase()) {
                        case "ram":
                            addCompletionToList(tabCompletions, "512", strings[3]);
                            addCompletionToList(tabCompletions, "1024", strings[3]);
                            addCompletionToList(tabCompletions, "2048", strings[3]);
                            addCompletionToList(tabCompletions, "4096", strings[3]);
                            addCompletionToList(tabCompletions, "8192", strings[3]);
                            break;
                        case "base":
                            for (BaseObject baseObject : TimoCloudAPI.getUniversalAPI().getBases()) {
                                addCompletionToList(tabCompletions, baseObject.getName(), strings[3]);
                            }
                            break;
                        default:
                            break;
                    }
                default:
                    break;
            }
        }
        return tabCompletions;
    }

    private void addServerGroupCompletions(String s, Set<String> list) {
        serverGroupNames.forEach(serverGroupName -> addCompletionToList(list, serverGroupName, s));
    }

    private void addServerCompletions(String s, Set<String> list) {
        serverNames.forEach(serverName -> addCompletionToList(list, serverName, s));
    }

    private void addProxyGroupCompletions(String s, Set<String> list) {
        proxyGroupNames.forEach(proxyGroupName -> addCompletionToList(list, proxyGroupName, s));
    }

    private void addProxyCompletions(String s, Set<String> list) {
        proxyNames.forEach(proxyName -> addCompletionToList(list, proxyName, s));
    }

    private void addBaseCompletions(String s, Set<String> list) {
        baseNames.forEach(baseName -> addCompletionToList(list, baseName, s));
    }

    private void addCompletionToList(Set<String> set, String completion, String s) {
        if (completion.toLowerCase().startsWith(s.toLowerCase()))
            set.add(completion);
    }

    public void addSubCommandName(String name) {
        subCommandNames.add(name);
    }

    public void removeSubCommandName(String name) {
        subCommandNames.add(name);
    }

    public void addServerGroupName(String name) {
        serverGroupNames.add(name);
    }

    public void addServerName(String name) {
        serverNames.add(name);
    }

    public void addProxyGroupName(String name) {
        proxyGroupNames.add(name);
    }

    public void addProxyName(String name) {
        proxyNames.add(name);
    }

    public void addBaseName(String name) {
        baseNames.add(name);
    }

    public void removeServerGroupName(String name) {
        serverGroupNames.remove(name);
    }

    public void removeServerName(String name) {
        serverNames.remove(name);
    }

    public void removeProxyGroupName(String name) {
        proxyGroupNames.remove(name);
    }

    public void removeProxyName(String name) {
        proxyNames.remove(name);
    }

    public void removeBaseName(String name) {
        baseNames.remove(name);
    }
}
