package cloud.timo.TimoCloud.api.events.base;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseNotReadyEventBasicImplementation implements BaseNotReadyEvent {

    private BaseObject baseObject;

    public BaseNotReadyEventBasicImplementation(BaseObject instance) {
        this.baseObject = instance;
    }

    @Override
    public BaseObject getBase() {
        return baseObject;
    }

    @Override
    public EventType getType() {
        return EventType.B_NOT_READY;
    }
}
