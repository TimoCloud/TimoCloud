package cloud.timo.TimoCloud.api.events.propertyChanges.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public class BaseAvailableRamChangedEvent extends BasePropertyChangedEvent<Integer> {

    public BaseAvailableRamChangedEvent(BaseObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_AVAILABLE_RAM_CHANGED;
    }
}
