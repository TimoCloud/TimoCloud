package cloud.timo.TimoCloud.api.events.player;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PlayerServerChangeEventBasicImplementation implements PlayerServerChangeEvent {

    private PlayerObject playerObject;
    private String serverFrom;
    private String serverTo;

    public PlayerServerChangeEventBasicImplementation(PlayerObject playerObject, String serverFrom, String serverTo) {
        this.playerObject = playerObject;
        this.serverFrom = serverFrom;
        this.serverTo = serverTo;
    }

    @Override
    public PlayerObject getPlayer() {
        return playerObject;
    }

    @Override
    public ServerObject getServerFrom() {
        return TimoCloudAPI.getUniversalAPI().getServer(serverFrom);
    }

    @Override
    public ServerObject getServerTo() {
        return TimoCloudAPI.getUniversalAPI().getServer(serverTo);
    }

    @Override
    public EventType getType() {
        return EventType.PLAYER_SERVER_CHANGE;
    }
}
