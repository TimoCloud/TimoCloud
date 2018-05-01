package cloud.timo.TimoCloud.api.events;

import cloud.timo.TimoCloud.api.objects.CordObject;

public class CordDisconnectEvent implements Event {

    private CordObject cord;

    public CordDisconnectEvent() {
    }

    public CordDisconnectEvent(CordObject cord) {
        this.cord = cord;
    }

    public CordObject getCord() {
        return cord;
    }

    @Override
    public EventType getType() {
        return EventType.CORD_DISCONNECT;
    }
}
