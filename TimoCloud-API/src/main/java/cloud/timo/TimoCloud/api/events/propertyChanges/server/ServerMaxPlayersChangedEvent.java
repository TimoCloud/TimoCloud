package cloud.timo.TimoCloud.api.events.propertyChanges.server;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerObject;

public class ServerMaxPlayersChangedEvent extends ServerPropertyChangedEvent<Integer> {

    public ServerMaxPlayersChangedEvent(ServerObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.S_MAX_PLAYERS_CHANGED;
    }
}
