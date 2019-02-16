package cloud.timo.TimoCloud.api.events.server;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerMaxPlayersChangeEventBasicImplementation extends ServerPropertyChangeEvent<Integer> implements ServerMaxPlayersChangeEvent {

    public ServerMaxPlayersChangeEventBasicImplementation(ServerObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.S_MAX_PLAYERS_CHANGE;
    }
}
