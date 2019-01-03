package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.objects.ProxyChooseStrategy;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.properties.ProxyGroupProperties;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.ProxyGroupObjectCoreImplementation;
import cloud.timo.TimoCloud.lib.utils.EnumUtil;

import java.util.*;
import java.util.stream.Collectors;

public class ProxyGroup implements Group {

    private String name;
    private int maxPlayerCountPerProxy;
    private int maxPlayerCount;
    private int keepFreeSlots;
    private int minAmount;
    private int maxAmount;
    private int ram;
    private String motd;
    private boolean isStatic;
    private int priority;
    private Collection<String> serverGroups;
    private boolean allServerGroups;
    private String baseName;
    private Set<String> hostNames;
    private ProxyChooseStrategy proxyChooseStrategy;
    private Map<String, Proxy> proxies = new HashMap<>();

    public ProxyGroup(ProxyGroupProperties properties) {
        construct(properties);
    }

    public ProxyGroup(String name, int maxPlayerCountPerProxy, int maxPlayerCount, int keepFreeSlots, int minAmount, int maxAmount, int ram, String motd, boolean isStatic, int priority, Collection<String> serverGroups, String baseName, String proxyChooseStrategy, Collection<String> hostNames) {
        construct(name, maxPlayerCountPerProxy, maxPlayerCount, keepFreeSlots, minAmount, maxAmount, ram, motd, isStatic, priority, serverGroups, baseName, proxyChooseStrategy, hostNames);
    }

    public ProxyGroup(Map<String, Object> properties) {
        construct(properties);
    }

    public void construct(Map<String, Object> properties) {
        try {
            String name = (String) properties.get("name");
            ProxyGroupProperties defaultProperties = new ProxyGroupProperties(name);
            construct(
                    name,
                    ((Number) properties.getOrDefault("players-per-proxy", defaultProperties.getMaxPlayerCountPerProxy())).intValue(),
                    ((Number) properties.getOrDefault("max-players", defaultProperties.getMaxPlayerCount())).intValue(),
                    ((Number) properties.getOrDefault("keep-free-slots", defaultProperties.getKeepFreeSlots())).intValue(),
                    ((Number) properties.getOrDefault("min-amount", defaultProperties.getMinAmount())).intValue(),
                    ((Number) properties.getOrDefault("max-amount", defaultProperties.getMaxAmount())).intValue(),
                    ((Number) properties.getOrDefault("ram", defaultProperties.getRam())).intValue(),
                    (String) properties.getOrDefault("motd", defaultProperties.getMotd()),
                    (Boolean) properties.getOrDefault("static", defaultProperties.isStatic()),
                    ((Number) properties.getOrDefault("priority", defaultProperties.getPriority())).intValue(),
                    (Collection<String>) properties.getOrDefault("serverGroups", defaultProperties.getServerGroups()),
                    (String) properties.getOrDefault("base", defaultProperties.getBaseName()),
                    (String) properties.getOrDefault("proxy-choose-strategy", defaultProperties.getProxyChooseStrategy().name()),
                    (Collection<String>) properties.getOrDefault("hostNames", defaultProperties.getHostNames()));
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading server group '" + properties.get("name") + "':");
            e.printStackTrace();
        }
    }

    public void construct(ProxyGroupProperties properties) {
        construct(properties.getName(), properties.getMaxPlayerCountPerProxy(), properties.getMaxPlayerCount(), properties.getKeepFreeSlots(), properties.getMinAmount(), properties.getMaxAmount(), properties.getRam(), properties.getMotd(), properties.isStatic(), properties.getPriority(), properties.getServerGroups(), properties.getBaseName(), properties.getProxyChooseStrategy().name(), properties.getHostNames());
    }

    public void construct(String name, int playersPerProxy, int maxPlayers, int keepFreeSlots, int minAmount, int maxAmount, int ram, String motd, boolean isStatic, int priority, Collection<String> serverGroups, String baseName, String proxyChooseStrategy, Collection<String> hostnames) {
        this.name = name;
        this.maxPlayerCountPerProxy = playersPerProxy;
        this.maxPlayerCount = maxPlayers;
        this.keepFreeSlots = keepFreeSlots;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.ram = ram;
        this.motd = motd;
        this.isStatic = isStatic;
        this.priority = priority;

        setServerGroups(serverGroups);

        this.baseName = baseName;
        if (isStatic() && getBaseName() == null) {
            TimoCloudCore.getInstance().severe("Static proxy group " + getName() + " has no base specified. Please specify a base name in order to get the group started.");
        }
        this.proxyChooseStrategy = EnumUtil.valueOf(ProxyChooseStrategy.class, proxyChooseStrategy);
        if (this.proxyChooseStrategy == null) this.proxyChooseStrategy = ProxyChooseStrategy.BALANCE;

        this.hostNames = hostnames.stream().map(String::trim).collect(Collectors.toSet());

        reload();
    }

    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("name", getName());
        properties.put("players-per-proxy", getMaxPlayerCountPerProxy());
        properties.put("max-players", getMaxPlayerCount());
        properties.put("min-amount", getMinAmount());
        properties.put("max-amount", getMaxAmount());
        properties.put("keep-free-slots", getKeepFreeSlots());
        properties.put("ram", getRam());
        properties.put("motd", getMotd());
        properties.put("static", isStatic());
        properties.put("priority", getPriority());
        properties.put("serverGroups", allServerGroups ? Collections.singletonList("*") : getServerGroups());
        properties.put("hostNames", getHostNames());
        if (getBaseName() != null) properties.put("base", getBaseName());
        return properties;
    }

    public void addProxy(Proxy proxy) {
        if (proxy == null) {
            TimoCloudCore.getInstance().severe("Fatal error: Tried to add proxy which is null. Please report this.");
            return;
        }
        proxies.put(proxy.getId(), proxy);
        TimoCloudCore.getInstance().getInstanceManager().addProxy(proxy);
    }

    public void removeProxy(Proxy proxy) {
        proxies.remove(proxy.getId());
        TimoCloudCore.getInstance().getInstanceManager().removeProxy(proxy);
    }

    public void onProxyConnect(Proxy proxy) {

    }

    public void registerServer(Server server) {
        for (Proxy proxy : getProxies()) proxy.registerServer(server);
    }

    public void unregisterServer(Server server) {
        for (Proxy proxy : getProxies()) proxy.unregisterServer(server);
    }

    public void stopAllProxies() {
        for (Proxy proxy : getProxies()) {
            proxy.stop();
            removeProxy(proxy);
        }
    }

    public Set<Server> getRegisteredServers() {
        return getServerGroups().stream().map(ServerGroup::getServers).flatMap(Collection::stream).filter(Server::isRegistered).collect(Collectors.toSet());
    }

    public void reload() {
        for (Proxy proxy : getProxies()) {
            List<Server> removeServers = new ArrayList<>(proxy.getRegisteredServers());
            for (Server server : removeServers) proxy.unregisterServer(server);
            for (Server server : getRegisteredServers()) proxy.registerServer(server);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public GroupType getType() {
        return GroupType.PROXY;
    }

    public int getOnlinePlayerCount() {
        return getProxies().stream().mapToInt(Proxy::getOnlinePlayerCount).sum();
    }

    public int getMaxPlayerCountPerProxy() {
        return maxPlayerCountPerProxy;
    }

    public void setMaxPlayerCountPerProxy(int maxPlayerCountPerProxy) {
        this.maxPlayerCountPerProxy = maxPlayerCountPerProxy;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public void setMaxPlayerCount(int maxPlayerCount) {
        this.maxPlayerCount = maxPlayerCount;
    }

    public int getKeepFreeSlots() {
        return keepFreeSlots;
    }

    public void setKeepFreeSlots(int keepFreeSlots) {
        this.keepFreeSlots = keepFreeSlots;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Override
    public int getRam() {
        if (ram < 128) {
            ram *= 1024;
        }
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @return Returns the names of the server-groups enabled for this proxy group, "*" if all server groups are enabled
     */
    public Collection<String> getServerGroupNames() {
        return serverGroups;
    }

    /**
     * @return Returns the server-groups enabled for this proxy group
     */
    public Collection<ServerGroup> getServerGroups() {
        if (allServerGroups) return TimoCloudCore.getInstance().getInstanceManager().getServerGroups();
        return getServerGroupNames()
                .stream()
                .map(name -> TimoCloudCore.getInstance().getInstanceManager().getServerGroupByName(name))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void setServerGroups(Collection<String> serverGroups) {
        this.serverGroups = new HashSet<>();
        this.allServerGroups = false;
        for (String groupName : serverGroups) {
            groupName = groupName.trim();
            if (groupName.equals("*")) {
                this.allServerGroups = true;
                continue;
            }
            this.serverGroups.add(groupName);
        }
    }

    @Override
    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public ProxyChooseStrategy getProxyChooseStrategy() {
        return proxyChooseStrategy;
    }

    public void setProxyChooseStrategy(ProxyChooseStrategy proxyChooseStrategy) {
        this.proxyChooseStrategy = proxyChooseStrategy;
    }

    public Set<String> getHostNames() {
        return hostNames;
    }

    public void setHostNames(Set<String> hostNames) {
        this.hostNames = hostNames;
    }

    public Collection<Proxy> getProxies() {
        return new HashSet<>(proxies.values());
    }

    public Proxy getProxyById(String id) {
        return proxies.get(id);
    }

    public ProxyGroupObject toGroupObject() {
        return new ProxyGroupObjectCoreImplementation(
                getName(),
                getProxies().stream().map(Proxy::toProxyObject).collect(Collectors.toSet()),
                getOnlinePlayerCount(),
                getMaxPlayerCount(),
                getMaxPlayerCountPerProxy(),
                getKeepFreeSlots(),
                getMinAmount(),
                getMaxAmount(),
                getRam(),
                getMotd(),
                isStatic(),
                getPriority(),
                getServerGroups().stream().map(ServerGroup::getName).collect(Collectors.toSet()),
                getBaseName(),
                getProxyChooseStrategy().name(),
                Collections.unmodifiableSet(getHostNames())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProxyGroup that = (ProxyGroup) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }
}
