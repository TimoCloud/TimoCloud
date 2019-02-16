package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseNameChangeEventBasicImplementation extends BasePropertyChangeEvent<String> implements BaseNameChangeEvent {

    public BaseNameChangeEventBasicImplementation(BaseObject instance, String oldValue, String newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_NAME_CHANGE;
    }
}
