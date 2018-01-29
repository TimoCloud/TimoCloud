package cloud.timo.TimoCloud.api.objects;

import java.util.List;

public interface ServerGroupObject {

    /**
     * @return The group's name
     */
    String getName();

    /**
     * Returns all starting/running servers of the group
     * @return A list of {@link ServerObject} which contains all server objects
     */
    List<ServerObject> getServers();

    /**
     * The OnlineAmount or Keep-Online-Amount is the amount of servers TimoCloud wants to always be online. Called 'onlineAmount' in the groups.yml
     * Attention: This is NOT the amount of online servers, but the minimum amount of servers TimoCloud is keeping online
     * @return An int containing the OnlineAmount
     */
    int getOnlineAmount();

    /**
     * The MaxAmount specifies the maximal amount of servers TimoCloud keeps online at the same time - no matter what onlineAmount says
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
     * If a base is assigned to the group, this will return its name. If the dynamic automatic-base-selection system is used, this will return null.
     * @return A String if the base is set statically, else null
     */
    String getBase();

    /**
     * If a state of a server is included in the list of sortOut states, TimoCloud does not count the server as online server. This could mean that the server is starting, ingame, restarting, offline, ...
     * @return A list of {@link String} containing the sortOut states
     */
    List<String> getSortOutStates();

}
