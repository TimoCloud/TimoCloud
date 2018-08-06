package cloud.timo.TimoCloud.api.objects;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddress;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * A cord object stands for a BungeeCord instance
 */

public interface ProxyObject {

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
     * @return The server's IP address
     */
    InetAddress getIpAddress();

    /**
     * @return The server's port
     */
    int getPort();

    /**
     * @return The address plugin messages to this proxy shall be addressed to
     */
    MessageClientAddress getMessageAddress();

    /**
     * Executes the given command as ConsoleSender on the server
     * @param command Without leading '/'
     * @return A future being completed when the command was executed
     */
    APIRequestFuture executeCommand(String command);

    /**
     * Stops the server
     * @return A future being completed when the server was stopped
     */
    APIRequestFuture stop();

    /**
     * Send a plugin message to the proxy
     * @param message The message which shall be sent
     */
    void sendPluginMessage(PluginMessage message);
}
