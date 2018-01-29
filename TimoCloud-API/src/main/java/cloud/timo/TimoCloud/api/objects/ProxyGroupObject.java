package cloud.timo.TimoCloud.api.objects;

import java.util.List;

public interface ProxyGroupObject {

    /**
     * @return The group's name
     */
    String getName();

    /**
     * Returns all starting/running proxies of the group
     * @return A list of {@link ProxyObject} which contains all proxy objects
     */
    List<ProxyObject> getProxies();

    /**
     * The total amount of players online on all proxies of this group
     * @return An int containing the total online players count
     */
    int getOnlinePlayerCount();

    /**
     * The total maximum amount of players who can be online on proxies of this group
     * @return An int containing the overall maximum player count
     */
    int getMaxPlayerCount();

    /**
     * The maximum player amount who can be online on one proxy
     * @return An int containing the maximum player account per proxy
     */
    int getMaxPlayerCountPerProxy();

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

}
