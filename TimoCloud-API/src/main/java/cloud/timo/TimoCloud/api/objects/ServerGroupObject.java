package cloud.timo.TimoCloud.api.objects;

import java.util.List;

public interface ServerGroupObject {

    /**
     * @return The group's name
     */
    String getName();

    /**
     * @return A list of {@link ServerObject} which contains all starting or running servers
     */
    List<ServerObject> getServers();

    /**
     * The OnlineAmount or Keep-Online-Amount is the amount of servers TimoCloud wants to always be online. Called 'onlineAmount' in the groups.yml
     * Attention: This is NOT the amount of online servers, but the minimum amount of servers TimoCloud is keeping online
     */
    int getOnlineAmount();

    /**
     * The MaxAmount specifies the maximal amount of servers TimoCloud keeps online at the same time - no matter what onlineAmount says
     */
    int getMaxAmount();

    /**
     * Maximum of ram a server of this group may use
     */
    int getRam();

    /**
     * If a group is static, servers will not be reset after restart. A static group can only start 1 server.
     */
    boolean isStatic();

    /**
     * If a base is assigned to the group, this will return its name. If the dynamic automatic-base-selection system is used, this will return null.
     * @return A String if the base has been set statically, else null
     */
    String getBase();

    /**
     * If a state of a server is included in the list of sortOut states, TimoCloud does not count the server as online server. This could mean that the server is starting, ingame, restarting, offline, ...
     */
    List<String> getSortOutStates();

}
