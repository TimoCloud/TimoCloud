package cloud.timo.TimoCloud.api.events;

import cloud.timo.TimoCloud.api.objects.ServerObject;

public class ServerRegisterEvent implements Event {

    private ServerObject serverObject;

    public ServerRegisterEvent() {
    }

    public ServerRegisterEvent(ServerObject serverObject) {
        this.serverObject = serverObject;
    }

    public ServerObject getServerObject() {
        return serverObject;
    }

    @Override
    public EventType getType() {
        return EventType.SERVER_REGISTER;
    }
}
