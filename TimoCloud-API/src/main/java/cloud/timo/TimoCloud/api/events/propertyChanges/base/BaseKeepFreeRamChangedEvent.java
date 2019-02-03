package cloud.timo.TimoCloud.api.events.propertyChanges.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public class BaseKeepFreeRamChangedEvent extends BasePropertyChangedEvent<Integer> {

    public BaseKeepFreeRamChangedEvent(BaseObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_KEEP_FREE_RAM_CHANGED;
    }
}
