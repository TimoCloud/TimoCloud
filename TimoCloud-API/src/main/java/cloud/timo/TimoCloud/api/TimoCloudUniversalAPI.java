package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.util.List;

/**
 * Use {@link TimoCloudAPI#getUniversalInstance()} to get an instance of this class
 */
public interface TimoCloudUniversalAPI {

    /**
     * Use this to get all groups
     * @return A list of {@link ServerGroupObject} which contains all existing groups
     */
    List<ServerGroupObject> getGroups();

    /**
     * Use this to get a group by name
     * @param groupName The groups name, case-insensitive
     * @return A {@link ServerGroupObject} which matches the given name
     */
    ServerGroupObject getGroup(String groupName);

    /**
     * Use this to get a server by name
     * @param serverName The servers name, case-insensitive
     * @return A {@link ServerObject} which matches the given name
     */
    ServerObject getServer(String serverName);
}
