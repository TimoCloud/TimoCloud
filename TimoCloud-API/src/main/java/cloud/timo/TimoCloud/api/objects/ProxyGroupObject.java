package cloud.timo.TimoCloud.api.objects;

import java.util.List;

public interface ProxyGroupObject {

    /**
     * @return The group's name
     */
    String getName();

    /**
     * @return All starting/running proxies of the group
     */
    List<ProxyObject> getProxies();

    /**
     * The total amount of players online on all proxies of this group
     */
    int getOnlinePlayerCount();

    /**
     * The total maximum amount of players who can be online on proxies of this group
     */
    int getMaxPlayerCount();

    /**
     * The maximum player amount who can be online on one cord
     */
    int getMaxPlayerCountPerProxy();

    /**
     * The amount of player slots TimoCloud will keep free. If there are not enough free slots, a new cord will be started.
     */
    int getKeepFreeSlots();

    /**
     * Maximum of ram a server of this group may use
     */
    int getRam();

    /**
     * @return The cord's motd
     */
    String getMotd();

    /**
     * If a group is static, servers will not be reset after restart. A static group can only start 1 server.
     */
    boolean isStatic();

    /**
     * The group's priority. Groups with higher priorities will be started sooner than groups with lower priorities.
     */
    int getPriority();

    /**
     * @return  The server groups which are proxied by this BungeeCord instance.
     */
    List<ServerGroupObject> getServerGroups();

    /**
     * If two groups are started by the same base, you can assume that they are running on the same machine.
     */
    String getBase();

    /**
     * The ProxyChooseStrategy tells TimoCloudCord what proxy it should choose when a player wants to join a proxy group
     * @return BALANCE, FILL or RANDOM
     */
    ProxyChooseStrategy getProxyChooseStrategy();

    /**
     * If using multiple proxies and TimoCloudCord, the hostnames specify which hostnames belong to this proxy group
     */
    List<String> getHostNames();

}
