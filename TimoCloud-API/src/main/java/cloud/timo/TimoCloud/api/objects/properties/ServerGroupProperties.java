package cloud.timo.TimoCloud.api.objects.properties;

import java.util.Arrays;
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

    public ServerGroupProperties(String name) {
        this.name = name;
        this.onlineAmount = 1;
        this.maxAmount = -1;
        this.ram = 1024;
        this.isStatic = false;
        this.priority = 1;
        this.baseName = null;
        this.sortOutStates = Arrays.asList("OFFLINE", "STARTING", "INGAME", "RESTARTING");
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

    public Boolean getStatic() {
        return isStatic;
    }

    public ServerGroupProperties setStatic(Boolean aStatic) {
        isStatic = aStatic;
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
}
