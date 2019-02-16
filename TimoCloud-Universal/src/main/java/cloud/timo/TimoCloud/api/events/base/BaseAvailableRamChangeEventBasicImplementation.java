package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseAvailableRamChangeEventBasicImplementation extends BasePropertyChangeEvent<Integer> implements BaseAvailableRamChangeEvent {

    public BaseAvailableRamChangeEventBasicImplementation(BaseObject instance, Integer oldValue, Integer newValue) {
        super(instance, oldValue, newValue);
    }


    @Override
    public EventType getType() {
        return EventType.B_AVAILABLE_RAM_CHANGE;
    }
}
