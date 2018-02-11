package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.util.List;

/**
 * Use {@link TimoCloudAPI#getUniversalInstance()} to get an instance of this class
 */
public interface TimoCloudUniversalAPI {

    /**
     * @return A list of {@link ServerGroupObject} which contains all server groups
     */
    List<ServerGroupObject> getServerGroups();

    /**
     * Use this to get a server group by name
     * @param groupName The groups name, case-insensitive
     * @return A {@link ServerGroupObject} which matches the given name
     */
    ServerGroupObject getServerGroup(String groupName);

    /**
     * Use this to get a server by name
     * @param serverName The server's name, case-insensitive
     * @return A {@link ServerObject} which matches the given name
     */
    ServerObject getServer(String serverName);

    /**
     * @return A list of {@link ProxyGroupObject} which contains all proxy groups
     */
    List<ProxyGroupObject> getProxyGroups();

    /**
     * Use this to get a proxy group by name
     * @param groupName The groups name, case-insensitive
     * @return A {@link ServerGroupObject} which matches the given name
     */
    ProxyGroupObject getProxyGroup(String groupName);

    /**
     * Use this to get a proxy by name
     * @param proxyName The proxy's name, case-insensitive
     * @return A {@link ServerObject} which matches the given name
     */
    ProxyObject getProxy(String proxyName);
}
