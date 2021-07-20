package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.api.events.proxyGroup.ProxyGroupCreatedEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupCreatedEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupDeletedEventBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.api.objects.properties.BaseProperties;
import cloud.timo.TimoCloud.common.events.EventTransmitter;
import cloud.timo.TimoCloud.common.utils.RandomIdGenerator;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.*;
import cloud.timo.TimoCloud.core.objects.storage.IdentifiableStorage;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import io.netty.channel.Channel;

import java.io.File;
import java.net.InetAddress;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoreInstanceManager {

    private IdentifiableStorage<ServerGroup> serverGroups;
    private IdentifiableStorage<ProxyGroup> proxyGroups;
    private IdentifiableStorage<Server> servers;
    private IdentifiableStorage<Proxy> proxies;
    private IdentifiableStorage<Base> bases;
    private IdentifiableStorage<Cord> cords;
    // TODO Store players here, not in proxy objects

    private static final int MAX_SERVERS = 2500;
    private static final int MAX_PROXIES = 500;
    private static final int MAX_BASES = 500;

    public void init() {
        makeInstances();
        loadEverything();
    }

    private void makeInstances() {
        serverGroups = new IdentifiableStorage<>();
        proxyGroups = new IdentifiableStorage<>();
        servers = new IdentifiableStorage<>();
        proxies = new IdentifiableStorage<>();
        bases = new IdentifiableStorage<>();
        cords = new IdentifiableStorage<>();
    }

    /**
     * Loads all server/proxy groups and bases from config files
     */
    public void loadEverything() {
        loadBases();
        loadServerGroups();
        loadProxyGroups();
    }

    /**
     * Loads server group configurations from config file
     */
    public void loadServerGroups() {
        try {
            IdentifiableStorage<ServerGroup> serverGroups = new IdentifiableStorage<>();
            JsonArray serverGroupsList = TimoCloudCore.getInstance().getFileManager().loadJsonArray(TimoCloudCore.getInstance().getFileManager().getServerGroupsFile());
            for (JsonElement jsonElement : serverGroupsList) {
                Map<String, Object> properties = new Gson().fromJson(jsonElement, new TypeToken<Map<String, Object>>() {
                }.getType());
                String name = (String) properties.get("name");
                ServerGroup serverGroup = getServerGroupByName(name);
                if (serverGroup != null) {
                    properties.put("id", serverGroup.getId());
                    serverGroup.construct(properties);
                } else serverGroup = new ServerGroup(properties);
                serverGroups.add(serverGroup);
            }
            this.serverGroups = serverGroups;
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading server groups: ");
            e.printStackTrace();
        }
    }

    /**
     * Loads proxy group configurations from config file
     */
    public void loadProxyGroups() {
        try {
            IdentifiableStorage<ProxyGroup> proxyGroups = new IdentifiableStorage<>();
            JsonArray proxyGroupsList = TimoCloudCore.getInstance().getFileManager().loadJsonArray(TimoCloudCore.getInstance().getFileManager().getProxyGroupsFile());
            for (JsonElement jsonElement : proxyGroupsList) {
                Map<String, Object> properties = new Gson().fromJson(jsonElement, new TypeToken<Map<String, Object>>() {
                }.getType());
                String name = (String) properties.get("name");
                ProxyGroup proxyGroup = getProxyGroupByName(name);
                if (proxyGroup != null) {
                    properties.put("id", proxyGroup.getId());
                    proxyGroup.construct(properties);
                } else proxyGroup = new ProxyGroup(properties);
                proxyGroups.add(proxyGroup);
            }
            this.proxyGroups = proxyGroups;
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading proxy groups: ");
            e.printStackTrace();
        }
    }

    public void loadBases() {
        try {
            IdentifiableStorage<Base> bases = new IdentifiableStorage<>();
            JsonArray baseList = TimoCloudCore.getInstance().getFileManager().loadJsonArray(TimoCloudCore.getInstance().getFileManager().getBasesFile());
            for (JsonElement jsonElement : baseList) {
                Map<String, Object> properties = new Gson().fromJson(jsonElement, new TypeToken<Map<String, Object>>() {
                }.getType());
                String id = (String) properties.get("id");
                String name = (String) properties.get("name");
                try {
                    Base base = getBaseById(id);
                    if (base != null) base.construct(properties);
                    else base = new Base(properties);
                    bases.add(base);
                } catch (Exception e) {
                    TimoCloudCore.getInstance().severe(String.format("Error while loading base with name %s and id %s: ", name, id));
                }
            }
            this.bases = bases;
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading bases: ");
            e.printStackTrace();
        }
    }

    /**
     * Saves both, server- & proxy configurations to config files
     */
    public void saveEverything() {
        saveBases();
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
     * Saves base configurations to config file
     */
    public void saveBases() {
        JsonArray bases = new JsonArray();
        getBases().stream().map(Base::getProperties).map(map -> new Gson().toJsonTree(map)).forEach(bases::add);
        try {
            TimoCloudCore.getInstance().getFileManager().saveJson(new Gson().toJsonTree(bases), TimoCloudCore.getInstance().getFileManager().getBasesFile());
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while saving basess: ");
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
     * Gets a server group by name (case-insensitive)
     *
     * @param name Case-insensitive
     * @return Server group object
     */
    public ServerGroup getServerGroupByName(String name) {
        return serverGroups.getByName(name);
    }

    /**
     * Gets a server group by id
     *
     * @param id The group's id
     * @return Server group object
     */
    public ServerGroup getServerGroupById(String id) {
        return serverGroups.getById(id);
    }

    /**
     * Gets a server group by name or id
     *
     * @param identifier The group's name or id
     * @return Server group object
     */
    public ServerGroup getServerGroupByIdentifier(String identifier) {
        return serverGroups.getByIdentifier(identifier);
    }

    /**
     * Gets a proxy group by name (case-insensitive)
     *
     * @param name Case-insensitive
     * @return Proxy group object
     */
    public ProxyGroup getProxyGroupByName(String name) {
        return proxyGroups.getByName(name);
    }

    /**
     * Gets a proxy group by id
     *
     * @param id The group's id
     * @return Proxy group object
     */
    public ProxyGroup getProxyGroupById(String id) {
        return proxyGroups.getById(id);
    }

    /**
     * Gets a proxy group by name or id
     *
     * @param identifier The group's name or id
     * @return Proxy group object
     */
    public ProxyGroup getProxyGroupByIdentifier(String identifier) {
        return proxyGroups.getByIdentifier(identifier);
    }

    /**
     * Creates a server group
     *
     * @param group The server group which shall be registered
     */
    public void createGroup(ServerGroup group) {
        serverGroups.add(group);
        saveServerGroups();
        EventTransmitter.sendEvent(new ServerGroupCreatedEventBasicImplementation(group.toGroupObject()));
    }

    /**
     * Creates a proxy group
     *
     * @param group The proxy group which shall be registered
     */
    public void createGroup(ProxyGroup group) {
        proxyGroups.add(group);
        saveProxyGroups();
        EventTransmitter.sendEvent(new ProxyGroupCreatedEventBasicImplementation(group.toGroupObject()));
    }

    /**
     * Deletes a server group
     *
     * @param group The server group which shall be deleted
     */
    public void deleteGroup(ServerGroup group) {
        serverGroups.remove(group);
        group.stopAllServers();
        saveServerGroups();
        EventTransmitter.sendEvent(new ServerGroupDeletedEventBasicImplementation(group.toGroupObject()));
    }

    /**
     * Deletes a proxy group
     *
     * @param group The proxy group which shall be deleted
     */
    public void deleteGroup(ProxyGroup group) {
        proxyGroups.remove(group);
        group.stopAllProxies();
        saveProxyGroups();
        EventTransmitter.sendEvent(new ProxyGroupCreatedEventBasicImplementation(group.toGroupObject()));
    }

    /**
     * Looks for a free base and starts an instance if a free base is found
     *
     * @param group The group of which an instance shall be started
     */
    private void startInstance(Group group) {
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
    private void startInstance(Group group, Base base) {
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
     * @return The started server
     */
    private Server startServer(ServerGroup group, Base base) {
        String name = getNotExistingName(group);
        if (name == null) return null;
        String token = RandomIdGenerator.generateId();
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
            int bestScore = occurrences.entrySet().stream().min(Comparator.comparingInt(Map.Entry::getValue)).get().getValue();
            List<String> best = occurrences.entrySet().stream()
                    .filter(m -> m.getValue() == bestScore)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            map = best.get(new Random().nextInt(best.size()));
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
     * @return The started proxy
     */
    private Proxy startProxy(ProxyGroup group, Base base) {
        String name = getNotExistingName(group);
        String token = RandomIdGenerator.generateId();
        String id = name + "_" + token;

        Proxy proxy = new Proxy(name, id, base, group);
        proxy.start();
        return proxy;
    }

    /**
     * @param group The group a free base shall be searched for
     * @return A base object if a free base is found, otherwise null
     */
    public Base getFreeBase(Group group) {
        if (group.isStatic() && group.getBase() == null)
            return null; // A static group has to have a base specified statically
        return getBases().stream()
                .filter(Base::isConnected)
                .filter(Base::isReady)
                .filter(base -> group.getBase() == null || group.getBase().equals(base))
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

        while (!staticDemands.isEmpty()) { // Start static instances first
            GroupInstanceDemand demand = staticDemands.poll();
            startInstance(demand.getGroup());
        }

        while (!demands.isEmpty()) { // Start non-static instances
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
     *
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
     *
     * @param group The server group a name shall be generated for
     * @param n     The server's id (starting from 1)
     * @return The name for a server of the given group with the given id
     */
    private String generateName(ServerGroup group, int n) {
        return group.isStatic() ? group.getName() : group.getName() + "-" + n;
    }

    /**
     * Generates a name for a new proxy instance
     *
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
     *
     * @param group The proxy group a name shall be generated for
     * @param n     The proxy's id (starting from 1)
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
        return 0; // This should never happen
    }

    /**
     * @param group A server group
     * @return How many additional instances of the given group are needed. Negative if more servers are online than needed
     */
    private int serversNeeded(ServerGroup group) {
        if (group.isStatic() && group.getServers().size() > 0) return 0;
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
        if (group.isStatic()) wanted = 1;
        if (group.getMaxAmount() > 0) wanted = Math.min(wanted, group.getMaxAmount());
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
        Base base = getBaseByName(name);
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
                Stream.concat(getProxies().stream(), getServers().stream()),
                Stream.concat(getCords().stream(), getBases().stream()
                )).collect(Collectors.toList());
    }

    /**
     * Searches for a server by name (case-insensitive)
     *
     * @param name The server's name (case-insensitive)
     * @return A server object
     * @deprecated Use {@link CoreInstanceManager#getServerById(String)} instead
     */
    @Deprecated
    public Server getServerByName(String name) {
        return servers.getByName(name);
    }

    /**
     * Searches for a server by id
     *
     * @param id The server's id
     * @return A server object
     */
    public Server getServerById(String id) {
        return servers.getById(id);
    }

    /**
     * Searches for a server by id first, if not found, by name
     *
     * @param identifier The server's id or name
     * @return A server object
     */
    public Server getServerByIdentifier(String identifier) {
        return servers.getByIdentifier(identifier);
    }

    /**
     * @param publicKey The server's public RSA key
     * @return A server object
     */
    public Server getServerByPublicKey(PublicKey publicKey) {
        return servers.getByPublicKey(publicKey);
    }

    /**
     * @return A collection of all servers
     */
    public Collection<Server> getServers() {
        return servers.values();
    }

    /**
     * Searches for a proxy by name (case-insensitive)
     *
     * @param name The proxy's name (case-insensitive)
     * @return A proxy object
     * @deprecated Use {@link CoreInstanceManager#getProxyById(String)} instead
     */
    @Deprecated
    public Proxy getProxyByName(String name) {
        return proxies.getByName(name);
    }

    /**
     * Searches for a proxy by id
     *
     * @param id The proxy's id
     * @return A proxy object
     */
    public Proxy getProxyById(String id) {
        return proxies.getById(id);
    }

    /**
     * @param publicKey The proxy's public RSA key
     * @return A proxy object
     */
    public Proxy getProxyByPublicKey(PublicKey publicKey) {
        return proxies.getByPublicKey(publicKey);
    }

    /**
     * Searches for a proxy by id first, if not found, by name
     *
     * @param identifier The proxy's id or name
     * @return A proxy object
     */
    public Proxy getProxyByIdentifier(String identifier) {
        return proxies.getByIdentifier(identifier);
    }

    /**
     * @return A collection of all proxies
     */
    public Collection<Proxy> getProxies() {
        return proxies.values();
    }

    /**
     * Converts an API object into an internal server object
     *
     * @param object The API object which shall be converted
     * @return An internal server object
     */
    public Server getServerByServerObject(ServerObject object) {
        if (object == null) return null;
        return getServerById(object.getId());
    }

    /**
     * Converts an API object into an internal proxy object
     *
     * @param object The API object which shall be converted
     * @return An internal proxy object
     */
    public Proxy getProxyByProxyObject(ProxyObject object) {
        if (object == null) return null;
        return getProxyById(object.getId());
    }

    /**
     * Registers the given server in the map for quick access by id
     */
    public void addServer(Server server) {
        servers.add(server);
    }

    /**
     * Removes the server from the storage
     */
    public void removeServer(Server server) {
        servers.remove(server);
    }

    /**
     * Registers the given server in storage
     */
    public void addProxy(Proxy proxy) {
        proxies.add(proxy);
    }

    /**
     * Removes the server from the map for quick access by id
     */
    public void removeProxy(Proxy proxy) {
        proxies.remove(proxy);
    }

    /**
     * @param name The base's name
     * @return A base object if a base with the given name exists, otherwise null
     */
    public Base getBaseByName(String name) {
        return bases.getByName(name);
    }

    /**
     * @param id The base's name
     * @return A base object if a base with the given id exists, otherwise null
     */
    public Base getBaseById(String id) {
        return bases.getById(id);
    }

    /**
     * Searches for a base by id first, if not found, by name
     *
     * @param identifier The base's id or name
     * @return A base object
     */
    public Base getBaseByIdentifier(String identifier) {
        return bases.getByIdentifier(identifier);
    }

    /**
     * @param publicKey The base's public RSA key
     * @return A base object
     */
    public Base getBaseByPublicKey(PublicKey publicKey) {
        return bases.getByPublicKey(publicKey);
    }

    public Base createBase(PublicKey publicKey) {
        Base base = new Base(new BaseProperties(RandomIdGenerator.generateId(), getNotExistingBaseName(), publicKey));
        bases.add(base);
        saveBases();
        return base;
    }

    private String getNotExistingBaseName() {
        for (int i = 1; i <= MAX_BASES; i++) {
            String name = generateBaseName(i);
            if (baseNameExists(name)) continue;
            return name;
        }
        TimoCloudCore.getInstance().severe("Fatal error: No fitting name for new base found. Please report this!");
        return null;
    }

    private String generateBaseName(int id) {
        return "BASE-" + id;
    }

    private boolean baseNameExists(String name) {
        return getBaseByName(name) != null;
    }

    /**
     * Called when key properties changed
     */
    public void serverDataUpdated(Server server) {
        servers.update(server);
    }

    /**
     * Called when key properties changed
     */
    public void proxyDataUpdated(Proxy proxy) {
        proxies.update(proxy);
    }

    /**
     * Called when key properties changed
     */
    public void baseDataUpdated(Base base) {
        bases.update(base);
    }

    /**
     * This method is called when a cord connects
     * If a cord with the given name already exists (from a previous connection), the properties will just be modified, otherwise a new cord object will be created
     *
     * @param name    The cord's name
     * @param address The IP address the cord connected from
     * @param channel The cord's netty socket channel
     * @return A cord object with the given properties
     */
    public Cord getOrCreateCord(String name, InetAddress address, Channel channel) {
        Cord cord = cords.getByIdentifier(name);
        if (cord == null) {
            cord = new Cord(name, address, channel);
            cords.add(cord);
        } else {
            cord.setChannel(channel);
            cord.setAddress(address);
        }
        return cord;
    }

    /**
     * @param identifier The cord's name or id
     * @return A cord object if a cord with the given name or id exists, otherwise null
     */
    public Cord getCord(String identifier) {
        return cords.getByIdentifier(identifier);
    }

    /**
     * @return A collection of all connected cords
     */
    public Collection<Cord> getCords() {
        return cords.values();
    }

    /**
     * This method searches case-insensitively for a key in a map
     *
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
     * @return A collection of all bases
     */
    public Collection<Base> getBases() {
        return bases.values();
    }

    /**
     * A helper method to divide and rounding up
     *
     * @param num     The number which shall be divided
     * @param divisor The divisor
     * @return The quotient, rounded up
     */
    private static int divideRoundUp(int num, int divisor) {
        return (num + divisor - 1) / divisor;
    }
}
