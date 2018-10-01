package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.*;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import io.netty.channel.Channel;

import java.io.File;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoreInstanceManager {

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

    /**
     * Loads all server/proxy groups from config files
     */
    public void loadGroups() {
        loadServerGroups();
        loadProxyGroups();
    }

    /**
     * Loads server group configurations from config file
     */
    public void loadServerGroups() {
        Map<String, ServerGroup> serverGroups = new HashMap<>();
        try {
            JsonArray serverGroupsList = TimoCloudCore.getInstance().getFileManager().loadJsonArray(TimoCloudCore.getInstance().getFileManager().getServerGroupsFile());
            for (JsonElement jsonElement : serverGroupsList) {
                Map<String, Object> properties = new Gson().fromJson(jsonElement, new TypeToken<Map<String, Object>>(){}.getType());
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

    /**
     * Loads proxy group configurations from config file
     */
    public void loadProxyGroups() {
        try {
            Map<String, ProxyGroup> proxyGroups = new HashMap<>();
            JsonArray proxyGroupsList = TimoCloudCore.getInstance().getFileManager().loadJsonArray(TimoCloudCore.getInstance().getFileManager().getProxyGroupsFile());
            for (JsonElement jsonElement : proxyGroupsList) {
                Map<String, Object> properties = new Gson().fromJson(jsonElement, new TypeToken<Map<String, Object>>(){}.getType());
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

    /**
     * Saves both, server- & proxy configurations to config files
     */
    public void saveGroups() {
        saveServerGroups();
        saveProxyGroups();
    }

    /**
     * Saves server group configurations to config file
     */
    public void saveServerGroups() {
        JsonArray serverGroups = new JsonArray();
        getServerGroups().stream().map(ServerGroup::getProperties).map(map -> new Gson().toJsonTree(map)).forEach(serverGroups::add);
        try {
            TimoCloudCore.getInstance().getFileManager().saveJson(new Gson().toJsonTree(serverGroups), TimoCloudCore.getInstance().getFileManager().getServerGroupsFile());
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while saving server groups: ");
            e.printStackTrace();
        }
    }

    /**
     * Saves proxy group configurations to config file
     */
    public void saveProxyGroups() {
        JsonArray proxyGroups = new JsonArray();
        getProxyGroups().stream().map(ProxyGroup::getProperties).map(map -> new Gson().toJsonTree(map)).forEach(proxyGroups::add);
        try {
            TimoCloudCore.getInstance().getFileManager().saveJson(new Gson().toJsonTree(proxyGroups), TimoCloudCore.getInstance().getFileManager().getProxyGroupsFile());
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while saving proxy groups: ");
            e.printStackTrace();
        }
    }

    /**
     * Tasks which should be performed every second
     */
    public void everySecond() {
        processInstanceDemands();
    }

    /**
     * Searches for a server OR proxy group by name (case-insensitive)
     *
     * @param name Case-insensitive
     * @return Group object
     */
    public Group getGroupByName(String name) {
        Group group = null;
        group = getProxyGroupByName(name);
        if (group != null) return group;
        return getServerGroupByName(name);
    }

    /**
     * Gets a server group by name (case-sensitive)
     *
     * @param name Case-sensitive
     * @return Server group object
     */
    public ServerGroup getServerGroupByExactName(String name) {
        return serverGroups.get(name);
    }

    /**
     * Gets a server group by name (case-insensitive)
     *
     * @param name Case-insensitive
     * @return Server group object
     */
    public ServerGroup getServerGroupByName(String name) {
        if (getServerGroupByExactName(name) != null) return getServerGroupByExactName(name);
        for (ServerGroup serverGroup : serverGroups.values())
            if (serverGroup.getName().equalsIgnoreCase(name)) return serverGroup;
        return null;
    }

    /**
     * Gets a proxy group by name (case-sensitive)
     *
     * @param name Case-sensitive
     * @return Proxy group object
     */
    public ProxyGroup getProxyGroupByExactName(String name) {
        return proxyGroups.get(name);
    }

    /**
     * Gets a proxy group by name (case-insensitive)
     *
     * @param name Case-insensitive
     * @return Proxy group object
     */
    public ProxyGroup getProxyGroupByName(String name) {
        if (getProxyGroupByExactName(name) != null) return getProxyGroupByExactName(name);
        for (ProxyGroup proxyGroup : proxyGroups.values())
            if (proxyGroup.getName().equalsIgnoreCase(name)) return proxyGroup;
        return null;
    }

    /**
     * Registers a server group
     *
     * @param group The server group which shall be registered
     */
    public void addGroup(ServerGroup group) {
        serverGroups.put(group.getName(), group);
    }

    /**
     * Registers a proxy group
     *
     * @param group The proxy group which shall be registered
     */
    public void addGroup(ProxyGroup group) {
        proxyGroups.put(group.getName(), group);
    }

    /**
     * Deletes a server group
     *
     * @param group The server group which shall be deleted
     */
    public void removeServerGroup(ServerGroup group) {
        getServerGroups().remove(group);
        group.stopAllServers();
        saveServerGroups();
    }

    /**
     * Deletes a proxy group
     *
     * @param group The proxy group which shall be deleted
     */
    public void removeProxyGroup(ProxyGroup group) {
        getProxyGroups().remove(group);
        group.stopAllProxies();
        saveProxyGroups();
    }

    /**
     * Looks for a free base and starts an instance if a free base is found
     * @param group The group of which an instance shall be started
     */
    public void startInstance(Group group) {
        Base base = getFreeBase(group);
        if (base == null) return;
        startInstance(group, base);
    }

    /**
     * Starts a server OR proxy, depending on the group type
     *
     * @param group The group of which an instance shall be started
     * @param base  The base the server/proxy shall be started on
     */
    public void startInstance(Group group, Base base) {
        if (group instanceof ServerGroup) {
            startServer((ServerGroup) group, base);
        } else if (group instanceof ProxyGroup) {
            startProxy((ProxyGroup) group, base);
        }
    }

    /**
     * Starts a new server instance of a server group
     *
     * @param group The group of which an instance shall be started
     * @param base  The base an the server shall be started on
     *
     * @return The started server
     */
    public Server startServer(ServerGroup group, Base base) {
        String name = getNotExistingName(group);
        String token = UUID.randomUUID().toString();
        String id = name + "_" + token;

        List<String> maps = getAvailableMaps(group);
        String map = null;
        if (maps.size() > 0) {
            Map<String, Integer> occurrences = new HashMap<>();
            for (String map1 : maps) occurrences.put(map1, 0);
            for (Server server : group.getServers()) {
                if (server.getMap() == null || server.getMap().isEmpty() || !occurrences.containsKey(server.getMap()))
                    continue;
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

        Server server = new Server(name, id, base, map, group);
        server.start();
        return server;
    }

    /**
     * @param group The group the maps shall be searched in
     * @return A list of available map templates
     */
    private List<String> getAvailableMaps(Group group) {
        File templates = TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory();
        List<String> valid = new ArrayList<>();
        for (File sub : templates.listFiles()) {
            if (sub.isDirectory() && sub.getName().startsWith(group.getName() + "_")) valid.add(fileToMapName(sub));
        }
        return valid;
    }

    /**
     * Helper method to convert a map template directory to a map name
     *
     * @param file The map template directory
     * @return The map's name
     */
    private static String fileToMapName(File file) {
        if (!file.getName().contains("_")) return file.getName();
        String[] split = file.getName().split("_");
        return Arrays.stream(Arrays.copyOfRange(split, 1, split.length)).collect(Collectors.joining("_"));
    }

    /**
     * Starts a new proxy instance of a proxy group
     *
     * @param group The group of which an instance shall be started
     * @param base  The base an the proxy shall be started on
     *
     * @return The started proxy
     */
    public Proxy startProxy(ProxyGroup group, Base base) {
        String name = getNotExistingName(group);
        String token = UUID.randomUUID().toString();
        String id = name + "_" + token;

        Proxy proxy = new Proxy(name, id, base, group);
        proxy.start();
        return proxy;
    }

    /**
     * @return A collection of all server groups
     */
    public Collection<ServerGroup> getServerGroups() {
        return serverGroups.values();
    }

    /**
     * @return A collection of all proxy groups
     */
    public Collection<ProxyGroup> getProxyGroups() {
        return proxyGroups.values();
    }

    /**
     * @return A list of all server- and proxy groups
     */
    public List<Group> getGroups() {
        return Stream.concat(
                getServerGroups().stream(),
                getProxyGroups().stream()
        ).collect(Collectors.toList());
    }

    /**
     * @return A List of all servers, proxies, cords andbases
     */
    public List<Communicatable> getAllCommunicatableInstances() {
        return Stream.concat(
                Stream.concat(
                        getProxyGroups().stream().map(ProxyGroup::getProxies).flatMap(Collection::stream),
                        getServerGroups().stream().map(ServerGroup::getServers).flatMap(Collection::stream)),
                Stream.concat(
                        getCords().stream(),
                        getBases().stream()
                )).collect(Collectors.toList());
    }

    /**
     * Searches for a server by name (case-insensitive)
     *
     * @deprecated Use {@link CoreInstanceManager#getServerById(String)} instead
     * @param name The server's name (case-insensitive)
     * @return A server object
     */
    public Server getServerByName(String name) {
        for (ServerGroup group : getServerGroups())
            for (Server server : group.getServers())
                if (server != null && server.getName().equals(name))
                    return server;
        return null;
    }

    /**
     * Searches for a server by id
     *
     * @param id The server's id
     * @return A server object
     */
    public Server getServerById(String id) {
        for (ServerGroup group : getServerGroups()) {
            Server server = group.getServerById(id);
            if (server == null) continue;
            return server;
        }
        return null;
    }

    /**
     * Searches for a proxy by name (case-insensitive)
     *
     * @deprecated Use {@link CoreInstanceManager#getProxyById(String)} instead
     * @param name The proxy's name (case-insensitive)
     * @return A proxy object
     */
    public Proxy getProxyByName(String name) {
        for (ProxyGroup group : getProxyGroups())
            for (Proxy proxy : group.getProxies())
                if (proxy != null && proxy.getName().equals(name))
                    return proxy;
        return null;
    }

    /**
     * Searches for a proxy by id
     *
     * @param id The proxy's id
     * @return A proxy object
     */
    public Proxy getProxyById(String id) {
        for (ProxyGroup group : getProxyGroups()) {
            Proxy proxy = group.getProxyById(id);
            if (proxy == null) continue;
            return proxy;
        }
        return null;
    }

    /**
     * Converts an API object into an internal server object
     *
     * @param object The API object which shall be converted
     * @return An internal server object
     */
    public Server getServerByServerObject(ServerObject object) {
        return getServerById(object.getId());
    }

    /**
     * Converts an API object into an internal proxy object
     *
     * @param object The API object which shall be converted
     * @return An internal proxy object
     */
    public Proxy getProxyByProxyObject(ProxyObject object) {
        return getProxyById(object.getId());
    }

    /**
     * @param group The group a free base shall be searched for
     * @return A base object if a free base is found, otherwise null
     */
    public Base getFreeBase(Group group) {
        if (group.isStatic() && group.getBaseName() == null)
            return null; // A static group has to have a base specified statically
        return getBases().stream()
                .filter(Base::isConnected)
                .filter(Base::isReady)
                .filter(base -> group.getBaseName() == null || group.getBaseName().equals(base.getName()))
                .filter(base -> base.getAvailableRam() >= group.getRam())
                .min(Comparator.comparingInt(Base::getAvailableRam)).orElse(null);
    }

    /**
     * This is TimoCloud's core method.
     * It stops unneeded servers/proxies, checks how many instances of server- & proxy groups are needed, looks for free bases and starts the instances
     * First, demands of static groups will be processed, then the demands of dynamic groups follow
     */
    public void processInstanceDemands() {
        if (TimoCloudCore.getInstance().isShuttingDown()) return;

        stopUnneededServers();
        stopUnneededProxies();

        Queue<GroupInstanceDemand> demands = new PriorityQueue<>();
        Queue<GroupInstanceDemand> staticDemands = new PriorityQueue<>();

        for (Group group : getGroups()) {
            int amount = needed(group);
            if (amount <= 0) continue;
            if (group.isStatic()) staticDemands.add(new GroupInstanceDemand(group, 1));
            else demands.add(new GroupInstanceDemand(group, amount));
        }

        while (! staticDemands.isEmpty()) { // Start static instances first
            GroupInstanceDemand demand = staticDemands.poll();
            startInstance(demand.getGroup());
        }

        while (! demands.isEmpty()) { // Start non-static instances
            GroupInstanceDemand demand = demands.poll();
            startInstance(demand.getGroup());
            demand.changeAmount(-1);
            if (demand.getAmount() > 0) demands.add(demand);
        }
    }

    /**
     * If there are empty, unneeded servers, they will be stopped
     */
    private void stopUnneededServers() {
        for (ServerGroup group : getServerGroups()) {
            int amount = serversNeeded(group);
            int stopAmount = -amount;
            for (int i = 0; i < stopAmount; i++) {
                for (Server server : group.getServers()) {
                    if (server.getOnlinePlayerCount() == 0 && isStateActive(server.getState(), server.getGroup())) {
                        TimoCloudCore.getInstance().info("Stopping server " + server.getName() + " because no players are online and it is no longer needed.");
                        server.stop();
                        break;
                    }
                }
            }
        }
    }

    /**
     * If there are empty, unneeded proxies, they will be stopped
     */
    private void stopUnneededProxies() {
        for (ProxyGroup group : getProxyGroups()) {
            int amount = proxiesNeeded(group);
            int stopAmount = -amount;
            for (int i = 0; i < stopAmount; i++) {
                for (Proxy proxy : group.getProxies()) {
                    if (proxy.getOnlinePlayerCount() == 0) {
                        TimoCloudCore.getInstance().info("Stopping proxy " + proxy.getName() + " because no players are online and it is no longer needed.");
                        proxy.stop();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Generates a name for a new server instance
     * @param group The group a name shall be created for
     * @return A not yet existing server name
     */
    private String getNotExistingName(ServerGroup group) {
        for (int i = 1; i <= MAX_SERVERS; i++) {
            String name = generateName(group, i);
            if (serverNameExists(name)) continue;
            return name;
        }
        TimoCloudCore.getInstance().severe("Fatal error: No fitting name for server group " + group.getName() + " found. Please report this!");
        return null;
    }

    /**
     * @param name A server name
     * @return Whether a server with this name already exists
     */
    private boolean serverNameExists(String name) {
        return getServerByName(name) != null;
    }

    /**
     * Generates a server name
     * @param group The server group a name shall be generated for
     * @param n The server's id (starting from 1)
     * @return The name for a server of the given group with the given id
     */
    private String generateName(ServerGroup group, int n) {
        return group.isStatic() ? group.getName() : group.getName() + "-" + n;
    }

    /**
     * Generates a name for a new proxy instance
     * @param group The group a name shall be created for
     * @return A not yet existing proxy name
     */
    private String getNotExistingName(ProxyGroup group) {
        for (int i = 1; i <= MAX_PROXIES; i++) {
            String name = generateName(group, i);
            if (proxyNameExists(name)) continue;
            return name;
        }
        TimoCloudCore.getInstance().severe("Fatal error: No fitting name for proxy group " + group.getName() + " found. Please report this!");
        return null;
    }

    /**
     * @param name A proxy name
     * @return Whether a proxy with this name already exists
     */
    private boolean proxyNameExists(String name) {
        return getProxyByName(name) != null;
    }

    /**
     * Generates a proxy name
     * @param group The proxy group a name shall be generated for
     * @param n The proxy's id (starting from 1)
     * @return The name for a proxy of the given group with the given id
     */
    private String generateName(ProxyGroup group, int n) {
        return group.isStatic() ? group.getName() : group.getName() + "-" + n;
    }

    /**
     * @param group A server/proxy group
     * @return How many additional instances of the given group are needed. Negative if more servers/proxies are online than needed
     */
    private int needed(Group group) {
        if (group instanceof ServerGroup) return serversNeeded((ServerGroup) group);
        if (group instanceof ProxyGroup) return proxiesNeeded((ProxyGroup) group);
        return 0; // This should not happen
    }

    /**
     * @param group A server group
     * @return How many additional instances of the given group are needed. Negative if more servers are online than needed
     */
    private int serversNeeded(ServerGroup group) {
        int running = (int) group.getServers().stream().filter((server) -> isStateActive(server.getState(), group) || server.isStarting()).count();
        int needed = group.getOnlineAmount() - running;
        return group.getMaxAmount() > 0 ? Math.min(needed, group.getMaxAmount() - group.getServers().size()) : needed;
    }

    /**
     * @param group A server/proxy group
     * @return How many additional instances of the given group are needed. Negative if more proxies are online than needed
     */
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

    /**
     * @param state A server state (e.g. ONLINE)
     * @param group A server group
     * @return Whether a server with the given state is considered as active (free) or as used
     */
    private boolean isStateActive(String state, ServerGroup group) {
        return !(state.equals("OFFLINE") || group.getSortOutStates().contains(state));
    }

    /**
     * @param name The base's name
     * @return Whether a base with this name exists and is connected
     */
    public boolean isBaseConnected(String name) {
        Base base = getBase(name);
        return base != null && base.isConnected();
    }

    /**
     * @param name The cord's name
     * @return Whether a cord with this name exists and is connected
     */
    public boolean isCordConnected(String name) {
        Cord cord = getCord(name);
        return cord != null && cord.isConnected();
    }

    /**
     * This method is called when a base connects
     * If a base with the given name already exists (from a previous connection), the properties will just be modified, otherwise a new base object will be created
     * @param name The base's name
     * @param address The IP address the base connected from
     * @param publicAddress The base's public IP address players can connect to
     * @param channel The base's netty socket channel
     * @return A base object with the given properties
     */
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

    /**
     * @param name The base's name
     * @return A base object if a base with the given name exists, otherwise null
     */
    public Base getBase(String name) {
        return (Base) searchInMap(name, bases);
    }

    /**
     * This method is called when a cord connects
     * If a cord with the given name already exists (from a previous connection), the properties will just be modified, otherwise a new cord object will be created
     * @param name The cord's name
     * @param address The IP address the cord connected from
     * @param channel The cord's netty socket channel
     * @return A cord object with the given properties
     */
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

    /**
     * @param name The cord's name
     * @return A cord object if a cord with the given name exists, otherwise null
     */
    public Cord getCord(String name) {
        return (Cord) searchInMap(name, cords);
    }

    /**
     * @return A collection of all connected cords
     */
    public Collection<Cord> getCords() {
        return cords.values();
    }

    /**
     * This method searches case-insensitively for a key in a map
     * @param key The key which shall be searched for (case-insensitive)
     * @param map The map in which shall be searched
     * @return The key's value if existing, otherwise null
     */
    private static Object searchInMap(String key, Map<String, ?> map) {
        if (map.containsKey(key)) return map.get(key);
        for (String k : map.keySet()) {
            if (k.equalsIgnoreCase(key)) return map.get(k);
        }
        return null;
    }

    /**
     * @return A collection of all connected bases
     */
    public Collection<Base> getBases() {
        return bases.values();
    }

    /**
     * A helper method to divide and rounding up
     * @param num The number which shall be divided
     * @param divisor The divisor
     * @return The quotient, rounded up
     */
    private static int divideRoundUp(int num, int divisor) {
        return (num + divisor - 1) / divisor;
    }
}
