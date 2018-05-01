package cloud.timo.TimoCloud.api.objects;

import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

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
     */
    String getExtra();

    /**
     * An extra is a custom value users can set per API. An example use case would be 'Teaming' or 'NoTeaming'
     */
    void setExtra(String extra);

    /**
     * If a server's map is assigned randomly, the map name will be the part of the map directory's name after the '_'. E.g. 'BedWars_VILLAGE' becomes 'VILLAGE'. If no random maps exists, the 'defaultMapName' property from config.yml will be used.
     */
    String getMap();

    /**
     * The server's MOTD (= message of the day)
     */
    String getMotd();

    /**
     * @return A list with all online players
     */
    List<PlayerObject> getOnlinePlayers();

    /**
     * The server's current online player count
     */
    int getOnlinePlayerCount();

    /**
     * The server's maximum player count
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

    /**
     * Send a plugin message to the server
     * @param message The message which shall be sent
     */
    void sendPluginMessage(PluginMessage message);
}
