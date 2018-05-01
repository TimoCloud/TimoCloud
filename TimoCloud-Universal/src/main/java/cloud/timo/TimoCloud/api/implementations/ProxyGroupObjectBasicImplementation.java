package cloud.timo.TimoCloud.api.implementations;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ProxyChooseStrategy;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;

import java.util.List;
import java.util.stream.Collectors;

public class ProxyGroupObjectBasicImplementation implements ProxyGroupObject {

    private String name;
    private List<ProxyObject> proxies;
    private int onlinePlayerCount;
    private int maxPlayerCount;
    private int maxPlayerCountPerProxy;
    private int keepFreeSlots;
    private int ram;
    private String motd;
    private boolean isStatic;
    private int priority;
    private List<String> serverGroups;
    private String base;
    private ProxyChooseStrategy proxyChooseStrategy;
    private List<String> hostNames;

    public ProxyGroupObjectBasicImplementation() {}

    public ProxyGroupObjectBasicImplementation(String name, List<ProxyObject> proxies, int onlinePlayerCount, int maxPlayerCount, int maxPlayerCountPerProxy, int keepFreeSlots, int ram, String motd, boolean isStatic, int priority, List<String> serverGroups, String base, String proxyChooseStrategy, List<String> hostNames) {
        this.name = name;
        this.proxies = proxies;
        this.onlinePlayerCount = onlinePlayerCount;
        this.maxPlayerCount = maxPlayerCount;
        this.maxPlayerCountPerProxy = maxPlayerCountPerProxy;
        this.keepFreeSlots= keepFreeSlots;
        this.ram = ram;
        this.motd = motd;
        this.isStatic = isStatic;
        this.priority = priority;
        this.serverGroups = serverGroups;
        this.base = base;
        this.proxyChooseStrategy = ProxyChooseStrategy.valueOf(proxyChooseStrategy);
        this.hostNames = hostNames;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ProxyObject> getProxies() {
        return proxies;
    }

    @Override
    public int getOnlinePlayerCount() {
        return onlinePlayerCount;
    }

    @Override
    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    @Override
    public int getMaxPlayerCountPerProxy() {
        return maxPlayerCountPerProxy;
    }

    @Override
    public int getKeepFreeSlots() {
        return keepFreeSlots;
    }

    @Override
    public int getRam() {
        return ram;
    }

    @Override
    public String getMotd() {
        return motd;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public List<ServerGroupObject> getServerGroups() {
        return serverGroups.stream().map(serverGroup -> TimoCloudAPI.getUniversalAPI().getServerGroup(serverGroup)).collect(Collectors.toList());
    }

    @Override
    public String getBase() {
        return base;
    }

    @Override
    public ProxyChooseStrategy getProxyChooseStrategy() {
        return proxyChooseStrategy;
    }

    @Override
    public List<String> getHostNames() {
        return hostNames;
    }
}
