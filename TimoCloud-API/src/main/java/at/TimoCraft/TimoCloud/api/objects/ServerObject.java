package at.TimoCraft.TimoCloud.api.objects;

import at.TimoCraft.TimoCloud.api.implementations.GroupObjectBasicImplementation;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface ServerObject {

    /**
     * @return The servers name
     */
    String getName();

    /**
     * @return The group the server is part of
     */
    GroupObject getGroup();

    /**
     * Use this method if you only need the groups name - it is more performant than retrieving the group object by name as {@link #getGroup()} does
     * @return The name of the group the server is part of.
     */
    String getGroupName();

    /**
     * @return The servers state
     */
    String getState();

    /**
     * Sets the servers state and sends it to TimoCloud BungeeCord
     * @param state The state, e.g. 'INGAME' or 'FULL'
     */
    void setState(String state);

    /**
     * An extra is a custom value users can set per API. An example use case would be 'Teaming' or 'NoTeaming'
     * @return The servers extra
     */
    String getExtra();

    /**
     * An extra is a custom value users can set per API. An example use case would be 'Teaming' or 'NoTeaming'
     * @param extra A string containing the extra
     */
    void setExtra(String extra);

    /**
     * If a servers map is assigned randomly, the map name will be the part of the map directories name after the '_'. E.g. 'BedWars_VILLAGE' becomes 'VILLAGE'. If no random maps exist, the 'defaultMapName' property from config.yml will be used.
     * @return The servers map name
     */
    String getMap();

    /**
     * The servers MOTD (= message of the day) can be faked by manipulating ServerListPingEvent
     * @return A string containing the MOTD
     */
    String getMotd();

    /**
     * The servers current player count can be faked by manipulating ServerListPingEvent
     * @return An integer containing the amount of players currently online
     */
    int getCurrentPlayers();

    /**
     * The servers maximum player count can be faked by manipulating ServerListPingEvent
     * @return An integer containing the amount of maximum online players
     */
    int getMaxPlayers();

    /**
     * @return The servers IP address and port players can connect to
     */
    InetSocketAddress getSocketAddress();

    /**
     * @return The servers IP address
     */
    InetAddress getIpAddress();

    /**
     * @return The servers port
     */
    int getPort();

    /**
     * @return Returns if the servers current state is contained in its groups sortOutStates list
     */
    boolean isSortedOut();


    /**
     * Executes the given command as ConsoleSender on the server
     * @param command *Without leading /*
     */
    void executeCommand(String command);

    /**
     * Stops the server
     */
    void stop();
}
