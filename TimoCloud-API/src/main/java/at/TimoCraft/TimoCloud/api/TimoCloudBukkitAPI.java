package at.TimoCraft.TimoCloud.api;

import at.TimoCraft.TimoCloud.api.objects.GroupObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObject;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Use {@link TimoCloudAPI#getBukkitInstance()} to get an instance of this class
 */
public interface TimoCloudBukkitAPI {

    /**
     *
     * @return The server you are on as ServerObject
     */
    ServerObject getThisServer();

    /**
     * @return Server name
     */
    String getServerName();

    /**
     * @deprecated Use {@link #getThisServer()} to get the {@link ServerObject} and use {@link ServerObject#getState()} to get the state
     * @return Current server state
     */
    @Deprecated
    String getState();

    /**
     * Will be sent to TimoCloud BungeeCord
     * @deprecated Use {@link #getThisServer()} to get the {@link ServerObject} and use {@link ServerObject#setState(String)} to set the state
     * @param state Any string, e.g. "ONLINE", "INGAME", "RESTARTING"
     */
    @Deprecated
    void setState(String state);

    /**
     * If a server uses a random map
     * @deprecated You don't need to know if the map has been chosen randomly
     * @return If server map has been chosen randomly
     */
    @Deprecated
    boolean isRandomMap();

    /**
     * @deprecated Use {@link #getThisServer()} to get the {@link ServerObject} and use {@link ServerObject#getMap()} to get the map
     * @return If map was randomly chosen, this will return the map name, if you call the template "YOURGROUP_VILLAGE", it will return VILLAGE. If you don't have multiple maps, it will return the defaultMapName set in config
     */
    @Deprecated
    String getMapName();

    /**
     * @deprecated Use {@link #getThisServer()} to get the {@link ServerObject} and use {@link ServerObject#getExtra()} to get the extra
     * @return Current server extra
     */
    @Deprecated
    String getExtra();

    /**
     * Will be sent to TimoCloud BungeeCord
     * @deprecated Use {@link #getThisServer()} to get the {@link ServerObject} and use {@link ServerObject#setExtra(String)} to set the extra
     * @param extra Any string, maybe a map name?
     */
    @Deprecated
    void setExtra(String extra);

    /**
     * @deprecated Use {@link TimoCloudAPI#getUniversalInstance()} to get the {@link at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI}, use {@link at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI#getServer(String)} to get wanted server and use {@link ServerObject#getState()} to get the servers state
     * @param server The servers name
     * @return The servers state - if not found, it will return OFFLINE
     */
    @Deprecated
    String getState(String server);

    /**
     * @deprecated Use {@link TimoCloudAPI#getUniversalInstance()} to get the {@link at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI}, use {@link at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI#getServer(String)} to get wanted server and use {@link ServerObject#getMap()} to get the servers map
     * @param server The servers name
     * @return The servers (randomly chosen?) map name
     */
    @Deprecated
    String getMapName(String server);

    /**
     * @deprecated Use {@link TimoCloudAPI#getUniversalInstance()} to get the {@link at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI}, use {@link at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI#getServer(String)} to get wanted server and use {@link ServerObject#getExtra()} to get the servers extra
     * @param server The servers name
     * @return The servers Extra - if not found, it will return an empty String ("")
     */
    @Deprecated
    String getExtra(String server);

    /**
     * @deprecated Use {@link TimoCloudAPI#getUniversalInstance()} to get the {@link at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI}, use {@link at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI#getServer(String)} to get wanted server and use {@link ServerObject#getCurrentPlayers()} to get the servers current player count
     * @param server The servers name
     * @return The servers state - if not found, it will return 0
     */
    @Deprecated
    int getCurrentPlayers(String server);

    /**
     * @deprecated Use {@link TimoCloudAPI#getUniversalInstance()} to get the {@link at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI}, use {@link at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI#getServer(String)} to get wanted server and use {@link ServerObject#getMaxPlayers()} to get the servers maximal player count
     * @param server The servers name
     * @return The servers state - if not found, it will return 0
     */
    @Deprecated
    int getMaxPlayers(String server);

    /**
     * @deprecated Use {@link TimoCloudAPI#getUniversalInstance()}, get the wanted group by {@link at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI#getGroup(String)} and get the servers by {@link GroupObject#getServers()}
     * Returns a list of server names registered in BungeeCord by TimoCloud. For example, you can use it to send a player to a random lobby server:
     * Attention! When calling the first time, the method will return an empty list and will only add the group to the list of groups which should be queried. So call the method once you enable your plugin!
     * @param group The server group (e.g. "Lobby")
     * @return List of connected servers in this group
     */
    @Deprecated
    List<String> getOnlineServersByGroup(String group);

    /**
     * @deprecated Use {@link TimoCloudAPI#getUniversalInstance()}, get the wanted group by using {@link at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI#getGroup(String)}, get the servers by {@link GroupObject#getServers()}, get a random server from the list, use {@link ServerObject#getName()} to get its name and send to player to the server as shown here: https://goo.gl/rMvKwE
     * Sends a player to a random server of a given group.
     * Attention! This calls the method getOnlineServersByGroup(String group), so you have to be aware of the same thing as mentioned above: Calling the method the first time will not have any affect, just to add the group to the list of groups which should be queried. So call the method getOnlineServersByGroup(String group) when loading your plugin.
     * @param player Player you want to send
     * @param group Group you want to randomly choose a server
     */
    @Deprecated
    void sendPlayerToRandomServerOfGroup(Player player, String group);

    /**
     * The given command will be executed on BungeeCord
     * @param command Without leading '/'
     */
    void sendCommandToBungeeCord(String command);
}
