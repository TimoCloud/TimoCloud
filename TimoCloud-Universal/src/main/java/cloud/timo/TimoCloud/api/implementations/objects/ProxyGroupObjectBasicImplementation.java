package cloud.timo.TimoCloud.api.implementations.objects;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.implementations.async.APIRequestImplementation;
import cloud.timo.TimoCloud.api.objects.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static cloud.timo.TimoCloud.api.async.APIRequestType.*;

public class ProxyGroupObjectBasicImplementation implements ProxyGroupObject {

    private String name;
    private Collection<ProxyObject> proxies;
    private int onlinePlayerCount;
    private int maxPlayerCount;
    private int maxPlayerCountPerProxy;
    private int keepFreeSlots;
    private int minAmount;
    private int maxAmount;
    private int ram;
    private String motd;
    private boolean isStatic;
    private int priority;
    private Collection<String> serverGroups;
    private String base;
    private ProxyChooseStrategy proxyChooseStrategy;
    private Collection<String> hostNames;

    public ProxyGroupObjectBasicImplementation() {
    }

    public ProxyGroupObjectBasicImplementation(String name, Collection<ProxyObject> proxies, int onlinePlayerCount, int maxPlayerCount, int maxPlayerCountPerProxy, int keepFreeSlots, int minAmount, int maxAmount, int ram, String motd, boolean isStatic, int priority, Collection<String> serverGroups, String base, String proxyChooseStrategy, Collection<String> hostNames) {
        this.name = name;
        this.proxies = proxies;
        this.onlinePlayerCount = onlinePlayerCount;
        this.maxPlayerCount = maxPlayerCount;
        this.maxPlayerCountPerProxy = maxPlayerCountPerProxy;
        this.keepFreeSlots = keepFreeSlots;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
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
    public Collection<ProxyObject> getProxies() {
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
    public APIRequestFuture setMaxPlayerCount(int value) {
        return new APIRequestImplementation(PG_SET_MAX_PLAYER_COUNT, getName(), value).submit();
    }

    @Override
    public int getMaxPlayerCountPerProxy() {
        return maxPlayerCountPerProxy;
    }

    @Override
    public APIRequestFuture setMaxPlayerCountPerProxy(int value) {
        return new APIRequestImplementation(PG_SET_MAX_PLAYER_CONT_PER_PROXY, getName(), value).submit();
    }

    @Override
    public int getKeepFreeSlots() {
        return keepFreeSlots;
    }

    @Override
    public APIRequestFuture setKeepFreeSlots(int value) {
        return new APIRequestImplementation(PG_SET_KEEP_FREE_SLOTS, getName(), value).submit();
    }

    @Override
    public int getMinAmount() {
        return minAmount;
    }

    @Override
    public APIRequestFuture setMinAmount(int value) {
        return new APIRequestImplementation(PG_SET_MIN_AMOUNT, getName(), value).submit();
    }

    @Override
    public int getMaxAmount() {
        return maxAmount;
    }

    @Override
    public APIRequestFuture setMaxAmount(int value) {
        return new APIRequestImplementation(PG_SET_MAX_AMOUNT, getName(), value).submit();
    }

    @Override
    public int getRam() {
        return ram;
    }

    @Override
    public APIRequestFuture setRam(int value) {
        return new APIRequestImplementation(PG_SET_RAM, getName(), value).submit();
    }

    @Override
    public String getMotd() {
        return motd;
    }

    @Override
    public APIRequestFuture setMotd(String value) {
        return new APIRequestImplementation(PG_SET_MOTD, getName(), value).submit();
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public APIRequestFuture setStatic(boolean value) {
        return new APIRequestImplementation(PG_SET_STATIC, getName(), value).submit();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public APIRequestFuture setPriority(int value) {
        return new APIRequestImplementation(PG_SET_PRIORITY, getName(), value).submit();
    }

    @Override
    public List<ServerGroupObject> getServerGroups() {
        return serverGroups.stream().map(serverGroup -> TimoCloudAPI.getUniversalAPI().getServerGroup(serverGroup)).collect(Collectors.toList());
    }

    @Override
    public BaseObject getBase() {
        return TimoCloudAPI.getUniversalAPI().getBase(base);
    }

    @Override
    public APIRequestFuture setBase(BaseObject value) {
        return new APIRequestImplementation(PG_SET_BASE, getName(), value).submit();
    }

    @Override
    public ProxyChooseStrategy getProxyChooseStrategy() {
        return proxyChooseStrategy;
    }

    @Override
    public APIRequestFuture setProxyChooseStrategy(ProxyChooseStrategy value) {
        return new APIRequestImplementation(PG_SET_PROXY_CHOOSE_STRATEGY, getName(), value).submit();
    }

    @Override
    public Collection<String> getHostNames() {
        return hostNames;
    }

    @Override
    public APIRequestFuture setHostNames(Collection<String> value) {
        return new APIRequestImplementation(PG_SET_HOST_NAMES, getName(), value).submit();
    }
}
