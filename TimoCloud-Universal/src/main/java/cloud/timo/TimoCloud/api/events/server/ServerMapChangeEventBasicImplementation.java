package cloud.timo.TimoCloud.api.events.server;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerMapChangeEventBasicImplementation extends ServerPropertyChangeEvent<String> implements ServerMapChangeEvent {

    public ServerMapChangeEventBasicImplementation(ServerObject instance, String oldValue, String newValue) {
        super(instance, oldValue, newValue);
    }

    @Override
    public EventType getType() {
        return EventType.S_MAP_CHANGE;
    }
}
