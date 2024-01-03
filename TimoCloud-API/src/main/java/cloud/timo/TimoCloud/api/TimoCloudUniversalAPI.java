package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.objects.*;
import cloud.timo.TimoCloud.api.objects.properties.ProxyGroupProperties;
import cloud.timo.TimoCloud.api.objects.properties.ServerGroupProperties;

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
     * @param identifier The group's name or id, case-sensitive
     * @return A {@link ServerGroupObject} corresponding to the given name
     */
    ServerGroupObject getServerGroup(String identifier);

    /**
     * Use this to get a server by name
     * @param identifier The server's name or id, case-sensitive
     * @return A {@link ServerObject} corresponding to the given name or id
     */
    ServerObject getServer(String identifier);

    /**
     * @return A collection of all servers in the network
     */
    Collection<ServerObject> getServers();

    /**
     * @return A collection of {@link ProxyGroupObject} containing all proxy groups
     */
    Collection<ProxyGroupObject> getProxyGroups();

    /**
     * Use this to get a proxy group by name
     * @param identifier The group's name or id, case-sensitive
     * @return A {@link ServerGroupObject} corresponding to the given name
     */
    ProxyGroupObject getProxyGroup(String identifier);

    /**
     * Use this to get a proxy by name
     * @param identifier The proxy's name or id
     * @return A {@link ServerObject} corresponding to the given name or id
     */
    ProxyObject getProxy(String identifier);

    /**
     * @return A collection of all proxies in the network
     */
    Collection<ProxyObject> getProxies();

    /**
     * @return A collection of all bases
     */
    Collection<BaseObject> getBases();

    /**
     * @param identifier The base's name or id, case-sensitive
     * @return A {@link BaseObject} corresponding to the given name
     */
    BaseObject getBase(String identifier);

    /**
     * @return A collection of all connected {@link CordObject}s
     */
    Collection<CordObject> getCords();

    /**
     * @param identifier The cord's name or id, case-sensitive
     * @return A {@link CordObject} corresponding to the given name
     */
    CordObject getCord(String identifier);

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

    /**
     * @return A collection of all players in the network
     */
    Collection<PlayerObject> getPlayers();

    /**
     * Give a base the permission to connect to the core.
     * @param publickey The publickey that should get permission to connect to the core (Base64 encoding)
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> registerBase(String publickey);

    /**
     * @param properties The group's properties
     * @return An APIRequestFuture being completed once the group has been created
     */
    APIRequestFuture<ServerGroupObject> createServerGroup(ServerGroupProperties properties);

    /**
     * @param properties The group's properties
     * @return An APIRequestFuture being completed once the group has been created
     */
    APIRequestFuture<ProxyGroupObject> createProxyGroup(ProxyGroupProperties properties);

    /**
     * @return The current instance's
     */
    IdentifiableObject getThisInstance();
}
