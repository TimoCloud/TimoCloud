package cloud.timo.TimoCloud.api.objects;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddress;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;
import cloud.timo.TimoCloud.api.objects.log.LogFractionObject;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * A cord object stands for a BungeeCord instance
 */

public interface ProxyObject extends IdentifiableObject {

    /**
     * @return The cord's name
     */
    String getName();

    /**
     * @return The proxy's unique id
     */
    String getId();

    /**
     * @return The group the cord is part of
     */
    ProxyGroupObject getGroup();

    /**
     * @return A list with all online players
     */
    List<PlayerObject> getOnlinePlayers();

    /**
     * @return The cord's current player count
     */
    int getOnlinePlayerCount();

    /**
     * @return The base the proxy has been started by
     */
    BaseObject getBase();

    /**
     * @return The cord's IP address and port players can connect to
     */
    InetSocketAddress getSocketAddress();

    /**
     * @return The proxy's IP address
     */
    InetAddress getIpAddress();

    /**
     * @return The proxy's port
     */
    int getPort();

    /**
     * @return The address plugin protocol to this proxy shall be addressed to
     */
    MessageClientAddress getMessageAddress();

    /**
     * Executes the given command as ConsoleSender on the proxy
     * @param command Without leading '/'
     * @return A future being completed when the command was executed
     */
    APIRequestFuture<Void> executeCommand(String command);

    /**
     * Stops the proxy
     * @return A future being completed when the proxy was stopped
     */
    APIRequestFuture<Void> stop();

    /**
     * create Cloudflare records for the proxy only use if you have unregistered first
     * @return A future being completed when the records were created
     */
    APIRequestFuture<Void> createCloudflareRecords();

    /**
     * delete Cloudflare records for the proxy
     * @return A future being completed when the records were deleted
     */
    APIRequestFuture<Void> deleteCloudflareRecords();

    /**
     * Send a plugin message to the proxy
     * @param message The message which shall be sent
     */
    void sendPluginMessage(PluginMessage message);

    /**
     * @param startTime The timestamp at which the record of the log should start (0 if you want all log entries since the proxy's start)
     * @param endTime The timestamp at which the record of the log should end
     * @return A LogFractionObject containing all log entries within the given slot of time
     */
    APIRequestFuture<LogFractionObject> getLogFraction(long startTime, long endTime);

    /**
     * @param startTime The timestamp at which the record of the log should start (0 if you want all log entries since the proxy's start)
     * @return A LogFractionObject containing all log entries after the given start time
     */
    APIRequestFuture<LogFractionObject> getLogFraction(long startTime);
}
