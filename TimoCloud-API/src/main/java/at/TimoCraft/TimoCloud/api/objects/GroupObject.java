package at.TimoCraft.TimoCloud.api.objects;

import java.util.List;

public interface GroupObject {
    /**
     * Returns all starting/running servers of the group
     * @return A list of {@link ServerObject} which contains all server objects
     */
    List<ServerObject> getServers();

    /**
     * @return The groups name
     */
    String getName();

    /**
     * The StartupAmount or Keep-Online-Amount is the amount of servers TimoCloud wants to always be online. Called 'onlineAmount' in the groups.yml
     * @return An int containing the StartupAmount
     */
    int getStartupAmount();

    /**
     * The MaxAmount specifies the maximal amount of servers TimoCloud keeps online at the same time - no matter what startupAmount says
     * @return An int containing the MaxAmount
     */
    int getMaxAmount();

    /**
     * Maximum of ram a server of this group may use
     * @return An int containing the ram in MB (megabytes)
     */
    int getRam();

    /**
     * If a group is static, servers will not be reset after restart. A static group can only start 1 server.
     * @return A boolean which tells you if the
     */
    boolean isStatic();

    /**
     * If two groups are started by the same base, you can assume that they are running on the same machine.
     * @return The name of the base which starts the group.
     */
    String getBase();

    /**
     * If a state of a server is included in the list of sortOut states, TimoCloud does not count the server as online server. This could mean that the server is starting, ingame, restarting, offline, ...
     * @return A list of {@link String} containing the sortOut states
     */
    List<String> getSortOutStates();

}
