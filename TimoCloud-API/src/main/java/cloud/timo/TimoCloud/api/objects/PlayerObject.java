package cloud.timo.TimoCloud.api.objects;

import java.net.InetAddress;
import java.util.UUID;

public interface PlayerObject {

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
     * @return The last time the player was online, in form of a timestamp (milliseconds). If the player is currently online, this will return 0. If the last online time can't be found in the database, this will return -1
     */
    long getLastOnline();

}
