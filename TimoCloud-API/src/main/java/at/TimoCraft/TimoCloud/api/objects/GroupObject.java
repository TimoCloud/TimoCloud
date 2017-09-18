package at.TimoCraft.TimoCloud.api.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timo on 02.09.17.
 */
public class GroupObject implements Serializable {

    private List<ServerObject> startingServers;
    private List<ServerObject> runningServers;
    private String name;
    private int startupAmount;
    private int maxAmount;
    private int ram;
    private boolean isStatic;
    private String base;
    private List<String> sortOutStates;

    public GroupObject(List<ServerObject> startingServers, List<ServerObject> runningServers, String name, int startupAmount, int maxAmount, int ram, boolean isStatic, String base, List<String> sortOutStates) {
        this.runningServers = runningServers;
        this.startingServers = startingServers;
        for (ServerObject serverObject : getAllServers()) serverObject.setGroup(this);
        this.name = name;
        this.startupAmount = startupAmount;
        this.maxAmount = maxAmount;
        this.ram = ram;
        this.isStatic = isStatic;
        this.base = base;
        this.sortOutStates = sortOutStates;
    }

    public List<ServerObject> getRunningServers() {
        return runningServers;
    }

    public List<ServerObject> getStartingServers() {
        return startingServers;
    }

    public List<ServerObject> getAllServers() {
        List<ServerObject> allServers = new ArrayList<>();
        allServers.addAll(startingServers);
        allServers.addAll(runningServers);
        return allServers;
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
