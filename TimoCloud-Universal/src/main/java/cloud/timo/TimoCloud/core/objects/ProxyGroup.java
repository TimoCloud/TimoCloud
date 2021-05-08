package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.events.proxyGroup.*;
import cloud.timo.TimoCloud.api.internal.links.ProxyGroupObjectLink;
import cloud.timo.TimoCloud.api.objects.ProxyChooseStrategy;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.properties.ProxyGroupProperties;
import cloud.timo.TimoCloud.common.events.EventTransmitter;
import cloud.timo.TimoCloud.common.utils.EnumUtil;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.ProxyGroupObjectCoreImplementation;

import java.util.*;
import java.util.stream.Collectors;

public class ProxyGroup implements Group {

    private String id;
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
    private Base base;
    private Set<String> hostNames;
    private ProxyChooseStrategy proxyChooseStrategy;
    private Map<String, Proxy> proxies = new HashMap<>();
    private List<String> javaParameters;
    private String jrePath;

    public ProxyGroup(ProxyGroupProperties properties) {
        construct(properties);
    }

    public ProxyGroup(String id, String name, int maxPlayerCountPerProxy, int maxPlayerCount, int keepFreeSlots, int minAmount, int maxAmount, int ram, String motd, boolean isStatic, int priority, Collection<String> serverGroups, String baseName, String proxyChooseStrategy, Collection<String> hostNames, List<String> javaParameters, String jdkPath) {
        construct(id, name, maxPlayerCountPerProxy, maxPlayerCount, keepFreeSlots, minAmount, maxAmount, ram, motd, isStatic, priority, serverGroups, baseName, proxyChooseStrategy, hostNames, javaParameters, jdkPath);
    }

    public ProxyGroup(Map<String, Object> properties) {
        construct(properties);
    }

    public void construct(Map<String, Object> properties) {
        try {
            String name = (String) properties.get("name");
            ProxyGroupProperties defaultProperties = new ProxyGroupProperties(name);
            construct(
                    (String) properties.getOrDefault("id", defaultProperties.getId()),
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
                    (String) properties.getOrDefault("base", defaultProperties.getBaseIdentifier()),
                    (String) properties.getOrDefault("proxy-choose-strategy", defaultProperties.getProxyChooseStrategy().name()),
                    (Collection<String>) properties.getOrDefault("hostNames", defaultProperties.getHostNames()),
                    (List<String>) properties.getOrDefault("javaParameters", defaultProperties.getJavaParameters()),
                    ((String) properties.getOrDefault("jrePath", defaultProperties.getJrePath())));
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading server group '" + properties.get("name") + "':");
            e.printStackTrace();
        }
    }

    public void construct(ProxyGroupProperties properties) {
        construct(properties.getId(), properties.getName(), properties.getMaxPlayerCountPerProxy(), properties.getMaxPlayerCount(), properties.getKeepFreeSlots(), properties.getMinAmount(), properties.getMaxAmount(), properties.getRam(), properties.getMotd(), properties.isStatic(), properties.getPriority(), properties.getServerGroups(), properties.getBaseIdentifier(), properties.getProxyChooseStrategy().name(), properties.getHostNames(), properties.getJavaParameters(), properties.getJrePath());
    }

    public void construct(String id, String name, int playersPerProxy, int maxPlayers, int keepFreeSlots, int minAmount, int maxAmount, int ram, String motd, boolean isStatic, int priority, Collection<String> serverGroups, String baseIdentifier, String proxyChooseStrategy, Collection<String> hostNames, List<String> javaParameters, String jdkPath) {
        this.id = id;
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
        this.javaParameters = javaParameters;
        this.jrePath = jdkPath;
        setServerGroups(serverGroups);

        if (baseIdentifier != null)
            this.base = TimoCloudCore.getInstance().getInstanceManager().getBaseByIdentifier(baseIdentifier);
        if (isStatic() && getBase() == null) {
            TimoCloudCore.getInstance().severe("Static proxy group " + getName() + " has no base specified. Please specify a base name in order to get the group started.");
        }
        this.proxyChooseStrategy = EnumUtil.valueOf(ProxyChooseStrategy.class, proxyChooseStrategy);
        if (this.proxyChooseStrategy == null) this.proxyChooseStrategy = ProxyChooseStrategy.BALANCE;

        this.hostNames = hostNames.stream().map(String::trim).collect(Collectors.toSet());

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
        properties.put("javaParameters", getJavaParameters());
        properties.put("jrePath", getJrePath());
        if (getBase() != null) properties.put("base", getBase().getId());
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
    public String getId() {
        return id;
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
        int oldValue = maxPlayerCountPerProxy;
        this.maxPlayerCountPerProxy = maxPlayerCountPerProxy;
        EventTransmitter.sendEvent(new ProxyGroupMaxPlayerCountPerProxyChangeEventBasicImplementation(toGroupObject(), oldValue, maxPlayerCountPerProxy));
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public void setMaxPlayerCount(int maxPlayerCount) {
        int oldValue = getMaxPlayerCount();
        this.maxPlayerCount = maxPlayerCount;
        EventTransmitter.sendEvent(new ProxyGroupMaxPlayerCountChangeEventBasicImplementation(toGroupObject(), oldValue, maxPlayerCount));
    }

    public int getKeepFreeSlots() {
        return keepFreeSlots;
    }

    public void setKeepFreeSlots(int keepFreeSlots) {
        int oldValue = getKeepFreeSlots();
        this.keepFreeSlots = keepFreeSlots;
        EventTransmitter.sendEvent(new ProxyGroupKeepFreeSlotsChangeEventBasicImplementation(toGroupObject(), oldValue, keepFreeSlots));
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        int oldValue = getMinAmount();
        this.minAmount = minAmount;
        EventTransmitter.sendEvent(new ProxyGroupMinAmountChangeEventBasicImplementation(toGroupObject(), oldValue, minAmount));
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        int oldValue = getMaxAmount();
        this.maxAmount = maxAmount;
        EventTransmitter.sendEvent(new ProxyGroupMaxAmountChangeEventBasicImplementation(toGroupObject(), oldValue, maxAmount));
    }

    public List<String> getJavaParameters() {
        return javaParameters;
    }

    public void setJavaParameters(List<String> javaParameters) {
        List<String> oldValue = getJavaParameters();
        this.javaParameters = javaParameters;
        EventTransmitter.sendEvent(new ProxyGroupJavaParametersChangeEventBasicImplementation(toGroupObject(), oldValue, javaParameters));
    }

    public void setJrePath(String jrePath) {
        this.jrePath = jrePath;
    }

    public String getJrePath() {
        return jrePath;
    }

    @Override
    public int getRam() {
        if (ram < 128) {
            ram *= 1024;
        }
        return ram;
    }

    public void setRam(int ram) {
        int oldValue = getRam();
        this.ram = ram;
        EventTransmitter.sendEvent(new ProxyGroupRamChangeEventBasicImplementation(toGroupObject(), oldValue, ram));
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        String oldValue = getMotd();
        this.motd = motd;
        EventTransmitter.sendEvent(new ProxyGroupMotdChangeEventBasicImplementation(toGroupObject(), oldValue, motd));
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        boolean oldValue = isStatic();
        isStatic = aStatic;
        EventTransmitter.sendEvent(new ProxyGroupStaticChangeEventBasicImplementation(toGroupObject(), oldValue, isStatic));

    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        int oldValue = getPriority();
        this.priority = priority;
        EventTransmitter.sendEvent(new ProxyGroupPriorityChangeEventBasicImplementation(toGroupObject(), oldValue, priority));
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
        //TODO Work for Timo?
    }

    @Override
    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        Base oldValue = getBase();
        this.base = base;
        EventTransmitter.sendEvent(new ProxyGroupBaseChangeEventBasicImplementation(toGroupObject(), oldValue.toBaseObject(), base.toBaseObject()));
    }

    public ProxyChooseStrategy getProxyChooseStrategy() {
        return proxyChooseStrategy;
    }

    public void setProxyChooseStrategy(ProxyChooseStrategy proxyChooseStrategy) {
        ProxyChooseStrategy oldValue = getProxyChooseStrategy();
        this.proxyChooseStrategy = proxyChooseStrategy;
        EventTransmitter.sendEvent(new ProxyGroupProxyChooseStrategyChangeEventBasicImplementation(toGroupObject(), oldValue, proxyChooseStrategy));
    }

    public Set<String> getHostNames() {
        return hostNames == null ? Collections.emptySet() : hostNames;
    }

    public void setHostNames(Set<String> hostNames) {
        this.hostNames = hostNames;
        //TODO Work for Timo?
    }

    public Collection<Proxy> getProxies() {
        return new HashSet<>(proxies.values());
    }

    public Proxy getProxyById(String id) {
        return proxies.get(id);
    }

    public ProxyGroupObject toGroupObject() {
        return new ProxyGroupObjectCoreImplementation(
                getId(),
                getName(),
                getProxies().stream().map(Proxy::toLink).collect(Collectors.toSet()),
                getMaxPlayerCount(),
                getMaxPlayerCountPerProxy(),
                getKeepFreeSlots(),
                getMinAmount(),
                getMaxAmount(),
                getRam(),
                getMotd(),
                isStatic(),
                getPriority(),
                getServerGroups().stream().map(ServerGroup::toLink).collect(Collectors.toSet()),
                getBase() == null ? null : getBase().toLink(),
                getProxyChooseStrategy().name(),
                Collections.unmodifiableSet(getHostNames()),
                getJavaParameters(),
                getJrePath()
        );
    }

    public ProxyGroupObjectLink toLink() {
        return new ProxyGroupObjectLink(getId(), getName());
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
