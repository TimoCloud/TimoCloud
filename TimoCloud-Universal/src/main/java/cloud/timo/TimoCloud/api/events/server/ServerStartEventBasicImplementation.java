package cloud.timo.TimoCloud.api.events.server;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerStartEventBasicImplementation implements ServerStartEvent {

    private ServerObject serverObject;

    public ServerStartEventBasicImplementation(ServerObject serverObject) {
        this.serverObject = serverObject;
    }

    public ServerObject getServer() {
        return serverObject;
    }

    @Override
    public EventType getType() {
        return EventType.SERVER_START;
    }
}
