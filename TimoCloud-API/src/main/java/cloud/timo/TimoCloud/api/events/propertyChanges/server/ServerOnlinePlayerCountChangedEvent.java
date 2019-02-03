package cloud.timo.TimoCloud.api.events.propertyChanges.server;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerObject;

public class ServerOnlinePlayerCountChangedEvent extends ServerPropertyChangedEvent<Integer> {

    public ServerOnlinePlayerCountChangedEvent(ServerObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.S_ONLINE_PLAYER_COUNT_CHANGED;
    }
}
