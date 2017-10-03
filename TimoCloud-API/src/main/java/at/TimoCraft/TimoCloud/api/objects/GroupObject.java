package at.TimoCraft.TimoCloud.api.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timo on 02.09.17.
 */
public class GroupObject implements Serializable {

    private List<ServerObject> servers;
    private String name;
    private int startupAmount;
    private int maxAmount;
    private int ram;
    private boolean isStatic;
    private String base;
    private List<String> sortOutStates;

    public GroupObject() {}

    public GroupObject(List<ServerObject> servers, String name, int startupAmount, int maxAmount, int ram, boolean isStatic, String base, List<String> sortOutStates) {
        this.servers = servers;
        this.name = name;
        this.startupAmount = startupAmount;
        this.maxAmount = maxAmount;
        this.ram = ram;
        this.isStatic = isStatic;
        this.base = base;
        this.sortOutStates = sortOutStates;
    }

    public List<ServerObject> getServers() {
        return servers;
    }

    public String getName() {
        return name;
    }

    public int getStartupAmount() {
        return startupAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getRam() {
        return ram;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public String getBase() {
        return base;
    }

    public List<String> getSortOutStates() {
        return sortOutStates;
    }

}
