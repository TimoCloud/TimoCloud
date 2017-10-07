package at.TimoCraft.TimoCloud.bungeecord.managers;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.objects.BaseObject;
import at.TimoCraft.TimoCloud.bungeecord.objects.Group;
import at.TimoCraft.TimoCloud.bungeecord.objects.Server;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BungeeServerManager {

    private List<Integer> portsInUse;
    private List<Group> groups;
    private List<String> startedServers;
    private Map<Group, List<String>> willBeStarted;
    private Map<String, BaseObject> bases;

    public static final int MIN_PORT = 40000;
    public static final int MAX_PORT = 50000;
    public static final long SERVER_START_DELAY = 1;
    public static final long STATIC_SERVER_START_DELAY = 5;


    public void init() {
        makeInstances();
        loadGroups();
    }

    private void makeInstances() {
        portsInUse = new ArrayList<>();
        groups = new ArrayList<>();
        startedServers = new ArrayList<>();
        willBeStarted = new HashMap<>();
        bases = new HashMap<>();
        startedServers = new ArrayList<>();
    }

    public void stopAllServers() {
        for (Group group : groups) group.stopAllServers();
    }

    public Group getGroupByExactName(String name) {
        for (Group group : groups) if (group.getName().equals(name)) return group;
        return null;
    }

    public Group getGroupByName(String name) {
        for (Group group : groups) if (group.getName().equals(name)) return group;
        for (Group group : groups) if (group.getName().equalsIgnoreCase(name)) return group;
        return null;
    }

    public void loadGroups() {
        Configuration groupsConfig = TimoCloud.getInstance().getFileManager().getGroups();
        for (String groupName : groupsConfig.getKeys()) {
            Group group = getGroupByExactName(groupName);
            if (group == null) {
                group = new Group();
                groups.add(group);
            }
            group.construct(
                    groupName,
                    groupsConfig.getInt(groupName + ".onlineAmount"),
                    groupsConfig.getInt(groupName + ".maxAmount"),
                    groupsConfig.getInt(groupName + ".ramInMegabyte") == 0 ? groupsConfig.getInt(groupName + ".ramInGigabyte") * 1024 : groupsConfig.getInt(groupName + ".ramInMegabyte"),
                    groupsConfig.getBoolean(groupName + ".static"),
                    groupsConfig.getString(groupName + ".base"),
                    groupsConfig.getStringList(groupName + ".sortOut")
            );
        }
    }

    public int getFreePort() {
        for (int i = MIN_PORT; i <= MAX_PORT; i++) {
            if (isPortFree(i)) {
                i = registerPort(i);
                return i;
            }
        }
        return 25565; // This should never happen
    }

    public boolean isPortFree(int port) {
        if (portsInUse.contains(port)) return false;
        try (ServerSocket socket = new ServerSocket(port)) {
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int registerPort(int port) {
        if (isPortFree(port)) {
            portsInUse.add(port);
            return port;
        }
        return getFreePort();
    }

    public void unregisterPort(int port) {
        portsInUse.remove(Integer.valueOf(port));
    }

    public ServerInfo getRandomLobbyServer(ServerInfo notThis) {
        Group group = getGroupByName(TimoCloud.getInstance().getFileManager().getConfig().getString("fallbackGroup"));
        if (group.getServers().size() == 0)
            return TimoCloud.getInstance().getProxy().getServerInfo(TimoCloud.getInstance().getFileManager().getConfig().getString("emergencyFallback"));
        List<Server> servers;
        if (notThis == null) {
            servers = group.getServers();
        } else {
            servers = new ArrayList<>();
            for (Server server : group.getServers()) {
                if (!server.getServerInfo().equals(notThis)) {
                    servers.add(server);
                }
            }
            if (servers.size() == 0) {
                TimoCloud.severe("No running fallback server found. Maybe you should start more lobby servers. This could happen because a player has been kicked from a lobby and there was no OTHER free lobby server.");
                return notThis;
            }
        }

        return servers.get(new Random().nextInt(servers.size())).getServerInfo();
    }

    public void startServer(Group group, String name) {
        getServersWillBeStarted(group).add(name);
        int port = getFreePort();
        String token = UUID.randomUUID().toString();
        Server server = new Server(name, group, port, token);
        group.addStartingServer(server);
        TimoCloud.getInstance().getProxy().getScheduler().schedule(TimoCloud.getInstance(), () -> {
            TimoCloud.getInstance().getProxy().getScheduler().runAsync(TimoCloud.getInstance(), () -> startServerFromAsyncContext(name, group.getName(), group.getRam(), port, group.isStatic(), token, group.getBase()));
            getServersWillBeStarted(group).remove(name);
            server.onStart();
        }, group.isStatic() ? STATIC_SERVER_START_DELAY : SERVER_START_DELAY, 0, TimeUnit.SECONDS);
    }

    public void startServerFromAsyncContext(String name, String group, int ram, int port, boolean isStatic, String token, BaseObject base) {
        JSONObject json = new JSONObject();
        json.put("type", "STARTSERVER");
        json.put("server", name);
        json.put("group", group);
        json.put("ram", ram);
        json.put("port", port);
        json.put("static", isStatic);
        json.put("token", token);
        try {
            base.getChannel().writeAndFlush(json.toJSONString());
            TimoCloud.info("Told base " + base.getName() + " to start server " + name + ".");
        } catch (Exception e) {
            TimoCloud.severe("Error while starting server " + name + ": Base " + base.getName() + " not connected.");
            return;
        }
    }

    public boolean isRunning(String name) {
        return startedServers.contains(name);
    }

    public void addServer(String name) {
        if (!isRunning(name)) {
            startedServers.add(name);
            return;
        }
        TimoCloud.severe("Tried to add already running server: " + name);
    }

    public void removeServer(String name) {
        try {
            if (TimoCloud.getInstance().getProxy().getServers().containsKey(name)) {
                TimoCloud.getInstance().getProxy().getServers().remove(name);
            }
            if (startedServers.contains(name)) {
                startedServers.remove(name);
                return;
            }
        } catch (Exception e) {
        }
        TimoCloud.severe("Tried to remove not started server: " + name);
    }

    public boolean groupExists(Group group) {
        return groups.contains(group);
    }

    public void saveGroup(Group group) throws IOException {
        TimoCloud.getInstance().getFileManager().getGroups().set(group.getName() + ".onlineAmount", group.getStartupAmount());
        TimoCloud.getInstance().getFileManager().getGroups().set(group.getName() + ".maxAmount", group.getMaxAmount());
        TimoCloud.getInstance().getFileManager().getGroups().set(group.getName() + (group.getRam() < 128 ? ".ramInGigabyte" : ".ramInMegabyte"), group.getRam());
        TimoCloud.getInstance().getFileManager().getGroups().set(group.getName() + ".static", group.isStatic());
        TimoCloud.getInstance().getFileManager().getGroups().set(group.getName() + ".base", group.getBaseName());
        TimoCloud.getInstance().getFileManager().getGroups().set(group.getName() + ".sortOut", group.getSortOutStates());
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(TimoCloud.getInstance().getFileManager().getGroups(), TimoCloud.getInstance().getFileManager().getGroupsFile());
        if (!groups.contains(group)) groups.add(group);
    }

    public void removeGroup(Group group) throws IOException {
        group.stopAllServers();
        groups.remove(group);
        TimoCloud.getInstance().getFileManager().getGroups().set(group.getName(), null);
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(TimoCloud.getInstance().getFileManager().getGroups(), TimoCloud.getInstance().getFileManager().getGroupsFile());
    }

    public List<Group> getGroups() {
        return groups;
    }

    public Server getServerByName(String name) {
        for (Group group : getGroups())
            for (Server server : group.getServers())
                if (server != null && server.getName().equals(name))
                    return server;
        return null;
    }

    public Server getServerByToken(String token) {
        for (Group group : getGroups())
            for (Server server : group.getServers())
                if (server != null && server.getToken().equals(token))
                    return server;
        return null;
    }

    public void checkEnoughOnline() {
        for (Group group : getGroups()) checkEnoughOnline(group);
    }

    public void checkEnoughOnline(Group group) {
        if (TimoCloud.getInstance().isShuttingDown()) return;
        if (group.getBase() == null || !group.getBase().isConnected()) return;
        int needed = serversNeeded(group);
        for (int i = 0; i < needed; i++) startServer(group, getNotExistingName(group));
    }

    private String getNotExistingName(Group group) {
        for (int i = 1; i < 100000; i++) {
            String name = generateName(group, i);
            if (!nameExists(name, group)) {
                return name;
            }
        }
        TimoCloud.severe("Fatal error: No fitting name for group " + group.getName() + " found. Please report this!");
        return group.getName();
    }

    private List<String> getServersWillBeStarted(Group group) {
        willBeStarted.putIfAbsent(group, new ArrayList<>());
        return willBeStarted.get(group);
    }

    private boolean nameExists(String name, Group group) {
        return getServersWillBeStarted(group).contains(name) || getServerByName(name) != null;
    }

    private String generateName(Group group, int n) {
        return group.isStatic() ? group.getName() : group.getName() + "-" + n;
    }

    public int serversNeeded(Group group) {
        int running = 0;
        for (Server server : group.getServers()) {
            if (server == null) {
                TimoCloud.severe("Fatal error: Server is null. Please report this.");
            }
            if (isStateActive(server.getState(), group) || server.isStarting()) running++;
        }
        running += getServersWillBeStarted(group).size();
        return Math.max(0,
                Math.min(
                        group.getStartupAmount()-running,
                        group.getMaxAmount()-(group.getServers().size() + getServersWillBeStarted(group).size())
                ));

    }

    private boolean isStateActive(String state, Group group) {
        return !(state.equals("OFFLINE") || group.getSortOutStates().contains(state));
    }

    public void addBase(String name, BaseObject base) {
        bases.put(name, base);
        base.setConnected(true);
        for (Group group : getGroups()) {
            if (group.getBaseName().equalsIgnoreCase(name)) {
                group.setBase(base);
            }
        }
        TimoCloud.info("Base " + name + " connected.");
    }

    public void onBaseDisconnect(BaseObject base) {
        base.setConnected(false);
        bases.remove(base.getName());
        TimoCloud.info("Base " + base.getName() + " disconnected.");
    }

    public BaseObject getBase(String name) {
        return bases.get(name);
    }

}
