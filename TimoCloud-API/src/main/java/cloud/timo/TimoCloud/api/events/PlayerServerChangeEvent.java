package cloud.timo.TimoCloud.api.events;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

public class PlayerServerChangeEvent implements Event {

    private PlayerObject playerObject;
    private String serverFrom;
    private String serverTo;

    public PlayerServerChangeEvent() {
    }

    public PlayerServerChangeEvent(PlayerObject playerObject, String serverFrom, String serverTo) {
        this.playerObject = playerObject;
        this.serverFrom = serverFrom;
        this.serverTo = serverTo;
    }

    public PlayerObject getPlayer() {
        return playerObject;
    }

    public ServerObject getServerFrom() {
        return TimoCloudAPI.getUniversalAPI().getServer(serverFrom);
    }

    public ServerObject getServerTo() {
        return TimoCloudAPI.getUniversalAPI().getServer(serverTo);
    }

    @Override
    public EventType getType() {
        return EventType.PLAYER_SERVER_CHANGE;
    }
}
