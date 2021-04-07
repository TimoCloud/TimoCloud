package cloud.timo.TimoCloud.api.objects;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;

import java.util.Collection;

public interface ProxyGroupObject extends IdentifiableObject {

    /**
     * @return The group's name
     */
    String getName();

    /**
     * @return All starting/running proxies of the group
     */
    Collection<ProxyObject> getProxies();

    /**
     * @return The total amount of players online on all proxies of this group
     */
    int getOnlinePlayerCount();

    /**
     * @return The total maximum amount of players who can be online on proxies of this group
     */
    int getMaxPlayerCount();

    /**
     * Changes the group's maximum player count
     *
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setMaxPlayerCount(int value);

    /**
     * @return The maximum player amount who can be online on one cord
     */
    int getMaxPlayerCountPerProxy();

    /**
     * Changes the group's maximum player count per proxy
     *
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setMaxPlayerCountPerProxy(int value);

    /**
     * @return The amount of player slots TimoCloud will keep free. If there are not enough free slots, a new proxy will be started.
     */
    int getKeepFreeSlots();

    /**
     * Changes the group's amount of slots which should be kept free
     *
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setKeepFreeSlots(int value);

    /**
     * @return The minimum amount of proxies of this group that will be online
     */
    int getMinAmount();

    /**
     * Changes the group's minimum instance amount
     *
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setMinAmount(int value);

    /**
     * @return The maximum amount of proxies of this group that may be online
     */
    int getMaxAmount();

    /**
     * Changes the group's maximum instance amount
     *
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setMaxAmount(int value);

    /**
     * @return Maximum of ram a proxy of this group may use in megabytes
     */
    int getRam();

    /**
     * Changes the group's ram
     *
     * @param value The maximum amount of ram a proxy of this group may use
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setRam(int value);

    /**
     * @return The proxies' motd
     */
    String getMotd();

    /**
     * Changes the group's motd
     *
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setMotd(String value);

    /**
     * If a group is static, servers will not be reset after restart. A static group can only start 1 server.
     */
    boolean isStatic();

    /**
     * Changes whether the group is static or not.
     * <b>Please note that changing this should be done with care. In order to avoid problems, the group should be restarted immediately after doing so. Please note that the template directory is different for static and non-static groups.</b>
     *
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setStatic(boolean value);

    /**
     * @return The group's priority. Groups with higher priorities will be started before groups with lower priorities.
     */
    int getPriority();

    /**
     * Changes the group's priority
     *
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setPriority(int value);

    /**
     * @return The server groups connections to which are proxied by this BungeeCord instance.
     */
    Collection<ServerGroupObject> getServerGroups();

    /**
     * @return The base proxies of this group will be started by. <b>If you let TimoCloud choose a base dynamically, this will return null!</b>
     */
    BaseObject getBase();

    /**
     * Changes the base proxies of this group shall be started by
     *
     * @param value If null, a base will be selected dynamically whenever a new proxy gets started
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setBase(BaseObject value);

    /**
     * The ProxyChooseStrategy tells TimoCloudCord what proxy it should choose when a player wants to join a proxy group
     *
     * @return BALANCE, FILL or RANDOM
     */
    ProxyChooseStrategy getProxyChooseStrategy();

    /**
     * Changes the group's proxy choose strategy
     *
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setProxyChooseStrategy(ProxyChooseStrategy value);

    /**
     * If using multiple proxies and TimoCloudCord, the hostnames specify which hostnames belong to this proxy group
     */
    Collection<String> getHostNames();

    /**
     * Changes the group's hostnames
     *
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setHostNames(Collection<String> value);

    /**
     * Attention: This permanently deletes the proxy group from the network
     *
     * @return A future being completed when the group was deleted
     */
    APIRequestFuture<Void> delete();

    /**
     * @return A String of all changeable java start parameters
     */
    Collection<String> getJavaParameters();

    /**
     * Changes the java start parameters
     *
     * @return A future being completed when the parameters was changed
     */
    APIRequestFuture<Void> setJavaParameters(Collection<String> value);

    /**
     * @return A String of The JDKPath using to Start the Proxy
     */
    String getJrePath();
}
