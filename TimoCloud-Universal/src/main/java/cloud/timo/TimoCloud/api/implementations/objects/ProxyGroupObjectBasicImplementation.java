package cloud.timo.TimoCloud.api.implementations.objects;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.implementations.async.APIRequestImplementation;
import cloud.timo.TimoCloud.api.internal.links.*;
import cloud.timo.TimoCloud.api.objects.*;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cloud.timo.TimoCloud.api.async.APIRequestType.*;

@NoArgsConstructor
public class ProxyGroupObjectBasicImplementation implements ProxyGroupObject, LinkableObject<ProxyGroupObject> {

    private String id;
    private String name;
    private Collection<ProxyObjectLink> proxies;
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
    private Collection<ServerGroupObjectLink> serverGroups;
    private BaseObjectLink base;
    private ProxyChooseStrategy proxyChooseStrategy;
    private Collection<String> hostNames;

    public ProxyGroupObjectBasicImplementation(String id, String name, Collection<ProxyObjectLink> proxies, int onlinePlayerCount, int maxPlayerCount, int maxPlayerCountPerProxy, int keepFreeSlots, int minAmount, int maxAmount, int ram, String motd, boolean isStatic, int priority, Collection<ServerGroupObjectLink> serverGroups, BaseObjectLink base, String proxyChooseStrategy, Collection<String> hostNames) {
        this.id = id;
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
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<ProxyObject> getProxies() {
        return proxies.stream().map(ProxyObjectLink::resolve).collect(Collectors.toSet());
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
    public APIRequestFuture<Void> setMaxPlayerCount(int value) {
        return new APIRequestImplementation<Void>(PG_SET_MAX_PLAYER_COUNT, getName(), value).submit();
    }

    @Override
    public int getMaxPlayerCountPerProxy() {
        return maxPlayerCountPerProxy;
    }

    @Override
    public APIRequestFuture<Void> setMaxPlayerCountPerProxy(int value) {
        return new APIRequestImplementation<Void>(PG_SET_MAX_PLAYER_COUNT_PER_PROXY, getName(), value).submit();
    }

    @Override
    public int getKeepFreeSlots() {
        return keepFreeSlots;
    }

    @Override
    public APIRequestFuture<Void> setKeepFreeSlots(int value) {
        return new APIRequestImplementation<Void>(PG_SET_KEEP_FREE_SLOTS, getName(), value).submit();
    }

    @Override
    public int getMinAmount() {
        return minAmount;
    }

    @Override
    public APIRequestFuture<Void> setMinAmount(int value) {
        return new APIRequestImplementation<Void>(PG_SET_MIN_AMOUNT, getName(), value).submit();
    }

    @Override
    public int getMaxAmount() {
        return maxAmount;
    }

    @Override
    public APIRequestFuture<Void> setMaxAmount(int value) {
        return new APIRequestImplementation<Void>(PG_SET_MAX_AMOUNT, getName(), value).submit();
    }

    @Override
    public int getRam() {
        return ram;
    }

    @Override
    public APIRequestFuture<Void> setRam(int value) {
        return new APIRequestImplementation<Void>(PG_SET_RAM, getName(), value).submit();
    }

    @Override
    public String getMotd() {
        return motd;
    }

    @Override
    public APIRequestFuture<Void> setMotd(String value) {
        return new APIRequestImplementation<Void>(PG_SET_MOTD, getName(), value).submit();
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public APIRequestFuture<Void> setStatic(boolean value) {
        return new APIRequestImplementation<Void>(PG_SET_STATIC, getName(), value).submit();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public APIRequestFuture<Void> setPriority(int value) {
        return new APIRequestImplementation<Void>(PG_SET_PRIORITY, getName(), value).submit();
    }

    @Override
    public List<ServerGroupObject> getServerGroups() {
        return Collections.unmodifiableList(serverGroups.stream().map(ServerGroupObjectLink::resolve).collect(Collectors.toList()));
    }

    @Override
    public BaseObject getBase() {
        return base.resolve();
    }

    @Override
    public APIRequestFuture<Void> setBase(BaseObject value) {
        return new APIRequestImplementation<Void>(PG_SET_BASE, getName(), value).submit();
    }

    @Override
    public ProxyChooseStrategy getProxyChooseStrategy() {
        return proxyChooseStrategy;
    }

    @Override
    public APIRequestFuture<Void> setProxyChooseStrategy(ProxyChooseStrategy value) {
        return new APIRequestImplementation<Void>(PG_SET_PROXY_CHOOSE_STRATEGY, getName(), value).submit();
    }

    @Override
    public Collection<String> getHostNames() {
        return hostNames;
    }

    @Override
    public APIRequestFuture<Void> setHostNames(Collection<String> value) {
        return new APIRequestImplementation<Void>(PG_SET_HOST_NAMES, getName(), value).submit();
    }

    @Override
    public ProxyGroupObjectLink toLink() {
        return new ProxyGroupObjectLink(this);
    }
}
