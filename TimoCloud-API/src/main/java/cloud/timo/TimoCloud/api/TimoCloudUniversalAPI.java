package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.objects.*;

import java.util.List;
import java.util.UUID;

/**
 * Use {@link TimoCloudAPI#getUniversalAPI()} to get an instance of this class
 */
public interface TimoCloudUniversalAPI {

    /**
     * @return A list of {@link ServerGroupObject} which contains all server groups
     */
    List<ServerGroupObject> getServerGroups();

    /**
     * Use this to get a server group by name
     * @param groupName The groups name, case-insensitive
     * @return A {@link ServerGroupObject} which matches the given name
     */
    ServerGroupObject getServerGroup(String groupName);

    /**
     * Use this to get a server by name
     * @param serverName The server's name, case-insensitive
     * @return A {@link ServerObject} which matches the given name
     */
    ServerObject getServer(String serverName);

    /**
     * @return A list of {@link ProxyGroupObject} which contains all proxy groups
     */
    List<ProxyGroupObject> getProxyGroups();

    /**
     * Use this to get a proxy group by name
     * @param groupName The groups name, case-insensitive
     * @return A {@link ServerGroupObject} which matches the given name
     */
    ProxyGroupObject getProxyGroup(String groupName);

    /**
     * Use this to get a proxy by name
     * @param proxyName The proxy's name, case-insensitive
     * @return A {@link ServerObject} which matches the given name
     */
    ProxyObject getProxy(String proxyName);

    /**
     * @return Returns a list of all connected {@link CordObject}s
     */
    List<CordObject> getCords();

    /**
     * @param name The cord's name, case-insensitive
     * @return A {@link CordObject} which matches the given name
     */
    CordObject getCord(String name);

    /**
     * @param uuid The player's Minecraft UUID
     * @return If the player is online, this will return a PlayerObject, else null
     */
    PlayerObject getPlayer(UUID uuid);

    /**
     * @param name The player's Minecraft name
     * @return If the player is online, this will return a PlayerObject, else null
     */
    PlayerObject getPlayer(String name);
}
