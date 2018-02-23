package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.objects.ProxyChooseStrategy;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
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
    private Collection<ServerGroup> serverGroups;
    private boolean allServerGroups;
    private String baseName;
    private List<String> hostNames;
    private ProxyChooseStrategy proxyChooseStrategy;
    private List<Proxy> proxies = new ArrayList<>();
    private List<Server> registeredServers = new ArrayList<>();

    public ProxyGroup(String name, int maxPlayerCountPerProxy, int maxPlayerCount, int keepFreeSlots, int minAmount, int maxAmount, int ram, String motd, boolean isStatic, int priority, List<String> serverGroups, String baseName, String proxyChooseStrategy, List<String> hostNames) {
        construct(name, maxPlayerCountPerProxy, maxPlayerCount, keepFreeSlots, minAmount, maxAmount, ram, motd, isStatic, priority, serverGroups, baseName, proxyChooseStrategy, hostNames);
    }

    public ProxyGroup(Map<String, Object> properties) {
        construct(properties);
    }

    public void construct(Map<String, Object> properties) {
        try {
            construct(
                    (String) properties.get("name"),
                    ((Number) properties.getOrDefault("players-per-proxy", 500)).intValue(),
                    ((Number) properties.getOrDefault("max-players", 100)).intValue(),
                    ((Number) properties.getOrDefault("keep-free-slots", 100)).intValue(),
                    ((Number) properties.getOrDefault("min-amount", 0)).intValue(),
                    ((Number) properties.getOrDefault("max-amount", 3)).intValue(),
                    ((Number) properties.getOrDefault("ram", 1)).intValue(),
                    (String) properties.getOrDefault("motd", "&6This is a &bTimo&7Cloud &6Proxy\n&aChange this MOTD in your config or per command"),
                    (Boolean) properties.getOrDefault("static", false),
                    ((Number) properties.getOrDefault("priority", 1)).intValue(),
                    (List<String>) properties.getOrDefault("serverGroups", Collections.singletonList("*")),
                    (String) properties.getOrDefault("base", null),
                    (String) properties.getOrDefault("proxy-choose-strategy", "BALANCE"),
                    (List<String>) properties.getOrDefault("hostNames", new ArrayList<String>()));
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while loading server group '" + properties.get("name") + "':");
            e.printStackTrace();
        }
    }

    public void construct(String name, int playersPerProxy, int maxPlayers, int keepFreeSlots, int minAmount, int maxAmount, int ram, String motd, boolean isStatic, int priority, List<String> serverGroups, String baseName, String proxyChooseStrategy, List<String> hostnames) {
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
        this.serverGroups = new ArrayList<>();
        this.allServerGroups = false;

        for (String groupName : serverGroups) {
            groupName = groupName.trim();
            if (groupName.equals("*")) {
                this.allServerGroups = true;
                this.serverGroups = new ArrayList<>();
                break;
            }
            ServerGroup serverGroup = TimoCloudCore.getInstance().getServerManager().getServerGroupByName(groupName);
            if (serverGroup != null) this.serverGroups.add(serverGroup);
        }

        this.baseName = baseName;
        if (isStatic() && getBaseName() == null) {
            TimoCloudCore.getInstance().severe("Static proxy group " + getName() + " has no base specified. Please specify a base name in order to get the group started.");
        }
        this.proxyChooseStrategy = EnumUtil.valueOf(ProxyChooseStrategy.class, proxyChooseStrategy);
        if (this.proxyChooseStrategy == null) this.proxyChooseStrategy = ProxyChooseStrategy.BALANCE;

        this.hostNames = hostnames.stream().map(String::trim).collect(Collectors.toList());
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
        properties.put("serverGroups", getServerGroups());
        properties.put("hostNames", getHostNames());
        if (getBaseName() != null) properties.put("base", getBaseName());
        return properties;
    }

    public void addStartingProxy(Proxy proxy) {
        if (proxy == null)
            TimoCloudCore.getInstance().severe("Fatal error: Tried to add proxy which is null. Please report this.");
        if (proxies.contains(proxy)) return;
        proxies.add(proxy);
    }

    public void removeProxy(Proxy proxy) {
        if (getProxies().contains(proxy)) getProxies().remove(proxy);
    }

    public void onProxyConnect(Proxy proxy) {

    }

    public void registerServer(Server server) {
        registeredServers.add(server);
        for (Proxy proxy : getProxies()) proxy.registerServer(server);
    }

    public void unregisterServer(Server server) {
        if (registeredServers.contains(server)) registeredServers.remove(server);
        for (Proxy proxy : getProxies()) proxy.unregisterServer(server);
    }

    public void stopAllProxies() {
        List<Proxy> proxies = (ArrayList<Proxy>) ((ArrayList<Proxy>) getProxies()).clone();
        for (Proxy proxy : proxies) proxy.stop();
        this.proxies.removeAll(proxies);
    }

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

    public Collection<ServerGroup> getServerGroups() {
        if (allServerGroups) return TimoCloudCore.getInstance().getServerManager().getServerGroups();
        return serverGroups;
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

    public List<String> getHostNames() {
        return hostNames;
    }

    public List<Proxy> getProxies() {
        return proxies;
    }

    public List<Server> getRegisteredServers() {
        return registeredServers;
    }

    public ProxyGroupObject toGroupObject() {
        ProxyGroupObjectCoreImplementation groupObject = new ProxyGroupObjectCoreImplementation(
                getName(),
                getProxies().stream().map(Proxy::toProxyObject).collect(Collectors.toList()),
                getOnlinePlayerCount(),
                getMaxPlayerCount(),
                getMaxPlayerCountPerProxy(),
                getKeepFreeSlots(),
                getRam(),
                getMotd(),
                isStatic(),
                getPriority(),
                getServerGroups().stream().map(ServerGroup::getName).collect(Collectors.toList()),
                getBaseName(),
                getProxyChooseStrategy().name(),
                getHostNames()
        );
        groupObject.getProxies().sort(Comparator.comparing(ProxyObject::getName));
        return groupObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProxyGroup that = (ProxyGroup) o;

        if (maxPlayerCountPerProxy != that.maxPlayerCountPerProxy) return false;
        if (maxPlayerCount != that.maxPlayerCount) return false;
        if (keepFreeSlots != that.keepFreeSlots) return false;
        if (maxAmount != that.maxAmount) return false;
        if (ram != that.ram) return false;
        if (isStatic != that.isStatic) return false;
        if (priority != that.priority) return false;
        if (!name.equals(that.name)) return false;
        if (motd != null ? !motd.equals(that.motd) : that.motd != null) return false;
        if (serverGroups != null ? !serverGroups.equals(that.serverGroups) : that.serverGroups != null) return false;
        if (baseName != null ? !baseName.equals(that.baseName) : that.baseName != null) return false;
        if (proxies != null ? !proxies.equals(that.proxies) : that.proxies != null) return false;
        return registeredServers != null ? registeredServers.equals(that.registeredServers) : that.registeredServers == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + maxPlayerCountPerProxy;
        result = 31 * result + maxPlayerCount;
        result = 31 * result + keepFreeSlots;
        result = 31 * result + maxAmount;
        result = 31 * result + ram;
        result = 31 * result + (motd != null ? motd.hashCode() : 0);
        result = 31 * result + (isStatic ? 1 : 0);
        result = 31 * result + priority;
        result = 31 * result + (serverGroups != null ? serverGroups.hashCode() : 0);
        result = 31 * result + (baseName != null ? baseName.hashCode() : 0);
        result = 31 * result + (proxies != null ? proxies.hashCode() : 0);
        result = 31 * result + (registeredServers != null ? registeredServers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getName();
    }
}
