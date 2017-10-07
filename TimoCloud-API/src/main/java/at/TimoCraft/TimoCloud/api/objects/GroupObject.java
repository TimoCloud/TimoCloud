package at.TimoCraft.TimoCloud.api.objects;

import java.util.List;

public class GroupObject {

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
    public GroupObject() {}

    /**
     * Do not use this - this will be done by TimoCloud
     */
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

    /**
     * Returns all starting/running servers of the group
     * @return A list of {@link ServerObject} which contains all server objects
     */
    public List<ServerObject> getServers() {
        return servers;
    }

    /**
     * @return The groups name
     */
    public String getName() {
        return name;
    }

    /**
     * The StartupAmount or Keep-Online-Amount is the amount of servers TimoCloud wants to always be online. Called 'onlineAmount' in the groups.yml
     * @return An int containing the StartupAmount
     */
    public int getStartupAmount() {
        return startupAmount;
    }

    /**
     * The MaxAmount specifies the maximal amount of servers TimoCloud keeps online at the same time - no matter what startupAmount says
     * @return An int containing the MaxAmount
     */
    public int getMaxAmount() {
        return maxAmount;
    }

    /**
     * Maximum of ram a server of this group may use
     * @return An int containing the ram in MB (megabytes)
     */
    public int getRam() {
        return ram;
    }

    /**
     * If a group is static, servers will not be reset after restart. A static group can only start 1 server.
     * @return A boolean which tells you if the
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * If two groups are started by the same base, you can assume that they are running on the same machine.
     * @return The name of the base which starts the group.
     */
    public String getBase() {
        return base;
    }

    /**
     * If a state of a server is included in the list of sortOut states, TimoCloud does not count the server as online server. This could mean that the server is starting, ingame, restarting, offline, ...
     * @return A list of {@link String} containing the sortOut states
     */
    public List<String> getSortOutStates() {
        return sortOutStates;
    }

}
