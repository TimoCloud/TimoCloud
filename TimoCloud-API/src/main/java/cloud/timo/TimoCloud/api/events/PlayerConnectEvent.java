package cloud.timo.TimoCloud.api.events;

import cloud.timo.TimoCloud.api.objects.Event;
import cloud.timo.TimoCloud.api.objects.PlayerObject;

public class PlayerConnectEvent implements Event {

    private PlayerObject playerObject;

    public PlayerConnectEvent() {
    }

    public PlayerConnectEvent(PlayerObject playerObject) {
        this.playerObject = playerObject;
    }

    public PlayerObject getPlayer() {
        return playerObject;
    }

    @Override
    public EventType getType() {
        return EventType.PLAYER_CONNECT;
    }
}
