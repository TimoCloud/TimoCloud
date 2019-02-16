package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerGroupStaticChangeEventBasicImplementation extends ServerGroupPropertyChangeEvent<Boolean> implements ServerGroupStaticChangeEvent {

    public ServerGroupStaticChangeEventBasicImplementation(ServerGroupObject instance, Boolean oldValue, Boolean newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.SG_STATIC_CHANGE;
    }
}
