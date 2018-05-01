package cloud.timo.TimoCloud.api.events;

import cloud.timo.TimoCloud.api.objects.CordObject;

public class CordConnectEvent implements Event {

    private CordObject cord;

    public CordConnectEvent() {
    }

    public CordConnectEvent(CordObject cord) {
        this.cord = cord;
    }

    public CordObject getCord() {
        return cord;
    }

    @Override
    public EventType getType() {
        return EventType.CORD_CONNECT;
    }
}
