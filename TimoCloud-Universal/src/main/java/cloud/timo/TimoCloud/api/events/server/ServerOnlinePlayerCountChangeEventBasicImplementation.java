package cloud.timo.TimoCloud.api.events.server;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerOnlinePlayerCountChangeEventBasicImplementation extends ServerPropertyChangeEvent<Integer> implements ServerOnlinePlayerCountChangeEvent {

    public ServerOnlinePlayerCountChangeEventBasicImplementation(ServerObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.S_ONLINE_PLAYER_COUNT_CHANGE;
    }
}
