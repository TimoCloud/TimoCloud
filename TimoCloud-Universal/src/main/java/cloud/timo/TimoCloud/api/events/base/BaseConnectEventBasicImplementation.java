package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseConnectEventBasicImplementation extends BasePropertyChangeEvent<Boolean>  implements BaseConnectEvent {

    public BaseConnectEventBasicImplementation(BaseObject instance, Boolean oldValue, Boolean newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.B_CPU_LOAD_CHANGE;
    }
}
