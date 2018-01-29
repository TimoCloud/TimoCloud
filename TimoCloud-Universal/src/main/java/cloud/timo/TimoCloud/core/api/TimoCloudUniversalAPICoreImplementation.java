package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.api.implementations.ServerGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.ServerGroup;

import java.util.Arrays;
import java.util.List;

public class TimoCloudUniversalAPICoreImplementation implements TimoCloudUniversalAPI {
    @Override
    public List<ServerGroupObject> getGroups() {
        return Arrays.asList(TimoCloudCore.getInstance().getServerManager().getGroups().stream().map(ServerGroup::toGroupObject).toArray(ServerGroupObjectBasicImplementation[]::new));
    }

    @Override
    public ServerGroupObject getGroup(String groupName) {
        return TimoCloudCore.getInstance().getServerManager().getGroupByName(groupName).toGroupObject();
    }

    @Override
    public ServerObject getServer(String serverName) {
        return TimoCloudCore.getInstance().getServerManager().getServerByName(serverName).toServerObject();
    }
}
