package cloud.timo.TimoCloud.api.events.propertyChanges.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public class BaseMaxRamChangedEvent extends BasePropertyChangedEvent<Integer> {

    public BaseMaxRamChangedEvent(BaseObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_MAX_RAM_CHANGED;
    }
}
