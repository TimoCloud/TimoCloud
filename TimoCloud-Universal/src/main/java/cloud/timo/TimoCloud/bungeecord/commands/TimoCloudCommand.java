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
                    addCompletionToList(tabCompletions, "help", strings[0]);
                    addCompletionToList(tabCompletions, "version", strings[0]);
                    addCompletionToList(tabCompletions, "reload", strings[0]);
                    addCompletionToList(tabCompletions, "reloadplugins", strings[0]);
                    addCompletionToList(tabCompletions, "addbase", strings[0]);
                    addCompletionToList(tabCompletions, "editbase", strings[0]);
                    addCompletionToList(tabCompletions, "addgroup", strings[0]);
                    addCompletionToList(tabCompletions, "removegroup", strings[0]);
                    addCompletionToList(tabCompletions, "editgroup", strings[0]);
                    addCompletionToList(tabCompletions, "restart", strings[0]);
                    addCompletionToList(tabCompletions, "groupinfo", strings[0]);
                    addCompletionToList(tabCompletions, "listgroups", strings[0]);
                    addCompletionToList(tabCompletions, "baseinfo", strings[0]);
                    addCompletionToList(tabCompletions, "listbases", strings[0]);
                    addCompletionToList(tabCompletions, "sendcommand", strings[0]);
                    addCompletionToList(tabCompletions, "check", strings[0]);
                    addCompletionToList(tabCompletions, "shutdown", strings[0]);
                    break;
                case 2:
                    switch (strings[0].toLowerCase()) {
                        case "editbase":
                        case "baseinfo":
                            addBaseCompletions(tabCompletions, strings[1]);
                            break;
                        case "addgroup":
                            addCompletionToList(tabCompletions, "server", strings[1]);
                            addCompletionToList(tabCompletions, "proxy", strings[1]);
                            break;
                        case "removegroup":
                        case "editgroup":
                        case "groupinfo":
                            addServerGroupCompletions(tabCompletions, strings[1]);
                            addProxyGroupCompletions(tabCompletions, strings[1]);
                            break;
                        case "restart":
                            addServerGroupCompletions(tabCompletions, strings[1]);
                            addServerCompletions(tabCompletions, strings[1]);
                            addProxyGroupCompletions(tabCompletions, strings[1]);
                            addProxyCompletions(tabCompletions, strings[1]);
                            addBaseCompletions(tabCompletions, strings[1]);
                            break;
                        case "sendcommand":
                            addServerCompletions(tabCompletions, strings[1]);
                            addProxyCompletions(tabCompletions, strings[1]);
                            break;
                        default:
                            break;
                    }
                    break;
                case 3:
                    switch (strings[0].toLowerCase()) {
                        case "editbase":
                            addCompletionToList(tabCompletions, "name", strings[2]);
                            addCompletionToList(tabCompletions, "maxram", strings[2]);
                            addCompletionToList(tabCompletions, "keepfreeram", strings[2]);
                            addCompletionToList(tabCompletions, "maxcpuload", strings[2]);
                            break;
                        case "editgroup":
                            ProxyGroupObject proxyGroupObject = TimoCloudAPI.getUniversalAPI().getProxyGroup(strings[1]);
                            ServerGroupObject serverGroupObject = TimoCloudAPI.getUniversalAPI().getServerGroup(strings[1]);

                            if (proxyGroupObject != null) {
                                addCompletionToList(tabCompletions, "minamount", strings[2]);
                                addCompletionToList(tabCompletions, "maxamount", strings[2]);
                                addCompletionToList(tabCompletions, "ram", strings[2]);
                                addCompletionToList(tabCompletions, "static", strings[2]);
                                addCompletionToList(tabCompletions, "base", strings[2]);
                                addCompletionToList(tabCompletions, "priority", strings[2]);
                                addCompletionToList(tabCompletions, "playersperproxy", strings[2]);
                                addCompletionToList(tabCompletions, "keepfreeslots", strings[2]);
                                addCompletionToList(tabCompletions, "maxplayers", strings[2]);
                            }
                            if (serverGroupObject != null) {
                                addCompletionToList(tabCompletions, "onlineamount", strings[2]);
                                addCompletionToList(tabCompletions, "ram", strings[2]);
                                addCompletionToList(tabCompletions, "static", strings[2]);
                                addCompletionToList(tabCompletions, "base", strings[2]);
                                addCompletionToList(tabCompletions, "priority", strings[2]);
                            }
                            break;
                        default:
                            break;
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

    private void addServerGroupCompletions(Set<String> set, String s) {
        serverGroupNames.forEach(serverGroupName -> addCompletionToList(set, serverGroupName, s));
    }

    private void addServerCompletions(Set<String> set, String s) {
        serverNames.forEach(serverName -> addCompletionToList(set, serverName, s));
    }

    private void addProxyGroupCompletions(Set<String> set, String s) {
        proxyGroupNames.forEach(proxyGroupName -> addCompletionToList(set, proxyGroupName, s));
    }

    private void addProxyCompletions(Set<String> set, String s) {
        proxyNames.forEach(proxyName -> addCompletionToList(set, proxyName, s));
    }

    private void addBaseCompletions(Set<String> set, String s) {
        baseNames.forEach(baseName -> addCompletionToList(set, baseName, s));
    }

    private void addCompletionToList(Set<String> set, String completion, String s) {
        if (completion.startsWith(s))
            set.add(completion);
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
