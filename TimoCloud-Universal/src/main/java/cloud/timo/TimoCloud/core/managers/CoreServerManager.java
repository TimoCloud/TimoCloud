package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.*;
import com.sun.management.OperatingSystemMXBean;
import io.netty.channel.Channel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.*;

public class CoreServerManager {

    private Map<String, ServerGroup> serverGroups;
    private Map<String, ProxyGroup> proxyGroups;
    private Map<String, Base> bases;
    private Map<String, Cord> cords;

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
        cords = new HashMap<>();
    }

    public void loadGroups() {
        loadServerGroups();
        loadProxyGroups();
    }

    public void loadServerGroups() {
        Map<String, ServerGroup> serverGroups = new HashMap<>();
        try {
            JSONArray serverGroupsJson = TimoCloudCore.getInstance().getFileManager().loadJson(TimoCloudCore.getInstance().getFileManager().getServerGroupsFile());
            for (Object object : serverGroupsJson) {
                JSONObject jsonObject = (JSONObject) object;
                String name = (String) jsonObject.get("name");
                ServerGroup serverGroup = getServerGroupByName(name);
                if (serverGroup != null) serverGroup.construct(jsonObject);
                else serverGroup = new ServerGroup(jsonObject);
                serverGroups.put(serverGroup.getName(), serverGroup);
            }
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading server groups: ");
            e.printStackTrace();
        }
        this.serverGroups = serverGroups;
    }

    public void loadProxyGroups() {
        try {
            Map<String, ProxyGroup> proxyGroups = new HashMap<>();
            JSONArray proxyGroupsJson = TimoCloudCore.getInstance().getFileManager().loadJson(TimoCloudCore.getInstance().getFileManager().getProxyGroupsFile());
            for (Object object : proxyGroupsJson) {
                JSONObject jsonObject = (JSONObject) object;
                String name = (String) jsonObject.get("name");
                ProxyGroup proxyGroup = getProxyGroupByName(name);
                if (proxyGroup != null) proxyGroup.construct(jsonObject);
                else proxyGroup = new ProxyGroup(jsonObject);
                proxyGroups.put(proxyGroup.getName(), proxyGroup);
            }
            this.proxyGroups = proxyGroups;
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

    public void everySecond() {
        checkEnoughOnline();
    }

    public Group getGroupByName(String name) {
        Group group = null;
        group = getProxyGroupByName(name);
        if (group != null) return group;
        return getServerGroupByName(name);
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

    public void addGroup(ServerGroup group) {
        serverGroups.put(group.getName(), group);
    }

    public void addGroup(ProxyGroup group) {
        proxyGroups.put(group.getName(), group);
    }

    public void removeServerGroup(ServerGroup group) {
        if (getServerGroups().contains(group)) getServerGroups().remove(group);
        group.stopAllServers();
        saveServerGroups();
    }

    public void removeProxyGroup(ProxyGroup group) {
        if (getProxyGroups().contains(group)) getProxyGroups().remove(group);
        group.stopAllProxies();
        saveProxyGroups();
    }

    public void start(Group group, Base base) {
        if (group instanceof ServerGroup) {
            startServer((ServerGroup) group, base);
        } else if (group instanceof ProxyGroup) {
            startProxy((ProxyGroup) group, base);
        }
    }

    public void startServer(ServerGroup group, Base base) {
        String name = getNotExistingName(group);
        String token = UUID.randomUUID().toString();

        List<File> maps = getAvailableMaps(group);
        String map = null;
        if (maps.size() > 0) {
            Map<String, Integer> occurrences = new HashMap<>();
            for (Server server : group.getServers()) {
                if (server.getMap() == null || server.getMap().isEmpty()) continue;
                if (occurrences.containsKey(server.getMap())) occurrences.put(server.getMap(), occurrences.get(server.getMap()) + 1);
                else occurrences.put(server.getMap(), 1);
            }
            int bestScore = -1;
            String best = null;
            for (String key : occurrences.keySet()) {
                if (bestScore == -1 || occurrences.get(key) < bestScore) {
                    best = key;
                    bestScore = occurrences.get(key);
                }
            }
            map = best;
        }

        Server server = new Server(name, group, base, map, token);
        server.start();
    }

    private List<File> getAvailableMaps(Group group) {
        File templates = TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory();
        List<File> valid = new ArrayList<>();
        for (File sub : templates.listFiles()) {
            if (sub.isDirectory() && sub.getName().startsWith(group.getName() + "_")) valid.add(sub);
        }
        return valid;
    }

    public void startProxy(ProxyGroup group, Base base) {
        String name = getNotExistingName(group);
        String token = UUID.randomUUID().toString();
        Proxy proxy = new Proxy(name, group, base, token);
        proxy.start();
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
        List<GroupInstanceDemand> staticDemands = new ArrayList<>();
        for (ProxyGroup group : getProxyGroups()) {
            int amount = proxiesNeeded(group);
            if (amount <= 0) continue;
            if (group.isStatic()) staticDemands.add(new GroupInstanceDemand(group, amount));
            else demands.add(new GroupInstanceDemand(group, amount));
        }
        for (ServerGroup group : getServerGroups()) {
            int amount = serversNeeded(group);
            if (amount <= 0) continue;
            if (group.isStatic()) staticDemands.add(new GroupInstanceDemand(group, amount));
            else demands.add(new GroupInstanceDemand(group, amount));
        }

        List<Base> bases = new ArrayList<>();
        for (Base base : getBases()) {
            if (base.isConnected() && base.isReady()) bases.add(base);
        }

        while (! (staticDemands.isEmpty() || bases.isEmpty())) {
            GroupInstanceDemand demand = getMostImportant(staticDemands);
            Base base = null;
            for (Base b : bases) {
                if (b.getName().equals(demand.getGroup().getBaseName())) base = b;
            }
            if (base == null) continue;
            if (base.getAvailableRam()<demand.getGroup().getRam()) continue;
            demands.remove(demand);
            bases.remove(base);
            demand.changeAmount(-1);
            start(demand.getGroup(), base);
        }

        while (! (demands.isEmpty() || bases.isEmpty())) {
            GroupInstanceDemand demand = getMostImportant(demands);
            int bestDiff = -1;
            Base bestBase = null;
            for (Base base : bases) {
                if (base.getAvailableRam() < demand.getGroup().getRam()) continue;
                    int diff = base.getAvailableRam()-demand.getGroup().getRam();
                    if (bestDiff == -1 || diff < bestDiff) {
                        bestDiff = diff;
                        bestBase = base;
                }
            }
            if (bestBase == null) {
                demands.remove(demand);
                continue;
            }
            demand.changeAmount(-1);
            if (demand.getAmount() <=0) demands.remove(demand);
            bases.remove(bestBase);
            start(demand.getGroup(), bestBase);
        }
    }

    public long getFreeMemory() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize() / (1024*1024); // Convert to megabytes
    }

    private GroupInstanceDemand getMostImportant(Collection<GroupInstanceDemand> demands) {
        int best = -1;
        GroupInstanceDemand bestDemand = null;
        for (GroupInstanceDemand demand : demands) {
            int score = demand.getAmount()*demand.getGroup().getPriority();
            if (score > best) {
                best = score;
                bestDemand = demand;
            }
        }
        return bestDemand;
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
        int playersOnline = group.getOnlinePlayerCount();
        int slotsWanted = playersOnline + group.getKeepFreeSlots();
        int wanted = divideRoundUp(slotsWanted, group.getMaxPlayerCountPerProxy());
        int needed = Math.max(wanted - running, 0);
        return Math.max(0,
                Math.min(
                        divideRoundUp(group.getMaxPlayerCount(), group.getMaxPlayerCountPerProxy()),
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

    public Cord getCord(String name, InetAddress address, Channel channel) {
        Cord cord = cords.get(name);
        if (cord == null) {
            cord = new Cord(name, address, channel);
            cords.put(name, cord);
        } else {
            cord.setChannel(channel);
            cord.setAddress(address);
        }
        return cord;
    }

    public Cord getCord(String name) {
        return cords.get(name);
    }

    public Collection<Base> getBases() {
        return bases.values();
    }

    private static int divideRoundUp(int num, int divisor) {
        return (num + divisor - 1) / divisor;
    }
}
