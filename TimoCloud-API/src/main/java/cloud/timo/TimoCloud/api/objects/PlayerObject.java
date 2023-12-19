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
     * @return An APIRequestFuture with Boolean specifying if the player has been sent to the server successfully
     */
    APIRequestFuture<Boolean> sendToServer(ServerObject serverObject);

    /**
     * @param message the message which is sent to the player
     * @return An APIRequestFuture with Boolean specifying if the message successfully sent to the Player
     */
    APIRequestFuture<Boolean> sendMessage(String message);

}
