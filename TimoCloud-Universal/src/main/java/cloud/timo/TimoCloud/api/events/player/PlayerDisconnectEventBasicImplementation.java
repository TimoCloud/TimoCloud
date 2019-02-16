package cloud.timo.TimoCloud.api.events.player;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PlayerDisconnectEventBasicImplementation implements PlayerDisconnectEvent {

    private PlayerObject playerObject;

    public PlayerDisconnectEventBasicImplementation(PlayerObject playerObject) {
        this.playerObject = playerObject;
    }

    @Override
    public PlayerObject getPlayer() {
        return playerObject;
    }

    @Override
    public EventType getType() {
        return EventType.PLAYER_DISCONNECT;
    }
}
