package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseKeepFreeRamChangeEventBasicImplementation extends BasePropertyChangeEvent<Integer> implements BaseKeepFreeRamChangeEvent {

    public BaseKeepFreeRamChangeEventBasicImplementation(BaseObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_KEEP_FREE_RAM_CHANGE;
    }
}
