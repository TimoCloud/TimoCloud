package cloud.timo.TimoCloud.api.events.propertyChanges.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public class BaseConnectedChangedEvent extends BasePropertyChangedEvent<Boolean> {

    public BaseConnectedChangedEvent(BaseObject instance, Boolean oldValue, Boolean newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_CONNECTED_CHANGED;
    }
}
