package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseMaxRamChangeEventBasicImplementation extends BasePropertyChangeEvent<Integer> implements BaseMaxRamChangeEvent {

    public BaseMaxRamChangeEventBasicImplementation(BaseObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_MAX_RAM_CHANGE;
    }
}
