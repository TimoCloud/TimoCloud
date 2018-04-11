package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.api.implementations.ProxyObjectBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.ServerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.*;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import com.sun.management.OperatingSystemMXBean;
import io.netty.channel.Channel;
import org.json.simple.JSONArray;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            List serverGroupsList = TimoCloudCore.getInstance().getFileManager().loadJson(TimoCloudCore.getInstance().getFileManager().getServerGroupsFile());
            if (serverGroupsList == null) serverGroupsList = new ArrayList();
            for (Object object : serverGroupsList) {
                Map<String, Object> properties = (Map<String, Object>) object;
                String name = (String) properties.get("name");
                ServerGroup serverGroup = getServerGroupByName(name);
                if (serverGroup != null) serverGroup.construct(properties);
                else serverGroup = new ServerGroup(properties);
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
            List proxyGroupsList = TimoCloudCore.getInstance().getFileManager().loadJson(TimoCloudCore.getInstance().getFileManager().getProxyGroupsFile());
            if (proxyGroupsList == null) proxyGroupsList = new ArrayList();
            for (Object object : proxyGroupsList) {
                Map<String, Object> properties = (Map<String, Object>) object;
                String name = (String) properties.get("name");
                ProxyGroup proxyGroup = getProxyGroupByName(name);
                if (proxyGroup != null) proxyGroup.construct(properties);
                else proxyGroup = new ProxyGroup(properties);
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
        JSONArray serverGroups = new JSONArray();
        serverGroups.addAll(getServerGroups().stream().map(ServerGroup::getProperties).collect(Collectors.toList()));
        try {
            TimoCloudCore.getInstance().getFileManager().saveJson(serverGroups, TimoCloudCore.getInstance().getFileManager().getServerGroupsFile());
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while saving server groups: ");
            e.printStackTrace();
        }
    }

    public void saveProxyGroups() {
        JSONArray proxyGroups = new JSONArray();
        proxyGroups.addAll(getProxyGroups().stream().map(ProxyGroup::getProperties).collect(Collectors.toList()));
        try {
            TimoCloudCore.getInstance().getFileManager().saveJson(proxyGroups, TimoCloudCore.getInstance().getFileManager().getProxyGroupsFile());
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

        List<String> maps = getAvailableMaps(group);
        String map = null;
        if (maps.size() > 0) {
            Map<String, Integer> occurrences = new HashMap<>();
            for (String map1 : maps) occurrences.put(map1, 0);
            for (Server server : group.getServers()) {
                if (server.getMap() == null || server.getMap().isEmpty() || !occurrences.containsKey(server.getMap())) continue;
                occurrences.put(server.getMap(), occurrences.get(server.getMap()) + 1);
            }
            int bestScore = -1;
            String best = null;
            for (String map1 : maps) {
                if (bestScore == -1 || occurrences.get(map1) < bestScore) {
                    best = map1;
                    bestScore = occurrences.get(map1);
                }
            }
            map = best;
        }

        Server server = new Server(name, group, base, map, token);
        server.start();
    }

    private List<String> getAvailableMaps(Group group) {
        File templates = TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory();
        List<String> valid = new ArrayList<>();
        for (File sub : templates.listFiles()) {
            if (sub.isDirectory() && sub.getName().startsWith(group.getName() + "_")) valid.add(fileToMapName(sub));
        }
        return valid;
    }

    private static String fileToMapName(File file) {
        if (!file.getName().contains("_")) return file.getName();
        String[] split = file.getName().split("_");
        return Arrays.stream(Arrays.copyOfRange(split, 1, split.length)).collect(Collectors.joining("_"));
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

    public List<Communicatable> getAllCommunicatableInstances() {
        return Stream.concat(
                getProxyGroups().stream().map(ProxyGroup::getProxies).flatMap(List::stream),
                getServerGroups().stream().map(ServerGroup::getServers).flatMap(List::stream)
        ).collect(Collectors.toList());
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

    public Server getServerByServerObject(ServerObject object) {
        return getServerByToken(((ServerObjectBasicImplementation) object).getToken());
    }

    public Proxy getProxyByProxyObject(ProxyObject object) {
        return getProxyByToken(((ProxyObjectBasicImplementation) object).getToken());
    }

    public void checkEnoughOnline() {
        if (TimoCloudCore.getInstance().isShuttingDown()) return;
        List<GroupInstanceDemand> demands = new ArrayList<>();
        List<GroupInstanceDemand> staticDemands = new ArrayList<>();
        for (ProxyGroup group : getProxyGroups()) {
            int amount = proxiesNeeded(group);
            if (amount == 0) continue;
            if (group.isStatic()) {
                if (amount > 0) staticDemands.add(new GroupInstanceDemand(group, 1));
            } else {
                if (amount > 0) {
                    demands.add(new GroupInstanceDemand(group, amount));
                } else { // We have too many proxies
                    int stop = -amount;
                    for (int i = 0; i < stop; i++) {
                        Proxy stoppable = null;
                        for (Proxy proxy : group.getProxies()) {
                            if (proxy.getOnlinePlayerCount() == 0) stoppable = proxy;
                        }
                        if (stoppable == null) break;
                        TimoCloudCore.getInstance().info("Stopping proxy " + stoppable.getName() + " because no players are online and it is not needed anymore.");
                        stoppable.stop();
                    }
                }
            }
        }
        for (ServerGroup group : getServerGroups()) {
            int amount = serversNeeded(group);
            if (amount <= 0) continue;
            if (group.isStatic()) staticDemands.add(new GroupInstanceDemand(group, 1));
            else demands.add(new GroupInstanceDemand(group, amount));
        }

        List<Base> bases = new ArrayList<>();
        for (Base base : getBases()) {
            if (base.isConnected() && base.isReady()) bases.add(base);
        }

        while (!(staticDemands.isEmpty() || bases.isEmpty())) { // Start static servers first
            GroupInstanceDemand demand = getMostImportant(staticDemands);
            staticDemands.remove(demand);
            Base base = null;
            for (Base b : bases) {
                if (b.getName().equals(demand.getGroup().getBaseName())) base = b;
            }
            if (base == null) continue;
            if (base.getAvailableRam() < demand.getGroup().getRam()) continue;
            bases.remove(base);
            start(demand.getGroup(), base);
        }

        while (!(demands.isEmpty() || bases.isEmpty())) { // Start non-static servers
            GroupInstanceDemand demand = getMostImportant(demands);
            demand.changeAmount(-1);
            if (demand.getAmount() <= 0) demands.remove(demand);
            int bestDiff = -1;
            Base bestBase = null;
            for (Base base : bases) {
                if (base.getAvailableRam() < demand.getGroup().getRam()) continue;
                int diff = base.getAvailableRam() - demand.getGroup().getRam();
                if (bestDiff == -1 || diff < bestDiff) {
                    bestDiff = diff;
                    bestBase = base;
                }
            }
            if (bestBase == null) {
                demands.remove(demand);
                continue;
            }
            bases.remove(bestBase);
            start(demand.getGroup(), bestBase);
        }
    }

    private GroupInstanceDemand getMostImportant(Collection<GroupInstanceDemand> demands) {
        int best = -1;
        GroupInstanceDemand bestDemand = null;
        for (GroupInstanceDemand demand : demands) {
            int score = demand.getScore();
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

    private int serversNeeded(ServerGroup group) {
        int running = (int) group.getServers().stream().filter((server) -> isStateActive(server.getState(), group) || server.isStarting()).count();
        int needed = group.getOnlineAmount() - running;
        return Math.max(0, group.getMaxAmount() > 0 ? Math.min(needed, group.getMaxAmount()) : needed);
    }

    private int proxiesNeeded(ProxyGroup group) {
        int running = group.getProxies().size();
        int playersOnline = group.getOnlinePlayerCount();
        int slotsWanted = playersOnline + group.getKeepFreeSlots();
        int slotsLimit = divideRoundUp(group.getMaxPlayerCount(), group.getMaxPlayerCountPerProxy()); // We don't need more slots than maxPlayerCount
        int wanted = Math.min(
                divideRoundUp(slotsWanted, group.getMaxPlayerCountPerProxy()),
                slotsLimit);
        wanted = Math.max(wanted, group.getMinAmount());
        if (group.getMaxAmount() > 0) wanted = Math.min(wanted, group.getMaxAmount());
        if (group.isStatic()) wanted = 1;
        return wanted - running;
    }

    private boolean isStateActive(String state, ServerGroup group) {
        return !(state.equals("OFFLINE") || group.getSortOutStates().contains(state));
    }

    public boolean isBaseConnected(String name) {
        Base base = getBase(name);
        return base != null && base.isConnected();
    }

    public boolean isCordConnected(String name) {
        Cord cord = getCord(name);
        return cord != null && cord.isConnected();
    }

    public Base getOrCreateBase(String name, InetAddress address, InetAddress publicAddress, Channel channel) {
        Base base = bases.getOrDefault(name, null);
        if (base == null) {
            base = new Base(name, address, publicAddress, channel);
            bases.put(name, base);
        } else {
            base.setChannel(channel);
            base.setAddress(address);
        }
        return base;
    }

    public Base getBase(String name) {
        return (Base) searchInMap(name, bases);
    }

    public Cord getOrCreateCord(String name, InetAddress address, Channel channel) {
        Cord cord = cords.getOrDefault(name, null);
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
        return (Cord) searchInMap(name, cords);
    }

    private static Object searchInMap(String key, Map<String, ?> map) {
        if (map.containsKey(key)) return map.get(key);
        for (String k : map.keySet()) {
            if (k.equalsIgnoreCase(key)) return map.get(k);
        }
        return null;
    }

    public Collection<Base> getBases() {
        return bases.values();
    }

    private static int divideRoundUp(int num, int divisor) {
        return (num + divisor - 1) / divisor;
    }
}
