package cloud.timo.TimoCloud.api.objects;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * A proxy object stands for a BungeeCord instance
 */

public interface ProxyObject {

    /**
     * @return The proxy's name
     */
    String getName();

    /**
     * @return The group the proxy is part of
     */
    ProxyGroupObject getGroup();

    /**
     * The proxy's current player count
     * @return An integer containing the amount of players currently online
     */
    int getOnlinePlayerCount();

    /**
     * The proxy's maximum player count
     * @return An integer containing the amount of maximum online players
     */
    int getMaxPlayerCount();

    /**
     * @return The proxy's IP address and port players can connect to
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
     * @param command *Without leading /*
     */
    void executeCommand(String command);

    /**
     * Stops the server
     */
    void stop();
}
