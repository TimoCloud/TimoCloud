package cloud.timo.TimoCloud.api.events.propertyChanges.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public class BaseCpuLoadChangedEvent extends BasePropertyChangedEvent<Double> {

    public BaseCpuLoadChangedEvent(BaseObject instance, Double oldValue, Double newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_CPU_LOAD_CHANGED;
    }
}
