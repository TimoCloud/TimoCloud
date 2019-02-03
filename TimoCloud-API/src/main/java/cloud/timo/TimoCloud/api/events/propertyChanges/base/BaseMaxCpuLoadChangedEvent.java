package cloud.timo.TimoCloud.api.events.propertyChanges.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;

public class BaseMaxCpuLoadChangedEvent extends BasePropertyChangedEvent<Double> {

    public BaseMaxCpuLoadChangedEvent(BaseObject instance, Double oldValue, Double newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_MAX_CPU_LOAD_CHANGED;
    }
}
