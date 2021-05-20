package cloud.timo.TimoCloud.api.objects;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;

import java.net.InetAddress;
import java.util.UUID;

public interface PlayerObject extends IdentifiableObject {

    /**
     * @return The player's Minecraft name
     */
    String getName();

    /**
     * @return The player's Minecraft UUID
     */
    UUID getUuid();

    /**
     * @return The server the player currently is connected to
     */
    ServerObject getServer();

    /**
     * @return The proxy (BungeeCord) the player currently is connected to
     */
    ProxyObject getProxy();

    /**
     * @return The player's IP address
     */
    InetAddress getIpAddress();

    /**
     * Normally, a Player is always online if you are able to get its PlayerObject. However, when the Player is currently disconnecting, this will return false
     */
    boolean isOnline();

    /**
     * @param serverObject The server the player shall be sent to
     * @return A future being completed when the player is sent to the server
     */
    APIRequestFuture<Void> sendToServer(ServerObject serverObject);

}
