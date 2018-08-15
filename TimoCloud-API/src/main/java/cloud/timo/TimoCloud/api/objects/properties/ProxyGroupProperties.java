package cloud.timo.TimoCloud.api.objects.properties;

import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;
import cloud.timo.TimoCloud.api.objects.ProxyChooseStrategy;

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
    private ProxyChooseStrategy proxyChooseStrategy;
    private Collection<String> hostNames;

    public ProxyGroupProperties(String name) {
        this.name = name;
        this.maxPlayerCountPerProxy = getDefaultPropertiesProvider().getMaxPlayerCountPerProxy();
        this.maxPlayerCount = getDefaultPropertiesProvider().getMaxPlayerCount();
        this.keepFreeSlots = getDefaultPropertiesProvider().getKeepFreeSlots();
        this.minAmount = getDefaultPropertiesProvider().getMinAmount();
        this.maxAmount = getDefaultPropertiesProvider().getMaxAmount();
        this.ram = getDefaultPropertiesProvider().getRam();
        this.motd = getDefaultPropertiesProvider().getMotd();
        this.isStatic = getDefaultPropertiesProvider().isStatic();
        this.priority = getDefaultPropertiesProvider().getPriority();
        this.serverGroups = getDefaultPropertiesProvider().getServerGroups();
        this.baseName = getDefaultPropertiesProvider().getBaseName();
        this.proxyChooseStrategy = getDefaultPropertiesProvider().getProxyChooseStrategy();
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

    public Boolean isStatic() {
        return isStatic;
    }

    public ProxyGroupProperties setStatic(Boolean isStatic) {
        this.isStatic = isStatic;
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

    public ProxyChooseStrategy getProxyChooseStrategy() {
        return proxyChooseStrategy;
    }

    public ProxyGroupProperties setProxyChooseStrategy(ProxyChooseStrategy proxyChooseStrategy) {
        this.proxyChooseStrategy = proxyChooseStrategy;
        return this;
    }

    public Collection<String> getHostNames() {
        return hostNames;
    }

    public ProxyGroupProperties setHostNames(Collection<String> hostNames) {
        this.hostNames = hostNames;
        return this;
    }

    private static ProxyGroupDefaultPropertiesProvider getDefaultPropertiesProvider() {
        return TimoCloudInternalAPI.getImplementationAPI().getProxyGroupDefaultPropertiesProvider();
    }

    public interface ProxyGroupDefaultPropertiesProvider {

        Integer getMaxPlayerCountPerProxy();

        Integer getMaxPlayerCount();

        Integer getKeepFreeSlots();

        Integer getMinAmount();

        Integer getMaxAmount();

        Integer getRam();

        String getMotd();

        Boolean isStatic();

        Integer getPriority();

        Collection<String> getServerGroups();

        String getBaseName();

        ProxyChooseStrategy getProxyChooseStrategy();

    }
}
