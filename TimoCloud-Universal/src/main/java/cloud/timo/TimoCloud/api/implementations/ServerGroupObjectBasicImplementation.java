package cloud.timo.TimoCloud.api.implementations;

import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.util.List;

public class ServerGroupObjectBasicImplementation implements ServerGroupObject {

    private List<ServerObject> servers;
    private String name;
    private int startupAmount;
    private int maxAmount;
    private int ram;
    private boolean isStatic;
    private String base;
    private List<String> sortOutStates;

    /**
     * Do not use this - this will be done by TimoCloud
     */
    public ServerGroupObjectBasicImplementation() {}

    /**
     * Do not use this - this will be done by TimoCloud
     */
    public ServerGroupObjectBasicImplementation(String name, List<ServerObject> servers, int startupAmount, int maxAmount, int ram, boolean isStatic, String base, List<String> sortOutStates) {
        this.name = name;
        this.servers = servers;
        this.startupAmount = startupAmount;
        this.maxAmount = maxAmount;
        this.ram = ram;
        this.isStatic = isStatic;
        this.base = base;
        this.sortOutStates = sortOutStates;
    }

    @Override
    public List<ServerObject> getServers() {
        return servers;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOnlineAmount() {
        return startupAmount;
    }

    @Override
    public int getMaxAmount() {
        return maxAmount;
    }

    @Override
    public int getRam() {
        return ram;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public String getBase() {
        return base;
    }

    @Override
    public List<String> getSortOutStates() {
        return sortOutStates;
    }

}
