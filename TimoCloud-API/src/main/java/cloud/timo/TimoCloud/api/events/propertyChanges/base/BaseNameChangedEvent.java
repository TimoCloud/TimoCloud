package cloud.timo.TimoCloud.api.events.propertyChanges.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public class BaseNameChangedEvent extends BasePropertyChangedEvent<String> {

    public BaseNameChangedEvent(BaseObject instance, String oldValue, String newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_NAME_CHANGED;
    }
}
