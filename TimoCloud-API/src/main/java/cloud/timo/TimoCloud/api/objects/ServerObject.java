package cloud.timo.TimoCloud.api.objects;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddress;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import cloud.timo.TimoCloud.api.objects.log.LogFractionObject;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;

public interface ServerObject extends IdentifiableObject {

    /**
     * @return The server's name
     */
    String getName();

    /**
     * @return The server's unique id
     */
    String getId();

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
     *
     * @param state The state, e.g. 'INGAME' or 'FULL'
     * @return A future being completed when the state was changed
     */
    APIRequestFuture<Void> setState(String state);

    /**
     * An extra is a custom value users can set per API. An example use case would be 'Teaming' or 'NoTeaming'
     */
    String getExtra();

    /**
     * An extra is a custom value users can set per API. An example use case would be 'Teaming' or 'NoTeaming'
     *
     * @return A future being completed when the extra was changed
     */
    APIRequestFuture<Void> setExtra(String extra);

    /**
     * If a server's map is assigned randomly, the map name will be the part of the map directory's name after the '_'. E.g. 'BedWars_VILLAGE' becomes 'VILLAGE'. If no random maps exists, the 'defaultMapName' property from config.yml will be used.
     */
    String getMap();

    /**
     * The server's MOTD (= message of the day)
     */
    String getMotd();

    /**
     * @return A collection of all online players
     */
    Collection<PlayerObject> getOnlinePlayers();

    /**
     * The server's current online player count
     */
    int getOnlinePlayerCount();

    /**
     * The server's maximum player count
     */
    int getMaxPlayerCount();

    /**
     * Returns the base the server has been started by
     */
    BaseObject getBase();

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
     * @return The address plugin protocol to this server shall be addressed to
     */
    MessageClientAddress getMessageAddress();

    /**
     * Executes the given command as ConsoleSender on the server
     *
     * @param command <b>Without leading /</b>
     * @return A future being completed when the command was executed
     */
    APIRequestFuture<Void> executeCommand(String command);

    /**
     * Stops the server
     *
     * @return A future being completed when the server was stopped
     */
    APIRequestFuture<Void> stop();

    /**
     * Send a plugin message to the server
     *
     * @param message The message which shall be sent
     */
    void sendPluginMessage(PluginMessage message);

    /**
     * @param startTime The timestamp at which the record of the log should start (0 if you want all log entries since the server's start)
     * @param endTime   The timestamp at which the record of the log should end
     * @return A LogFractionObject containing all log entries within the given slot of time
     */
    APIRequestFuture<LogFractionObject> getLogFraction(long startTime, long endTime);

    /**
     * @param startTime The timestamp at which the record of the log should start (0 if you want all log entries since the server's start)
     * @return A LogFractionObject containing all log entries after the given start time
     */
    APIRequestFuture<LogFractionObject> getLogFraction(long startTime);
}
