package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.objects.*;

import java.util.Collection;
import java.util.UUID;

/**
 * Use {@link TimoCloudAPI#getUniversalAPI()} to get an instance of this class
 */
public interface TimoCloudUniversalAPI {

    /**
     * @return A collection of {@link ServerGroupObject} which contains all server groups
     */
    Collection<ServerGroupObject> getServerGroups();

    /**
     * Use this to get a server group by name
     * @param groupName The group's name, case-insensitive
     * @return A {@link ServerGroupObject} corresponding to the given name
     */
    ServerGroupObject getServerGroup(String groupName);

    /**
     * Use this to get a server by name
     * @param identifier The server's name or id, case-insensitive
     * @return A {@link ServerObject} corresponding to the given name or id
     */
    ServerObject getServer(String identifier);

    /**
     * @return A collection of {@link ProxyGroupObject} containing all proxy groups
     */
    Collection<ProxyGroupObject> getProxyGroups();

    /**
     * Use this to get a proxy group by name
     * @param groupName The group's name, case-insensitive
     * @return A {@link ServerGroupObject} corresponding to the given name
     */
    ProxyGroupObject getProxyGroup(String groupName);

    /**
     * Use this to get a proxy by name
     * @param identifier The proxy's name or id
     * @return A {@link ServerObject} corresponding to the given name or id
     */
    ProxyObject getProxy(String identifier);

    /**
     * @return A collection of all bases
     */
    Collection<BaseObject> getBases();

    /**
     * @param name The base's name, case-insensitive
     * @return A {@link BaseObject} corresponding to the given name
     */
    BaseObject getBase(String name);

    /**
     * @return A collection of all connected {@link CordObject}s
     */
    Collection<CordObject> getCords();

    /**
     * @param name The cord's name, case-insensitive
     * @return A {@link CordObject} corresponding to the given name
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
