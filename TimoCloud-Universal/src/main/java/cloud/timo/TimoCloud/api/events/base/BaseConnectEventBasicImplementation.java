package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseConnectEventBasicImplementation implements BaseConnectEvent {

    private BaseObject baseObject;

    public BaseConnectEventBasicImplementation(BaseObject instance) {
        this.baseObject = instance;
    }

    @Override
    public BaseObject getBase() {
        return baseObject;
    }

    @Override
    public EventType getType() {
        return EventType.B_CONNECT;
    }
}
