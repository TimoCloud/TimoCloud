package cloud.timo.TimoCloud.api.internal.links;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerGroupObjectLink extends IdentifiableLink<ServerGroupObject> {

    public ServerGroupObjectLink(ServerGroupObject serverGroupObject) {
        this(serverGroupObject.getId(), serverGroupObject.getName());
    }

    public ServerGroupObjectLink(String id, String name) {
        super(id, name);
    }

    @Override
    ServerGroupObject findTarget() {
        return TimoCloudAPI.getUniversalAPI().getServerGroup(getId());
    }

}
