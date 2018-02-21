package cloud.timo.TimoCloud.api.objects;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * A TimoCloudCord instance is a proxy which connects players to BungeeCord instances - depending on different integrated algorithms.
 * It can be helpful to balance players when starting proxies dynamically.
 */
public interface CordObject {

    /**
     * @return The Cord's name
     */
    String getName();

    /**
     * @return The Cord's socket address - players can connect to the returned ip address and port
     */
    InetSocketAddress getSocketAddress();

    /**
     * @return The Cord's ip address
     */
    InetAddress getIpAddress();

    /**
     * @return The Cord's server port players can connect to
     */
    int getPort();

    /**
     * Normally, a Cord is always connected if you are able to get its CordObject. However, when the Cord is currently disconnecting, this will return false
     */
    boolean isConnected();

}
