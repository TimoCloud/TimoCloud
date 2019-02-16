package cloud.timo.TimoCloud.api.events.server;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerUnregisterEventBasicImplementation implements ServerUnregisterEvent {

    private ServerObject serverObject;

    public ServerUnregisterEventBasicImplementation(ServerObject serverObject) {
        this.serverObject = serverObject;
    }

    public ServerObject getServer() {
        return serverObject;
    }

    @Override
    public EventType getType() {
        return EventType.SERVER_UNREGISTER;
    }
}
