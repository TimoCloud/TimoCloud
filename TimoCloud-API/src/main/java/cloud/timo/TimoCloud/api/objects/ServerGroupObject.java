package cloud.timo.TimoCloud.api.objects;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;

import java.util.Collection;

public interface ServerGroupObject extends IdentifiableObject {

    /**
     * @return The group's name
     */
    String getName();

    /**
     * @return A list of {@link ServerObject} which contains all starting or running servers
     */
    Collection<ServerObject> getServers();

    /**
     * The OnlineAmount or Keep-Online-Amount is the amount of servers TimoCloud wants to always be online. Called 'onlineAmount' in the groups.yml
     * Attention: This is NOT the amount of online servers, but the minimum amount of servers TimoCloud is keeping online
     */
    int getOnlineAmount();

    /**
     * Changes the group's maximum player count
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setOnlineAmount(int value);

    /**
     * The MaxAmount specifies the maximal amount of servers TimoCloud keeps online at the same time - no matter what onlineAmount says
     */
    int getMaxAmount();

    /**
     * Changes the group's maximum server amount
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setMaxAmount(int value);

    /**
     * Maximum of ram a server of this group may use in megabytes
     */
    int getRam();

    /**
     * Changes the group's ram
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setRam(int value);

    /**
     * If a group is static, servers will not be reset after restart. A static group can only start 1 server.
     */
    boolean isStatic();

    /**
     * Changes whether the group is static or not.
     * <b>Please note that changing this should be done with care. In order to avoid problems, the group should be restarted immediately after doing so. Please note that the template directory is different for static and non-static groups.</b>
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setStatic(boolean value);

    /**
     * If a base is assigned to the group, this will return its name. If the dynamic automatic-base-selection system is used, this will return null.
     * @return A String if the base has been set statically, else null
     */
    BaseObject getBase();

    /**
     * Changes the base servers of this group shall be started by
     * @param value If null, a base will be selected dynamically whenever a new server gets started
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setBase(BaseObject value);

    /**
     * If a state of a server is included in the list of sortOut states, TimoCloud does not consider the server as active.
     * This influences the server starting behavior: When the online amount is 3, for example, TimoCloud will count the group's <b>active</b> servers. If there is just 1 <b>active</b> server, 2 new ones will be started
     * Examples for such states would be STARTING, INGAME, RESTARTING, OFFLINE, ...
     */
    Collection<String> getSortOutStates();

    /**
     * Changes the sort-out states
     * @return A future being completed when the value was changed
     */
    APIRequestFuture<Void> setSortOutStates(Collection<String> value);

    /**
     * Attention: This permanently deletes the server group from the network
     * @return A future being completed when the group was deleted
     */
    APIRequestFuture<Void> delete();
}
