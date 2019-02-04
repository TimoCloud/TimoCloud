package cloud.timo.TimoCloud.api.internal.links;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerObject;

public class ServerObjectLink extends IdentifiableLink<ServerObject> {

    public ServerObjectLink(ServerObject serverObject) {
        this(serverObject.getId(), serverObject.getName());
    }

    public ServerObjectLink(String id, String name) {
        super(id, name);
    }

    @Override
    ServerObject findTarget() {
        return TimoCloudAPI.getUniversalAPI().getServer(getId());
    }

}
