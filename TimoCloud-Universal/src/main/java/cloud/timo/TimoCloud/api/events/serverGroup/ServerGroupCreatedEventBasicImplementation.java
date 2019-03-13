package cloud.timo.TimoCloud.api.events.serverGroup;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerGroupCreatedEventBasicImplementation implements ServerGroupCreatedEvent {

    private ServerGroupObject serverGroupObject;

    public ServerGroupCreatedEventBasicImplementation(ServerGroupObject serverGroupObject) {
        this.serverGroupObject = serverGroupObject;
    }

    @Override
    public ServerGroupObject getServerGroup() {
        return serverGroupObject;
    }

    @Override
    public EventType getType() {
        return EventType.SG_CREATED;
    }
}
