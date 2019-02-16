package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseReadyEventBasicImplementation implements BaseReadyEvent {

    private BaseObject baseObject;

    public BaseReadyEventBasicImplementation(BaseObject instance) {
        this.baseObject = instance;
    }

    @Override
    public BaseObject getBase() {
        return baseObject;
    }

    @Override
    public EventType getType() {
        return EventType.B_READY;
    }
}
