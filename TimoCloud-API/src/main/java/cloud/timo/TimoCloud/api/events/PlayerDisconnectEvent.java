package cloud.timo.TimoCloud.api.events;

import cloud.timo.TimoCloud.api.objects.Event;
import cloud.timo.TimoCloud.api.objects.PlayerObject;

public class PlayerDisconnectEvent implements Event {

    private PlayerObject playerObject;

    public PlayerDisconnectEvent() {
    }

    public PlayerDisconnectEvent(PlayerObject playerObject) {
        this.playerObject = playerObject;
    }

    public PlayerObject getPlayer() {
        return playerObject;
    }

    @Override
    public EventType getType() {
        return EventType.PLAYER_DISCONNECT;
    }
}
