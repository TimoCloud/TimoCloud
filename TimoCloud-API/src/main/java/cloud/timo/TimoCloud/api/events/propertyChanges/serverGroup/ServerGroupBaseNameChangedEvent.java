package cloud.timo.TimoCloud.api.events.propertyChanges.serverGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;

public class ServerGroupBaseNameChangedEvent extends ServerGroupPropertyChangedEvent<String> {

    public ServerGroupBaseNameChangedEvent(ServerGroupObject instance, String oldValue, String newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.SG_BASE_NAME_CHANGED;
    }
}
