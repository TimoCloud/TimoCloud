package cloud.timo.TimoCloud.api.objects.properties;

import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;
import cloud.timo.TimoCloud.api.objects.ProxyChooseStrategy;

import java.util.Collection;
import java.util.List;

public class ProxyGroupProperties {

    private String id;
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
    private String baseIdentifier;
    private ProxyChooseStrategy proxyChooseStrategy;
    private Collection<String> hostNames;
    private List<String> javaParameters;
    private String jrePath;

    public ProxyGroupProperties(String id, String name) {
        this.id = id;
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
        this.baseIdentifier = getDefaultPropertiesProvider().getBaseIdentifier();
        this.proxyChooseStrategy = getDefaultPropertiesProvider().getProxyChooseStrategy();
        this.hostNames = getDefaultPropertiesProvider().getHostNames();
        this.javaParameters = getDefaultPropertiesProvider().getJavaParameters();
        this.jrePath = getDefaultPropertiesProvider().getJrePath();
    }

    public ProxyGroupProperties(String name) {
        this(generateId(), name);
    }

    public String getId() {
        return id;
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

    public String getBaseIdentifier() {
        return baseIdentifier;
    }

    public ProxyGroupProperties setBaseIdentifier(String baseIdentifier) {
        this.baseIdentifier = baseIdentifier;
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

    public List<String> getJavaParameters() {
        return javaParameters;
    }

    public ProxyGroupProperties setJavaParameters(List<String> javaParameters) {
        this.javaParameters = javaParameters;
        return this;
    }

    public String getJrePath() {
        return jrePath;
    }

    public ProxyGroupProperties setJrePath(String jrePath) {
        this.jrePath = jrePath;
        return this;
    }

    private static ProxyGroupDefaultPropertiesProvider getDefaultPropertiesProvider() {
        return TimoCloudInternalAPI.getImplementationAPI().getProxyGroupDefaultPropertiesProvider();
    }

    public static String generateId() {
        return getDefaultPropertiesProvider().generateId();
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

        String getBaseIdentifier();

        ProxyChooseStrategy getProxyChooseStrategy();

        Collection<String> getHostNames();

        List<String> getJavaParameters();

        String getJrePath();

        String generateId();

    }
}
