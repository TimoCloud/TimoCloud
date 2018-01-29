package cloud.timo.TimoCloud.api.objects;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface ServerObject {

    /**
     * @return The server's name
     */
    String getName();

    /**
     * @return The group the server is part of
     */
    ServerGroupObject getGroup();

    /**
     * @return The server's state
     */
    String getState();

    /**
     * Sets the server's state and sends it to TimoCloud BungeeCord
     * @param state The state, e.g. 'INGAME' or 'FULL'
     */
    void setState(String state);

    /**
     * An extra is a custom value users can set per API. An example use case would be 'Teaming' or 'NoTeaming'
     * @return The server's extra
     */
    String getExtra();

    /**
     * An extra is a custom value users can set per API. An example use case would be 'Teaming' or 'NoTeaming'
     * @param extra A string containing the extra
     */
    void setExtra(String extra);

    /**
     * If a server's map is assigned randomly, the map name will be the part of the map directory's name after the '_'. E.g. 'BedWars_VILLAGE' becomes 'VILLAGE'. If no random maps exists, the 'defaultMapName' property from config.yml will be used.
     * @return The server's map name
     */
    String getMap();

    /**
     * The server's MOTD (= message of the day)
     * @return A string containing the MOTD
     */
    String getMotd();

    /**
     * The server's current player count
     * @return An integer containing the amount of players currently online
     */
    int getOnlinePlayerCount();

    /**
     * The server's maximum player count
     * @return An integer containing the amount of maximum online players
     */
    int getMaxPlayerCount();

    /**
     * Returns the name of the base the server has been started by
     */
    String getBase();

    /**
     * @return The server's IP address and port players can connect to
     */
    InetSocketAddress getSocketAddress();

    /**
     * @return The server's IP address
     */
    InetAddress getIpAddress();

    /**
     * @return The server's port
     */
    int getPort();

    /**
     * @return Returns if the server's current state is contained in its group's sortOutStates list
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
