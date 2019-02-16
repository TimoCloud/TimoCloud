package cloud.timo.TimoCloud.api.events.player;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PlayerConnectEventBasicImplementation implements PlayerConnectEvent {

    private PlayerObject playerObject;

    public PlayerConnectEventBasicImplementation(PlayerObject playerObject) {
        this.playerObject = playerObject;
    }

    @Override
    public PlayerObject getPlayer() {
        return playerObject;
    }

    @Override
    public EventType getType() {
        return EventType.PLAYER_CONNECT;
    }
}
