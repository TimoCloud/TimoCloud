package cloud.timo.TimoCloud.api.objects.properties;

import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;

import java.util.Collection;

public class ServerGroupProperties {

    private String id;
    private String name;
    private Integer onlineAmount;
    private Integer maxAmount;
    private Integer ram;
    private Boolean isStatic;
    private Integer priority;
    private String baseIdentifier;
    private Collection<String> sortOutStates;
    private Collection<String> javaParameters;
    private Collection<String> spigotParameters;

    public ServerGroupProperties(String id, String name) {
        this.id = id;
        this.name = name;
        this.onlineAmount = getDefaultPropertiesProvider().getOnlineAmount();
        this.maxAmount = getDefaultPropertiesProvider().getMaxAmount();
        this.ram = getDefaultPropertiesProvider().getRam();
        this.isStatic = getDefaultPropertiesProvider().isStatic();
        this.priority = getDefaultPropertiesProvider().getPriority();
        this.baseIdentifier = getDefaultPropertiesProvider().getBaseIdentifier();
        this.sortOutStates = getDefaultPropertiesProvider().getSortOutStates();
        this.javaParameters = getDefaultPropertiesProvider().getJavaParameters();
        this.spigotParameters = getDefaultPropertiesProvider().getSpigotParameters();
    }

    public ServerGroupProperties(String name) {
        this(generateId(), name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ServerGroupProperties setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getOnlineAmount() {
        return onlineAmount;
    }

    public ServerGroupProperties setOnlineAmount(Integer onlineAmount) {
        this.onlineAmount = onlineAmount;
        return this;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public ServerGroupProperties setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
        return this;
    }

    public Integer getRam() {
        return ram;
    }

    public ServerGroupProperties setRam(Integer ram) {
        this.ram = ram;
        return this;
    }

    public Boolean isStatic() {
        return isStatic;
    }

    public ServerGroupProperties setStatic(Boolean isStatic) {
        this.isStatic = isStatic;
        return this;
    }

    public Integer getPriority() {
        return priority;
    }

    public ServerGroupProperties setPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public String getBaseIdentifier() {
        return baseIdentifier;
    }

    public ServerGroupProperties setBaseIdentifier(String baseIdentifier) {
        this.baseIdentifier = baseIdentifier;
        return this;
    }

    public Collection<String> getSortOutStates() {
        return sortOutStates;
    }

    public ServerGroupProperties setSortOutStates(Collection<String> sortOutStates) {
        this.sortOutStates = sortOutStates;
        return this;
    }

    public Collection<String> getJavaParameters() {
        return javaParameters;
    }

    public ServerGroupProperties setJavaParameters(Collection<String> javaParameters) {
        this.javaParameters = javaParameters;
        return this;
    }

    public Collection<String> getSpigotParameters() {
        return spigotParameters;
    }

    public ServerGroupProperties setSpigotParameters(Collection<String> spigotParameters) {
        this.spigotParameters = spigotParameters;
        return this;
    }

    private static ServerGroupDefaultPropertiesProvider getDefaultPropertiesProvider() {
        return TimoCloudInternalAPI.getImplementationAPI().getServerGroupDefaultPropertiesProvider();
    }

    public static String generateId() {
        return getDefaultPropertiesProvider().generateId();
    }

    public interface ServerGroupDefaultPropertiesProvider {

        Integer getOnlineAmount();

        Integer getMaxAmount();

        Integer getRam();

        Boolean isStatic();

        Integer getPriority();

        String getBaseIdentifier();

        Collection<String> getSortOutStates();

        String generateId();

        Collection<String> getJavaParameters();

        Collection<String> getSpigotParameters();

    }
}
