package cloud.timo.TimoCloud.api.objects.properties;

import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;

import java.util.Collection;

public class ServerGroupProperties {

    private String name;
    private Integer onlineAmount;
    private Integer maxAmount;
    private Integer ram;
    private Boolean isStatic;
    private Integer priority;
    private String baseName;
    private Collection<String> sortOutStates;

    private ServerGroupProperties() {
        this.onlineAmount = getDefaultPropertiesProvider().getOnlineAmount();
        this.maxAmount = getDefaultPropertiesProvider().getMaxAmount();
        this.ram = getDefaultPropertiesProvider().getRam();
        this.isStatic = getDefaultPropertiesProvider().isStatic();
        this.priority = getDefaultPropertiesProvider().getPriority();
        this.baseName = getDefaultPropertiesProvider().getBaseName();
        this.sortOutStates = getDefaultPropertiesProvider().getSortOutStates();
    }
    
    public ServerGroupProperties(String name) {
        this();
        this.name = name;
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

    public String getBaseName() {
        return baseName;
    }

    public ServerGroupProperties setBaseName(String baseName) {
        this.baseName = baseName;
        return this;
    }

    public Collection<String> getSortOutStates() {
        return sortOutStates;
    }

    public ServerGroupProperties setSortOutStates(Collection<String> sortOutStates) {
        this.sortOutStates = sortOutStates;
        return this;
    }

    private static ServerGroupDefaultPropertiesProvider getDefaultPropertiesProvider() {
        return TimoCloudInternalAPI.getImplementationAPI().getServerGroupDefaultPropertiesProvider();
    }

    public interface ServerGroupDefaultPropertiesProvider {

        Integer getOnlineAmount();

        Integer getMaxAmount();

        Integer getRam();

        Boolean isStatic();

        Integer getPriority();

        String getBaseName();

        Collection<String> getSortOutStates();

    }
}
