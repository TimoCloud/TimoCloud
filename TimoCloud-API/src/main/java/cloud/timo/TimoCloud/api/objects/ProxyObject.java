package cloud.timo.TimoCloud.api.objects;

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
     * Returns the name of the base the proxy has been started by
     */
    String getBase();

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
     * Executes the given command as ConsoleSender on the server
     * @param command Without leading '/'
     */
    void executeCommand(String command);

    /**
     * Stops the server
     */
    void stop();

    /**
     * Send a plugin message to the proxy
     * @param message The message which shall be sent
     */
    void sendPluginMessage(PluginMessage message);
}
