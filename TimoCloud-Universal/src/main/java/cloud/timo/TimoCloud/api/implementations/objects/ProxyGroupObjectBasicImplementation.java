package cloud.timo.TimoCloud.api.implementations.objects;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.implementations.async.APIRequestImplementation;
import cloud.timo.TimoCloud.api.internal.links.*;
import cloud.timo.TimoCloud.api.objects.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cloud.timo.TimoCloud.api.async.APIRequestType.*;

@NoArgsConstructor
public class ProxyGroupObjectBasicImplementation implements ProxyGroupObject, LinkableObject<ProxyGroupObject> {

    // Assign short json property names so that the JSON object is smaller
    @JsonProperty("i")
    private String id;
    @JsonProperty("n")
    private String name;
    @JsonProperty("ps")
    private Collection<ProxyObjectLink> proxies;
    @JsonProperty("mpc")
    private int maxPlayerCount;
    @JsonProperty("mpcp")
    private int maxPlayerCountPerProxy;
    @JsonProperty("kf")
    private int keepFreeSlots;
    @JsonProperty("mia")
    private int minAmount;
    @JsonProperty("maa")
    private int maxAmount;
    @JsonProperty("r")
    private int ram;
    @JsonProperty("mo")
    private String motd;
    @JsonProperty("s")
    private boolean isStatic;
    @JsonProperty("pr")
    private int priority;
    @JsonProperty("sg")
    private Collection<ServerGroupObjectLink> serverGroups;
    @JsonProperty("b")
    private BaseObjectLink base;
    @JsonProperty("pcs")
    private ProxyChooseStrategy proxyChooseStrategy;
    @JsonProperty("hn")
    private Collection<String> hostNames;
    @JsonProperty("jp")
    private List<String> javaParameters;
    @JsonProperty("jdrp")
    private String jrePath;

    public ProxyGroupObjectBasicImplementation(String id, String name, Collection<ProxyObjectLink> proxies, int maxPlayerCount, int maxPlayerCountPerProxy, int keepFreeSlots, int minAmount, int maxAmount, int ram, String motd, boolean isStatic, int priority, Collection<ServerGroupObjectLink> serverGroups, BaseObjectLink base, String proxyChooseStrategy, Collection<String> hostNames, List<String> javaParameters, String jrePath) {
        this.id = id;
        this.name = name;
        this.proxies = proxies;
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
        this.javaParameters = javaParameters;
        this.jrePath = jrePath;
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
        return getProxies().stream().mapToInt(ProxyObject::getOnlinePlayerCount).sum();
    }

    @Override
    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    @Override
    public APIRequestFuture<Void> setMaxPlayerCount(int value) {
        return new APIRequestImplementation<Void>(PG_SET_MAX_PLAYER_COUNT, getId(), value).submit();
    }

    @Override
    public int getMaxPlayerCountPerProxy() {
        return maxPlayerCountPerProxy;
    }

    @Override
    public APIRequestFuture<Void> setMaxPlayerCountPerProxy(int value) {
        return new APIRequestImplementation<Void>(PG_SET_MAX_PLAYER_COUNT_PER_PROXY, getId(), value).submit();
    }

    @Override
    public int getKeepFreeSlots() {
        return keepFreeSlots;
    }

    @Override
    public APIRequestFuture<Void> setKeepFreeSlots(int value) {
        return new APIRequestImplementation<Void>(PG_SET_KEEP_FREE_SLOTS, getId(), value).submit();
    }

    @Override
    public int getMinAmount() {
        return minAmount;
    }

    @Override
    public APIRequestFuture<Void> setMinAmount(int value) {
        return new APIRequestImplementation<Void>(PG_SET_MIN_AMOUNT, getId(), value).submit();
    }

    @Override
    public int getMaxAmount() {
        return maxAmount;
    }

    @Override
    public APIRequestFuture<Void> setMaxAmount(int value) {
        return new APIRequestImplementation<Void>(PG_SET_MAX_AMOUNT, getId(), value).submit();
    }

    @Override
    public int getRam() {
        return ram;
    }

    @Override
    public APIRequestFuture<Void> setRam(int value) {
        return new APIRequestImplementation<Void>(PG_SET_RAM, getId(), value).submit();
    }

    @Override
    public String getMotd() {
        return motd;
    }

    @Override
    public APIRequestFuture<Void> setMotd(String value) {
        return new APIRequestImplementation<Void>(PG_SET_MOTD, getId(), value).submit();
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public APIRequestFuture<Void> setStatic(boolean value) {
        return new APIRequestImplementation<Void>(PG_SET_STATIC, getId(), value).submit();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public APIRequestFuture<Void> setPriority(int value) {
        return new APIRequestImplementation<Void>(PG_SET_PRIORITY, getId(), value).submit();
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
        return new APIRequestImplementation<Void>(PG_SET_BASE, getId(), value).submit();
    }

    @Override
    public ProxyChooseStrategy getProxyChooseStrategy() {
        return proxyChooseStrategy;
    }

    @Override
    public APIRequestFuture<Void> setProxyChooseStrategy(ProxyChooseStrategy value) {
        return new APIRequestImplementation<Void>(PG_SET_PROXY_CHOOSE_STRATEGY, getId(), value).submit();
    }

    @Override
    public Collection<String> getHostNames() {
        return hostNames;
    }

    @Override
    public APIRequestFuture<Void> setHostNames(Collection<String> value) {
        return new APIRequestImplementation<Void>(PG_SET_HOST_NAMES, getId(), value).submit();
    }

    @Override
    public APIRequestFuture<Void> delete() {
        return new APIRequestImplementation<Void>(PG_DELETE, getId()).submit();
    }

    @Override
    public Collection<String> getJavaParameters() {
        return javaParameters;
    }

    @Override
    public APIRequestFuture<Void> setJavaParameters(Collection<String> value) {
        return new APIRequestImplementation<Void>(PG_SET_JAVA_START_PARAMETERS, getId(), value).submit();
    }

    @Override
    public String getJrePath() {
        return jrePath;
    }

    @Override
    public ProxyGroupObjectLink toLink() {
        return new ProxyGroupObjectLink(this);
    }

    public void setIdInternally(String id) {
        this.id = id;
    }

    public void setNameInternally(String name) {
        this.name = name;
    }

    public void setMaxPlayerCountInternally(int i) {
        this.maxPlayerCount = i;
    }

    public void setMaxPlayerCountPerProxyInternally(int i) {
        this.maxPlayerCountPerProxy = i;
    }

    public void setKeepFreeSlotsInternally(int i) {
        this.keepFreeSlots = i;
    }

    public void setMinAmountInternally(int i) {
        this.minAmount = i;
    }

    public void setMaxAmoutInternally(int i) {
        this.maxAmount = i;
    }

    public void setRamInternally(int i) {
        this.ram = i;
    }

    public void setMotdInternally(String motd) {
        this.motd = motd;
    }

    public void setStaticInternally(boolean b) {
        this.isStatic = b;
    }

    public void setPriorityInternally(int i) {
        this.priority = i;
    }

    public void setBaseInternally(BaseObject base) {
        this.base = new BaseObjectLink(base);
    }

    public void setProxyChooseStrategyInternally(ProxyChooseStrategy proxyChooseStrategyInternally) {
        this.proxyChooseStrategy = proxyChooseStrategyInternally;
    }

    public void addProxyInternally(ProxyObjectLink proxy) {
        this.proxies.add(proxy);
    }

    public void removeProxyInternally(ProxyObjectLink proxy) {
        this.proxies.remove(proxy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProxyGroupObjectBasicImplementation that = (ProxyGroupObjectBasicImplementation) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
