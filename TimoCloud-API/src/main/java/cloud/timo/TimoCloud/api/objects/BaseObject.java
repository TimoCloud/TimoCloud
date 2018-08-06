package cloud.timo.TimoCloud.api.objects;

import java.net.InetAddress;
import java.util.Collection;

public interface BaseObject {

    /**
     * @return The base's name. Usually in the format BASE-1, ...
     */
    String getName();

    /**
     * @return The base's ip address
     */
    InetAddress getIpAddress();

    /**
     * @return The base's server's current average CPU load
     */
    Double getCpuLoad();

    /**
     * @return The amount of usable RAM in megabytes
     */
    int getAvailableRam();

    /**
     * @return The maximum amount of RAM servers/proxies on this base may use in total in megabytes
     */
    int getMaxRam();

    /**
     * @return Whether the base is currently connected to the Core
     */
    boolean isConnected();

    /**
     * @return Whether the base is ready to start instances. This depends on whether it is connected to the core, its cpu load and available ram
     */
    boolean isReady();

    /**
     * @return A collection of all servers started on this base
     */
    Collection<ServerObject> getServers();

    /**
     * @return A collection of all proxies started on this base
     */
    Collection<ProxyObject> getProxies();

}
