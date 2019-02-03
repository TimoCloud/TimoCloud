package cloud.timo.TimoCloud.api.events.propertyChanges.server;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerObject;

public class ServerPortChangedEvent extends ServerPropertyChangedEvent<Integer> {

    public ServerPortChangedEvent(ServerObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.S_PORT_CHANGED;
    }
}
