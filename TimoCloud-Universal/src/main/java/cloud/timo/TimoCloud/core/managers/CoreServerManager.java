package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.*;
import io.netty.channel.Channel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CoreServerManager {

    private Map<String, ServerGroup> serverGroups;
    private Map<String, ProxyGroup> proxyGroups;
    private Map<String, Base> bases;

    private static final int MAX_SERVERS = 2500;
    private static final int MAX_PROXIES = 500;

    public void init() {
        makeInstances();
        loadGroups();
    }

    private void makeInstances() {
        serverGroups = new HashMap<>();
        proxyGroups = new HashMap<>();
        bases = new HashMap<>();
    }

    public void loadGroups() {
        loadServerGroups();
        loadProxyGroups();
    }

    public void loadServerGroups() {
        try {
            serverGroups = new HashMap<>();
            JSONArray serverGroupsJson = TimoCloudCore.getInstance().getFileManager().loadJson(TimoCloudCore.getInstance().getFileManager().getServerGroupsFile());
            for (Object object : serverGroupsJson) {
                JSONObject jsonObject = (JSONObject) object;
                String name = (String) jsonObject.get("name");
                if (getServerGroupByName(name) != null) {
                    TimoCloudCore.getInstance().severe("Error while loading server group '" + name + "': A group with this name already exists.");
                    continue;
                }
                ServerGroup serverGroup = new ServerGroup(jsonObject);
                serverGroups.put(name, serverGroup);
            }
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading server groups: ");
            e.printStackTrace();
        }
    }

    public void loadProxyGroups() {
        try {
            proxyGroups = new HashMap<>();
            JSONArray proxyGroupsJson = TimoCloudCore.getInstance().getFileManager().loadJson(TimoCloudCore.getInstance().getFileManager().getProxyGroupsFile());
            for (Object object : proxyGroupsJson) {
                JSONObject jsonObject = (JSONObject) object;
                String name = (String) jsonObject.get("name");
                if (getProxyGroupByName(name) != null) {
                    TimoCloudCore.getInstance().severe("Error while loading proxy group '" + name + "': A group with this name already exists.");
                    continue;
                }
                ProxyGroup proxyGroup = new ProxyGroup(jsonObject);
                proxyGroups.put(name, proxyGroup);
            }
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading proxy groups: ");
            e.printStackTrace();
        }
    }

    public void saveGroups() {
        saveServerGroups();
        saveProxyGroups();
    }

    public void saveServerGroups() {
        JSONArray jsonArray = new JSONArray();
        for (ServerGroup serverGroup : getServerGroups()) {
            jsonArray.add(serverGroup.toJsonObject());
        }
        try {
            TimoCloudCore.getInstance().getFileManager().saveJson(jsonArray, TimoCloudCore.getInstance().getFileManager().getServerGroupsFile());
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while saving server groups: ");
            e.printStackTrace();
        }
    }

    public void saveProxyGroups() {
        JSONArray jsonArray = new JSONArray();
        for (ProxyGroup proxyGroup : getProxyGroups()) {
            jsonArray.add(proxyGroup.toJsonObject());
        }
        try {
            TimoCloudCore.getInstance().getFileManager().saveJson(jsonArray, TimoCloudCore.getInstance().getFileManager().getProxyGroupsFile());
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while saving proxy groups: ");
            e.printStackTrace();
        }
    }

    public ServerGroup getServerGroupByExactName(String name) {
        return serverGroups.get(name);
    }

    public ServerGroup getServerGroupByName(String name) {
        if (getServerGroupByExactName(name) != null) return getServerGroupByExactName(name);
        for (ServerGroup serverGroup : serverGroups.values())
            if (serverGroup.getName().equalsIgnoreCase(name)) return serverGroup;
        return null;
    }

    public ProxyGroup getProxyGroupByExactName(String name) {
        return proxyGroups.get(name);
    }

    public ProxyGroup getProxyGroupByName(String name) {
        if (getProxyGroupByExactName(name) != null) return getProxyGroupByExactName(name);
        for (ProxyGroup proxyGroup : proxyGroups.values())
            if (proxyGroup.getName().equalsIgnoreCase(name)) return proxyGroup;
        return null;
    }

    public void startServer(ServerGroup group) {
        String name = getNotExistingName(group);
        String token = UUID.randomUUID().toString();
        Server server = new Server(name, group, token);
        group.addStartingServer(server);
        TimoCloudBungee.getInstance().getProxy().getScheduler().schedule(TimoCloudBungee.getInstance(), () -> {
            TimoCloudBungee.getInstance().getProxy().getScheduler().runAsync(TimoCloudBungee.getInstance(), () -> startServerFromAsyncContext(name, group.getName(), group.getRam(), port, group.isStatic(), token, group.getBase()));
            getServersWillBeStarted(group).remove(name);
            server.onStart();
        }, group.isStatic() ? STATIC_SERVER_START_DELAY : SERVER_START_DELAY, 0, TimeUnit.SECONDS);
    }

    public void startServerFromAsyncContext(String name, String group, int ram, int port, boolean isStatic, String token, Base base) {
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
            TimoCloudCore.getInstance().info("Told base " + base.getName() + " to start server " + name + ".");
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while starting server " + name + ": TimoCloudBase " + base.getName() + " not connected.");
            return;
        }
    }

    public Collection<ServerGroup> getServerGroups() {
        return serverGroups.values();
    }

    public Collection<ProxyGroup> getProxyGroups() {
        return proxyGroups.values();
    }

    public Server getServerByName(String name) {
        for (ServerGroup group : getServerGroups())
            for (Server server : group.getServers())
                if (server != null && server.getName().equals(name))
                    return server;
        return null;
    }

    public Server getServerByToken(String token) {
        for (ServerGroup group : getServerGroups())
            for (Server server : group.getServers())
                if (server != null && server.getToken().equals(token))
                    return server;
        return null;
    }

    public Proxy getProxyByName(String name) {
        for (ProxyGroup group : getProxyGroups())
            for (Proxy proxy : group.getProxies())
                if (proxy != null && proxy.getName().equals(name))
                    return proxy;
        return null;
    }

    public Proxy getProxyByToken(String token) {
        for (ProxyGroup group : getProxyGroups())
            for (Proxy proxy : group.getProxies())
                if (proxy != null && proxy.getToken().equals(token))
                    return proxy;
        return null;
    }

    public void checkEnoughOnline() {
        if (TimoCloudCore.getInstance().isShuttingDown()) return;
        List<GroupInstanceDemand> demands = new ArrayList<>();
        for (ProxyGroup group : getProxyGroups()) {
            int amount = proxiesNeeded(group);
            if (amount <= 0) continue;
            demands.add(new GroupInstanceDemand(group, amount));
        }
        for (ServerGroup group : getServerGroups()) {
            int amount = serversNeeded(group);
            if (amount <= 0) continue;
            demands.add(new GroupInstanceDemand(group, amount));
        }

        demands.sort(Comparator.comparingInt(GroupInstanceDemand::getAmount).reversed());
        List<Base> bases = new ArrayList<>();
        for (Base base : getBases()) {
            if (base.isReady()) bases.add(base);
        }
        for (GroupInstanceDemand demand : de)
    }

    private String getNotExistingName(ServerGroup group) {
        for (int i = 1; i < MAX_SERVERS; i++) {
            String name = generateName(group, i);
            if (!nameExists(name, group)) {
                return name;
            }
        }
        TimoCloudCore.getInstance().severe("Fatal error: No fitting name for server group " + group.getName() + " found. Please report this!");
        return null;
    }

    private boolean nameExists(String name, ServerGroup group) {
        return getServerByName(name) != null;
    }

    private String generateName(ServerGroup group, int n) {
        return group.isStatic() ? group.getName() : group.getName() + "-" + n;
    }

    private String getNotExistingName(ProxyGroup group) {
        for (int i = 1; i < MAX_PROXIES; i++) {
            String name = generateName(group, i);
            if (!nameExists(name, group)) {
                return name;
            }
        }
        TimoCloudCore.getInstance().severe("Fatal error: No fitting name for proxy group " + group.getName() + " found. Please report this!");
        return null;
    }

    private boolean nameExists(String name, ProxyGroup group) {
        return getProxyByName(name) != null;
    }

    private String generateName(ProxyGroup group, int n) {
        return group.isStatic() ? group.getName() : group.getName() + "-" + n;
    }

    public int serversNeeded(ServerGroup group) {
        int running = (int) group.getServers().stream().filter((server) -> isStateActive(server.getState(), group) || server.isStarting()).count();
        int needed = group.getOnlineAmount() - running;
        return Math.max(0, group.getMaxAmount() > 0 ? Math.min(needed, group.getMaxAmount()) : needed);
    }

    public int proxiesNeeded(ProxyGroup group) {
        int running = group.getProxies().size();
        int playersOnline = group.getProxies().stream().mapToInt((proxy) -> proxy.getOnlinePlayerCount()).sum();
        int slotsWanted = playersOnline + group.getKeepFreeSlots();
        int wanted = divideRoundUp(slotsWanted, group.getPlayersPerProxy());
        int needed = Math.max(wanted - running, 0);
        return Math.max(0,
                Math.min(
                        divideRoundUp(group.getMaxPlayers(), group.getPlayersPerProxy()),
                        group.getMaxAmount() > 0 ? Math.min(
                                needed,
                                group.getMaxAmount()) : needed));
    }

    private boolean isStateActive(String state, ServerGroup group) {
        return !(state.equals("OFFLINE") || group.getSortOutStates().contains(state));
    }

    public Base getBase(String name, InetAddress address, Channel channel) {
        Base base = bases.get(name);
        if (base == null) {
            base = new Base(name, address, channel);
            bases.put(name, base);
        } else {
            base.setChannel(channel);
            base.setAddress(address);
        }
        return base;
    }

    public Base getBase(String name) {
        return bases.get(name);
    }

    public Collection<Base> getBases() {
        return bases.values();
    }

    private static int divideRoundUp(int num, int divisor) {
        return (num + divisor - 1) / divisor;
    }
}
