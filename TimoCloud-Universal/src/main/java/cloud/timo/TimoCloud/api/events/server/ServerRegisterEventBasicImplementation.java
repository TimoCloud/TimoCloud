package cloud.timo.TimoCloud.api.events.server;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ServerRegisterEventBasicImplementation implements ServerRegisterEvent {

    private ServerObject serverObject;

    public ServerRegisterEventBasicImplementation(ServerObject serverObject) {
        this.serverObject = serverObject;
    }

    public ServerObject getServer() {
        return serverObject;
    }

    @Override
    public EventType getType() {
        return EventType.SERVER_REGISTER;
    }
}
