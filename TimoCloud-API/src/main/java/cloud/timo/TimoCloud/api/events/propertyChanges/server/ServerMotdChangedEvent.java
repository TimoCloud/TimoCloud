package cloud.timo.TimoCloud.api.events.propertyChanges.server;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerObject;

public class ServerMotdChangedEvent extends ServerPropertyChangedEvent<String> {

    public ServerMotdChangedEvent(ServerObject instance, String oldValue, String newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.S_MOTD_CHANGED;
    }
}
