package at.TimoCraft.TimoCloud.api;

import at.TimoCraft.TimoCloud.api.objects.GroupObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObject;

import java.util.List;

/**
 * Use {@link TimoCloudAPI#getUniversalInstance()} to get an instance of this class
 */
public interface TimoCloudUniversalAPI {

    /**
     * Use this to get all groups
     * @return A list of {@link GroupObject} which contains all existing groups
     */
    List<GroupObject> getGroups();

    /**
     * Use this to get a group by name
     * @param groupName The groups name, case-insensitive
     * @return A {@link GroupObject} which matches the given name
     */
    GroupObject getGroup(String groupName);

    /**
     * Use this to get a server by name
     * @param serverName The servers name, case-insensitive
     * @return A {@link ServerObject} which matches the given name
     */
    ServerObject getServer(String serverName);
}
