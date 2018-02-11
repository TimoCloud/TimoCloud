package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class CommandManager {

    private void send(String message) {
        TimoCloudCore.getInstance().info(message);
    }

    private void sendError(String message) {
        TimoCloudCore.getInstance().severe(message);
    }

    public void onCommand(String command, String ... args) {
        try {
            if (command.equalsIgnoreCase("reload")) {
                TimoCloudCore.getInstance().getFileManager().load();
                TimoCloudCore.getInstance().getServerManager().loadGroups();
                send("Successfully reloaded from configuration!");
                return;
            }
            if (command.equalsIgnoreCase("version")) {
                send("TimoCloud version " + TimoCloudCore.class.getPackage().getImplementationVersion() + " by TimoCrafter.");
                return;
            }
            if (command.equalsIgnoreCase("help")) {
                sendHelp();
                return;
            }
            if (Arrays.asList("stop", "end", "exit", "quit").contains(command.toLowerCase())) {
                System.exit(0);
                return;
            }

            if (command.equalsIgnoreCase("listgroups")) {
                Collection<ServerGroup> serverGroups = TimoCloudCore.getInstance().getServerManager().getServerGroups();
                Collection<ProxyGroup> proxyGroups = TimoCloudCore.getInstance().getServerManager().getProxyGroups();

                send("ServerGroups (" + serverGroups.size() + "):");
                for (ServerGroup group : serverGroups) {
                    displayGroup(group);
                }
                send("ProxyGroups (" + proxyGroups.size() + "):");
                for (ProxyGroup group : proxyGroups) {
                    displayGroup(group);
                }
                return;
            }
            if (command.equalsIgnoreCase("removegroup")) {
                String name = args[0];
                try {
                    ServerGroup serverGroup = TimoCloudCore.getInstance().getServerManager().getServerGroupByName(name);
                    ProxyGroup proxyGroup = TimoCloudCore.getInstance().getServerManager().getProxyGroupByName(name);
                    if (serverGroup == null && proxyGroup == null) {
                        sendError("The group " + name + " does not exist. Type 'listgroups' for a list of all groups.");
                        return;
                    }
                    if (serverGroup != null)
                        TimoCloudCore.getInstance().getServerManager().removeServerGroup(serverGroup);
                    if (proxyGroup != null) TimoCloudCore.getInstance().getServerManager().removeProxyGroup(proxyGroup);

                    send("Successfully deleted group &e" + name);
                } catch (Exception e) {
                    sendError("Error while saving groups.yml. See console for mor information.");
                    e.printStackTrace();
                }
                return;
            }
            if (command.equalsIgnoreCase("restartgroup")) {
                String name = args[0];
                ServerGroup serverGroup = TimoCloudCore.getInstance().getServerManager().getServerGroupByName(name);
                ProxyGroup proxyGroup = TimoCloudCore.getInstance().getServerManager().getProxyGroupByName(name);
                if (serverGroup == null && proxyGroup == null) {
                    sendError("Group " + name + " does not exist.");
                    return;
                }
                if (serverGroup != null) {
                    serverGroup.stopAllServers();
                    send("All servers of group " + serverGroup.getName() + " have successfully been restarted.");
                }
                if (proxyGroup != null) {
                    proxyGroup.stopAllProxies();
                    send("All proxies of group " + proxyGroup.getName() + " have successfully been restarted.");
                }
                return;
            }
            if (command.equalsIgnoreCase("stopserver") || command.equalsIgnoreCase("restartserver")) {
                String name = args[0];
                Server server = TimoCloudCore.getInstance().getServerManager().getServerByName(name);
                if (server == null) {
                    sendError("Server " + name + " does not exist.");
                    return;
                }
                server.stop();
                send("Server " + server.getName() + " has successfully been stopped.");
                return;
            }
            if (command.equalsIgnoreCase("stopproxy") || command.equalsIgnoreCase("restartproxy")) {
                String name = args[0];
                Proxy proxy = TimoCloudCore.getInstance().getServerManager().getProxyByName(name);
                if (proxy == null) {
                    sendError("Proxy " + name + " does not exist.");
                    return;
                }
                proxy.stop();
                send("Proxy " + proxy.getName() + " has successfully been stopped.");
                return;
            }
            if (command.equalsIgnoreCase("sendcommand")) {
                String target = args[0];
                ServerGroup serverGroup = TimoCloudCore.getInstance().getServerManager().getServerGroupByName(target);
                ProxyGroup proxyGroup = TimoCloudCore.getInstance().getServerManager().getProxyGroupByName(target);

                Server server = TimoCloudCore.getInstance().getServerManager().getServerByName(target);
                Proxy proxy = TimoCloudCore.getInstance().getServerManager().getProxyByName(target);

                if (server == null && proxyGroup == null && server == null && proxy == null) {
                    sendError("Could not find any group, server or proxy with the name '" + target + "'");
                    return;
                }
                String cmd = "";
                for (int i = 1; i < args.length; i++) cmd += args[i] + " ";
                cmd = cmd.trim();
                if (cmd.length() == 0) {
                    sendError("Please provide a command.");
                    sendHelp();
                    return;
                }

                if (serverGroup != null) for (Server server1 : serverGroup.getServers()) server1.executeCommand(cmd);
                if (proxyGroup != null) for (Proxy proxy1 : proxyGroup.getProxies()) proxy1.executeCommand(cmd);
                if (server != null) server.executeCommand(cmd);
                if (proxy != null) proxy.executeCommand(cmd);

                send("The command has successfully been executed on the target.");
                return;
            }
            if (command.equalsIgnoreCase("addgroup")) {
                String type = args[0];
                String name = args[1];

                if (TimoCloudCore.getInstance().getServerManager().getGroupByName(name) != null) {
                    sendError("This group already exists.");
                    return;
                }
                Group group = null;
                if (type.equalsIgnoreCase("server")) {
                    int amount = Integer.parseInt(args[2]);
                    int maxAmount = Integer.parseInt(args[3]);
                    int ram = Integer.parseInt(args[4]);
                    boolean isStatic = Boolean.parseBoolean(args[5]);
                    int priority = Integer.parseInt(args[6]);
                    String base = args.length > 7 ? args[7] : null;

                    ServerGroup serverGroup = new ServerGroup(name, amount, maxAmount, ram, isStatic, priority, base, Arrays.asList("OFFLINE", "STARTING", "INGAME"));
                    TimoCloudCore.getInstance().getServerManager().addGroup(serverGroup);
                    TimoCloudCore.getInstance().getServerManager().saveServerGroups();
                    group = serverGroup;
                } else if (type.equalsIgnoreCase("proxy")) {
                    int playersPerProxy = Integer.parseInt(args[2]);
                    int maxPlayers = Integer.parseInt(args[3]);
                    int keepFreeSlots = Integer.parseInt(args[4]);
                    int maxAmount = Integer.parseInt(args[5]);
                    int ram = Integer.parseInt(args[6]);
                    boolean isStatic = Boolean.parseBoolean(args[7]);
                    int priority = Integer.parseInt(args[8]);
                    String baseName = args.length > 9 ? args[9] : null;

                    ProxyGroup proxyGroup = new ProxyGroup(name, playersPerProxy, maxPlayers, keepFreeSlots, maxAmount, ram, null, isStatic, priority, Collections.singletonList("*"), baseName, null, new ArrayList<>());
                    TimoCloudCore.getInstance().getServerManager().addGroup(proxyGroup);
                    TimoCloudCore.getInstance().getServerManager().saveProxyGroups();
                    group = proxyGroup;
                } else {
                    sendError("Unknown group type: '" + type + "'");
                }

                try {
                    send("Group " + name + " has successfully been created.");
                    displayGroup(group);
                } catch (Exception e) {
                    sendError("Error while saving group: ");
                    e.printStackTrace();
                }
                return;
            }
            if (command.equalsIgnoreCase("editgroup")) {
                String groupName = args[0];
                Group group = TimoCloudCore.getInstance().getServerManager().getGroupByName(groupName);
                if (group == null) {
                    sendError("Group " + groupName + " not found. Get a list of all groups with 'listgroups'");
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
                            sendError("No valid argument found. Please use \n" +
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
                            sendError("No valid argument found. Please use \n" +
                                    "editgroup <name> <playersPerProxy (int) | maxPlayers (int) | keepFreeSlots (int) | maxAmount (int) | base (String) | ram (int) | static (boolean) | priority (int)> <value>");
                            return;
                    }
                    TimoCloudCore.getInstance().getServerManager().saveProxyGroups();
                }
                send("Group " + group.getName() + " has successfully been edited. New data: ");
                displayGroup(group);
                return;
            }
        } catch (Exception e) {
            sendError("Wrong usage.");
            sendHelp();
            return;
        }
        sendError("Unknown command: " + command);
        sendHelp();
    }

    private void displayGroup(Group group) {
        if (group instanceof ServerGroup) displayGroup((ServerGroup) group);
        else if (group instanceof ProxyGroup) displayGroup((ProxyGroup) group);
    }

    private void displayGroup(ServerGroup group) {
        send("  " + group.getName() +
                        " (RAM: " + group.getRam() + "M" +
                        ",7Keep-Online-Amount: " + group.getOnlineAmount() +
                        ", Max-Amount: " + group.getMaxAmount() +
                        ", static: " + group.isStatic() +
                        ")");
        send("  Servers: " + group.getServers().size());
        for (Server server : group.getServers()) {
            send("    " + server.getName() +
                    " (Base: " + server.getBase().getName() + ") " +
                    " (State: " + server.getState() + ") " +
                    (server.getMap() == null || server.getMap().equals("") ? "" : (" (Map: &e" + server.getMap() + ")")));
        }
    }

    private void displayGroup(ProxyGroup group) {
        send("  " + group.getName() +
                " (RAM: " + group.getRam() + "M" +
                ", Current-Online-Players: " + group.getOnlinePlayerCount() +
                ", Total-Max-Players: " + group.getMaxPlayerCount() +
                ", Players-Per-Proxy: " + group.getMaxPlayerCountPerProxy() +
                ", Keep-Free-Slots: " + group.getKeepFreeSlots() +
                ", Max-Amount: " + group.getMaxAmount() +
                ", Priority: " + group.getPriority() +
                ", static: " + group.isStatic() +
                ")");
        send("  Proxies: " + group.getProxies().size());
        for (Proxy proxy : group.getProxies()) {
            send("    " + proxy.getName() +
                    " (Base: " + proxy.getBase().getName() + ") " +
                    " (Players: " + proxy.getOnlinePlayerCount() + ") ");
        }
    }

    public void sendHelp() {
        send("Available commands for TimoCloud:");
        send("  help - shows this page");
        send("  version - shows the plugin version");
        send("  reload - reloads all configs");
        send("  addgroup server <groupName (String)> <onlineAmount (int)> <maxAmount (int)> <ram (int)> <static (boolean)> <priority (int)> <base (String), optional> - creates a server group. If no base is specified, one will get chosen dynamically.");
        send("  addgroup proxy <groupName (String)> <playersPerProxy (int)> <maxPlayers (int)> <keepFreeSlots (int)> <maxAmount (int)> <ram (int)> <static (boolean)> <priority (int)> <base (String), optional> - creates a cord group. If no base is specified, one will get chosen dynamically.");
        send("  removegroup <groupName> - deletes a group");
        send("  editgroup <name> <onlineAmount (int) | maxAmount (int) | base (String) | ram (int) | static (boolean) | priority (int)> <value> - edits the give setting of a server group");
        send("  editgroup <name> <playersPerProxy (int) | maxPlayers (int) | keepFreeSlots (int) | maxAmount (int) | base (String) | ram (int) | static (boolean) | priority (int)> <value> - edits the give setting of a cord group");
        send("  listgroups - lists all groups and started servers");
        send("  restartgroup <groupName> - restarts all servers in a given group");
        send("  restartserver <serverName> - restarts the given server");
        send("  sendcommand <groupName/serverName> <command> - sends the given command to all server of a given group or the given server");
    }

}
