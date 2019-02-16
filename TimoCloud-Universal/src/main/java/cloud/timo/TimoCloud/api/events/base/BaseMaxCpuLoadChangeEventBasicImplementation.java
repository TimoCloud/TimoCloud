package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseMaxCpuLoadChangeEventBasicImplementation extends BasePropertyChangeEvent<Double> implements BaseMaxCpuLoadChangeEvent {

    public BaseMaxCpuLoadChangeEventBasicImplementation(BaseObject instance, Double oldValue, Double newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_MAX_CPU_LOAD_CHANGE;
    }
}
