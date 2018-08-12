package cloud.timo.TimoCloud.api.objects.properties;

import java.util.Collection;

public class ProxyGroupProperties {
    private String name;
    private Integer maxPlayerCountPerProxy;
    private Integer maxPlayerCount;
    private Integer keepFreeSlots;
    private Integer minAmount;
    private Integer maxAmount;
    private Integer ram;
    private String motd;
    private Boolean isStatic;
    private Integer priority;
    private Collection<String> serverGroups;
    private String baseName;
    private String proxyChooseStrategyName;

    public ProxyGroupProperties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ProxyGroupProperties setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getMaxPlayerCountPerProxy() {
        return maxPlayerCountPerProxy;
    }

    public ProxyGroupProperties setMaxPlayerCountPerProxy(Integer maxPlayerCountPerProxy) {
        this.maxPlayerCountPerProxy = maxPlayerCountPerProxy;
        return this;
    }

    public Integer getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public ProxyGroupProperties setMaxPlayerCount(Integer maxPlayerCount) {
        this.maxPlayerCount = maxPlayerCount;
        return this;
    }

    public Integer getKeepFreeSlots() {
        return keepFreeSlots;
    }

    public ProxyGroupProperties setKeepFreeSlots(Integer keepFreeSlots) {
        this.keepFreeSlots = keepFreeSlots;
        return this;
    }

    public Integer getMinAmount() {
        return minAmount;
    }

    public ProxyGroupProperties setMinAmount(Integer minAmount) {
        this.minAmount = minAmount;
        return this;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public ProxyGroupProperties setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
        return this;
    }

    public Integer getRam() {
        return ram;
    }

    public ProxyGroupProperties setRam(Integer ram) {
        this.ram = ram;
        return this;
    }

    public String getMotd() {
        return motd;
    }

    public ProxyGroupProperties setMotd(String motd) {
        this.motd = motd;
        return this;
    }

    public Boolean getStatic() {
        return isStatic;
    }

    public ProxyGroupProperties setStatic(Boolean aStatic) {
        isStatic = aStatic;
        return this;
    }

    public Integer getPriority() {
        return priority;
    }

    public ProxyGroupProperties setPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public Collection<String> getServerGroups() {
        return serverGroups;
    }

    public ProxyGroupProperties setServerGroups(Collection<String> serverGroups) {
        this.serverGroups = serverGroups;
        return this;
    }

    public String getBaseName() {
        return baseName;
    }

    public ProxyGroupProperties setBaseName(String baseName) {
        this.baseName = baseName;
        return this;
    }

    public String getProxyChooseStrategyName() {
        return proxyChooseStrategyName;
    }

    public ProxyGroupProperties setProxyChooseStrategyName(String proxyChooseStrategyName) {
        this.proxyChooseStrategyName = proxyChooseStrategyName;
        return this;
    }
}
