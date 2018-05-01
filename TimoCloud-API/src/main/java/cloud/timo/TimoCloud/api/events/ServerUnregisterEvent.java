package cloud.timo.TimoCloud.api.events;

import cloud.timo.TimoCloud.api.objects.ServerObject;

public class ServerUnregisterEvent implements Event {

    private ServerObject serverObject;

    public ServerUnregisterEvent() {
    }

    public ServerUnregisterEvent(ServerObject serverObject) {
        this.serverObject = serverObject;
    }

    public ServerObject getServerObject() {
        return serverObject;
    }

    @Override
    public EventType getType() {
        return EventType.SERVER_UNREGISTER;
    }
}
