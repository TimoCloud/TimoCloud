package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseCpuLoadChangeEventBasicImplementation extends BasePropertyChangeEvent<Double> implements BaseCpuLoadChangeEvent {

    public BaseCpuLoadChangeEventBasicImplementation(BaseObject instance, Double oldValue, Double newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_CPU_LOAD_CHANGE;
    }
}
