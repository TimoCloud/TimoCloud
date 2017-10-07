package at.TimoCraft.TimoCloud.bungeecord.api;

import at.TimoCraft.TimoCloud.api.TimoCloudUniversalAPI;
import at.TimoCraft.TimoCloud.api.objects.GroupObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObject;
import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.objects.Group;

import java.util.Arrays;
import java.util.List;

public class TimoCloudUniversalAPIBungeeImplementation implements TimoCloudUniversalAPI {
    @Override
    public List<GroupObject> getGroups() {
        return Arrays.asList(TimoCloud.getInstance().getServerManager().getGroups().stream().map(Group::toGroupObject).toArray(GroupObject[]::new));
    }

    @Override
    public GroupObject getGroup(String groupName) {
        return TimoCloud.getInstance().getServerManager().getGroupByName(groupName).toGroupObject();
    }

    @Override
    public ServerObject getServer(String serverName) {
        return TimoCloud.getInstance().getServerManager().getServerByName(serverName).toServerObject();
    }
}
