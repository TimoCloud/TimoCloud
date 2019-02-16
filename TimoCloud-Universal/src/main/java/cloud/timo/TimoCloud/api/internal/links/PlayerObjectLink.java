package cloud.timo.TimoCloud.api.internal.links;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.PlayerObject;

import java.util.UUID;

public class PlayerObjectLink extends IdentifiableLink<PlayerObject> {

    public PlayerObjectLink(PlayerObject playerObject) {
        this(playerObject.getId(), playerObject.getName());
    }

    public PlayerObjectLink(String id, String name) {
        super(id, name);
    }

    @Override
    PlayerObject findTarget() {
        return TimoCloudAPI.getUniversalAPI().getPlayer(UUID.fromString(getId()));
    }

}
