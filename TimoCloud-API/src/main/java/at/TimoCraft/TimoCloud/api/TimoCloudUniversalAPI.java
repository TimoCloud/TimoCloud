package at.TimoCraft.TimoCloud.api;

import at.TimoCraft.TimoCloud.api.objects.GroupObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObject;

import java.util.List;

/**
 * Created by Timo on 11.09.17.
 */
public interface TimoCloudUniversalAPI {

    /**
     * Use this to get all groups
     * @return List of all groups
     */
    List<GroupObject> getGroups();

    /**
     * Use this to get a group by name. Case-insensitive.
     * @param groupName The groups name
     * @return A group object
     */
    GroupObject getGroup(String groupName);

    /**
     * Use this to get a server by name. Case-insensitive.
     * @param serverName
     * @return A server object
     */
    ServerObject getServer(String serverName);
}
