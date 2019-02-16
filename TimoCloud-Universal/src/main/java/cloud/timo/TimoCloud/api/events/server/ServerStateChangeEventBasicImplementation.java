package cloud.timo.TimoCloud.api.events.server;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerStateChangeEventBasicImplementation extends ServerPropertyChangeEvent<String> implements ServerStateChangeEvent {

    public ServerStateChangeEventBasicImplementation(ServerObject instance, String oldValue, String newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.S_STATE_CHANGE;
    }
}
