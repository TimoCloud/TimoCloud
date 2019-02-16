package cloud.timo.TimoCloud.api.events.cord;

import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.objects.CordObject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CordConnectEventBasicImplementation implements CordConnectEvent {

    private CordObject cord;

    public CordConnectEventBasicImplementation(CordObject cord) {
        this.cord = cord;
    }

    @Override
    public CordObject getCord() {
        return cord;
    }

    @Override
    public EventType getType() {
        return EventType.CORD_CONNECT;
    }
}
