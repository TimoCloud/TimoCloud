package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.*;
import cloud.timo.TimoCloud.lib.debugger.DataCollector;
import cloud.timo.TimoCloud.lib.utils.ChatColorUtil;
import org.json.simple.JSONObject;

import java.io.File;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandManager {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

    private void send(String message) {
        TimoCloudCore.getInstance().info(message);
    }

    private void sendError(String message) {
        TimoCloudCore.getInstance().severe(message);
    }

    private void sendError(Consumer<String> sendMessage, String message) {
        sendMessage.accept("&c" + message);
    }
    
    private void sendError(Consumer<String> sendMessage, boolean local, String message) {
        if (local) sendError(sendMessage, message);
        else sendError(sendMessage, message);
    }

    public void onCommand(String command) {
        onCommand((str) -> send(ChatColorUtil.toLegacyText(str + "&r")), true, command);
    }

    public void onCommand(Consumer<String> sendMessage, boolean local, String command) {
        String[] split = command.split(" ");
        if (split.length < 1) return;
        String cmd = split[0];
        String[] args = split.length == 1 ? new String[0] : Arrays.copyOfRange(split, 1, split.length);
        onCommand(sendMessage, local, cmd, args);
    }

    public void onCommand(Consumer<String> sendMessage, boolean local, String command, String ... args) {
        try {
            if (command.equalsIgnoreCase("reload")) {
                TimoCloudCore.getInstance().getFileManager().load();
                TimoCloudCore.getInstance().getServerManager().loadGroups();
                sendMessage.accept("&2Successfully reloaded from configuration!");
                return;
            }
            if (command.equalsIgnoreCase("version")) {
                sendMessage.accept("&bTimo&fCloud &6version &e" + TimoCloudCore.class.getPackage().getImplementationVersion() + " &6by &cTimoCrafter.");
                return;
            }
            if (command.equalsIgnoreCase("help")) {
                sendHelp(sendMessage);
                return;
            }
            if (Arrays.asList("stop", "end", "exit", "quit").contains(command.toLowerCase())) {
                System.exit(0);
                return;
            }

            if (command.equalsIgnoreCase("groupinfo")) {
                Group group = TimoCloudCore.getInstance().getServerManager().getGroupByName(args[0]);
                displayGroup(sendMessage, group);
                return;
            }

            if (command.equalsIgnoreCase("baseinfo")) {
                Base base = TimoCloudCore.getInstance().getServerManager().getBase(args[0]);
                if (base == null) {
                    sendError(sendMessage, "Could not find base '" + args[0] + "'.");
                    return;
                }
                displayBase(sendMessage, base);
                return;
            }

            if (command.equalsIgnoreCase("listgroups")) {
                Collection<ServerGroup> serverGroups = TimoCloudCore.getInstance().getServerManager().getServerGroups();
                Collection<ProxyGroup> proxyGroups = TimoCloudCore.getInstance().getServerManager().getProxyGroups();

                sendMessage.accept("&6ServerGroups (&3" + serverGroups.size() + "&6):");
                for (ServerGroup group : serverGroups) {
                    displayGroup(sendMessage, group);
                }
                sendMessage.accept("&6ProxyGroups (&3" + proxyGroups.size() + "&6):");
                for (ProxyGroup group : proxyGroups) {
                    displayGroup(sendMessage, group);
                }
                return;
            }

            if (command.equalsIgnoreCase("listbases")) {
                List<Base> bases = TimoCloudCore.getInstance().getServerManager().getBases().stream().filter(Base::isConnected).collect(Collectors.toList());
                sendMessage.accept("&6Bases (&3" + bases.size() + "&6):");
                for (Base base : bases) {
                    displayBase(sendMessage, base);
                }
                return;
            }

            if (command.equalsIgnoreCase("removegroup") || command.equalsIgnoreCase("deletegroup")) {
                String name = args[0];
                try {
                    ServerGroup serverGroup = TimoCloudCore.getInstance().getServerManager().getServerGroupByName(name);
                    ProxyGroup proxyGroup = TimoCloudCore.getInstance().getServerManager().getProxyGroupByName(name);
                    if (serverGroup == null && proxyGroup == null) {
                        sendError(sendMessage, local, "The group " + name + " does not exist. Type 'listgroups' for a list of all groups.");
                        return;
                    }
                    if (serverGroup != null)
                        TimoCloudCore.getInstance().getServerManager().removeServerGroup(serverGroup);
                    if (proxyGroup != null) TimoCloudCore.getInstance().getServerManager().removeProxyGroup(proxyGroup);

                    sendMessage.accept("Successfully deleted group &e" + name);
                } catch (Exception e) {
                    sendError(sendMessage, local, "Error while saving groups.yml. See console for mor information.");
                    e.printStackTrace();
                }
                return;
            }

            if (command.equalsIgnoreCase("restart") || command.equalsIgnoreCase("stop")) {
                String target = args[0];
                ServerGroup serverGroup = TimoCloudCore.getInstance().getServerManager().getServerGroupByName(target);
                ProxyGroup proxyGroup = TimoCloudCore.getInstance().getServerManager().getProxyGroupByName(target);

                Server server = TimoCloudCore.getInstance().getServerManager().getServerByName(target);
                Proxy proxy = TimoCloudCore.getInstance().getServerManager().getProxyByName(target);

                if (serverGroup == null && proxyGroup == null && server == null && proxy == null) {
                    sendError(sendMessage, local, "Could not find any group, server or proxy with the name '" + target + "'");
                    return;
                }

                if (serverGroup != null) serverGroup.stopAllServers();
                else if (proxyGroup != null) proxyGroup.stopAllProxies();
                else if (server != null) server.stop();
                else if (proxy != null) proxy.stop();

                sendMessage.accept("&2The group/server/proxy has successfully been stopped/restarted.");
                return;
            }
            if (command.equalsIgnoreCase("sendcommand")) {
                String target = args[0];
                ServerGroup serverGroup = TimoCloudCore.getInstance().getServerManager().getServerGroupByName(target);
                ProxyGroup proxyGroup = TimoCloudCore.getInstance().getServerManager().getProxyGroupByName(target);

                Server server = TimoCloudCore.getInstance().getServerManager().getServerByName(target);
                Proxy proxy = TimoCloudCore.getInstance().getServerManager().getProxyByName(target);

                if (serverGroup == null && proxyGroup == null && server == null && proxy == null) {
                    sendError(sendMessage, local, "Could not find any group, server or proxy with the name '" + target + "'");
                    return;
                }
                String cmd = "";
                for (int i = 1; i < args.length; i++) cmd += args[i] + " ";
                cmd = cmd.trim();
                if (cmd.length() == 0) {
                    sendError(sendMessage, local, "Please provide a command.");
                    sendHelp(sendMessage);
                    return;
                }

                if (serverGroup != null) for (Server server1 : serverGroup.getServers()) server1.executeCommand(cmd);
                else if (proxyGroup != null) for (Proxy proxy1 : proxyGroup.getProxies()) proxy1.executeCommand(cmd);
                else if (server != null) server.executeCommand(cmd);
                else if (proxy != null) proxy.executeCommand(cmd);

                sendMessage.accept("&2The command has successfully been executed on the target.");
                return;
            }
            if (command.equalsIgnoreCase("addgroup")) {
                String type = args[0];
                String name = args[1];

                if (TimoCloudCore.getInstance().getServerManager().getGroupByName(name) != null) {
                    sendError(sendMessage, local, "This group already exists.");
                    return;
                }
                Group group;
                if (type.equalsIgnoreCase("server")) {
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("name", name);
                    properties.put("online-amount", Integer.parseInt(args[2]));
                    properties.put("ram", Integer.parseInt(args[3]));
                    boolean isStatic = Boolean.parseBoolean(args[4]);
                    properties.put("static", isStatic);
                    if (isStatic && args.length > 5) properties.put("base", args[5]);

                    if (isStatic && ! properties.containsKey("base")) {
                        sendError(sendMessage, "If you create a static group, you have to specify a base!");
                        return;
                    }

                    ServerGroup serverGroup = new ServerGroup(properties);
                    TimoCloudCore.getInstance().getServerManager().addGroup(serverGroup);
                    TimoCloudCore.getInstance().getServerManager().saveServerGroups();
                    group = serverGroup;
                } else if (type.equalsIgnoreCase("proxy")) {
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("name", name);
                    properties.put("ram", Integer.parseInt(args[2]));
                    boolean isStatic = Boolean.parseBoolean(args[3]);
                    properties.put("static", isStatic);
                    if (isStatic && args.length > 4) properties.put("base", args[4]);

                    if (isStatic && ! properties.containsKey("base")) {
                        sendError(sendMessage, "If you create a static group, you have to specify a base!");
                        return;
                    }

                    ProxyGroup proxyGroup = new ProxyGroup(properties);
                    TimoCloudCore.getInstance().getServerManager().addGroup(proxyGroup);
                    TimoCloudCore.getInstance().getServerManager().saveProxyGroups();
                    group = proxyGroup;
                } else {
                    sendError(sendMessage, local, "Unknown group type: '" + type + "'");
                    return;
                }

                try {
                    sendMessage.accept("&2Group &e" + name + " &2has successfully been created.");
                    displayGroup(sendMessage, group);
                } catch (Exception e) {
                    sendError(sendMessage, local, "Error while saving group: ");
                    e.printStackTrace();
                }
                return;
            }
            if (command.equalsIgnoreCase("editgroup")) {
                String groupName = args[0];
                Group group = TimoCloudCore.getInstance().getServerManager().getGroupByName(groupName);
                if (group == null) {
                    sendError(sendMessage, local, "Group " + groupName + " not found. Get a list of all groups with 'listgroups'");
                    return;
                }
                String key = args[1];
                String value = args[2];
                if (group instanceof ServerGroup) {
                    ServerGroup serverGroup = (ServerGroup) group;
                    switch (key.toLowerCase()) {
                        case "onlineamount":
                            serverGroup.setOnlineAmount(Integer.parseInt(value));
                            break;
                        case "maxamount":
                            serverGroup.setMaxAmount(Integer.parseInt(value));
                            break;
                        case "base":
                            String baseName = value;
                            if (baseName.equalsIgnoreCase("none") || baseName.equalsIgnoreCase("dynamic"))
                                baseName = null;
                            serverGroup.setBaseName(baseName);
                            break;
                        case "ram":
                            int ram = Integer.parseInt(value);
                            serverGroup.setRam(ram);
                            break;
                        case "static":
                            boolean isStatic = Boolean.parseBoolean(value);
                            serverGroup.setStatic(isStatic);
                            break;
                        case "priority":
                            int priority = Integer.parseInt(value);
                            serverGroup.setPriority(priority);
                            break;
                        default:
                            sendError(sendMessage, local, "No valid argument found. Please use \n" +
                                    "editgroup <name> <onlineAmount (int) | maxAmount (int) | base (String) | ram (int) | static (boolean) | priority (int)> <value>");
                            return;
                    }
                    TimoCloudCore.getInstance().getServerManager().saveServerGroups();
                } else if (group instanceof ProxyGroup) {
                    ProxyGroup proxyGroup = (ProxyGroup) group;
                    switch (key.toLowerCase()) {
                        case "playersperproxy":
                            proxyGroup.setMaxPlayerCountPerProxy(Integer.parseInt(value));
                            break;
                        case "maxplayers":
                            proxyGroup.setMaxPlayerCount(Integer.parseInt(value));
                            break;
                        case "keepfreeslots":
                            proxyGroup.setKeepFreeSlots(Integer.parseInt(value));
                            break;
                        case "minamount":
                            proxyGroup.setMinAmount(Integer.parseInt(value));
                            break;
                        case "maxamount":
                            proxyGroup.setMaxAmount(Integer.parseInt(value));
                            break;
                        case "base":
                            String baseName = value;
                            if (baseName.equalsIgnoreCase("none") || baseName.equalsIgnoreCase("dynamic"))
                                baseName = null;
                            proxyGroup.setBaseName(baseName);
                            break;
                        case "ram":
                            int ram = Integer.parseInt(value);
                            proxyGroup.setRam(ram);
                            break;
                        case "static":
                            boolean isStatic = Boolean.parseBoolean(value);
                            proxyGroup.setStatic(isStatic);
                            break;
                        case "priority":
                            int priority = Integer.parseInt(value);
                            proxyGroup.setPriority(priority);
                            break;
                        default:
                            sendError(sendMessage, local, "No valid argument found. Please use \n" +
                                    "editgroup <name> <playersPerProxy (int) | maxPlayers (int) | keepFreeSlots (int) | minAmount (int) | maxAmount (int) | base (String) | ram (int) | static (boolean) | priority (int)> <value>");
                            return;
                    }
                    TimoCloudCore.getInstance().getServerManager().saveProxyGroups();
                }
                sendMessage.accept("&2Group &e" + group.getName() + " &2has successfully been edited. New data: ");
                displayGroup(sendMessage, group);
                return;
            }
            if (command.equalsIgnoreCase("debug")) {
                try {
                    JSONObject jsonObject = DataCollector.collectData(TimoCloudCore.getInstance());
                    TimoCloudCore.getInstance().getFileManager().saveJson(jsonObject, new File(
                            TimoCloudCore.getInstance().getFileManager().getDebugDirectory(), DATE_FORMAT.format(new Date()) + ".json"));
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe("An error occured while collecting debugging data: ");
                    TimoCloudCore.getInstance().severe(e);
                }
                return;
            }
        } catch (Exception e) {
            sendError(sendMessage, local, "Wrong usage.");
            sendHelp(sendMessage);
            return;
        }
        sendError(sendMessage, local, "Unknown command: " + command);
        sendHelp(sendMessage);
    }

    private void displayGroup(Consumer<String> sendMessage, Group group) {
        if (group instanceof ServerGroup) displayGroup(sendMessage, (ServerGroup) group);
        else if (group instanceof ProxyGroup) displayGroup(sendMessage, (ProxyGroup) group);
    }

    private void displayGroup(Consumer<String> sendMessage, ServerGroup group) {
        sendMessage.accept("  &e" + group.getName() +
                        " &7(&6RAM&7: &2" + group.getRam() + "MB" +
                        "&7, &6Keep-Online-Amount&7: &2" + group.getOnlineAmount() +
                        "&7, &6Max-Amount&7: &2" + group.getMaxAmount() +
                        "&7, &6static&7: &2" + group.isStatic() +
                        "&7)");
        sendMessage.accept("  &6Servers&7: &2" + group.getServers().size());
        for (Server server : group.getServers()) {
            sendMessage.accept("    &e" + server.getName() +
                    " &7(&6Base&7: &2" + server.getBase().getName() + "&7) " +
                    " &7(&6State&7: " + server.getState() + "&7) " +
                    (server.getMap() == null || server.getMap().equals("") ? "" : (" &7(&6Map&7: &e" + server.getMap() + "&7)")));
        }
    }

    private void displayGroup(Consumer<String> sendMessage, ProxyGroup group) {
        sendMessage.accept("  &e" + group.getName() +
                " &7(&6RAM&7: &2" + group.getRam() + "MB" +
                "&7, &6Current-Online-Players&7: &2" + group.getOnlinePlayerCount() +
                "&7, &6Total-Max-Players&7: &2" + group.getMaxPlayerCount() +
                "&7, &6Players-Per-Proxy&7: &2" + group.getMaxPlayerCountPerProxy() +
                "&7, &6Keep-Free-Slots&7: &2" + group.getKeepFreeSlots() +
                "&7, &6Max-Amount&7: &2" + group.getMaxAmount() +
                "&7, &6Min-Amount&7: &2" + group.getMinAmount() +
                "&7, &6Priority&7: &2" + group.getPriority() +
                "&7, &6static&7: &2" + group.isStatic() +
                "&7)");
        sendMessage.accept("  &6Proxies&7: &2" + group.getProxies().size());
        for (Proxy proxy : group.getProxies()) {
            sendMessage.accept("    " + proxy.getName() +
                    " &7(&6Base&7: &2" + proxy.getBase().getName() + "&7) " +
                    " &7(&6Players&7: &2" + proxy.getOnlinePlayerCount() + "&7) ");
        }
    }

    private void displayBase(Consumer<String> sendMessage, Base base) {
        sendMessage.accept("  &e" + base.getName() +
                " &7(&6Free RAM&7: &2" + base.getAvailableRam() + "MB" +
                "&7, &6Max RAM&7: &2" + base.getMaxRam() + "MB" +
                "&7, &6CPU load&7: &2" + (int) base.getCpu() + "%" +
                "&7, &6IP Address&7: &2" + formatIp(base.getAddress()) +
                "&7, &6Ready&7: &2" + formatBoolean(base.isReady()) +
                "&7, &6Connected&7: &2" + formatBoolean(base.isConnected()) +
                "&7)");
    }

    private void sendHelp(Consumer<String> sendMessage) {
        sendMessage.accept("&6Available commands for &bTimo&fCloud&7:");
        sendMessage.accept("  &6help &7- &7shows this page");
        sendMessage.accept("  &6version &7- &7shows the plugin version");
        sendMessage.accept("  &6reload &7- &7reloads all configs");
        sendMessage.accept("  &6addgroup server &7<&2groupName &7(&9String&7)> <&2onlineAmount &7(&9int&7)> <&2ram &7(&9int&7)> <&2static &7(&9boolean&7)> <&2base &7(&9String&7), &6only needed if static=true&7> - &7creates a server group");
        sendMessage.accept("  &6addgroup proxy &7<&2groupName &7(&9String&7)> <&2ram &7(&9int&7)> <&2static &7(&9boolean&7)> <&2base &7(&9String&7), &6only needed if static=true&7> - &7creates a proxy group");
        sendMessage.accept("  &6removegroup &7<&2groupName&7> - &7deletes a group");
        sendMessage.accept("  &6editgroup &7<&2name&7> <&2onlineAmount &7(&9int&7) | &2maxAmount &7(&9int&7) | &2ram &7(&9int&7) | &2static &7(&9boolean&7) | &2priority &7(&9int&7) | &2base &7(&9String&7)> <&2value&7> - &7edits the give setting of a server group");
        sendMessage.accept("  &6editgroup &7<&2name&7> <&2playersPerProxy &7(&9int&7) | &2maxPlayers &7(&9int&7) | &2keepFreeSlots &7(&9int&7) | &2minAmount &7(&9int&7) | &2maxAmount &7(&9int&7) | &2ram &7(&9int&7) | &2static &7(&9boolean&7) | &2priority &7(&9int&7) | &2base &7(&9String&7)> <&2value&7> - &7edits the give setting of a proxy group");
        sendMessage.accept("  &6restart &7<&2groupName&7 | &2serverName&7 | &2proxyName&7> - &7restarts the given group, server or proxy");
        sendMessage.accept("  &6groupinfo &7<&2groupName&7> - displays group info");
        sendMessage.accept("  &6listgroups &7- &7lists all groups and started servers");
        sendMessage.accept("  &6baseinfo &7<&2baseName&7> - displays base info");
        sendMessage.accept("  &6listbases &7- &7lists all bases");
        sendMessage.accept("  &6sendcommand &7<&2groupName&7 | &2serverName&7 | &2proxyName&7> <&2command&7> - &7sends the given command to all server of a given group or the given server");
    }

    private static String formatBoolean(boolean b) {
        return b ? "&2true" : "&cfalse";
    }

    private static String formatIp(InetAddress ip) {
        String s = ip.toString();
        if (s.startsWith("/")) s = s.substring(1);
        return s;
    }

}
