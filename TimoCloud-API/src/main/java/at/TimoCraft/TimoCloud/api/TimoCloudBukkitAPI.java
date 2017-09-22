package at.TimoCraft.TimoCloud.api;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Use TimoCloudBukkitAPI.getBukkitInstance() to work
 */
public interface TimoCloudBukkitAPI {

    /**
     * @return Server name
     */
    String getServerName();

    /**
     * @return Current server state
     */
    String getState();

    /**
     * Will be sent to TimoCloud BungeeCord
     *
     * @param state Any string, e.g. "ONLINE", "INGAME", "RESTARTING"
     */
    void setState(String state);

    /**
     * To enable random maps, please remove the folder base/templates/YOURGROUP and create directories like "YOURGROUP_VILLAGE" instead - they will be randomly chosen
     * @return If server (map) was randomly chosen
     */
    boolean isRandomMap();

    /**
     * @return If map was randomly chosen, this will return the map name, if you call the template "YOURGROUP_VILLAGE", it will return VILLAGE. If you don't have multiple maps, it will return the defaultMapName set in config
     */
    String getMapName();

    /**
     * @return Current server extra
     */
    String getExtra();

    /**
     * Will be sent to TimoCloud BungeeCord
     *
     * @param extra Any string, maybe a map name?
     */
    void setExtra(String extra);

    /**
     *
     * @param server The servers name
     * @return The servers state - if not found, it will return OFFLINE
     */
    String getState(String server);

    /**
     * @param server The servers name
     * @return The servers (randomly chosen?) map name
     */
    String getMapName(String server);

    /**
     *
     * @param server The servers name
     * @return The servers Extra - if not found, it will return an empty String ("")
     */
    String getExtra(String server);

    /**
     *
     * @param server The servers name
     * @return The servers state - if not found, it will return 0
     */
    int getCurrentPlayers(String server);

    /**
     *
     * @param server The servers name
     * @return The servers state - if not found, it will return 0
     */
    int getMaxPlayers(String server);

    /**
     * Returns a list of server names registered in BungeeCord by TimoCloud. For example, you can use it to send a player to a random lobby server:
     * Attention! When calling the first time, the method will return an empty list and will only add the group to the list of groups which should be queried. So call the method once you enable your plugin!
     * @param group The server group (e.g. "Lobby")
     * @return List of connected servers in this group
     */
    List<String> getOnlineServersByGroup(String group);

    /**
     * Sends a player to a random server of a given group.
     * Attention! This calls the method getOnlineServersByGroup(String group), so you have to be aware of the same thing as mentioned above: Calling the method the first time will not have any affect, just to add the group to the list of groups which should be queried. So call the method getOnlineServersByGroup(String group) when loading your plugin.
     * @param player Player you want to send
     * @param group Group you want to randomly choose a server
     */
    void sendPlayerToRandomServerOfGroup(Player player, String group);

    /**
     * @param command This command will be executed on BungeeCord
     */
    void sendCommandToBungeeCord(String command);
}
