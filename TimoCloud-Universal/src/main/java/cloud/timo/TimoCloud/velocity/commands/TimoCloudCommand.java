package cloud.timo.TimoCloud.velocity.commands;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.*;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.utils.ChatColorUtil;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import cloud.timo.TimoCloud.velocity.managers.VelocityMessageManager;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.*;
import java.util.stream.Collectors;

public class TimoCloudCommand implements SimpleCommand {

    private Map<String, Invocation> senders;

    private Set<String> serverGroupNames = new HashSet<>();
    private Set<String> serverNames = new HashSet<>();
    private Set<String> proxyGroupNames = new HashSet<>();
    private Set<String> proxyNames = new HashSet<>();
    private Set<String> baseNames = new HashSet<>();

    public TimoCloudCommand() {
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
    public void execute(Invocation invocation) {
        try {
            String[] args = invocation.arguments();
            if (args.length < 1) {
                sendVersion(invocation);
                return;
            }
            if (!invocation.source().hasPermission("timocloud.admin")) {
                VelocityMessageManager.noPermission(invocation);
                return;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                TimoCloudVelocity.getInstance().getFileManager().load();
                VelocityMessageManager.sendMessage(invocation, "&aSuccessfully reloaded from configuration!");
                // Do not return because we want to reload the Core configuration as well
            }
            if (args[0].equalsIgnoreCase("version")) {
                sendVersion(invocation);
                return;
            }
            String command = Arrays.stream(args).collect(Collectors.joining(" "));
            String sendername = "console";
            if (invocation.source() instanceof Player) {
                sendername = ((Player) invocation.source()).getUsername();
            }
            senders.put(sendername, invocation);

            TimoCloudVelocity.getInstance().getSocketClientHandler().sendMessage(Message.create()
                    .setType(MessageType.CORE_PARSE_COMMAND)
                    .setData(command)
                    .set("sender", sendername)
                    .toString());
        } catch (Exception e) {
            invocation.source().sendMessage(Component.text(ChatColorUtil.translateAlternateColorCodes('&', "&cAn error occured while exeuting command. Please see console for more details.")));
            TimoCloudVelocity.getInstance().severe(e);
        }
    }

    private void sendVersion(Invocation sender) {
        VelocityMessageManager.sendMessage(sender, "&bTimoCloud Version &e[&6" + TimoCloudVelocity.getInstance().getServer().getPluginManager().getPlugin("timocloud").get().getDescription().getVersion().get() + "&e] &bby &6TimoCrafter");
    }

    public void sendMessage(String senderName, String message) {
        if (getSender(senderName) == null) return;
        getSender(senderName).source().sendMessage(Component.text(ChatColorUtil.translateAlternateColorCodes('&', message)));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] strings = invocation.arguments();
        if (invocation.source().hasPermission("timocloud.admin")) {
            Set<String> tabCompletions = new HashSet<>();

            if (strings.length == 0) {
                addCompletionToList(tabCompletions, "help");
                addCompletionToList(tabCompletions, "version");
                addCompletionToList(tabCompletions, "reload");
                addCompletionToList(tabCompletions, "addbase");
                addCompletionToList(tabCompletions, "editbase");
                addCompletionToList(tabCompletions, "addgroup");
                addCompletionToList(tabCompletions, "removegroup");
                addCompletionToList(tabCompletions, "editgroup");
                addCompletionToList(tabCompletions, "restart");
                addCompletionToList(tabCompletions, "groupinfo");
                addCompletionToList(tabCompletions, "listgroups");
                addCompletionToList(tabCompletions, "baseinfo");
                addCompletionToList(tabCompletions, "listbases");
                addCompletionToList(tabCompletions, "sendcommand");
                addCompletionToList(tabCompletions, "check");
                addCompletionToList(tabCompletions, "shutdown");
            }
            if (strings.length == 1) {
                addCompletionToList(tabCompletions, "help", strings[0]);
                addCompletionToList(tabCompletions, "version", strings[0]);
                addCompletionToList(tabCompletions, "reload", strings[0]);
                addCompletionToList(tabCompletions, "editbase", strings[0]);
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
                addCompletionToList(tabCompletions, "check", strings[0]);
                addCompletionToList(tabCompletions, "shutdown", strings[0]);
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
                if (strings[0].equalsIgnoreCase("editbase")) {
                    addBaseCompletions(strings[1], tabCompletions);
                }
                if (strings[0].equalsIgnoreCase("restart")) {
                    addServerGroupCompletions(strings[1], tabCompletions);
                    addServerCompletions(strings[1], tabCompletions);
                    addProxyGroupCompletions(strings[1], tabCompletions);
                    addProxyCompletions(strings[1], tabCompletions);
                    addBaseCompletions(strings[1], tabCompletions);
                }
                if (strings[0].equalsIgnoreCase("baseinfo")) {
                    addBaseCompletions(strings[1], tabCompletions);
                }
                if (strings[0].equalsIgnoreCase("sendcommand")) {
                    addServerCompletions(strings[1], tabCompletions);
                    addProxyCompletions(strings[1], tabCompletions);
                }
            }
            if (strings.length == 3) {
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
            }
            return new ArrayList<>(tabCompletions);
        }
        return new ArrayList<>();
    }

    private void addCompletionToList(Set<String> tabCompletions, String completion) {
        tabCompletions.add(completion);
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

    private Invocation getSender(String name) {
        if (!senders.containsKey(name)) return null;
        return senders.get(name);
    }
}
