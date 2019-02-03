package cloud.timo.TimoCloud.api.events.propertyChanges.serverGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;

public class ServerGroupStaticChangedEvent extends ServerGroupPropertyChangedEvent<Boolean> {

    public ServerGroupStaticChangedEvent(ServerGroupObject instance, Boolean oldValue, Boolean newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.SG_STATIC_CHANGED;
    }
}
